package de.dytanic.cloudnetserver.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnet.permission.PermissionPool;
import de.dytanic.cloudnetserver.CloudNetServer;

/**
 * Created by Tareko on 01.06.2017.
 */
public class PacketInUpdatePermissionPool
                extends Packet {

    public PacketInUpdatePermissionPool() {}

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        if(document.contains("permissionpool"))
        {
            PermissionPool permissionPool = document.getObject("permissionpool", PermissionPool.class);
            CloudNetServer.getInstance().getCloudNetwork().setPermissionPool(permissionPool);
        }
    }
}
