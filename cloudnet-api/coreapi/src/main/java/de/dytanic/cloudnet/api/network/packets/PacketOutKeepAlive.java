package de.dytanic.cloudnet.api.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;

/**
 * Created by Tareko on 25.05.2017.
 */
public class PacketOutKeepAlive
                    extends Packet {

    public PacketOutKeepAlive()
    {
        super(0, new Document());
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender) {}
}