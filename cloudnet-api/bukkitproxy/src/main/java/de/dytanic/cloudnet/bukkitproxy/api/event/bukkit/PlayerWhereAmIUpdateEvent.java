package de.dytanic.cloudnet.bukkitproxy.api.event.bukkit;

import de.dytanic.cloudnet.player.PlayerWhereAmI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Calls when a player is updated
 */
@AllArgsConstructor
public class PlayerWhereAmIUpdateEvent
        extends Event {

    private PlayerWhereAmI playerWhereAmI;

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

    public PlayerWhereAmI getPlayerWhereAmI()
    {
        return playerWhereAmI;
    }
}
