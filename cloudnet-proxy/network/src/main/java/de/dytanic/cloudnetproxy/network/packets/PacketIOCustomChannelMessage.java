package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.ServerType;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.CloudNetProxyServer;

/**
 * Created by Tareko on 27.07.2017.
 */
public class PacketIOCustomChannelMessage extends Packet {

    private PacketIOCustomChannelMessage(Document document)
    {
        super(221, document);
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender)
    {
        ServerType serviceType = ServerType.valueOf(data.getString("servicetype"));
        if(serviceType.equals(ServerType.PROXY))
        {
            for(CloudNetProxyServer cloudNetProxyServer : CloudNetProxy.getInstance().getProxyServer())
            {
                cloudNetProxyServer.sendProxyPacket(new PacketIOCustomChannelMessage(data));
            }
        }

        if(serviceType.equals(ServerType.SPIGOT))
        {
            CloudNetProxy.getInstance().sendAllGameServers(new PacketIOCustomChannelMessage(data));
        }
    }
}