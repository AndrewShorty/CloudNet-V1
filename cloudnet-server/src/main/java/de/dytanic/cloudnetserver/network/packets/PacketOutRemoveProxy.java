package de.dytanic.cloudnetserver.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;

/**
 * Created by Tareko on 28.05.2017.
 */
public class PacketOutRemoveProxy
            extends Packet {

    public PacketOutRemoveProxy(String serverid)
    {
        super(305, new Document().append("serverid", serverid));
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender) {}
}
