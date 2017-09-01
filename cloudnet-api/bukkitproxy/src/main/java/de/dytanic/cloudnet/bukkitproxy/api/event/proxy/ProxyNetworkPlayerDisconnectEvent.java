package de.dytanic.cloudnet.bukkitproxy.api.event.proxy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

/**
 * Calls when a player disconncted to the network in a one Proxy
 */
@AllArgsConstructor
public class ProxyNetworkPlayerDisconnectEvent
            extends Event {

    private String name;
    private UUID uniqueId;

    public String getName()
    {
        return name;
    }

    public UUID getUniqueId()
    {
        return uniqueId;
    }
}
