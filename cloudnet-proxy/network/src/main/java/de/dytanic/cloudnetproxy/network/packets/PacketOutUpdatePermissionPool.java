package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;

/**
 * Created by Tareko on 01.06.2017.
 */
public class PacketOutUpdatePermissionPool
                extends Packet {

    public PacketOutUpdatePermissionPool()
    {
        super(208, new Document().append("permissionpool", CloudNetProxy.getInstance().getPermissionPool()));
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender) {}
}