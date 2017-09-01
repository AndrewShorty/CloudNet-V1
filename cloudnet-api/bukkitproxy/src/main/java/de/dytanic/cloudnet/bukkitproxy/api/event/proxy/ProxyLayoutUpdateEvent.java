package de.dytanic.cloudnet.bukkitproxy.api.event.proxy;

import de.dytanic.cloudnet.ProxyLayout;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

/**
 * Calls when the Proxy-Layout was changed.
 */
@AllArgsConstructor
public class ProxyLayoutUpdateEvent
                extends Event {

    private ProxyLayout proxyLayout;

    public ProxyLayout getProxyLayout()
    {
        return proxyLayout;
    }
}
