package de.dytanic.cloudnet.bukkitproxy.packet;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.sign.Sign;

/**
 * Created by Tareko on 06.06.2017.
 */
public class PacketOutHandleSign
            extends Packet {

    public PacketOutHandleSign(HandleType handleType, Sign sign)
    {
        super(213, new Document().append("handle", handleType.name()).append("serverselectors", sign));
    }

    public enum HandleType
    {
        ADD,
        REMOVE;
    }

}
