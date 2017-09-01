package de.dytanic.cloudnet.bukkitproxy.api.event.proxy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

/**
 * Called if the command /setrank was used
 */
@AllArgsConstructor
@Getter
public class ProxyRankUpdateEvent extends Event {

    private String name;
    private String rank;
    private long timeOut;

}