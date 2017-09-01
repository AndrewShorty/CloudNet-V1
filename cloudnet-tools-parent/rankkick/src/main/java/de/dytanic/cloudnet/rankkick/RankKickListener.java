package de.dytanic.cloudnet.rankkick;

import de.dytanic.cloudnet.bukkitproxy.api.event.proxy.ProxyRankUpdateEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.text.SimpleDateFormat;

/**
 * Created by Tareko on 17.07.2017.
 */
public class RankKickListener
            implements Listener {

    @EventHandler
    public void onRankKick(ProxyRankUpdateEvent e)
    {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(e.getName());
        if(proxiedPlayer != null)
        {
            proxiedPlayer.disconnect(ChatColor.translateAlternateColorCodes('&', RankKickPlugin.getInstance().getConfiguration().getString("kickMessage")
            .replace("%rank%", e.getRank())
            .replace("%time%", (e.getTimeOut() > 0 ? new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(e.getTimeOut()) : "LIFETIME"))));
        }
    }

}