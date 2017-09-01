package de.dytanic.cloudnet.bukkitproxy;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.api.network.packets.*;
import de.dytanic.cloudnet.bukkitproxy.api.CloudProxy;
import de.dytanic.cloudnet.bukkitproxy.api.event.proxy.ProxyCloudChannelMessageEvent;
import de.dytanic.cloudnet.bukkitproxy.api.event.proxy.ProxyCloudNetworkUpdateEvent;
import de.dytanic.cloudnet.bukkitproxy.api.event.proxy.ProxyPermissionUpdateEvent;
import de.dytanic.cloudnet.bukkitproxy.api.event.proxy.ProxyPlayerWhereAmIUpdateEvent;
import de.dytanic.cloudnet.bukkitproxy.command.*;
import de.dytanic.cloudnet.bukkitproxy.packet.PacketIOCustomProxyMessage;
import de.dytanic.cloudnet.bukkitproxy.packet.PacketIOUpdateProxyLayout;
import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnet.network.packet.*;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

/**
 * Created by Tareko on 28.05.2017.
 */
public class CloudNetProxyPlugin
        extends Plugin {

    @Getter
    private static CloudNetProxyPlugin instance;

    @Override
    public void onEnable()
    {
        instance = this;

        PacketPool packetPool = PacketPool.newSimpledPacketPool();

        CloudNetAPI cloudNetAPI = new CloudNetAPI(PacketOutAuth.Type.PROXY, new Runnable() {
            @Override
            public void run()
            {
                ProxyServer.getInstance().stop("CloudNet-Stop");
            }
        }, packetPool
                .append(0, PacketOutKeepAlive.class)
                .append(201, PacketInUpdateNetwork.class)
                .append(202, PacketIOUpdateProxyLayout.class)

                .append(205, PacketIOCustomProxyMessage.class)
                .append(206, PacketIOUpdatePlayerWhereAmI.class)
                .append(207, PacketIORemovePlayerWhereAmI.class)
                .append(208, PacketInUpdatePermissionPool.class)
                .append(221, PacketIOCustomChannelMessage.class)
                , new PacketHandlerImpl());

        new CloudProxy(this, cloudNetAPI);

        getProxy().getPluginManager().registerCommand(this, new CommandHub());
        getProxy().getPluginManager().registerCommand(this, new CommandCloud());
        getProxy().getPluginManager().registerCommand(this, new CommandDevCloud());

        /*=======================================================================================*/

        getProxy().getPluginManager().registerCommand(null, new CommandAlert());
        getProxy().getPluginManager().registerCommand(null, new CloudNetProxyCommandImpl.CNPCommandFind());
        getProxy().getPluginManager().registerCommand(null, new CloudNetProxyCommandImpl.CNPCommandIP());
        getProxy().getPluginManager().registerCommand(null, new CloudNetProxyCommandImpl.CNPCommandList());
        getProxy().getPluginManager().registerCommand(null, new CloudNetProxyCommandImpl.CNPCommandSend());
        /*=======================================================================================*/

        getProxy().getPluginManager().registerListener(this, new CloudNetProxyListener());

        CloudProxy.getInstance().update();

        for (ListenerInfo listenerInfo : getProxy().getConfig().getListeners())
        {
            listenerInfo.getServerPriority().clear();
        }

        System.out.println(NetworkUtils.header((short) 2));

        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run()
            {
                if (CloudProxy.getInstance().getProxyLayout().getCreateServerCommandProperties().isEnabled())
                {
                    CommandCreateServer commandCreateServer = new CommandCreateServer();
                    getProxy().getPluginManager().registerCommand(CloudNetProxyPlugin.this, commandCreateServer);
                    getProxy().getPluginManager().registerListener(CloudNetProxyPlugin.this, commandCreateServer);
                }

                if (CloudNetAPI.getInstance().getPermissionPool().isAvailable())
                {
                    getProxy().getPluginManager().registerCommand(CloudNetProxyPlugin.this, new CommandSetRank());
                    getProxy().getPluginManager().registerCommand(CloudNetProxyPlugin.this, new CommandCloudPermissions());
                }

            }
        }, 2, TimeUnit.SECONDS);

    }

    @Override
    public void onDisable()
    {
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
                    getProxy().getPluginManager().callEvent(new ProxyCloudNetworkUpdateEvent(
                            PacketInUpdateNetwork.UpdateType.valueOf(packet.getData().getString("type")),
                            packet.getData()));
                    break;
                case 206:
                    getProxy().getPluginManager().callEvent(new ProxyPlayerWhereAmIUpdateEvent(packet.getData().getObject("pwai", new TypeToken<PlayerWhereAmI>() {
                    }.getType())));
                    break;
                case 208:
                    getProxy().getPluginManager().callEvent(new ProxyPermissionUpdateEvent(CloudNetAPI.getInstance().getPermissionPool()));
                    break;
                case 221:
                    getProxy().getPluginManager().callEvent(new ProxyCloudChannelMessageEvent(packet.getData().getString("message"), packet.getData()
                    .getDocument("data")));
                    break;
                default:
                    break;
            }
        }
    }
}