package de.dytanic.cloudnet.bukkitproxy.api.event.proxy;

import de.dytanic.cloudnet.network.ServerInfo;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.plugin.Event;

/**
 * Created by Tareko on 06.07.2017.
 */
@AllArgsConstructor
public class ProxyServerInfoUpdateEvent
        extends Event {

    private ServerInfo serverInfo;

    public ServerInfo getServerInfo()
    {
        return serverInfo;
    }
}