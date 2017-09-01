package de.dytanic.cloudnetserver.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;

/**
 * Created by Tareko on 24.05.2017.
 */
public class PacketIOPing
                extends Packet {

    public PacketIOPing()
    {
        super(1, new Document().append("nanoTimeSend", System.nanoTime()));
    }

    private PacketIOPing(Document document)
    {
        super(1, document.append("nanoTimeReceive", System.nanoTime()));
    }

    @Override
    public void handleInput(Document value, PacketSender sender)
    {
        if(value.contains("nanoTimeReceive"))
        {
            System.out.println("Ping - " + (System.nanoTime() - value.getLong("nanoTimeSend")) + " nano seconds");
        }
        else
        {
            sender.sendPacket(new PacketIOPing(value));
        }
    }
}
