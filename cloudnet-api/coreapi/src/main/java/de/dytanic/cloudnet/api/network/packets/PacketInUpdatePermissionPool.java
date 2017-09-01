package de.dytanic.cloudnet.api.network.packets;

import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnet.permission.PermissionPool;

/**
 * Created by Tareko on 01.06.2017.
 */
public class PacketInUpdatePermissionPool
                extends Packet {

    //208
    public PacketInUpdatePermissionPool() {}

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        if(document.contains("permissionpool"))
        {
            PermissionPool permissionPool = document.getObject("permissionpool", PermissionPool.class);
            CloudNetAPI.getInstance().getCloudNetwork().setPermissionPool(permissionPool);
        }
    }
}