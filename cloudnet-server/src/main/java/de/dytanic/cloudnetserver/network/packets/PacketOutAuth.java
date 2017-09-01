package de.dytanic.cloudnetserver.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;

/**
 * Created by Tareko on 19.06.2017.
 */
public class PacketOutAuth
            extends Packet{

    public PacketOutAuth(String cloudId, String servicekey)
    {
        super(2, new Document().append("cnsId", cloudId).append("servicekey", servicekey).append("type", "CNS"));
    }
}