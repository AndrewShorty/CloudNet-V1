package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.ProxyLayout;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;

/**
 * Created by Tareko on 28.05.2017.
 */
public class PacketInUpdateProxyLayout
                extends Packet {

    public PacketInUpdateProxyLayout(ProxyLayout proxyLayout)
    {
        super(202, new Document().append("proxylayout", proxyLayout));
    }

    public PacketInUpdateProxyLayout() {}

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        CloudNetProxy.getInstance().setProxyLayout(document.getObject("proxylayout", ProxyLayout.class));
        CloudNetProxy.getInstance().getConfig().setProxyLayout(
                CloudNetProxy.getInstance().getProxyLayout());
        CloudNetProxy.getInstance().getConfig().saveAndReloadConfig();
        CloudNetProxy.getInstance().updateProxyLayout();
    }
}