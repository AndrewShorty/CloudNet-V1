package de.dytanic.cloudnet.bukkitproxy.api.event.proxy;

import de.dytanic.cloudnet.api.network.packets.PacketInUpdateNetwork;
import de.dytanic.cloudnet.lib.document.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

/**
 * Calls when the CNP send's a "Packet201NetOutUpdateNetwork" Packet
 */
@AllArgsConstructor
public class ProxyCloudNetworkUpdateEvent
            extends Event{

    private PacketInUpdateNetwork.UpdateType updateType;
    private Document metaData;

    public PacketInUpdateNetwork.UpdateType getUpdateType()
    {
        return updateType;
    }

    public Document getMetaData()
    {
        return metaData;
    }
}
