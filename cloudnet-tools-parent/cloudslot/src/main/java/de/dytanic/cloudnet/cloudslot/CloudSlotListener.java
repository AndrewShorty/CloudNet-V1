package de.dytanic.cloudnet.cloudslot;

import de.dytanic.cloudnet.bukkitproxy.api.event.bukkit.CloudServerStartupEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Tareko on 27.07.2017.
 */
public class CloudSlotListener implements Listener {

    @EventHandler
    public void onStartup(CloudServerStartupEvent e)
    {
        e.getCloudServer().setMaxPlayers(CloudSlotPlugin.getPlugin(CloudSlotPlugin.class).getConfig().getInt("slots"));
    }

}