package de.dytanic.cloudnet.bukkitproxy.packet;

import de.dytanic.cloudnet.bukkitproxy.api.CloudProxy;
import de.dytanic.cloudnet.bukkitproxy.api.event.proxy.ProxyCustomMessageReceiveEvent;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import net.md_5.bungee.api.ProxyServer;

/**
 * Created by Tareko on 28.05.2017.
 */
public class PacketIOCustomProxyMessage
                extends Packet {

    public PacketIOCustomProxyMessage() {}

    public PacketIOCustomProxyMessage(String message, Document dataCatcher)
    {
        super(205, new Document().append("message", message).append("data", dataCatcher));
    }

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        ProxyServer.getInstance().getScheduler().runAsync(CloudProxy.getInstance().getPlugin(), () -> {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyCustomMessageReceiveEvent(document.getString("message"), document.getDocument("data")));
        });
    }
}