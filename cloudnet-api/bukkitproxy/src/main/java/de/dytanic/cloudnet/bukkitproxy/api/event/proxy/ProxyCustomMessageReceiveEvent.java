package de.dytanic.cloudnet.bukkitproxy.api.event.proxy;

import de.dytanic.cloudnet.lib.document.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

/**
 * Calls when a BungeeCord-Service writes a "CustomProxyMessage"-Packet and the data was received
 */
@AllArgsConstructor
public class ProxyCustomMessageReceiveEvent
                extends Event{
    /**
     * The Message of this proxy messaging channel
     */
    private String message;
    /**
     * The Data of that
     */
    private Document dataCatcher;

    public Document getDataCatcher()
    {
        return dataCatcher;
    }

    public String getMessage()
    {
        return message;
    }
}
