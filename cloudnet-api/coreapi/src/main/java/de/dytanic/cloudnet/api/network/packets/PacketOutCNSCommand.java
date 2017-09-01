package de.dytanic.cloudnet.api.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.CNSInfo;
import de.dytanic.cloudnet.network.packet.Packet;

/**
 * Created by Tareko on 10.07.2017.
 */
public class PacketOutCNSCommand
            extends Packet {

    public PacketOutCNSCommand(CNSInfo address, String command)
    {
        super(219, new Document().append("cns", address.getServerId()).append("command", command));
    }
}