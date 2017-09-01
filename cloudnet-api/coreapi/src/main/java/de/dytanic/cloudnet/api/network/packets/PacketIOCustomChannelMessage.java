package de.dytanic.cloudnet.api.network.packets;

import de.dytanic.cloudnet.ServerType;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;

/**
 * Created by Tareko on 27.07.2017.
 */
public class PacketIOCustomChannelMessage extends Packet {

    public PacketIOCustomChannelMessage(ServerType serviceType, String message, Document components)
    {
        super(221, new Document().append("servicetype", serviceType.name()).append("message", message).append("data", components));
    }
}