package de.dytanic.cloudnet.api.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.servergroup.ServerGroup;

/**
 * Created by Tareko on 04.07.2017.
 */
public class PacketOutManageGroups
        extends Packet {

    public PacketOutManageGroups(String cns, ServerGroup group, UpdateType updateType)
    {
        super(216, new Document().append("cns", cns).append("group", group).append("type", updateType.name()));
    }

    public enum UpdateType {
        REMOVE,
        UPDATE,
        CREATE;
    }
}