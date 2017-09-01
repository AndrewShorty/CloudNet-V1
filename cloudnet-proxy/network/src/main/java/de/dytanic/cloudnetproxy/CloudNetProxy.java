package de.dytanic.cloudnetproxy;

import de.dytanic.cloudnet.CloudNetwork;
import de.dytanic.cloudnet.ProxyLayout;
import de.dytanic.cloudnet.ServerLayout;
import de.dytanic.cloudnet.Version;
import de.dytanic.cloudnet.lib.Acceptable;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.lib.threading.Callback;
import de.dytanic.cloudnet.lib.threading.Runnabled;
import de.dytanic.cloudnet.lib.threading.Scheduler;
import de.dytanic.cloudnet.logging.CloudNetLogging;
import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketPool;
import de.dytanic.cloudnet.permission.PermissionPool;
import de.dytanic.cloudnet.player.PlayerMetaData;
import de.dytanic.cloudnet.servergroup.ServerGroup;
import de.dytanic.cloudnet.servergroup.ServerGroupMode;
import de.dytanic.cloudnetproxy.database.DatabaseManager;
import de.dytanic.cloudnetproxy.database.backend.PermissionBackend;
import de.dytanic.cloudnetproxy.database.backend.PlayerDatabase;
import de.dytanic.cloudnetproxy.database.backend.SignBackend;
import de.dytanic.cloudnetproxy.database.backend.SignGroupLayoutsBackend;
import de.dytanic.cloudnetproxy.network.components.CNS;
import de.dytanic.cloudnetproxy.network.CloudNetProxyServer;
import de.dytanic.cloudnetproxy.network.components.MinecraftServer;
import de.dytanic.cloudnetproxy.network.components.ProxyServer;
import de.dytanic.cloudnetproxy.network.packets.*;
import de.dytanic.cloudnetproxy.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by Tareko on 26.05.2017.
 */
@Getter
public class CloudNetProxy
        implements Runnabled<CloudNetProxy>, Runnable {

    @Getter
    private static CloudNetProxy instance;

    @Setter
    private Set<CloudNetProxyServer> proxyServer;
    @Setter
    private ProxyLayout proxyLayout;
    @Setter
    private ServerLayout serverLayout;

    private List<String> arguments;
    private CloudNetProxyConfig config;
    private CloudNetLogging logging;
    private Version version;

    private DatabaseManager databaseManager;
    private PermissionBackend permissionBackend;
    private PlayerDatabase playerBackend;
    private SignBackend signBackend;
    private SignGroupLayoutsBackend signGroupLayoutsBackend;

    private PermissionPool permissionPool;

    private List<String> whitelist;
    private CloudNetServiceShutdown<CloudNetProxy> shutdown;
    private Scheduler scheduler = new Scheduler();
    private CloudNetwork cloudNetwork;
    private String serviceKey;
    private PacketPool packetPool = PacketPool.newSimpledPacketPool();

    private java.util.Map<String, CNS> cnsSystems = new HashMap<>();

    public CloudNetProxy(List<String> arguments, CloudNetLogging logging) throws Exception
    {
        this.instance = this;

            packetPool
            .append(0, PacketIOKeepAlive.class)
            .append(1, PacketIOPing.class)
            .append(2, PacketInAuth.class)
            .append(202, PacketInUpdateProxyLayout.class)
            .append(205, PacketIOCustomProxyMessage.class)
            .append(206, PacketIOUpdatePlayerWhereAmI.class)
            .append(207, PacketIORemovePlayerWhereAmI.class)
            .append(209, PacketIOHandleStartAndStop.class)
            .append(203, PacketInUpdateServerInfo.class)
            .append(204, PacketInUpdateProxyInfo.class)
            .append(210, PacketInUpdateGroupMember.class)
            .append(213, PacketInHandleSign.class)
            .append(214, PacketIOHandleAPI.class)
            .append(215, PacketInManagePermissions.class)
            .append(216, PacketIOManageGroups.class)
            .append(217, PacketInCNPDBCommand.class)
            .append(219, PacketIOCNSCommand.class)
            .append(301, PacketInServerData.class)
            .append(302, PacketInAddServer.class)
            .append(303, PacketInRemoveServer.class)
            .append(304, PacketInAddProxy.class)
            .append(305, PacketInRemoveProxy.class);

        this.proxyServer = new HashSet<>();

        this.version = new Version(CloudNetProxy.class.getPackage().getImplementationVersion());

        this.arguments = arguments;
        this.config = new CloudNetProxyConfig(logging.getReader());
        this.whitelist = config.loadWhitelist();
        this.proxyLayout = config.loadProxyLayout();
        this.logging = logging;

        this.shutdown = new CloudNetServiceShutdown<>(this);
        this.shutdown.getTasks().add(this);
        Runtime.getRuntime().addShutdownHook(shutdown);

        Thread scheduledThread = new Thread(scheduler);
        scheduledThread.setDaemon(true);
        scheduledThread.start();

        this.databaseManager = new DatabaseManager();
        this.permissionBackend = new PermissionBackend();
        //this.playerBackend = new PlayerBackend();
        this.playerBackend = new PlayerDatabase(this.databaseManager.getDatabase("cloudnet_players"));
        this.signBackend = new SignBackend();
        this.signGroupLayoutsBackend = new SignGroupLayoutsBackend();

        this.permissionPool = new PermissionPool();
        this.permissionPool.setAvailable(config.isPermissionSystemEnabled());
        Utils.addAll(permissionPool.getGroups(), permissionBackend.loadPermissions());

        this.serverLayout = config.loadServerLayout();
        this.serviceKey = config.getKey();

        for (CNS cns : config.loadServers())
        {
            this.cnsSystems.put(cns.getServerId(), cns);
        }

        for(int port : config.getConfiguration().getIntList("port"))
        {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run()
                {
                    System.out.println("Starting proxy-server with the port " + port);
                    new CloudNetProxyServer(arguments, config.getConfiguration().getString("hostName"), port);
                }
            });
            thread.setDaemon(true);
            thread.start();
        }

        this.cloudNetwork = updateNetwork();

        scheduler.runTaskRepeatSync(this, 0, 40);
    }

    public void updateNetwork(boolean sending)
    {
        scheduler.getExecutorService().execute(new Runnable() {
            @Override
            public void run()
            {
                CloudNetwork cloudNetwork = NetworkUtils.cloudNetwork();
                for (CNS cns : cnsSystems.values())
                {
                    if(cns.getCnsInfo() != null)
                    {
                        cloudNetwork.getCloudNetServers().add(cns.getCnsInfo());
                    }
                    for (ServerGroup group : cns.getGroups().values())
                    {
                        cloudNetwork.getGroups().put(group.getName(), group.toSimple());
                    }
                    for (MinecraftServer minecraftServer : cns.getServers().values())
                    {
                        cloudNetwork.getServers().put(minecraftServer.getServerId(), minecraftServer.getServerInfo());
                    }

                    for (ProxyServer proxyServer : cns.getProxys().values())
                    {
                        cloudNetwork.setOnlineCount(cloudNetwork.getOnlineCount() + proxyServer.getProxyInfo().getOnlineCount());
                        Utils.addAll(cloudNetwork.getOnlinePlayers(), proxyServer.getProxyInfo().getPlayers());
                    }
                }

                cloudNetwork.setPermissionPool(permissionPool);
                cloudNetwork.setServerLayout(serverLayout);
                cloudNetwork.setIpwhitelist(whitelist);

                CloudNetProxy.this.cloudNetwork = cloudNetwork;

                if (sending)
                {
                    PacketOutUpdateNetwork packetOutUpdateNetwork = new PacketOutUpdateNetwork(cloudNetwork, PacketOutUpdateNetwork.UpdateType.COMPLETE_NET);
                    sendAllPacketOnNetwork(packetOutUpdateNetwork);
                }
            }
        });
    }

    public CloudNetwork updateNetwork()
    {
        CloudNetwork cloudNetwork = NetworkUtils.cloudNetwork();

        for (CNS cns : cnsSystems.values())
        {
            if(cns.getCnsInfo() != null)
            {
                cloudNetwork.getCloudNetServers().add(cns.getCnsInfo());
            }
            for (ServerGroup group : cns.getGroups().values())
            {
                cloudNetwork.getGroups().put(group.getName(), group.toSimple());
            }
            for (MinecraftServer minecraftServer : cns.getServers().values())
            {
                cloudNetwork.getServers().put(minecraftServer.getServerId(), minecraftServer.getServerInfo());
            }

            for (ProxyServer proxyServer : cns.getProxys().values())
            {
                cloudNetwork.setOnlineCount(cloudNetwork.getOnlineCount() + proxyServer.getProxyInfo().getOnlineCount());
                Utils.addAll(cloudNetwork.getOnlinePlayers(), proxyServer.getProxyInfo().getPlayers());
                cloudNetwork.getProxys().put(proxyServer.getServerId(), proxyServer.getProxyInfo().toSimple());
            }
        }

        cloudNetwork.setPermissionPool(permissionPool);
        cloudNetwork.setServerLayout(serverLayout);
        cloudNetwork.setIpwhitelist(whitelist);

        this.cloudNetwork = cloudNetwork;

        return cloudNetwork;
    }

    public void updateOnlineCount()
    {
        int onlinecount = 0;
        cloudNetwork.getOnlinePlayers().clear();
        for (CNS cns : cnsSystems.values())
        {
            for (ProxyServer proxyServer : cns.getProxys().values())
            {
                onlinecount = onlinecount + proxyServer.getProxyInfo().getOnlineCount();
                Utils.addAll(cloudNetwork.getOnlinePlayers(), proxyServer.getProxyInfo().getPlayers());
            }
        }

        PacketOutUpdateNetwork packetOutUpdateNetwork = new PacketOutUpdateNetwork(onlinecount, cloudNetwork.getOnlinePlayers(), PacketOutUpdateNetwork.UpdateType.ONLINE_COUNT);
        sendAllPacketOnNetwork(packetOutUpdateNetwork);

    }

    public void sendAllPacketOnNetwork(Packet packet)
    {
        for(CloudNetProxyServer proxyServer : proxyServer)
        {
            proxyServer.sendAllPacket(packet);
        }
    }

    public void handlePlayer(String name, Callback<PlayerMetaData> callback)
    {
        scheduler.runTaskAsync(new Runnable() {
            @Override
            public void run()
            {
                playerBackend.getDatabase().loadDocuments();
                for (Document document : playerBackend.getDatabase().getDocuments())
                {
                    PlayerMetaData playerMetaData = document.getObject("playermetadata", PlayerMetaData.class);
                    if (playerMetaData.getName().equals(name))
                    {
                        callback.call(playerMetaData);
                        return;
                    }
                }
            }
        });
    }

    public void handlePlayer(String name, Callback<PlayerMetaData> callback, Callback<Void> nullCalll)
    {
        scheduler.runTaskAsync(new Runnable() {
            @Override
            public void run()
            {
                playerBackend.getDatabase().loadDocuments();
                for (Document document : playerBackend.getDatabase().getDocuments())
                {
                    PlayerMetaData playerMetaData = document.getObject("playermetadata", PlayerMetaData.class);
                    if (playerMetaData.getName().equals(name))
                    {
                        callback.call(playerMetaData);
                        return;
                    }
                }

                nullCalll.call(null);
            }
        });
    }

    public HashMap<String, ProxyServer> getNotConnectedProxys()
    {
        HashMap<String, ProxyServer> proxys = new HashMap<>();
        for (CNS cns : cnsSystems.values())
        {
            Utils.addAll(proxys, cns.getProxys(), new Acceptable<ProxyServer>() {
                @Override
                public boolean isAccepted(ProxyServer value)
                {
                    return value.getChannel() == null;
                }
            });
        }
        return proxys;
    }

    public HashMap<String, MinecraftServer> getNotConnectedServers()
    {
        HashMap<String, MinecraftServer> proxys = new HashMap<>();
        for (CNS cns : cnsSystems.values())
        {
            Utils.addAll(proxys, cns.getServers(), new Acceptable<MinecraftServer>() {
                @Override
                public boolean isAccepted(MinecraftServer value)
                {
                    return value.getChannel() == null;
                }
            });
        }
        return proxys;
    }

    public MinecraftServer getServer(String serverId)
    {

        for (CNS cns : cnsSystems.values())
        {
            if (cns.getServers().containsKey(serverId)) return cns.getServers().get(serverId);
        }

        return null;
    }

    public ProxyServer getProxy(String serverId)
    {

        for (CNS cns : cnsSystems.values())
        {
            if (cns.getProxys().containsKey(serverId)) return cns.getProxys().get(serverId);
        }

        return null;
    }

    public void handlePlayer(UUID uniqueId, Callback<PlayerMetaData> callback)
    {
        scheduler.runTaskSync(new Runnable() {
            @Override
            public void run()
            {
                callback.call(playerBackend.getPlayer(uniqueId));
            }
        });
    }

    public void updateProxyLayout()
    {
        this.config.reloadConfig();
        this.proxyLayout = config.loadProxyLayout();

        for(CloudNetProxyServer proxyServer : this.proxyServer)
        proxyServer.sendProxyPacket(new PacketInUpdateProxyLayout(proxyLayout));
    }

    public void sendAllLobbys(Packet packet)
    {
        for (CNS cns : cnsSystems.values())
        {
            for (MinecraftServer minecraftServer : cns.getServers().values())
            {
                if (minecraftServer.getGroupMode().equals(ServerGroupMode.LOBBY))
                {
                    minecraftServer.sendPacket(packet);
                }
            }
        }
    }

    public void sendAllGameServers(Packet packet)
    {
        for(CNS cns : cnsSystems.values())
        {
            for(MinecraftServer minecraftServer : cns.getServers().values()) minecraftServer.sendPacket(packet);
        }
    }

    @Override
    public void run(CloudNetProxy proxy)
    {

        if (proxyServer != null)
        {
            for(CloudNetProxyServer proxyServer : this.proxyServer)
            {
                proxyServer.getBossGroup().shutdownGracefully();
                proxyServer.getWorkerGroup().shutdownGracefully();
            }
        }
        for (CNS c : cnsSystems.values())
        {
            c.disconnctFromThisCNS();
        }

        databaseManager.save().getThread().stop();

        logging.shutdownAll();
        scheduler.cancelAllTasks();
    }

    @Override
    public void run()
    {
        for (CNS cns : cnsSystems.values())
        {
            long active = System.currentTimeMillis();
            for(MinecraftServer minecraftServer : cns.getServers().values())
            {
                if(minecraftServer.getUpdatePacketTime() != 0 && (active - minecraftServer.getUpdatePacketTime()) > 15000)
                {
                    PacketIOHandleStartAndStop packetIOHandleStartAndStop =
                            new PacketIOHandleStartAndStop(new Document().append("serverid", minecraftServer.getServerId()),
                            PacketIOHandleStartAndStop.HandleType.STOP_SERVER);
                    cns.sendPacket(packetIOHandleStartAndStop);
                }
            }
        }
    }
}