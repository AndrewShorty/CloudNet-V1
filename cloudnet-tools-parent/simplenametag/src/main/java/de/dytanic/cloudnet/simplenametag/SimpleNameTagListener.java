package de.dytanic.cloudnet.simplenametag;

import de.dytanic.cloudnet.bukkitproxy.api.CloudServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Tareko on 10.06.2017.
 */
public class SimpleNameTagListener
            implements Listener {

    @EventHandler
    public void handleJoin(PlayerJoinEvent e)
    {
        e.getPlayer().getServer().getScheduler().runTaskLaterAsynchronously(
                e.getPlayer().getServer().getPluginManager().getPlugin("CloudNet-SimpleNameTags"), new Runnable() {
                    @Override
                    public void run()
                    {
                        CloudServer.getInstance().updateNameTags(e.getPlayer());
                    }
                }, 5L);
    }
}