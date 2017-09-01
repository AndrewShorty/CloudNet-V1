package de.dytanic.cloudnet.api;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.CloudNetwork;
import de.dytanic.cloudnet.ServerType;
import de.dytanic.cloudnet.api.network.CloudNetConnector;
import de.dytanic.cloudnet.api.network.packets.*;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.lib.threading.Callback;
import de.dytanic.cloudnet.lib.threading.Scheduler;
import de.dytanic.cloudnet.network.CNSInfo;
import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnet.network.SimpleProxyInfo;
import de.dytanic.cloudnet.network.packet.PacketHandleProcessor;
import de.dytanic.cloudnet.network.packet.PacketPool;
import de.dytanic.cloudnet.permission.PermissionGroup;
import de.dytanic.cloudnet.permission.PermissionPool;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import de.dytanic.cloudnet.servergroup.ServerGroup;
import de.dytanic.cloudnet.servergroup.ServerGroupMode;
import de.dytanic.cloudnet.servergroup.SimpleServerGroup;
import de.dytanic.cloudnet.service.ServiceType;
import io.netty.util.ResourceLeakDetector;
import lombok.Getter;

import java.io.File;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by Tareko on 28.05.2017.
 */
public final class CloudNetAPI {

    @Getter
    private static CloudNetAPI instance;

    /**
     * Returns the CloudNetwork object for simpled Network Data for the Proxy and Bukkit Software
     */

    private CloudNetwork cloudNetwork = NetworkUtils.cloudNetwork();

    /**
     * MetaData implements all default datas for the Bukkit or Proxy Informations
     */
    private Document metaData;

    /**
     * Returns the serverid from this instance;
     */
    private String serverId;

    /**
     * Returns the uniqueId from this instance;
     */
    private UUID uniqueId;

    /**
     * Returns the Netty CNP Connector with SSL function if is enabled
     */
    private CloudNetConnector cnpConnector;

    /**
     * The internal Scheduler-System for using Asychronized and Sychronized methods in a single external thread
     */
    private final Scheduler scheduler = new Scheduler();

    /**
     * Returns the CloudId from the Server
     */
    private String cloudId;

    /**
     * Scheduled Thread from the scheduler system
     */
    private Thread scheduledThread;

    @Deprecated
    public CloudNetAPI(PacketOutAuth.Type type, Runnable shutdown, PacketPool packetPool, PacketHandleProcessor.PacketHandlerAbstract packetHandlerAbstract)
    {

        this.instance = this;

        this.metaData = Document.loadDocument(new File("cloudnet.json"));
        this.serverId = metaData.getString("serverId");
        this.uniqueId = UUID.fromString(metaData.getString("uniqueId"));
        this.cloudId = metaData.getString("cloudid");

        cnpConnector = new CloudNetConnector(
                scheduler,
                metaData.getBoolean("ssl"),
                packetPool,
                metaData.getString("netconnectorremote"),
                Integer.valueOf(metaData.getString("netconnectorbind").split(":")[1]), packetHandlerAbstract, shutdown
        );

        if (!cnpConnector.isConnected())
        {
            shutdown.run();
            return;
        }

        cnpConnector.getChannel().writeAndFlush(new PacketOutAuth(type, uniqueId, serverId)).syncUninterruptibly();

        scheduledThread = new Thread(scheduler);
        scheduledThread.start();

        try
        {
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
        } catch (Exception ex)
        {
        }
    }

    public CloudNetAPI writeCNPCommand(String command)
    {
        cnpConnector.sendPacket(new PacketOutCNPDBCommand(command));
        return this;
    }

    public CloudNetAPI writeCNSCommand(CNSInfo cnsInfo, String command)
    {
        if (cnsInfo == null || command == null) return this;
        cnpConnector.sendPacket(new PacketOutCNSCommand(cnsInfo, command));
        return this;
    }

    public CloudNetAPI reloadPermissions()
    {
        cnpConnector.sendPacket(new PacketOutCNPDBCommand("rlp"));
        return this;
    }

    public CloudNetAPI reload()
    {
        cnpConnector.sendPacket(new PacketOutCNPDBCommand("rl"));
        return this;
    }

    public CloudNetAPI reload(CNSInfo cnsInfo)
    {
        cnpConnector.sendPacket(new PacketOutCNSCommand(cnsInfo, "rl"));
        return this;
    }

    public Collection<PlayerWhereAmI> getOnlinePlayers()
    {
        return cloudNetwork.getOnlinePlayers().values();
    }

    public Collection<UUID> getOnlinePlayersByUUID()
    {
        Collection<UUID> collection = new CopyOnWriteArrayList<>();
        for (String uuid : cloudNetwork.getOnlinePlayers().keySet())
        {
            collection.add(UUID.fromString(uuid));
        }
        return collection;
    }

    public Collection<String> getOnlinePlayersByName()
    {
        Collection<String> names = new CopyOnWriteArrayList<>();
        for (PlayerWhereAmI playerWhereAmI : getOnlinePlayers())
        {
            names.add(playerWhereAmI.getName());
        }
        return names;
    }

    public Collection<SimpleProxyInfo> getProxys()
    {
        return cloudNetwork.getProxys().values();
    }

    public Collection<String> getProxysByName()
    {
        return cloudNetwork.getProxys().keySet();
    }

    /**
     * Returns all active online CNS registered groups
     *
     * @return
     */
    public java.util.Map<String, SimpleServerGroup> getGroups()
    {
        return cloudNetwork.getGroups();
    }

    /**
     * Returns from the CloudNetwork the servers
     *
     * @return
     */
    public java.util.Map<String, ServerInfo> getServers()
    {
        return cloudNetwork.getServers();
    }

    /**
     * Returns from the CloudNet* the permission handler instance
     *
     * @return
     */
    public PermissionPool getPermissionPool()
    {
        return cloudNetwork.getPermissionPool();
    }

    /**
     * Returns all servers from a specific group
     *
     * @param group
     * @return
     */
    public List<String> getServers(String group)
    {
        List<String> liste = new CopyOnWriteArrayList<>();
        for (ServerInfo serverInfo : cloudNetwork.getServers().values())
        {
            if (serverInfo.getGroup().equalsIgnoreCase(group)) liste.add(serverInfo.getName());
        }
        return liste;
    }

    public Collection<ServerInfo> getServerInfos()
    {
        return cloudNetwork.getServers().values();
    }

    /**
     * Returns all servers from a specific group
     *
     * @param group
     * @return
     */
    public List<ServerInfo> getServerInfos(String group)
    {
        List<ServerInfo> copy = new CopyOnWriteArrayList<>();
        for (ServerInfo info : cloudNetwork.getServers().values())
        {
            if (info.getGroup().equalsIgnoreCase(group))
            {
                copy.add(info);
            }
        }
        return copy;
    }

    public CNSInfo getCloudNetServer(String serverId)
    {

        for (CNSInfo cnsInfo : cloudNetwork.getCloudNetServers())
        {
            if (cnsInfo.getServerId().equals(serverId)) return cnsInfo;
        }
        return null;

    }

    public List<CNSInfo> getOnlineCloudNetServers()
    {
        return cloudNetwork.getCloudNetServers();
    }

    public SimpleProxyInfo getProxyInfo(String proxy)
    {

        return cloudNetwork.getProxys().get(proxy);
    }

    /**
     * Returns the OnlineCount from one Group
     */
    public int getOnlineCountByGroup(String group)
    {
        int online = 0;
        for (String server : getServers(group))
        {
            online = online + getServerInfo(server).getOnlineCount();
        }
        return online;
    }

    public CloudNetAPI createPermissionGroup(PermissionGroup permissionGroup)
    {
        cnpConnector.sendPacket(new PacketOutManagePermissions(permissionGroup, PacketOutManagePermissions.PermissionGroupHandle.CREATE));
        return this;
    }

    public CloudNetAPI deletePermissionGroup(PermissionGroup permissionGroup)
    {
        cnpConnector.sendPacket(new PacketOutManagePermissions(permissionGroup, PacketOutManagePermissions.PermissionGroupHandle.REMOVE));
        return this;
    }

    public CloudNetAPI updatePermissionGroup(PermissionGroup permissionGroup)
    {
        cnpConnector.sendPacket(new PacketOutManagePermissions(permissionGroup, PacketOutManagePermissions.PermissionGroupHandle.UPDATE));
        return this;
    }

    public CloudNetAPI addPermission(UUID user, String permission, boolean value)
    {
        cnpConnector.sendPacket(new PacketOutManagePermissions(user, permission, value, PacketOutManagePermissions.PlayerPermissionHandle.ADD));
        return this;
    }

    public CloudNetAPI removePermission(UUID user, String permission)
    {
        cnpConnector.sendPacket(new PacketOutManagePermissions(user, permission, false, PacketOutManagePermissions.PlayerPermissionHandle.REMOVE));
        return this;
    }

    public CloudNetAPI createServerGroup(CNSInfo cnsInfo, ServerGroup serverGroup)
    {
        cnpConnector.sendPacket(new PacketOutManageGroups(cnsInfo.getServerId(), serverGroup, PacketOutManageGroups.UpdateType.CREATE));
        return this;
    }

    public CloudNetAPI deleteServerGroup(CNSInfo cnsInfo, ServerGroup serverGroup)
    {
        cnpConnector.sendPacket(new PacketOutManageGroups(cnsInfo.getServerId(), serverGroup, PacketOutManageGroups.UpdateType.REMOVE));
        return this;
    }

    public CloudNetAPI updateServerGroup(CNSInfo cnsInfo, ServerGroup serverGroup)
    {
        cnpConnector.sendPacket(new PacketOutManageGroups(cnsInfo.getServerId(), serverGroup, PacketOutManageGroups.UpdateType.UPDATE));
        return this;
    }

    public Collection<PermissionGroup> getPermissionsGroups()
    {
        return cloudNetwork.getPermissionPool().getGroups().values();
    }

    /**
     * Updating a PlayerWhereAmI objective internal PlayerMetaData
     *
     * @param playerWhereAmI
     */
    public void updatePlayer(PlayerWhereAmI playerWhereAmI)
    {
        CloudNetAPI.getInstance()
                .getCnpConnector().sendPacket(new PacketIOUpdatePlayerWhereAmI(playerWhereAmI, true));
    }

    /**
     * Updates a permission group from one player
     *
     * @param playerWhereAmI
     * @param permissionGroup
     * @param timeout
     * @return
     */
    public CloudNetAPI setRank(PlayerWhereAmI playerWhereAmI, PermissionGroup permissionGroup, long timeout)
    {
        return setRank(playerWhereAmI.getName(), permissionGroup.getName(), timeout);
    }

    /**
     * Updates a permission group from one player
     *
     * @param playerWhereAmI
     * @param permissionGroup
     * @param timeout
     * @return
     */
    public CloudNetAPI setRank(PlayerWhereAmI playerWhereAmI, String permissionGroup, long timeout)
    {
        return setRank(playerWhereAmI.getName(), permissionGroup, timeout);
    }

    /**
     * Updates a permission group from one player
     *
     * @param playerWhereAmI
     * @param permissionGroup
     * @return
     */
    public CloudNetAPI setRank(PlayerWhereAmI playerWhereAmI, PermissionGroup permissionGroup, int days)
    {
        return setRank(playerWhereAmI.getName(), permissionGroup.getName(), (60 * 1000 * 60 * 24 * days + System.currentTimeMillis()));
    }

    /**
     * Updates a permission group from one player
     *
     * @param playerWhereAmI
     * @param permissionGroup
     * @return
     */
    public CloudNetAPI setRank(PlayerWhereAmI playerWhereAmI, PermissionGroup permissionGroup)
    {
        return setRank(playerWhereAmI.getName(), permissionGroup.getName(), 0);
    }

    /**
     * Updates a permission group from one player
     *
     * @param playerWhereAmI
     * @param permissionGroup
     * @return
     */
    public CloudNetAPI setRank(PlayerWhereAmI playerWhereAmI, String permissionGroup)
    {
        return setRank(playerWhereAmI.getName(), permissionGroup, 0);
    }

    /**
     * Updates a permission group from one player
     *
     * @param permissionGroup
     * @param timeout
     * @return
     */
    public CloudNetAPI setRank(String name, String permissionGroup, long timeout)
    {
        CloudNetAPI.getInstance().getCnpConnector().sendPacket(new PacketOutUpdateGroupMember(name, permissionGroup, timeout));
        return this;
    }

    /**
     * Starting a minecraft server over a external root
     *
     * @param group
     * @param properties
     * @param priorityStop
     */
    public void startServer(SimpleServerGroup group, Document properties, boolean priorityStop)
    {
        PacketIOHandleStartAndStop packetIOHandleStartAndStop = new PacketIOHandleStartAndStop(
                new Document().append("group", group.getName()).append("prioritystop", priorityStop)
                        .append("properties", properties), PacketIOHandleStartAndStop.HandleType.START_SERVER
        );
        cnpConnector.sendPacket(packetIOHandleStartAndStop);
    }

    public CloudNetAPI startCustomServer(SimpleServerGroup group, String serverId, Document properties, boolean priorityStop, int maxplayers, int memory, boolean hide)
    {
        PacketIOHandleStartAndStop packetIOHandleStartAndStop = new PacketIOHandleStartAndStop(
                new Document()
                        .append("custom", new Document()
                                .append("serverid", serverId)
                                .append("memory", memory)
                                .append("maxplayers", maxplayers)
                                .append("hide", hide)
                        )
                        .append("group", group.getName())
                        .append("prioritystop", priorityStop)
                        .append("properties", properties), PacketIOHandleStartAndStop.HandleType.START_SERVER
        );
        cnpConnector.sendPacket(packetIOHandleStartAndStop);
        return this;
    }

    public void startServer(String group, Document properties, boolean priorityStop)
    {
        startServer(getGroupData(group), properties, priorityStop);
    }

    public void startHidedServer(SimpleServerGroup group, Document properties, boolean priorityStop)
    {
        PacketIOHandleStartAndStop packetIOHandleStartAndStop = new PacketIOHandleStartAndStop(
                new Document().append("group", group.getName()).append("prioritystop", priorityStop)
                        .append("hide", true)
                        .append("properties", properties), PacketIOHandleStartAndStop.HandleType.START_SERVER
        );
        cnpConnector.sendPacket(packetIOHandleStartAndStop);
    }

    /**
     * Sets a group to a specific memmber with a timeout in millis or 0 if the player has a life time
     */
    public void setPlayerToGroup(String name, PermissionGroup permissionGroup, long timeout)
    {
        if (name == null || permissionGroup == null) return;

        PacketOutUpdateGroupMember packetOutUpdateGroupMember = new PacketOutUpdateGroupMember(name, permissionGroup.getName(), timeout);
        CloudNetAPI.getInstance().getCnpConnector()
                .sendPacket(packetOutUpdateGroupMember);

    }

    /**
     * Sends a custom command to the server console
     *
     * @param serverInfo
     * @param command
     * @return
     */
    public CloudNetAPI sendServerCommand(ServerInfo serverInfo, String command)
    {
        if (serverInfo == null || command == null) return this;
        cnpConnector
                .sendPacket(new PacketIOHandleStartAndStop(new Document().append("serverid", serverInfo.getName()).append("command", command),
                        PacketIOHandleStartAndStop.HandleType.WRITE_COMMAND));
        return this;
    }

    public int getOnlineCount()
    {
        return cloudNetwork.getOnlineCount();
    }

    /**
     * Stopped a Server with a specific ServerId
     *
     * @param serverId
     */
    public CloudNetAPI stopServer(String serverId)
    {
        PacketIOHandleStartAndStop packetIOHandleStartAndStop = new PacketIOHandleStartAndStop(
                new Document().append("serverid", serverId), PacketIOHandleStartAndStop.HandleType.STOP_SERVER
        );
        cnpConnector.sendPacket(packetIOHandleStartAndStop);
        return this;
    }

    /**
     * With this method, you can create a custom static server with a custom id, but the idName musst be has the name of the group!!!!
     * if the group doesn't groupmode = STATIC, it throws an UnsupportedOperationException
     *
     * @param group
     * @param serverId
     * @param properties
     * @param priorityStop
     * @param memory
     * @return
     * @throws UnsupportedOperationException
     */
    public CloudNetAPI startStaticServer(SimpleServerGroup group, String serverId, Document properties, boolean priorityStop, int memory) throws UnsupportedOperationException
    {
        if (!group.getMode().equals(ServerGroupMode.STATIC))
            throw new UnsupportedOperationException("[INFO] The group isn't static mode type! [INFO]");
        PacketIOHandleStartAndStop packetIOHandleStartAndStop = new PacketIOHandleStartAndStop(
                new Document()
                        .append("static", new Document()
                                .append("serverid", serverId)
                                .append("memory", memory)
                        )
                        .append("group", group.getName())
                        .append("prioritystop", priorityStop)
                        .append("properties", properties), PacketIOHandleStartAndStop.HandleType.START_SERVER
        );
        cnpConnector.sendPacket(packetIOHandleStartAndStop);
        return this;
    }

    /**
     * Stop a aktive server and start a new server with properties and prioritystop
     *
     * @param serverId
     * @param properties
     * @param priorityStop
     */
    public CloudNetAPI restartServer(String serverId, Document properties, boolean priorityStop)
    {
        stopServer(serverId);
        SimpleServerGroup simpleServerGroup = getGroupData(getServerInfo(serverId).getGroup());
        if (simpleServerGroup != null)
        {
            startServer(simpleServerGroup, properties, priorityStop);
        }
        return this;
    }

    /**
     * Stop a aktive server and start a new server with properties and prioritystop
     *
     * @param serverId
     * @param properties
     * @param priorityStop
     */
    public CloudNetAPI restartStaticServer(String serverId, Document properties, boolean priorityStop, int memory)
    {
        ServerInfo serverInfo = CloudNetAPI.getInstance().getServerInfo(serverId);
        if (serverInfo != null)
        {
            stopServer(serverId);

            scheduler.runTaskSync(() ->
            {
                startStaticServer(CloudNetAPI.getInstance().getGroupData(serverInfo.getGroup()), serverId, properties, priorityStop, memory);
            });
        }

        return this;
    }

    public CloudNetAPI sendCustomMessage(ServerType serviceType, String message, Document document)
    {
        cnpConnector.sendPacket(new PacketIOCustomChannelMessage(serviceType, message, document));
        return this;
    }

    /**
     * Asynchronized Query API
     * <p>
     * get_uuid "name" returns the uuid from a player
     * get_offlineplayerwhereami_by_name returns a playerwhereami object with a playermetadata objective or null
     *
     * @param qry           Message
     * @param metaData      Objectives in key value set
     * @param resultHandler Query Result :)
     */
    public void query(String qry, Document metaData, Callback<Document> resultHandler)
    {
        PacketIOHandleAPI packetIOHandleAPI = new PacketIOHandleAPI(qry, metaData, resultHandler, UUID.randomUUID());
        CloudNetAPI.getInstance().getCnpConnector().sendPacket(packetIOHandleAPI);
    }

    /**
     * Returns the offline player asynchronized from the query api
     *
     * @param name
     * @param callback
     */
    public CloudNetAPI getOfflinePlayer(String name, Callback<PlayerWhereAmI> callback)
    {
        query("get_offlineplayerwhereami_by_name", new Document().append("name", name), new Callback<Document>() {
            @Override
            public void call(Document value)
            {
                if (value.contains("playerwhereami"))
                {
                    callback.call(value.getObject("playerwhereami", PlayerWhereAmI.class));
                }
            }
        });
        return this;
    }

    /**
     * Returns the UUID from a player
     *
     * @param name
     * @param uuidCallback
     */
    public CloudNetAPI getUUID(String name, Callback<UUID> uuidCallback)
    {
        query("get_uuid", new Document().append("name", name), new Callback<Document>() {
            @Override
            public void call(Document value)
            {
                if (value.contains("uuid"))
                {
                    uuidCallback.call(UUID.fromString(value.getString("uuid")));
                }
            }
        });
        return this;
    }

    public CloudNetAPI getServerGroup(String name, Callback<ServerGroup> result)
    {
        query("get_serverGroup", new Document().append("groupname", name), new Callback<Document>() {
            @Override
            public void call(Document value)
            {
                if (value.contains("group"))
                {
                    result.call(value.getObject("group", new TypeToken<ServerGroup>() {
                    }.getType()));
                }
            }
        });
        return this;
    }

    public PlayerWhereAmI getOnlinePlayer(String name)
    {

        for (PlayerWhereAmI playerWhereAmI : cloudNetwork.getOnlinePlayers().values())
            if (playerWhereAmI.getName() != null && playerWhereAmI.getName()
                    .toLowerCase().equalsIgnoreCase(name.toLowerCase())) return playerWhereAmI;
        return null;
    }

    /**
     * Returns the simple group data from a group or null if the group doesn't exists in the network active
     *
     * @param groupName
     * @return
     */
    public SimpleServerGroup getGroupData(String groupName)
    {
        return cloudNetwork.getGroups().get(groupName);
    }

    public PermissionGroup getPermissionGroup(String name)
    {
        return cloudNetwork.getPermissionPool().getGroups().get(name);
    }

    /**
     * Returns an online PlayerWhereAmI objective from this active network
     *
     * @param uuid
     * @return
     */
    public PlayerWhereAmI getOnlinePlayer(UUID uuid)
    {
        return cloudNetwork.getOnlinePlayers().get(uuid.toString());
    }

    /**
     * Returns the ServerInfo objective from one server or null if doesn't exists
     *
     * @param serverId
     * @return
     */
    public ServerInfo getServerInfo(String serverId)
    {
        return cloudNetwork.getServers().get(serverId);
    }

    public CloudNetConnector getCnpConnector()
    {
        return cnpConnector;
    }

    public CloudNetwork getCloudNetwork()
    {
        return cloudNetwork;
    }

    public Document getMetaData()
    {
        return metaData;
    }

    public Scheduler getScheduler()
    {
        return scheduler;
    }

    public String getCloudId()
    {
        return cloudId;
    }

    public String getServerId()
    {
        return serverId;
    }

    public Thread getScheduledThread()
    {
        return scheduledThread;
    }

    public UUID getUniqueId()
    {
        return uniqueId;
    }

    public void setCloudNetwork(CloudNetwork cloudNetwork)
    {
        this.cloudNetwork = cloudNetwork;
    }
}