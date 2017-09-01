package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.CloudNetProxyServer;
import de.dytanic.cloudnetproxy.network.components.CNS;
import de.dytanic.cloudnetproxy.network.components.MinecraftServer;

/**
 * Created by Tareko on 28.05.2017.
 */
public class PacketInRemoveServer
                extends Packet {

    public PacketInRemoveServer() {}

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        if(!(sender instanceof CNS)) return;
        CNS cns = (CNS) sender;

        if(cns.getServers().containsKey(document.getString("serverid")))
        {
            MinecraftServer minecraftServer = cns.getServers().get(document.getString("serverid"));
            if(minecraftServer.getChannel() != null)
            {
                minecraftServer.getChannel().close();
            }

            cns.getServers().remove(minecraftServer.getServerId());
            System.out.println("[" + cns.getServerId() + "] removed server [serverId=" +
                    minecraftServer.getServerId() + "/" + minecraftServer.getNetworkInfo().getHostName() + ":" + minecraftServer.getNetworkInfo().getPort() + "] from the Network");

            PacketOutUpdateNetwork packetOutUpdateNetwork = new PacketOutUpdateNetwork(minecraftServer.getServerInfo(), PacketOutUpdateNetwork.UpdateType.REMOVE_SERVER);
            for(CloudNetProxyServer cloudNetProxyServer : CloudNetProxy.getInstance().getProxyServer())
            cloudNetProxyServer.sendAllPacket(packetOutUpdateNetwork);
            CloudNetProxy.getInstance().updateNetwork();
        }
    }
}