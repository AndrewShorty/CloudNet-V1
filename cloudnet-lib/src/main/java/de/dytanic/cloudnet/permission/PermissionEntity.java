package de.dytanic.cloudnet.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Created by Tareko on 01.06.2017.
 */
@AllArgsConstructor
@Getter
public class PermissionEntity
{
    private UUID uniqueId;
    private java.util.Map<String, Boolean> permissions;

    @Setter private String permissionGroup;
    @Setter private long timeOut;

    public boolean hasPermission(PermissionPool permissionPool, String permission, String group)
    {
        String adminPermission = null;
        String[] block = permission.split(".");
        if(block.length > 1)
        {
            adminPermission = block[0] + ".*";
        }

        if(permissions.containsKey("*")) return true;
        else
        if((permissions.containsKey(permission)) && permissions.get(permission)) return true;
        else
        if(permissions.containsKey(permission) && !permissions.get(permission)) return false;
        else
        if(adminPermission != null && permissions.containsKey(adminPermission) && permissions.get(adminPermission)) return true;
        else
        if(adminPermission != null && permissions.containsKey(adminPermission) && !permissions.get(adminPermission)) return true;

        if(!permissionPool.getGroups().containsKey(permissionGroup)) return false;
        PermissionGroup permissionGroup = permissionPool.getGroups().get(this.permissionGroup);
        if(group != null)
        {
            if(permissionGroup.getServerGroupPermissions().containsKey(group))
            {
                if(permissionGroup.getServerGroupPermissions().get(group).contains(permission) ||
                        permissionGroup.getServerGroupPermissions().get(group).contains("*")
                        || (adminPermission != null && permissionGroup.getServerGroupPermissions().get(group).contains(adminPermission))) return true;
            }
        }

        if((permissionGroup.getPermissions().containsKey("*") && permissionGroup.getPermissions().get("*")) || permissionGroup.getPermissionDefault().equals(PermissionDefault.ADMINISTRATOR)) return true;

        PermissionDefault permissionDefault = permissionGroup.getPermissionDefault();
        if(permissionDefault.getPermissions().contains(permission))
        {
            return true;
        }

        if((permissionGroup.getPermissions().containsKey(permission) && permissionGroup.getPermissions().get(permission)) || (adminPermission != null && (permissionGroup.getPermissions().containsKey(adminPermission) &&
                permissionGroup.getPermissions().get(adminPermission)
        ))) return true;

        return false;
    }

}