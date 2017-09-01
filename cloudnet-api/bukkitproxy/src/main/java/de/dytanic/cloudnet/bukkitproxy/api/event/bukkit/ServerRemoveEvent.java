package de.dytanic.cloudnet.bukkitproxy.api.event.bukkit;

import de.dytanic.cloudnet.network.ServerInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called if a server is removed from the network
 * The server mustn't online
 */
@AllArgsConstructor
public class ServerRemoveEvent
        extends Event {

    private ServerInfo serverInfo;

    @Getter
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

    public ServerInfo getServerInfo()
    {
        return serverInfo;
    }
}