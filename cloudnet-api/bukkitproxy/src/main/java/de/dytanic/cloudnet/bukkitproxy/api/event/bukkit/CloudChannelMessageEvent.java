package de.dytanic.cloudnet.bukkitproxy.api.event.bukkit;

import de.dytanic.cloudnet.lib.document.Document;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Tareko on 27.07.2017.
 */
public class CloudChannelMessageEvent extends Event {

    private static HandlerList handlerList = new HandlerList();

    private String message;

    private Document data;

    public CloudChannelMessageEvent(String message, Document data)
    {
        this.message = message;
        this.data = data;
    }

    public Document getData()
    {
        return data;
    }

    public String getMessage()
    {
        return message;
    }

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
