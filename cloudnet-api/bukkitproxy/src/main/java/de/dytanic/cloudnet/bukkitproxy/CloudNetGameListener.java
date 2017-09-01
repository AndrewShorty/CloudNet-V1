package de.dytanic.cloudnet.bukkitproxy;

import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.api.network.packets.PacketIOHandleStartAndStop;
import de.dytanic.cloudnet.api.network.packets.PacketInUpdateNetwork;
import de.dytanic.cloudnet.bukkitproxy.api.CloudServer;
import de.dytanic.cloudnet.bukkitproxy.api.event.bukkit.CloudNetworkUpdateEvent;
import de.dytanic.cloudnet.bukkitproxy.api.event.bukkit.ServerInfoUpdateEvent;
import de.dytanic.cloudnet.bukkitproxy.api.event.bukkit.ServerRemoveEvent;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnet.permission.PermissionEntity;
import de.dytanic.cloudnet.permission.PermissionPool;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import de.dytanic.cloudnet.servergroup.ServerGroupMode;
import de.dytanic.cloudnet.servergroup.ServerState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * Created by Tareko on 21.06.2017.
 */
public class CloudNetGameListener
        implements Listener {

    @EventHandler
    public void handleUpdate(CloudNetworkUpdateEvent e)
    {
        if (e.getUpdateType().equals(PacketInUpdateNetwork.UpdateType.UPDATE_SERVER))
        {
            Bukkit.getServer().getPluginManager().callEvent(new ServerInfoUpdateEvent(
                    e.getMetaData().getObject("serverinfo", ServerInfo.class)
            ));
        } else if (e.getUpdateType().equals(PacketInUpdateNetwork.UpdateType.REMOVE_SERVER))
        {
            Bukkit.getPluginManager().callEvent(new ServerRemoveEvent(e.getMetaData().getObject("serverinfo", ServerInfo.class)));
        }
    }

    @Deprecated
    private String getVersion()
    {
        return org.bukkit.Bukkit.getServer().getClass().getPackage()
                .getName().split("\\.")[3];
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleLogin(PlayerLoginEvent e)
    {

        if (CloudNetAPI.getInstance().getOnlinePlayer(e.getPlayer().getUniqueId()) == null)
        {
            e.disallow(PlayerLoginEvent.Result.KICK_BANNED, "§cYour must connect from a internal Proxy-Server!");
            return;
        }

        if (!CloudServer.getInstance().getServerGroupMode().equals(ServerGroupMode.LOBBY)
                && CloudNetAPI.getInstance().getCloudNetwork().getOnlinePlayers().containsKey(e.getPlayer().getUniqueId().toString()))
        {
            if (CloudNetAPI.getInstance().getPermissionPool().isAvailable())
            {
                PlayerWhereAmI playerWhereAmI = CloudNetAPI.getInstance().getCloudNetwork().getOnlinePlayers().get(e.getPlayer().getUniqueId().toString());
                if (playerWhereAmI.getPlayerMetaData() != null
                        && CloudNetAPI.getInstance().getCloudNetwork().getPermissionPool().getGroups().containsKey(playerWhereAmI.getPlayerMetaData().getPermissionEntity().getPermissionGroup())
                        && CloudNetAPI.getInstance().getCloudNetwork().getGroups().containsKey(CloudServer.getInstance().getGroup()) &&
                        (CloudNetAPI.getInstance().getCloudNetwork().getPermissionPool().getGroups().get(
                                playerWhereAmI.getPlayerMetaData().getPermissionEntity().getPermissionGroup()
                        ).getJoinPower() < CloudNetAPI.getInstance().getCloudNetwork().getGroups().get(CloudServer.getInstance().getGroup()).getJoinPower()))
                {
                    e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', CloudNetAPI.getInstance().getCloudNetwork().getServerLayout().getNoPermissionToEnterServerMessage()));
                    return;
                }
            } else
            {
                if ((CloudNetAPI.getInstance().getGroupData(CloudServer.getInstance().getGroup()) != null &&
                        CloudNetAPI.getInstance().getGroupData(CloudServer.getInstance().getGroup()).getJoinPower() > 0
                ) && !e.getPlayer().hasPermission("cloudnet.joinpower." + CloudServer.getInstance().getServerGroup().getJoinPower()))
                {
                    e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', CloudNetAPI.getInstance().getCloudNetwork().getServerLayout().getNoPermissionToEnterServerMessage()));
                    return;
                }
            }
        }

        if (!CloudNetAPI.getInstance().getPermissionPool().isAvailable()) return;

        try
        {
            Class<?> c = Class.forName("org.bukkit.craftbukkit." + getVersion() + ".entity.CraftHumanEntity");
            Field field = c.getDeclaredField("perm");
            field.setAccessible(true);
            field.set(e.getPlayer(), new PermissibleImpl(e.getPlayer()));
        } catch (Exception ex)
        {
            try
            {
                Class<?> c = Class.forName("org.bukkit.craftbukkit.entity.CraftHumanEntity");
                Field field = c.getDeclaredField("perm");
                field.setAccessible(true);
                field.set(e.getPlayer(), new PermissibleImpl(e.getPlayer()));
            } catch (Exception exx)
            {
                exx.printStackTrace();
            }
        }
    }

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent e)
    {

        updateServerInfo();

        if (CloudServer.getInstance().isCanStartServer() && !CloudServer.getInstance().isBetaServer() && (Bukkit.getOnlinePlayers().size() >= CloudServer.getInstance().getMaxPlayers())
                && !CloudServer.getInstance().getServerGroupMode().equals(ServerGroupMode.STATIC))
        {
            if (CloudServer.getInstance().isCanStartServer() && CloudServer.getInstance().isAutoStartServerByFull())
            {
                CloudNetAPI.getInstance()
                        .getCnpConnector().sendPacket(new PacketIOHandleStartAndStop(
                        new Document().append("group", CloudServer.getInstance().getGroup())
                                .append("prioritystop", true), PacketIOHandleStartAndStop.HandleType.START_SERVER
                ));
                CloudServer.getInstance().setCanStartServer(false);
                Bukkit.getScheduler().runTaskLater(CloudServer.getInstance().getPlugin(), () ->
                {
                    CloudServer.getInstance().setCanStartServer(true);
                }, 6000);
            }
        }

        if (CloudServer.getInstance().isBetaServer())
        {
            e.getPlayer().sendMessage("§7This server is a development server. all of the server will save if stop. You can disable this server when you changed the maintenance.");
        }

    }

    @EventHandler
    public void handleQuit(PlayerQuitEvent e)
    {
        updateServerInfoDelay(e.getPlayer().getName());

        Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), new Runnable() {
            @Override
            public void run()
            {
                if (CloudServer.getInstance().getProperties().contains("createdServerUUID"))
                {
                    if (e.getPlayer().getUniqueId().equals(UUID.fromString(CloudServer.getInstance().getProperties().getString("createdServerUUID"))) &&
                            CloudServer.getInstance().getServerState() != ServerState.INGAME)
                    {
                        CloudNetAPI.getInstance().stopServer(CloudNetAPI.getInstance().getServerId());
                    }
                }
            }
        });
    }

    private class PermissibleImpl
            extends PermissibleBase {

        private final UUID uuid;

        public PermissibleImpl(Player player)
        {
            super(player);
            this.uuid = player.getUniqueId();
        }

        @Override
        public boolean isPermissionSet(String name)
        {
            return hasPermission(name);
        }

        @Override
        public boolean isPermissionSet(Permission perm)
        {
            return hasPermission(perm);
        }

        @Override
        public boolean hasPermission(Permission perm)
        {
            return hasPermission(perm.getName());
        }

        @Override
        public boolean hasPermission(String inName)
        {

            if(inName.toLowerCase().equalsIgnoreCase("bukkit.broadcast.user")) return true;

            String permission = inName.toLowerCase();

            if (!CloudNetAPI.getInstance().getPermissionPool().isAvailable()) return false;
            if (!CloudNetAPI.getInstance().getCloudNetwork().getOnlinePlayers().containsKey(uuid.toString()))
                return false;
            PlayerWhereAmI playerWhereAmI = CloudNetAPI.getInstance().getOnlinePlayer(uuid);
            if (playerWhereAmI.getPlayerMetaData() != null)
            {
                PermissionEntity permissionEntity = playerWhereAmI.getPlayerMetaData().getPermissionEntity();
                PermissionPool permissionPool = CloudNetAPI.getInstance().getCloudNetwork().getPermissionPool();
                return permissionEntity.hasPermission(permissionPool, permission, CloudServer.getInstance().getGroup());
            }
            return false;
        }
    }

    private void updateServerInfo()
    {
        CloudServer.getInstance().update();
    }

    private void updateServerInfoDelay(String name)
    {
        Bukkit.getScheduler().runTaskLaterAsynchronously(CloudServer.getInstance().getPlugin(), new Runnable() {

                    @Override
                    public void run()
                    {
                        CloudServer.getInstance().update();
                    }
                }

        , 1L);
    }

}
