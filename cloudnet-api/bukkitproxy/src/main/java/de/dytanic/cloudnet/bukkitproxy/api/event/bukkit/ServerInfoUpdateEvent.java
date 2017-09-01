package de.dytanic.cloudnet.bukkitproxy.api.event.bukkit;

import de.dytanic.cloudnet.network.ServerInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Tareko on 03.06.2017.
 */
@AllArgsConstructor
public class ServerInfoUpdateEvent
            extends Event {

    private ServerInfo serverInfo;

    @Getter private static HandlerList handlerList = new HandlerList();

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