package de.dytanic.cloudnetserver.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;

/**
 * Created by Tareko on 28.05.2017.
 */
public class PacketOutRemoveServer
            extends Packet {

    public PacketOutRemoveServer(String serverid)
    {
        super(303, new Document().append("serverid", serverid));
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender) {}
}
