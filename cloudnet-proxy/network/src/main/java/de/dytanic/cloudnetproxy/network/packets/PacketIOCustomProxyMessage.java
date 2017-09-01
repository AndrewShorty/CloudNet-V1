package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.components.CNS;
import de.dytanic.cloudnetproxy.network.components.ProxyServer;

/**
 * Created by Tareko on 30.05.2017.
 */
public class PacketIOCustomProxyMessage
                    extends Packet {

    public PacketIOCustomProxyMessage()
    {        super(0);
    }

    private PacketIOCustomProxyMessage(Document data)
    {
        super(205, data);
    }

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        PacketIOCustomProxyMessage packet205ServerOutCustomProxyMessage = new PacketIOCustomProxyMessage(document);
        for(CNS cns : CloudNetProxy.getInstance().getCnsSystems().values())
        {
            for(ProxyServer proxyServer : cns.getProxys().values())
            {
                proxyServer.sendPacket(packet205ServerOutCustomProxyMessage);
            }
        }
    }
}
