package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.components.CNS;

/**
 * Created by Tareko on 10.07.2017.
 */
public class PacketIOCNSCommand
                extends Packet {

    public PacketIOCNSCommand()
    {
    }

    public PacketIOCNSCommand(Document document)
    {
        super(219, document);
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender)
    {
        if(data.contains("command") && data.contains("cns"))
        {
            CNS cns = CloudNetProxy.getInstance().getCnsSystems().get(data.getString("cns"));
            if(cns != null)
            {
                cns.sendPacket(new PacketIOCNSCommand(data));
            }
        }
    }
}