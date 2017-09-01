package de.dytanic.cloudnet.api.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.permission.PermissionGroup;

import java.util.UUID;

/**
 * Created by Tareko on 04.07.2017.
 */
public class PacketOutManagePermissions
        extends Packet {

    //PermissionGroup update
    public PacketOutManagePermissions(PermissionGroup permissionGroup, PermissionGroupHandle handle)
    {
        super(215, new Document().append("permissiongroup", permissionGroup).append("handle", handle));
    }

    //Player PermissionAdd
    public PacketOutManagePermissions(UUID uuid, String permission, boolean value, PlayerPermissionHandle handle)
    {
        super(215, new Document().append("uuid", uuid).append("permission", permission).append("value", value).append("handle", handle.name()));
    }

    public enum PermissionGroupHandle {
        CREATE,
        UPDATE,
        REMOVE,
    }

    public enum PlayerPermissionHandle {
        ADD,
        REMOVE;
    }

}