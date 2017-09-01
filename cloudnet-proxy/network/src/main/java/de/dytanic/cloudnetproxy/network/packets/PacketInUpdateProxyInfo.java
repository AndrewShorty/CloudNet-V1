package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.*;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.CloudNetProxyServer;
import de.dytanic.cloudnetproxy.network.components.ProxyServer;

/**
 * Created by Tareko on 29.05.2017.
 */
public class PacketInUpdateProxyInfo
            extends Packet {

    public PacketInUpdateProxyInfo() {}

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        if(!(sender instanceof ProxyServer)) return;

        ProxyServer minecraftServer = (ProxyServer) sender;
        minecraftServer.setProxyInfo(document.getObject("proxyinfo", ProxyInfo.class));

        CloudNetProxy.getInstance().updateOnlineCount();

        PacketOutUpdateNetwork packetOutUpdateNetwork = new PacketOutUpdateNetwork(minecraftServer, PacketOutUpdateNetwork.UpdateType.UPDATE_PROXY);
        for(CloudNetProxyServer cloudNetProxyServer : CloudNetProxy.getInstance().getProxyServer())
            cloudNetProxyServer.sendAllPacket(packetOutUpdateNetwork);

    }
}