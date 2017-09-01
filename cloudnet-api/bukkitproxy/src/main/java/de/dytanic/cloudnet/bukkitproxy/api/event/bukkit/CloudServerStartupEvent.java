package de.dytanic.cloudnet.bukkitproxy.api.event.bukkit;

import de.dytanic.cloudnet.bukkitproxy.api.CloudServer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Tareko on 18.06.2017.
 */
@AllArgsConstructor
public class CloudServerStartupEvent
            extends Event{

    private static final HandlerList handlerList = new HandlerList();

    private CloudServer cloudServer;

    public static HandlerList getHandlerList()
    {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlerList;
    }

    public CloudServer getCloudServer()
    {
        return cloudServer;
    }
}
