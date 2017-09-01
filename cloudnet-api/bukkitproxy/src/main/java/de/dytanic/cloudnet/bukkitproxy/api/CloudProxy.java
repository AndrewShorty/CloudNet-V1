package de.dytanic.cloudnet.bukkitproxy.api;

import de.dytanic.cloudnet.Motd;
import de.dytanic.cloudnet.ProxyLayout;
import de.dytanic.cloudnet.TabList;
import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.bukkitproxy.packet.PacketIOUpdateProxyLayout;
import de.dytanic.cloudnet.bukkitproxy.packet.PacketOutUpdateProxyInfo;
import de.dytanic.cloudnet.bukkitproxy.packet.PacketIOCustomProxyMessage;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.ProxyInfo;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnet.permission.PermissionGroup;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.*;

/**
 * Created by Tareko on 28.05.2017.
 */
@Getter
public class CloudProxy {

    @Getter
    private static CloudProxy instance;

    @Setter
    private ProxyLayout proxyLayout = new ProxyLayout("§bCloud §8| §7", false, false,
            new TabList(false, "default header", "default footer"), Arrays.asList("Dytanic"),
            new Motd(" ", " "),
            new Motd("&bPlease wait...", "&cWartungsarbeiten"), false
            , 100, false, true, "§cWartungsarbeiten", "Lobby",
            "§cThis Network is in maintenance", "§7You connecting to the Hub server...",
            "§7The fallback server doesn't exists.",
            "&7The Network is full! Buy Premium under our server ip :)",
            "&7We don't have any fallback server from cloudnet.",
            new ProxyLayout.CreateServerCommandProperties(true, "The the following games is available: %groups%", "Your server starting..."));

    /**
     * Returns the Plugin instance of the CloudNetAPI
     */
    private Plugin plugin;
    /**
     * Returns the HostName of this Proxy instance
     */
    private String hostName;
    /**
     * Returns the Port of this Proxy instance
     */
    private int port;
    /**
     * Returns the Specific Fallback of this Proxy for native
     */
    private String fallback;
    /**
     * Returns the active max memory of this instance which can used
     */
    private int memory;

    public CloudProxy(Plugin plugin, CloudNetAPI cloudNetAPI)
    {
        if (instance != null) return;

        this.instance = this;

        this.plugin = plugin;
        this.hostName = cloudNetAPI.getMetaData().getString("host");
        this.port = cloudNetAPI.getMetaData().getInt("port");
        this.fallback = cloudNetAPI.getMetaData().getString("fallback");
        this.memory = cloudNetAPI.getMetaData().getInt("memory");
    }

    /**
     * Sends a custom Proxy-Message
     */
    public void sendCustomProxyMessage(String message, Document metaData)
    {
        CloudNetAPI.getInstance().getCnpConnector()
                .sendPacket(new PacketIOCustomProxyMessage(message, metaData));
    }

    public void kickPlayer(String name, String resaon)
    {
        if (name == null || resaon == null) return;
        sendCustomProxyMessage("cloudnet_kick_player", new Document().append("name", name).append("reason", resaon));
    }

    public void kickPlayer(UUID uuid, String reason)
    {
        if (uuid == null || reason == null) return;
        sendCustomProxyMessage("cloudnet_kick_player", new Document().append("uuid", uuid).append("reason", reason));
    }

    public void sendPlayer(UUID uuid, ServerInfo serverInfo)
    {
        if (uuid == null || serverInfo == null) return;
        sendCustomProxyMessage("cloudnet_send_player", new Document().append("uuid", uuid).append("server", serverInfo.getName()));
    }

    public void broadcast(String message)
    {
        CloudProxy.getInstance().sendCustomProxyMessage(
                "broadcast_message",
                new Document().append("message", message)
        );
    }

    public CloudProxy updateProxyLayout(ProxyLayout proxyLayout)
    {
        CloudNetAPI.getInstance().getCnpConnector().sendPacket(new PacketIOUpdateProxyLayout(proxyLayout));
        return this;
    }

    /**
     * Returns a mass of Fallback-Services
     *
     * @return
     */
    public List<String> fallback()
    {
        List<String> servers = new ArrayList<>();

        for (ServerInfo serverInfo : CloudNetAPI.getInstance().getCloudNetwork().getServers().values())
        {
            if (serverInfo.getGroup().equalsIgnoreCase(CloudProxy.getInstance().getProxyLayout().getFallback()))
            {
                servers.add(serverInfo.getName());
            }
        }

        if (servers.size() == 0)
        {
            for (ServerInfo serverInfo : CloudNetAPI.getInstance().getCloudNetwork().getServers().values())
            {
                if (serverInfo.getGroup().equalsIgnoreCase(CloudProxy.getInstance().getFallback()))
                {
                    servers.add(serverInfo.getName());
                }
            }
        }

        return servers;
    }

    /**
     * Returns a mass of Fallback-Services
     *
     * @return
     */
    public ServerInfo calcFallback()
    {
        ServerInfo fallback = null;

        for (ServerInfo serverInfo : CloudNetAPI.getInstance().getServerInfos(CloudProxy.getInstance().getProxyLayout().getFallback()))
        {
            if (fallback == null)
            {

                fallback = serverInfo;
                continue;

            }

            if (fallback.getOnlineCount() > serverInfo.getOnlineCount()) fallback = serverInfo;

        }

        if (fallback == null)
        {
            for (ServerInfo serverInfo : CloudNetAPI.getInstance().getServerInfos(CloudProxy.getInstance().getFallback()))
            {
                if (fallback == null)
                {
                    fallback = serverInfo;
                    continue;
                }

                if (fallback.getOnlineCount() > serverInfo.getOnlineCount()) fallback = serverInfo;
            }
        }

        return fallback;
    }

    public ServerInfo calcFallback(PlayerWhereAmI playerWhereAmI)
    {
        ServerInfo fallback = null;

        if (playerWhereAmI != null && playerWhereAmI.getPlayerMetaData() != null && playerWhereAmI.getPlayerMetaData().getPermissionEntity() != null)
        {
            PermissionGroup permissionGroup = CloudNetAPI.getInstance().getPermissionGroup(playerWhereAmI
                    .getPlayerMetaData().getPermissionEntity().getPermissionGroup());
            if (permissionGroup != null && permissionGroup.getPermissionFallback().isEnabled())
            {
                for (ServerInfo serverInfo : CloudNetAPI.getInstance().getServerInfos(permissionGroup.getPermissionFallback().getFallback()))
                {
                    if (fallback == null)
                    {

                        fallback = serverInfo;
                        continue;

                    }

                    if (fallback != null && serverInfo != null && fallback.getOnlineCount() > serverInfo.getOnlineCount())
                        fallback = serverInfo;

                }
            }
        }

        if (fallback == null)
        {
            for (ServerInfo serverInfo : CloudNetAPI.getInstance().getServerInfos(CloudProxy.getInstance().getProxyLayout().getFallback()))
            {
                if (fallback == null)
                {

                    fallback = serverInfo;
                    continue;

                }

                if (fallback.getOnlineCount() > serverInfo.getOnlineCount()) fallback = serverInfo;

            }
        }

        if (fallback == null)
        {
            for (ServerInfo serverInfo : CloudNetAPI.getInstance().getServerInfos(CloudProxy.getInstance().getFallback()))
            {
                if (fallback == null)
                {
                    fallback = serverInfo;
                    continue;
                }

                if (fallback.getOnlineCount() > serverInfo.getOnlineCount()) fallback = serverInfo;
            }
        }

        return fallback;
    }

    /**
     * Returns a mass of Fallback-Services
     *
     * @return
     */
    public ServerInfo calcFallback(String kicked)
    {
        ServerInfo fallback = null;

        for (ServerInfo serverInfo : CloudNetAPI.getInstance().getServerInfos(CloudProxy.getInstance().getProxyLayout().getFallback()))
        {
            if (fallback == null && !kicked.equals(serverInfo.getName()))
            {

                fallback = serverInfo;
                continue;

            }

            if (fallback != null && serverInfo != null && fallback.getOnlineCount() > serverInfo.getOnlineCount() && !kicked.equals(serverInfo.getName()))
                fallback = serverInfo;

        }

        if (fallback == null)
        {
            for (ServerInfo serverInfo : CloudNetAPI.getInstance().getServerInfos(CloudProxy.getInstance().getFallback()))
            {
                if (fallback == null && !kicked.equals(serverInfo.getName()))
                {
                    fallback = serverInfo;
                    continue;
                }

                if (fallback != null && serverInfo != null && fallback.getOnlineCount() > serverInfo.getOnlineCount() && !kicked.equals(serverInfo.getName()))
                    fallback = serverInfo;
            }
        }

        return fallback;
    }

    public ServerInfo calcFallback(PlayerWhereAmI playerWhereAmI, String kicked)
    {
        ServerInfo fallback = null;

        if (playerWhereAmI != null && playerWhereAmI.getPlayerMetaData() != null && playerWhereAmI.getPlayerMetaData().getPermissionEntity() != null)
        {
            PermissionGroup permissionGroup = CloudNetAPI.getInstance().getPermissionGroup(playerWhereAmI
                    .getPlayerMetaData().getPermissionEntity().getPermissionGroup());
            if (permissionGroup != null && permissionGroup.getPermissionFallback().isEnabled())
            {
                for (ServerInfo serverInfo : CloudNetAPI.getInstance().getServerInfos(permissionGroup.getPermissionFallback().getFallback()))
                {
                    if (fallback == null && !kicked.equals(serverInfo.getName()))
                    {

                        fallback = serverInfo;
                        continue;

                    }

                    if (fallback != null && serverInfo != null && fallback.getOnlineCount() > serverInfo.getOnlineCount() && !kicked.equals(serverInfo.getName()))
                        fallback = serverInfo;

                }
            }
        }

        if (fallback == null)
        {
            for (ServerInfo serverInfo : CloudNetAPI.getInstance().getServerInfos(CloudProxy.getInstance().getProxyLayout().getFallback()))
            {
                if (fallback == null && !kicked.equals(serverInfo.getName()))
                {

                    fallback = serverInfo;
                    continue;

                }

                if (fallback != null && serverInfo != null && fallback.getOnlineCount() > serverInfo.getOnlineCount() && !kicked.equals(serverInfo.getName()))
                    fallback = serverInfo;

            }
        }

        if (fallback == null)
        {
            for (ServerInfo serverInfo : CloudNetAPI.getInstance().getServerInfos(CloudProxy.getInstance().getFallback()))
            {
                if (fallback == null && !kicked.equals(serverInfo.getName()))
                {
                    fallback = serverInfo;
                    continue;
                }

                if (fallback != null && serverInfo != null && fallback.getOnlineCount() > serverInfo.getOnlineCount() && !kicked.equals(serverInfo.getName()))
                    fallback = serverInfo;
            }
        }

        return fallback;
    }

    /**
     * Update the Network ProxyInfo
     */
    public void update()
    {

        HashMap<String, PlayerWhereAmI> players = new HashMap<>();
        for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers())
        {
            if (pp.getServer() != null)
            {
                if (CloudNetAPI.getInstance().getOnlinePlayer(pp.getUniqueId()) != null)
                {
                    PlayerWhereAmI playerWhereAmI = CloudNetAPI.getInstance().getOnlinePlayer(pp.getUniqueId());
                    players.put(playerWhereAmI.getUniqueId().toString(), playerWhereAmI);
                } else
                {
                    players.put(pp.getUniqueId().toString(), new PlayerWhereAmI(pp.getUniqueId(), pp.getAddress().getHostString(), pp.getName(),
                            CloudNetAPI.getInstance().getServerId(), pp.getServer().getInfo().getName(), null));
                }
            }
        }

        ProxyInfo proxyInfo = new ProxyInfo(
                CloudNetAPI.getInstance().getServerId(),
                hostName,
                port,
                CloudNetAPI.getInstance().getCloudId(),
                CloudNetAPI.getInstance().getUniqueId(),
                true, players,
                memory, ProxyServer.getInstance().getOnlineCount(), fallback);

        CloudNetAPI.getInstance().getCnpConnector().sendPacket(new PacketOutUpdateProxyInfo(proxyInfo));
    }
}