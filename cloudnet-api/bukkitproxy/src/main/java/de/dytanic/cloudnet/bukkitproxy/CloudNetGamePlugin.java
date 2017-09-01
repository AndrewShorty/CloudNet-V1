package de.dytanic.cloudnet.bukkitproxy;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.api.network.packets.*;
import de.dytanic.cloudnet.bukkitproxy.api.CloudServer;
import de.dytanic.cloudnet.bukkitproxy.api.event.bukkit.*;
import de.dytanic.cloudnet.bukkitproxy.command.CommandResource;
import de.dytanic.cloudnet.bukkitproxy.packet.PacketInUpdateSignsSystem;
import de.dytanic.cloudnet.bukkitproxy.serverselectors.ServerSelectorSigns;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnet.network.packet.*;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import de.dytanic.cloudnet.servergroup.ServerGroupMode;
import de.dytanic.cloudnet.servergroup.ServerState;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;

/**
 * Created by Tareko on 28.05.2017.
 */
public class CloudNetGamePlugin
        extends JavaPlugin {

    @Getter
    private static CloudNetGamePlugin instance;

    @Override
    public void onEnable()
    {
        try
        {
            Class.forName("org.spigotmc.AsyncCatcher");
            Field field = Class.forName("org.spigotmc.AsyncCatcher").getField("enabled");
            field.setAccessible(true);
            field.set(null, false);

        } catch (Exception e)
        {
        }

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        instance = this;
        PacketPool packetPool = PacketPool.newSimpledPacketPool();

        CloudNetAPI cloudNetAPI = new CloudNetAPI(PacketOutAuth.Type.MINECRAFT, new Runnable() {
            @Override
            public void run()
            {
                Bukkit.shutdown();
            }
        }
                , packetPool
                .append(0, PacketOutKeepAlive.class)
                //In
                .append(201, PacketInUpdateNetwork.class)
                .append(206, PacketIOUpdatePlayerWhereAmI.class)
                .append(207, PacketIORemovePlayerWhereAmI.class)
                .append(208, PacketInUpdatePermissionPool.class)
                .append(212, PacketInUpdateSignsSystem.class)
                .append(214, PacketIOHandleAPI.class)
                .append(221, PacketIOCustomChannelMessage.class)
                , new PacketHandlerImpl());

        new CloudServer(this, cloudNetAPI);

        if (CloudServer.getInstance().getServerGroupMode().equals(ServerGroupMode.LOBBY))
        {
            getServer().getPluginManager().registerEvents(new ServerSelectorSigns(this), this);
        }

        getServer().getPluginManager().registerEvents(new CloudNetGameListener(), this);
        getCommand("resource").setExecutor(new CommandResource());
        getCommand("resource").setPermission("cloudnet.command.resource");

        System.out.println(NetworkUtils.header((short) 2));

        getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run()
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

                Bukkit.getPluginManager().callEvent(new CloudServerStartupEvent(CloudServer.getInstance()));
                CloudServer.getInstance().update();
            }
        });

        cloudNetAPI.getScheduler().runTaskRepeatSync(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    ServerListPingEvent serverListPingEvent = new ServerListPingEvent(new InetSocketAddress("127.0.0.1", 30530).getAddress(),
                            Bukkit.getMotd(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
                    Bukkit.getPluginManager().callEvent(serverListPingEvent);
                    if (!serverListPingEvent.getMotd().equalsIgnoreCase(CloudServer.getInstance().getMotd()))
                    {
                        CloudServer.getInstance().setMotd(serverListPingEvent.getMotd());

                        if(serverListPingEvent.getMotd().equalsIgnoreCase("INGAME") || serverListPingEvent.getMotd().equalsIgnoreCase("RUNNING"))
                        {
                            CloudServer.getInstance().changeToIngame(true);
                        }
                    }
                } catch (Exception ex)
                {
                }
            }
        }, 0, 10);
    }

    @Override
    public void onDisable()
    {

        CloudServer.getInstance().setServerState(ServerState.OFFLINE);
        CloudServer.getInstance().update();

        getServer().getMessenger().unregisterOutgoingPluginChannel(this);

        if (ServerSelectorSigns.getInstance() != null && ServerSelectorSigns.getInstance().getHandledThread() != null)
        {
            ServerSelectorSigns.getInstance().getHandledThread().stop();
            ServerSelectorSigns.getInstance().getSigns().clear();
            ServerSelectorSigns.getInstance().getLayouts().clear();
        }

        if (System.getProperty("os.name") != null && (System.getProperty("os.name").contains("Windows") ||
                System.getProperty("os.name").contains("windows")))
        {
            PacketIOHandleStartAndStop packetIOHandleStartAndStop = new PacketIOHandleStartAndStop(
                    new Document().append("serverid", CloudNetAPI.getInstance().getServerId()), PacketIOHandleStartAndStop.HandleType.STOP_SERVER
            );
            CloudNetAPI.getInstance().getCnpConnector().sendPacketSync(packetIOHandleStartAndStop);
        }
        CloudNetAPI.getInstance().getScheduler().cancelAllTasks();
        CloudNetAPI.getInstance().getScheduledThread().stop();
        CloudNetAPI.getInstance().getCnpConnector().getWorkerLoop().shutdownGracefully();
    }

    private class PacketHandlerImpl
            extends PacketHandleProcessor.PacketHandlerAbstract {
        @Override
        public void handleIncomingPacket(Packet packet, PacketSender packetSender)
        {
            switch (packet.getId())
            {
                case 201:
                    getServer().getPluginManager().callEvent(new CloudNetworkUpdateEvent(
                            PacketInUpdateNetwork.UpdateType.valueOf(packet.getData().getString("type")),
                            packet.getData()));

                    break;
                case 209:
                {
                    Document document = packet.getData();
                    if (document.getString("type").equalsIgnoreCase("WRITE_COMMAND"))
                    {
                        Document data = document.getDocument("data");
                        if (data.contains("command")) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), data.getString("command"));
                    }
                }
                break;
                case 208:
                    getServer().getPluginManager().callEvent(new PermissionUpdateEvent(CloudNetAPI.getInstance().getPermissionPool()));
                    break;
                case 206:
                    Bukkit.getPluginManager().callEvent(new PlayerWhereAmIUpdateEvent(packet.getData().getObject("pwai", new TypeToken<PlayerWhereAmI>() {
                    }.getType())));
                    break;
                case 221:
                    getServer().getPluginManager().callEvent(new CloudChannelMessageEvent(
                            packet.getData().getString("message"),
                            packet.getData().getDocument("data")
                            ));
                    break;
                default:
                    break;
            }
        }
    }
}
