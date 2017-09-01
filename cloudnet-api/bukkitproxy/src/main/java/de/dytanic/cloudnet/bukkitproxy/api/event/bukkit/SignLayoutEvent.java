package de.dytanic.cloudnet.bukkitproxy.api.event.bukkit;

import de.dytanic.cloudnet.network.ServerInfo;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 NOW UNSUPPORTED
 */
@Deprecated
public class SignLayoutEvent
        extends Event {

    private String[] layout;
    private ServerInfo serverInfo;
    private int tick;

    public SignLayoutEvent(ServerInfo serverInfo, int tick)
    {
        this.serverInfo = serverInfo;
        this.tick = tick;
    }

    private static HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers()
    {
        return handlerList;
    }

    public static HandlerList getHandlerList()
    {
        return handlerList;
    }

    public void setLayout(String[] layout)
    {
        this.layout = layout;
    }

    public ServerInfo getServerInfo()
    {
        return serverInfo;
    }

    public int getTick()
    {
        return tick;
    }

    public String[] getLayout()
    {
        return layout;
    }
}