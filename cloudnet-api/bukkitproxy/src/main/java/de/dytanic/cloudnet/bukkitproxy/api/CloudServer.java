package de.dytanic.cloudnet.bukkitproxy.api;

import de.dytanic.cloudnet.Value;
import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.bukkitproxy.packet.PacketIOCustomProxyMessage;
import de.dytanic.cloudnet.bukkitproxy.packet.PacketOutUpdateServerInfo;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnet.permission.PermissionGroup;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import de.dytanic.cloudnet.servergroup.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Instance from a Bukkit Server
 */
public final class CloudServer {

    private static CloudServer instance;

    public static CloudServer getInstance()
    {
        return instance;
    }

    private Document properties;
    private ServerState serverState;
    private String motd;
    private int maxPlayers;
    private String extra;

    private ServerMap map;
    private int memory;
    private JavaPlugin plugin;
    private long startUp;
    private String group;
    private ServerGroupMode serverGroupMode;
    private boolean betaServer;
    private ServerGroupProfile profile;
    private boolean hide;

    private boolean autoStartServerByFull = true;
    private volatile boolean canStartServer = true;
    private Value<UUID> owner = new Value<>(null);

    public CloudServer(JavaPlugin plugin, CloudNetAPI cloudNetAPI)
    {
        if (instance != null) return;
        this.instance = this;

        this.plugin = plugin;
        this.map = cloudNetAPI.getMetaData().getObject("map", ServerMap.class);
        this.properties = cloudNetAPI.getMetaData().getDocument("properties");
        this.memory = CloudNetAPI.getInstance().getMetaData().getInt("memory");

        this.maxPlayers = Bukkit.getMaxPlayers();
        this.motd = Bukkit.getMotd();
        this.serverState = ServerState.LOBBY;
        this.startUp = System.currentTimeMillis();
        this.group = CloudNetAPI.getInstance().getMetaData().getString("group");
        this.serverGroupMode = ServerGroupMode.valueOf(CloudNetAPI.getInstance().getMetaData().getString("mode"));
        this.betaServer = cloudNetAPI.getMetaData().getBoolean("beta");
        this.profile = cloudNetAPI.getMetaData().getObject("profile", ServerGroupProfile.class);
        this.hide = cloudNetAPI.getMetaData().getBoolean("hide");

        if (CloudServer.getInstance().getProperties().contains("createdServerUUID"))
        {
            owner.setValue(UUID.fromString(CloudServer.getInstance().getProperties().getString("createdServerUUID")));
        }
    }

    public SimpleServerGroup getServerGroup()
    {
        return CloudNetAPI.getInstance().getGroupData(group);
    }

    public CloudServer update()
    {
        try
        {
            ServerListPingEvent serverListPingEvent = new ServerListPingEvent(new InetSocketAddress("127.0.0.1", 30530).getAddress(),
                    Bukkit.getMotd(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
            Bukkit.getPluginManager().callEvent(serverListPingEvent);
            CloudServer.getInstance().setMotd(serverListPingEvent.getMotd());
        } catch (Exception ex)
        {
        }

        List<String> players = new ArrayList<>();
        if (Bukkit.getOnlinePlayers().size() != 0)
        {
            for (Player player : Bukkit.getOnlinePlayers())
            {
                players.add(player.getName());
            }
        }
        ServerInfo serverInfo = new ServerInfo(CloudNetAPI.getInstance().getServerId(), group, Bukkit.getServer().getIp(), Bukkit.getPort(),
                CloudNetAPI.getInstance().getCloudId(),
                CloudNetAPI.getInstance().getUniqueId(), true, players,
                memory, map, profile, startUp, motd, players.size(), maxPlayers, extra, serverState, properties, hide);
        CloudNetAPI.getInstance().getCnpConnector().sendPacket(new PacketOutUpdateServerInfo(serverInfo));
        return this;
    }

    public void sendCustomProxyMessage(String message, Document dataCatcher)
    {
        CloudNetAPI.getInstance()
                .getCnpConnector()
                .sendPacket(new PacketIOCustomProxyMessage(message, dataCatcher));
    }

    public CloudServer changeToIngame(boolean newServer)
    {

        setServerState(ServerState.INGAME);
        update();
        if (canStartServer)
        {
            CloudNetAPI.getInstance().startServer(CloudNetAPI.getInstance().getCloudNetwork().getGroups().get(group), new Document(), true);
        }
        return this;
    }

    /**
     * Updating and sets the NameTags for one target Player
     *
     * @param player
     */
    public void updateNameTags(Player player)
    {

        if (!CloudNetAPI.getInstance().getPermissionPool().isAvailable()) return;
        PlayerWhereAmI playerWhereAmI = CloudNetAPI.getInstance().getOnlinePlayer(player.getUniqueId());

        if (player.getScoreboard() == null) player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        if (playerWhereAmI.getPlayerMetaData() != null)
        {
            for (Player all : Bukkit.getOnlinePlayers())
            {
                if (all.getScoreboard() == null)
                {
                    all.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }

                {
                    PermissionGroup permissionGroup =
                            CloudNetAPI.getInstance().getPermissionPool().getGroups().get(playerWhereAmI.getPlayerMetaData().getPermissionEntity().getPermissionGroup());
                    if (permissionGroup != null)
                    {
                        Team team = all.getScoreboard().getTeam(permissionGroup.getTagId() + permissionGroup.getName());
                        if (team == null)
                            team = all.getScoreboard().registerNewTeam(permissionGroup.getTagId() + permissionGroup.getName());
                        team.setPrefix(ChatColor.translateAlternateColorCodes('&', permissionGroup.getPrefix()));
                        team.setSuffix(ChatColor.translateAlternateColorCodes('&', permissionGroup.getSuffix()));
                        player.setDisplayName(ChatColor.translateAlternateColorCodes('&', permissionGroup.getDisplay() + player.getName()));
                        team.addEntry(player.getName());
                    }
                }

                {
                    PlayerWhereAmI playerWhereAmI1 = CloudNetAPI.getInstance().getOnlinePlayer(all.getUniqueId());
                    if (playerWhereAmI1 != null)
                    {
                        PermissionGroup permissionGroup =
                                CloudNetAPI.getInstance().
                                        getCloudNetwork().getPermissionPool()
                                        .getGroups().get(playerWhereAmI1
                                        .getPlayerMetaData().getPermissionEntity().getPermissionGroup());
                        if (permissionGroup != null)
                        {
                            Team team = player.getScoreboard().getTeam(permissionGroup.getTagId() + permissionGroup.getName());
                            if (team == null)
                                team = player.getScoreboard().registerNewTeam(permissionGroup.getTagId() + permissionGroup.getName());
                            team.setPrefix(ChatColor.translateAlternateColorCodes('&', permissionGroup.getPrefix()));
                            team.setSuffix(ChatColor.translateAlternateColorCodes('&', permissionGroup.getSuffix()));
                            all.setDisplayName(ChatColor.translateAlternateColorCodes('&', permissionGroup.getDisplay() + all.getName()));
                            team.addEntry(all.getName());
                        }
                    }
                }
            }
        }
    }

    public void setProperties(Document properties)
    {
        this.properties = properties;
    }

    public void setServerState(ServerState serverState)
    {
        this.serverState = serverState;
    }

    public void setMotd(String motd)
    {
        this.motd = motd;
    }

    public void setMaxPlayers(int maxPlayers)
    {
        this.maxPlayers = maxPlayers;
    }

    public void setExtra(String extra)
    {
        this.extra = extra;
    }

    public int getMemory()
    {
        return memory;
    }

    public Document getProperties()
    {
        return properties;
    }

    public int getMaxPlayers()
    {
        return maxPlayers;
    }

    public JavaPlugin getPlugin()
    {
        return plugin;
    }

    public long getStartUp()
    {
        return startUp;
    }

    public ServerGroupMode getServerGroupMode()
    {
        return serverGroupMode;
    }

    public ServerMap getMap()
    {
        return map;
    }

    public ServerState getServerState()
    {
        return serverState;
    }

    public String getExtra()
    {
        return extra;
    }

    public String getGroup()
    {
        return group;
    }

    public String getMotd()
    {
        return motd;
    }

    public boolean isBetaServer()
    {
        return betaServer;
    }

    public ServerGroupProfile getProfile()
    {
        return profile;
    }

    public void setMap(ServerMap map)
    {
        this.map = map;
    }

    public void setProfile(ServerGroupProfile profile)
    {
        this.profile = profile;
    }

    public Value<UUID> getOwner()
    {
        return owner;
    }

    public boolean isAutoStartServerByFull()
    {
        return autoStartServerByFull;
    }

    public void setAutoStartServerByFull(boolean autoStartServerByFull)
    {
        this.autoStartServerByFull = autoStartServerByFull;
    }

    public boolean isHide()
    {
        return hide;
    }

    @Deprecated
    public boolean isCanStartServer()
    {
        return canStartServer;
    }

    @Deprecated
    public void setCanStartServer(boolean canStartServer)
    {
        this.canStartServer = canStartServer;
    }
}