package de.dytanic.cloudnet.api.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;

/**
 * Created by Tareko on 02.06.2017.
 */
public class PacketIOHandleStartAndStop
            extends Packet {

    public PacketIOHandleStartAndStop() {}

    public PacketIOHandleStartAndStop(Document handler, HandleType handleType)
    {
        super(209, new Document().append("data", handler).append("type", handleType.name()));
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender) {}

    public enum HandleType
    {
        START_SERVER,
        WRITE_COMMAND,
        STOP_SERVER;
    }
}