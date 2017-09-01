package de.dytanic.cloudnet.bukkitproxy;

import com.google.common.collect.Iterables;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.CloudNetwork;
import de.dytanic.cloudnet.ProxyLayout;
import de.dytanic.cloudnet.Value;
import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.bukkitproxy.api.CloudProxy;
import de.dytanic.cloudnet.api.network.packets.PacketIORemovePlayerWhereAmI;
import de.dytanic.cloudnet.api.network.packets.PacketIOUpdatePlayerWhereAmI;
import de.dytanic.cloudnet.api.network.packets.PacketInUpdateNetwork;
import de.dytanic.cloudnet.bukkitproxy.api.event.proxy.*;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnet.permission.PermissionEntity;
import de.dytanic.cloudnet.permission.PermissionPool;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * Created by Tareko on 20.06.2017.
 */
public class CloudNetProxyListener
        implements Listener {

    @EventHandler
    public void handleProxyCustomMessageReceive(ProxyCustomMessageReceiveEvent e)
    {
        switch (e.getMessage())
        {
            case "command":
                if (e.getDataCatcher().contains("command"))
                {
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), e.getDataCatcher().getString("command"));
                }
                break;
            case "broadcast_message":
                ProxyServer.getInstance().broadcast(e.getDataCatcher().getString("message"));
                break;
            case "cloudnet_event_login":
            {
                ProxyServer.getInstance().getPluginManager().callEvent(new ProxyNetworkPlayerLoginEvent(e.getDataCatcher().getString("name"), UUID.fromString(e.getDataCatcher().getString("uuid"))));
            }
            break;
            case "cloudnet_event_disconnect":
            {
                ProxyServer.getInstance().getPluginManager().callEvent(new ProxyNetworkPlayerDisconnectEvent(e.getDataCatcher().getString("name"), UUID.fromString(e.getDataCatcher().getString("uuid"))));
            }
            break;
            case "cloudnet_kick_player":
            {
                if (e.getDataCatcher().contains("uuid"))
                {
                    ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(UUID.fromString(e.getDataCatcher().getString("uuid")));
                    if (proxiedPlayer != null)
                    {
                        proxiedPlayer.disconnect(e.getDataCatcher().getString("reason"));
                    }
                }
                if (e.getDataCatcher().contains("name"))
                {
                    ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(e.getDataCatcher().getString("name"));
                    if (proxiedPlayer != null)
                    {
                        proxiedPlayer.disconnect(e.getDataCatcher().getString("reason"));
                    }
                }
            }
            break;
            case "cloudnet_send_player":
            {
                if (e.getDataCatcher().contains("uuid"))
                {
                    ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(UUID.fromString(e.getDataCatcher().getString("uuid")));
                    if (proxiedPlayer != null)
                    {
                        proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo(e.getDataCatcher().getString("server")));
                    }
                }
            }
            break;
            case "cloudnet_server_switch":
            {
                if (e.getDataCatcher().contains("playerwhereami"))
                {
                    PlayerWhereAmI playerWhereAmI = e.getDataCatcher().getObject("playerwhereami", new TypeToken<PlayerWhereAmI>() {
                    }.getType());
                    ProxyServer.getInstance().getPluginManager().callEvent(new ProxyNetworkPlayerSwitchServerEvent(playerWhereAmI, e.getDataCatcher().getString("server")));
                }
            }
            break;
            default:
                break;
        }
    }

    @EventHandler
    public void handleProxyCloudNetworkUpdateEvent(ProxyCloudNetworkUpdateEvent e)
    {
        if (e.getUpdateType().equals(PacketInUpdateNetwork.UpdateType.COMPLETE_NET))
        {
            CloudNetwork cloudNetwork = CloudNetAPI.getInstance().getCloudNetwork();
            Set<String> set = new HashSet<>();
            for (String server : ProxyServer.getInstance().getServers().keySet())
            {
                if (!cloudNetwork.getServers().containsKey(server))
                {
                    set.add(server);
                }
            }

            for (String s : set)
            {
                ProxyServer.getInstance().getServers().remove(s);
            }

            for (ListenerInfo listenerInfo : ProxyServer.getInstance().getConfig().getListeners())
            {
                Iterables.removeAll(listenerInfo.getServerPriority(), set);
            }

            set.clear();

            for (ServerInfo serverInfo : cloudNetwork.getServers().values())
            {
                if (CloudProxy.getInstance().getFallback().equals(serverInfo.getGroup())
                        || CloudProxy.getInstance().getProxyLayout().getFallback().equals(serverInfo.getGroup()))
                {
                    set.add(serverInfo.getName());
                }
                ProxyServer.getInstance().getServers().put(serverInfo.getName(), ProxyServer.getInstance().constructServerInfo(
                        serverInfo.getName(), new InetSocketAddress(serverInfo.getHost(), serverInfo.getPort())
                        , "", false
                ));
            }

            for (ListenerInfo listenerInfo : ProxyServer.getInstance().getConfig().getListeners())
            {
                Iterables.addAll(listenerInfo.getServerPriority(), set);
            }

        } else if (e.getUpdateType().equals(PacketInUpdateNetwork.UpdateType.REMOVE_PROXY))
        {
            if (CloudProxy.getInstance().getProxyLayout().isNotifySystem())
            {
                ProxyServer.getInstance().getScheduler().runAsync(CloudNetProxyPlugin.getInstance(), new Runnable() {
                    @Override
                    public void run()
                    {
                        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers())
                        {
                            if (player.hasPermission("cloudnet.notify"))
                            {
                                player.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The proxy " + e.getMetaData().getString("proxy") + " is unregistered on network now.");
                            }
                        }
                    }
                });
            }
        } else if (e.getUpdateType().equals(PacketInUpdateNetwork.UpdateType.UPDATE_PROXY))
        {
        } else if (e.getUpdateType().equals(PacketInUpdateNetwork.UpdateType.UPDATE_SERVER))
        {
            Value<Boolean> value = new Value<>(false);
            ServerInfo serverInfo = e.getMetaData().getObject("serverinfo", ServerInfo.class);
            if (!ProxyServer.getInstance().getServers().containsKey(serverInfo.getName()))
            {
                value.setValue(true);
                ProxyServer.getInstance().getServers().put(serverInfo.getName(), ProxyServer.getInstance().constructServerInfo(
                        serverInfo.getName(), new InetSocketAddress(serverInfo.getHost(), serverInfo.getPort())
                        , "", false
                ));
            }

            if (value.getValue() && CloudProxy.getInstance().getProxyLayout().isNotifySystem())
            {
                ProxyServer.getInstance().getScheduler().runAsync(CloudNetProxyPlugin.getInstance(), new Runnable() {
                    @Override
                    public void run()
                    {
                        //TODO: Do somth
                        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers())
                        {
                            if (player.hasPermission("cloudnet.notify"))
                            {
                                player.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The server " + serverInfo.getName() + " is registered on network now.");
                            }
                        }
                    }
                });
            }

            if (CloudProxy.getInstance().getFallback().equals(serverInfo.getGroup())
                    || CloudProxy.getInstance().getProxyLayout().getFallback().equals(serverInfo.getGroup()))
                for (ListenerInfo listenerInfo : ProxyServer.getInstance().getConfig().getListeners())
                {
                    if (!listenerInfo.getServerPriority().contains(serverInfo.getName()))
                    {
                        listenerInfo.getServerPriority().add(serverInfo.getName());
                    }
                }

            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerInfoUpdateEvent(serverInfo));

        } else if (e.getUpdateType().equals(PacketInUpdateNetwork.UpdateType.REMOVE_SERVER))
        {
            ServerInfo serverInfo = e.getMetaData().getObject("serverinfo", ServerInfo.class);
            ProxyServer.getInstance().getServers().remove(serverInfo.getName());

            if (CloudProxy.getInstance().getFallback().equals(serverInfo.getGroup())
                    || CloudProxy.getInstance().getProxyLayout().getFallback().equals(serverInfo.getGroup()))
                for (ListenerInfo listenerInfo : ProxyServer.getInstance().getConfig().getListeners())
                {
                    listenerInfo.getServerPriority().remove(serverInfo.getName());
                }

            if (CloudProxy.getInstance().getProxyLayout().isNotifySystem())
            {
                ProxyServer.getInstance().getScheduler().runAsync(CloudNetProxyPlugin.getInstance(), new Runnable() {
                    @Override
                    public void run()
                    {
                        //TODO: Do somth
                        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers())
                        {
                            if (player.hasPermission("cloudnet.notify"))
                            {
                                player.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The server " + serverInfo.getName() + " is unregistered on network now.");
                            }
                        }
                    }
                });
            }

        }
    }

    @EventHandler
    public void handleLogin(LoginEvent e)
    {
        CloudNetAPI.getInstance()
                .getCnpConnector()
                .sendPacket(new PacketIOUpdatePlayerWhereAmI(new PlayerWhereAmI(
                        e.getConnection().getUniqueId(), e.getConnection().getAddress().getHostString(),
                        e.getConnection().getName(), CloudNetAPI.getInstance().getServerId(),
                        "Lobby-1", null),
                        ProxyServer.getInstance().getOnlineCount(),
                        CloudNetAPI.getInstance().getServerId(), false
                ));

        PlayerWhereAmI playerWhereAmI;
        while (true)
        {
            try
            {
                Thread.sleep(1);
            } catch (InterruptedException e1)
            {
            }

            if ((playerWhereAmI = CloudNetAPI.getInstance().getOnlinePlayer(e.getConnection().getUniqueId())) != null)
            {

                if (!CloudProxy.getInstance().getProxyLayout().isDisableMaintenanceFunction()
                        || !CloudProxy.getInstance().getProxyLayout().isDisableLayoutFunction())
                {
                    if (CloudProxy.getInstance().getProxyLayout().isMaintenance())
                    {
                        if (!playerWhereAmI.getPlayerMetaData().getPermissionEntity()
                                .hasPermission(CloudNetAPI.getInstance().getPermissionPool(), "cloudnet.maintenance.join", null) &&
                                !CloudProxy.getInstance().getProxyLayout().getPlayerWhitelist().contains(e.getConnection().getName()))
                        {
                            e.setCancelled(true);
                            e.setCancelReason(CloudProxy.getInstance().getProxyLayout().getMaintenanceMessage());
                        }
                    }

                    if (ProxyServer.getInstance().getOnlineCount() >= CloudProxy.getInstance().getProxyLayout().getMaxOnlineCount()
                            && (playerWhereAmI.getPlayerMetaData() != null && !playerWhereAmI.getPlayerMetaData()
                            .getPermissionEntity().hasPermission(CloudNetAPI.getInstance().getPermissionPool(), "cloudnet.joinfull", null)))
                    {
                        e.setCancelled(true);
                        e.setCancelReason(ChatColor.translateAlternateColorCodes('&', CloudProxy.getInstance().getProxyLayout().getServerFullMessage()));
                    }

                }

                return;
            }
        }
    }

    @EventHandler
    public void handleProxyPostLogin(PostLoginEvent e)
    {
        CloudProxy.getInstance()
                .sendCustomProxyMessage("cloudnet_event_login", new Document().append("uuid", e.getPlayer().getUniqueId()).append("name", e.getPlayer().getName()));
    }

    @EventHandler
    public void handleSwitch(ServerSwitchEvent e)
    {
        ProxiedPlayer pp = e.getPlayer();
        if (CloudNetAPI.getInstance().getCloudNetwork().getOnlinePlayers().containsKey(e.getPlayer().getUniqueId().toString()))
        {
            PlayerWhereAmI playerWhereAmI = CloudNetAPI.getInstance().getOnlinePlayer(e.getPlayer().getUniqueId());
            playerWhereAmI.setServer(e.getPlayer().getServer().getInfo().getName());
            CloudNetAPI.getInstance().getCnpConnector().sendPacket(
                    new PacketIOUpdatePlayerWhereAmI(playerWhereAmI, ProxyServer.getInstance().getOnlineCount(), CloudNetAPI.getInstance().getServerId(), false)
            );
            CloudProxy.getInstance().sendCustomProxyMessage("cloudnet_server_switch", new Document().append("playerwhereami", playerWhereAmI).append("server", pp.getServer().getInfo().getName()));
        } else
        {
            PlayerWhereAmI playerWhereAmI = new PlayerWhereAmI(
                    pp.getUniqueId(), pp.getAddress().getHostString(), pp.getName(),
                    CloudNetAPI.getInstance().getServerId(), pp.getServer().getInfo().getName(), null);
            CloudNetAPI.getInstance().getCnpConnector().sendPacket(
                    new PacketIOUpdatePlayerWhereAmI(playerWhereAmI, ProxyServer.getInstance().getOnlineCount(), CloudNetAPI.getInstance().getServerId(), false)
            );
            CloudProxy.getInstance().sendCustomProxyMessage("cloudnet_server_switch", new Document().append("playerwhereami", playerWhereAmI).append("server", pp.getServer().getInfo().getName()));
        }

        if (CloudProxy.getInstance().getProxyLayout().getTabList().isEnabled())
        {
            e.getPlayer().setTabHeader(
                    new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', CloudProxy.getInstance().getProxyLayout().getTabList().getHeader()
                            .replace("%server%", e.getPlayer().getServer().getInfo().getName())
                            .replace("%group%", (CloudNetAPI.getInstance().getServerInfo(e.getPlayer().getServer().getInfo().getName()).getGroup()))
                            .replace("%proxy%", CloudNetAPI.getInstance().getServerId())
                            .replace("%online%", CloudNetAPI.getInstance().getCloudNetwork().getOnlineCount() + "")
                            .replace("%maxplayers%", CloudProxy.getInstance().getProxyLayout().getMaxOnlineCount() + "")
                    ).replace("%n", "\n"))),
                    new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', CloudProxy.getInstance().getProxyLayout().getTabList().getFooter()
                            .replace("%server%", e.getPlayer().getServer().getInfo().getName())
                            .replace("%group%", (CloudNetAPI.getInstance().getServerInfo(e.getPlayer().getServer().getInfo().getName()).getGroup()))
                            .replace("%proxy%", CloudNetAPI.getInstance().getServerId())
                            .replace("%online%", CloudNetAPI.getInstance().getCloudNetwork().getOnlineCount() + "")
                            .replace("%maxplayers%", CloudProxy.getInstance().getProxyLayout().getMaxOnlineCount() + "")
                    ).replace("%n", "\n"))));
        }
    }

    @EventHandler
    public void handleProxyLayOutUpdate(ProxyLayoutUpdateEvent e)
    {
        if (e.getProxyLayout().isMaintenance() && !e.getProxyLayout().isDisableMaintenanceFunction())
        {
            if (ProxyServer.getInstance().getOnlineCount() != 0)
                for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers())
                {
                    if (CloudNetAPI.getInstance().getCloudNetwork().getPermissionPool().isAvailable())
                    {
                        if (!p.hasPermission("cloudnet.maintenance.join") && !e.getProxyLayout().getPlayerWhitelist().contains(p.getName()))
                        {
                            p.disconnect(ChatColor.translateAlternateColorCodes('&',
                                    e.getProxyLayout().getMaintenanceMessage()));
                        }
                    } else
                    {
                        if (!e.getProxyLayout().getPlayerWhitelist().contains(p.getName()))
                        {
                            p.disconnect(ChatColor.translateAlternateColorCodes('&',
                                    e.getProxyLayout().getMaintenanceMessage()));
                        }
                    }
                }
        }
    }

    @EventHandler
    public void onNetworkConnect(ProxyNetworkPlayerLoginEvent e)
    {
        if (CloudProxy.getInstance().getProxyLayout().getTabList().isEnabled())
        {
            for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers())
            {
                proxiedPlayer.setTabHeader(
                        new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', CloudProxy.getInstance().getProxyLayout().getTabList().getHeader()
                                .replace("%server%", (proxiedPlayer.getServer() != null ? proxiedPlayer.getServer().getInfo().getName() : "null"))
                                .replace("%group%", (proxiedPlayer.getServer() != null ? CloudNetAPI.getInstance().getServerInfo(proxiedPlayer.getServer().getInfo().getName()).getGroup() : "null"))
                                .replace("%proxy%", CloudNetAPI.getInstance().getServerId())
                                .replace("%online%", (CloudNetAPI.getInstance().getCloudNetwork().getOnlineCount()) + "")
                                .replace("%maxplayers%", CloudProxy.getInstance().getProxyLayout().getMaxOnlineCount() + "")
                        ).replace("%n", "\n"))),
                        new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', CloudProxy.getInstance().getProxyLayout().getTabList().getFooter()
                                .replace("%server%", (proxiedPlayer.getServer() != null ? proxiedPlayer.getServer().getInfo().getName() : "null"))
                                .replace("%group%", (proxiedPlayer.getServer() != null ? CloudNetAPI.getInstance().getServerInfo(proxiedPlayer.getServer().getInfo().getName()).getGroup() : "null"))
                                .replace("%proxy%", CloudNetAPI.getInstance().getServerId())
                                .replace("%online%", (CloudNetAPI.getInstance().getCloudNetwork().getOnlineCount()) + "")
                                .replace("%maxplayers%", CloudProxy.getInstance().getProxyLayout().getMaxOnlineCount() + "")
                        ).replace("%n", "\n"))));
            }
        }
    }

    @EventHandler
    public void onNetworkLeft(ProxyNetworkPlayerDisconnectEvent e)
    {
        if (CloudProxy.getInstance().getProxyLayout().getTabList().isEnabled())
        {
            for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers())
            {
                proxiedPlayer.setTabHeader(
                        new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', CloudProxy.getInstance().getProxyLayout().getTabList().getHeader()
                                .replace("%server%", (proxiedPlayer.getServer() != null ? proxiedPlayer.getServer().getInfo().getName() : "null"))
                                .replace("%group%", (proxiedPlayer.getServer() != null ? CloudNetAPI.getInstance().getServerInfo(proxiedPlayer.getServer().getInfo().getName()).getGroup() : "null"))
                                .replace("%proxy%", CloudNetAPI.getInstance().getServerId())
                                .replace("%online%", (CloudNetAPI.getInstance().getCloudNetwork().getOnlineCount() - 1) + "")
                                .replace("%maxplayers%", CloudProxy.getInstance().getProxyLayout().getMaxOnlineCount() + "")
                        ).replace("%n", "\n"))),
                        new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', CloudProxy.getInstance().getProxyLayout().getTabList().getFooter()
                                .replace("%server%", (proxiedPlayer.getServer() != null ? proxiedPlayer.getServer().getInfo().getName() : "null"))
                                .replace("%group%", (proxiedPlayer.getServer() != null ? CloudNetAPI.getInstance().getServerInfo(proxiedPlayer.getServer().getInfo().getName()).getGroup() : "null"))
                                .replace("%proxy%", CloudNetAPI.getInstance().getServerId())
                                .replace("%online%", (CloudNetAPI.getInstance().getCloudNetwork().getOnlineCount() - 1) + "")
                                .replace("%maxplayers%", CloudProxy.getInstance().getProxyLayout().getMaxOnlineCount() + "")
                        ).replace("%n", "\n"))));
            }
        }
    }

    //a
    @EventHandler
    public void handleProxyPingEvent(ProxyPingEvent e)
    {
        if (!CloudProxy.getInstance().getProxyLayout().isDisableLayoutFunction())
        {
            ProxyLayout proxyLayout = CloudProxy.getInstance().getProxyLayout();
            int onlineCount = CloudNetAPI.getInstance().getCloudNetwork().getOnlineCount();
            if (proxyLayout.isMaintenance())
            {
                ServerPing serverPing = e.getResponse();
                serverPing.setDescription(
                        ChatColor.translateAlternateColorCodes('&', proxyLayout.getMaintenanceMotd().getFirstLine()) + "\n" +
                                ChatColor.translateAlternateColorCodes('&', proxyLayout.getMaintenanceMotd().getSecondLine()));
                serverPing.setVersion(new ServerPing.Protocol(ChatColor.translateAlternateColorCodes('&', proxyLayout.getMaintenanceDesign())
                        .replace("%online%", CloudNetAPI.getInstance().getCloudNetwork().getOnlineCount() + "").replace("%maxplayers%", proxyLayout.getMaxOnlineCount() + ""), 1));
                serverPing.setPlayers(new ServerPing.Players((proxyLayout.isAutoSlot() ? onlineCount + 1 : proxyLayout.getMaxOnlineCount()), onlineCount, null));
            } else
            {
                ServerPing serverPing = e.getResponse();
                serverPing.setDescription(
                        ChatColor.translateAlternateColorCodes('&', proxyLayout.getDefaultMotd().getFirstLine()) + "\n" +
                                ChatColor.translateAlternateColorCodes('&', proxyLayout.getDefaultMotd().getSecondLine()));
                serverPing.setPlayers(new ServerPing.Players((proxyLayout.isAutoSlot() ? onlineCount + 1 : proxyLayout.getMaxOnlineCount()), onlineCount, null));
            }
        }
    }

    @EventHandler
    public void handlePermCheck(PermissionCheckEvent e)
    {
        if (!CloudNetAPI.getInstance().getPermissionPool().isAvailable()) return;

        if (e.getSender() instanceof ProxiedPlayer)
        {
            PlayerWhereAmI playerWhereAmI = CloudNetAPI.getInstance().getOnlinePlayer(((ProxiedPlayer) e.getSender()).getUniqueId());
            if (playerWhereAmI != null && playerWhereAmI.getPlayerMetaData() != null)
            {

                PermissionEntity permissionEntity = playerWhereAmI.getPlayerMetaData().getPermissionEntity();
                PermissionPool permissionPool = CloudNetAPI.getInstance().getCloudNetwork().getPermissionPool();
                e.setHasPermission(permissionEntity.hasPermission(permissionPool, e.getPermission().toLowerCase(), null));
                return;
            }
            e.setHasPermission(false);
        }
    }

    @EventHandler
    public void handlePlayerDisconnect(PlayerDisconnectEvent e)
    {
        CloudNetAPI.getInstance()
                .getCnpConnector().sendPacket(

                new PacketIORemovePlayerWhereAmI(
                        e.getPlayer().getUniqueId(),
                        ProxyServer.getInstance().getOnlineCount() - 1,
                        CloudNetAPI.getInstance().getServerId())

        );
        CloudProxy.getInstance()
                .sendCustomProxyMessage("cloudnet_event_disconnect", new Document().append("uuid", e.getPlayer().getUniqueId()).append("name", e.getPlayer().getName()));
    }

    @EventHandler
    public void handleServerConnect(ServerConnectEvent e)
    {
        if (e.getPlayer().getServer() == null)
        {

            ServerInfo fallback = CloudProxy.getInstance().calcFallback(CloudNetAPI.getInstance().getCloudNetwork().getOnlinePlayers().get(e.getPlayer().getUniqueId().toString()));

            if (fallback != null)
            {
                e.setTarget(ProxyServer.getInstance().getServerInfo(fallback.getName()));
            } else
            {
                e.setCancelled(true);
                e.getPlayer().disconnect(ChatColor.translateAlternateColorCodes('&', CloudProxy.getInstance().getProxyLayout().getFallbackNotFoundMessage()));
            }

        }
    }

    @EventHandler
    public void handleServerKick(ServerKickEvent e)
    {
        ServerInfo serverInfo = CloudProxy.getInstance().calcFallback(CloudNetAPI.getInstance().getOnlinePlayer(e.getPlayer().getUniqueId()), e.getKickedFrom().getName());

        if (serverInfo != null)
        {
            e.getPlayer().sendMessage(e.getKickReasonComponent());
            e.setCancelled(true);
            e.setCancelServer(ProxyServer.getInstance().getServerInfo(serverInfo.getName()));
        } else
        {
            e.getPlayer().sendMessage(e.getKickReasonComponent());
            e.getPlayer().disconnect(ChatColor.translateAlternateColorCodes('&', CloudProxy.getInstance().getProxyLayout().getFallbackNotFoundMessage()).replace("%reaseon%", e.getKickReason()));
        }
    }
}