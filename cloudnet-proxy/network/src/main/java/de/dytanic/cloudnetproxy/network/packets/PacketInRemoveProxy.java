package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.CloudNetProxyServer;
import de.dytanic.cloudnetproxy.network.components.CNS;
import de.dytanic.cloudnetproxy.network.components.ProxyServer;

/**
 * Created by Tareko on 28.05.2017.
 */
public class PacketInRemoveProxy
        extends Packet {

    public PacketInRemoveProxy() {}

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        if(!(sender instanceof CNS)) return;
        CNS cns = (CNS) sender;

        if(cns.getProxys().containsKey(document.getString("serverid")))
        {
            ProxyServer minecraftServer = cns.getProxys().get(document.getString("serverid"));
            if(minecraftServer.getChannel() != null)
            {
                minecraftServer.getChannel().close();
            }

            cns.getProxys().remove(minecraftServer.getServerId());
            System.out.println("[" + cns.getServerId() + "] removed proxy [serverId=" +
                    minecraftServer.getServerId() + "/" + minecraftServer.getNetworkInfo().getHostName() + ":" +
                    minecraftServer.getNetworkInfo().getPort() + "] from the Network");

            PacketOutUpdateNetwork packetOutUpdateNetwork = new PacketOutUpdateNetwork(minecraftServer, PacketOutUpdateNetwork.UpdateType.REMOVE_PROXY);
            for(CloudNetProxyServer proxyServer : CloudNetProxy.getInstance().getProxyServer())
                proxyServer.sendAllPacket(packetOutUpdateNetwork);
            CloudNetProxy.getInstance().updateNetwork();
        }
    }
}