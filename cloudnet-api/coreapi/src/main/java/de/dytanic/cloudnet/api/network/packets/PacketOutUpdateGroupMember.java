package de.dytanic.cloudnet.api.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;

/**
 * Created by Tareko on 02.06.2017.
 */
public class PacketOutUpdateGroupMember
                extends Packet {

    public PacketOutUpdateGroupMember() {super(210, null);}

    public PacketOutUpdateGroupMember(String name, String group, long timeOut)
    {
        super(210, new Document().append("name", name).append("group", group).append("timeOut", timeOut));
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender) {}
}