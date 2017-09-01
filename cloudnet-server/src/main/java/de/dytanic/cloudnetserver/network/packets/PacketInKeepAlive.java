package de.dytanic.cloudnetserver.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;

/**
 * Created by Tareko on 25.05.2017.
 */
public class PacketInKeepAlive
                    extends Packet {

    public PacketInKeepAlive() {}

    public PacketInKeepAlive(Document document)
    {
        super(0, document);
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender) {}
}