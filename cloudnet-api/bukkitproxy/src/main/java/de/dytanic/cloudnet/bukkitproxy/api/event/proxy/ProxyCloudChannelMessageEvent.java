package de.dytanic.cloudnet.bukkitproxy.api.event.proxy;

import de.dytanic.cloudnet.lib.document.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

/**
 * Created by Tareko on 27.07.2017.
 */
@Getter
@AllArgsConstructor
public class ProxyCloudChannelMessageEvent extends Event {

    private String message;

    private Document document;

}