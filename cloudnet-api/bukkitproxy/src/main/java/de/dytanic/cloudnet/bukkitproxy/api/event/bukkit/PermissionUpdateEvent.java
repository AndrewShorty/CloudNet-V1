package de.dytanic.cloudnet.bukkitproxy.api.event.bukkit;

import de.dytanic.cloudnet.permission.PermissionPool;
import lombok.AllArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Tareko on 10.07.2017.
 */
@AllArgsConstructor
public class PermissionUpdateEvent
        extends Event {

    private PermissionPool permissionPool;

    public PermissionPool getPermissionPool()
    {
        return permissionPool;
    }

    private static HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList()
    {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlerList;
    }
}
