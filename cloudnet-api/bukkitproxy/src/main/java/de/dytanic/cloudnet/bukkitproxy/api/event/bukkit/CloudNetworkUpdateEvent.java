package de.dytanic.cloudnet.bukkitproxy.api.event.bukkit;

import de.dytanic.cloudnet.api.network.packets.PacketInUpdateNetwork;
import de.dytanic.cloudnet.lib.document.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Calls when the CNPDB send's a "Packet201NetOutUpdateNetwork" Packet
 */
@AllArgsConstructor
public class CloudNetworkUpdateEvent
                extends Event{

    private PacketInUpdateNetwork.UpdateType updateType;
    private Document metaData;

    @Getter private static HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList()
    {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlerList;
    }

    public Document getMetaData()
    {
        return metaData;
    }

    public PacketInUpdateNetwork.UpdateType getUpdateType()
    {
        return updateType;
    }
}
