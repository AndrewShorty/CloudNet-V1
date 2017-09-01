package de.dytanic.cloudnet.bukkitproxy.api.event.proxy;

import de.dytanic.cloudnet.permission.PermissionPool;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.plugin.Event;

/**
 * Calls when the permissions was updated in network
 */
@AllArgsConstructor
public class ProxyPermissionUpdateEvent
                extends Event {

    private PermissionPool permissionPool;

    public PermissionPool getPermissionPool()
    {
        return permissionPool;
    }
}