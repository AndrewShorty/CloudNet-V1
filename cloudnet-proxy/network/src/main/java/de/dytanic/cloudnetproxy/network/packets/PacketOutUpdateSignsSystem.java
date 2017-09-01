package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.sign.GroupsLayout;
import de.dytanic.cloudnet.sign.Sign;
import de.dytanic.cloudnet.sign.SignLayout;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by Tareko on 06.06.2017.
 */
public class PacketOutUpdateSignsSystem
            extends Packet{

    public PacketOutUpdateSignsSystem(java.util.Map<UUID, Sign> signs, Collection<GroupsLayout> groupsLayout)
    {
        super(212, new Document().append("signs", signs).append("layouts", groupsLayout));
    }
}
