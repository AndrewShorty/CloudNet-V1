package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.CloudNetProxyServer;
import de.dytanic.cloudnetproxy.network.components.MinecraftServer;

/**
 * Created by Tareko on 29.05.2017.
 */
public class PacketInUpdateServerInfo
            extends Packet {

    public PacketInUpdateServerInfo() {}

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        if(!(sender instanceof MinecraftServer)) return;

        MinecraftServer minecraftServer = (MinecraftServer) sender;
        minecraftServer.setServerInfo(document.getObject("serverinfo", ServerInfo.class));

        CloudNetProxy.getInstance().getCloudNetwork().getServers().put(minecraftServer.getServerId(), minecraftServer.getServerInfo());

        PacketOutUpdateNetwork packetOutUpdateNetwork = new PacketOutUpdateNetwork(minecraftServer.getServerInfo(), PacketOutUpdateNetwork.UpdateType.UPDATE_SERVER);
        for (CloudNetProxyServer proxyServer : CloudNetProxy.getInstance().getProxyServer())
        proxyServer.sendAllPacket(packetOutUpdateNetwork);
    }
}