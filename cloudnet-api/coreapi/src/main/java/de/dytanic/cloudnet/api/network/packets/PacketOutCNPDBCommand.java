package de.dytanic.cloudnet.api.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;

/**
 * Created by Tareko on 07.07.2017.
 */
public class PacketOutCNPDBCommand
            extends Packet{

    public PacketOutCNPDBCommand(String command)
    {
        super(217, new Document().append("command", command));
    }
}