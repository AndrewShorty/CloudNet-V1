package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.CloudNetProxyServer;
import de.dytanic.cloudnetproxy.network.NetworkInfo;
import de.dytanic.cloudnetproxy.network.components.CNS;
import de.dytanic.cloudnetproxy.network.components.ProxyServer;

import java.util.UUID;

/**
 * Created by Tareko on 28.05.2017.
 */
public class PacketInAddProxy
        extends Packet {

    public PacketInAddProxy()
    {
    }

    @Override
    public void handleInput(Document value, PacketSender sender)
    {
        if (!(sender instanceof CNS)) return;
        CNS cns = (CNS) sender;

        String serverId = value.getString("serverId");
        ProxyServer proxyServer = new ProxyServer(
                cns,
                UUID.fromString(value.getString("uniqueId")),
                new NetworkInfo(serverId, value.getString("host"), value.getInt("port")),
                new NetworkInfo(serverId, value.getString("connectioninfo").split(":")[0], Integer.parseInt(value.getString("connectioninfo").split(":")[1])),
                value.getString("fallback"), value.getInt("memory"));
        cns.getProxys().put(serverId, proxyServer);
        System.out.println("[" + cns.getServerId() + "] add proxy [serverId=" +
                serverId + "/" + proxyServer.getNetworkInfo().getHostName() + ":" +
                proxyServer.getNetworkInfo().getPort() + "] into the network");

        PacketOutUpdateNetwork packetOutUpdateNetwork = new PacketOutUpdateNetwork(proxyServer, PacketOutUpdateNetwork.UpdateType.UPDATE_PROXY);
        for (CloudNetProxyServer cloudNetProxyServer : CloudNetProxy.getInstance().getProxyServer())
            cloudNetProxyServer.sendAllPacket(packetOutUpdateNetwork);

        CloudNetProxy.getInstance().updateNetwork();
    }
}