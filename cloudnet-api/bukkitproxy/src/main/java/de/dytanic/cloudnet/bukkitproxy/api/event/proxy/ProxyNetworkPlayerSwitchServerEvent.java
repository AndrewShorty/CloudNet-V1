package de.dytanic.cloudnet.bukkitproxy.api.event.proxy;

import de.dytanic.cloudnet.player.PlayerWhereAmI;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Called when a Player switched a server and the event ServerSwitchEvent was called
 */
@AllArgsConstructor
public class ProxyNetworkPlayerSwitchServerEvent
            extends net.md_5.bungee.api.plugin.Event{

    private PlayerWhereAmI playerWhereAmI;
    private String server;

    public PlayerWhereAmI getPlayerWhereAmI()
    {
        return playerWhereAmI;
    }


    public String getServer()
    {
        return server;
    }
}