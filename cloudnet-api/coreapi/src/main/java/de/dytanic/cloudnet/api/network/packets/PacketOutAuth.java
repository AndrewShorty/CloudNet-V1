package de.dytanic.cloudnet.api.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;

import java.util.UUID;

/**
 * Created by Tareko on 02.06.2017.
 */
public class PacketOutAuth
            extends Packet {

    //2
    public PacketOutAuth()
    {
        super(2);
    }

    public PacketOutAuth(Type type, UUID uuid, String serverId)
    {
        super(2, new Document().append("type", type.name()).append("uniqueid", uuid.toString()).append("serverid", serverId));
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender) {}

    public enum Type
    {
        PROXY,
        MINECRAFT;
    }
}
