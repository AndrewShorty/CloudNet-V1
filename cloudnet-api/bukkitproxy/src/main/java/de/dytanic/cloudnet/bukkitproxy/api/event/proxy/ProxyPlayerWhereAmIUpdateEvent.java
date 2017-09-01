package de.dytanic.cloudnet.bukkitproxy.api.event.proxy;

import de.dytanic.cloudnet.player.PlayerWhereAmI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

/**
 * Created by Tareko on 24.06.2017.
 */
@AllArgsConstructor
public class ProxyPlayerWhereAmIUpdateEvent
                extends Event{

    private PlayerWhereAmI playerWhereAmI;

    public PlayerWhereAmI getPlayerWhereAmI()
    {
        return playerWhereAmI;
    }
}
