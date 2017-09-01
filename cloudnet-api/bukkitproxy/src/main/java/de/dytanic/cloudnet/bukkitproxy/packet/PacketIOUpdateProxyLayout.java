package de.dytanic.cloudnet.bukkitproxy.packet;

import de.dytanic.cloudnet.ProxyLayout;
import de.dytanic.cloudnet.bukkitproxy.api.CloudProxy;
import de.dytanic.cloudnet.bukkitproxy.api.event.proxy.ProxyLayoutUpdateEvent;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;

/**
 * Created by Tareko on 28.05.2017.
 */
public class PacketIOUpdateProxyLayout
                extends Packet {

    //202
    public PacketIOUpdateProxyLayout() {}

    public PacketIOUpdateProxyLayout(ProxyLayout proxyLayout)
    {
        super(202, new Document().append("proxylayout", proxyLayout));
    }

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        if(CloudProxy.getInstance() != null)
        {
            CloudProxy.getInstance().setProxyLayout(document.getObject("proxylayout", ProxyLayout.class));
            CloudProxy.getInstance().getProxyLayout().setFallbackNotFoundMessage(ChatColor.translateAlternateColorCodes('&',
                    CloudProxy.getInstance().getProxyLayout().getFallbackNotFoundMessage()
            ));
            CloudProxy.getInstance().getProxyLayout().setPrefix(ChatColor.translateAlternateColorCodes('&',
                    CloudProxy.getInstance().getProxyLayout().getPrefix()
            ));
            CloudProxy.getInstance().getProxyLayout().setHubCommandMessage(ChatColor.translateAlternateColorCodes('&',
                    CloudProxy.getInstance().getProxyLayout().getHubCommandMessage()
            ));
            CloudProxy.getInstance().getProxyLayout().setFallbackNotFoundMessage(ChatColor.translateAlternateColorCodes('&',
                    CloudProxy.getInstance().getProxyLayout().getFallbackNotFoundMessage()
            ));
            CloudProxy.getInstance().getProxyLayout().setMaintenanceMessage(ChatColor.translateAlternateColorCodes('&',
                    CloudProxy.getInstance().getProxyLayout().getMaintenanceMessage()
            ));
            ProxyServer.getInstance().getScheduler().runAsync(CloudProxy.getInstance().getPlugin(), () -> {
                ProxyServer.getInstance().getPluginManager().callEvent(new ProxyLayoutUpdateEvent(document.getObject("proxylayout", ProxyLayout.class)));
            });
        }
    }
}