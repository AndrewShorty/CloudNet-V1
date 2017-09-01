package de.dytanic.cloudnet.chat;

import de.dytanic.cloudnet.api.CloudNetAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by Tareko on 15.06.2017.
 */
public class ChatListener
            implements Listener{

    @EventHandler
    public void handleChatAsync(AsyncPlayerChatEvent e)
    {
        e.setCancelled(true);
        String textComponent = new String(ChatColor.translateAlternateColorCodes('&', ChatPlugin.getPlugin(ChatPlugin.class).getConfig().getString("format")
                .replace("%player%", e.getPlayer().getDisplayName())
                .replace("%prefixplayer%", ChatColor.translateAlternateColorCodes('&',
                        CloudNetAPI.getInstance().getPermissionGroup(CloudNetAPI.getInstance().getOnlinePlayer(e.getPlayer().getUniqueId()).getPlayerMetaData().getPermissionEntity().getPermissionGroup()).getPrefix()
                        ) + e.getPlayer().getName())
                .replace("%prefix%", ChatColor.translateAlternateColorCodes('&',
                        CloudNetAPI.getInstance().getPermissionGroup(CloudNetAPI.getInstance().getOnlinePlayer(e.getPlayer().getUniqueId()).getPlayerMetaData().getPermissionEntity().getPermissionGroup()).getPrefix()
                ))
                        .replace("%display%", ChatColor.translateAlternateColorCodes('&',
                                CloudNetAPI.getInstance().getPermissionGroup(CloudNetAPI.getInstance().getOnlinePlayer(e.getPlayer().getUniqueId()).getPlayerMetaData().getPermissionEntity().getPermissionGroup()).getDisplay()
                        ))
                        .replace("%name%", ChatColor.translateAlternateColorCodes('&', e.getPlayer().getName()
                        ))
                .replace("%group%", CloudNetAPI.getInstance().getOnlinePlayer(e.getPlayer().getUniqueId()).getPlayerMetaData().getPermissionEntity().getPermissionGroup())
                ).replace("%message%", e.getPlayer().hasPermission("cloudnet.chat.color") ? ChatColor.translateAlternateColorCodes('&',
                e.getMessage()) : e.getMessage()
                ));
        for(Player all : Bukkit.getOnlinePlayers())
        {
            all.sendMessage(textComponent);
        }
    }
}