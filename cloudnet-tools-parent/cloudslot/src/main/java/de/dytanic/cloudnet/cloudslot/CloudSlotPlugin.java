package de.dytanic.cloudnet.cloudslot;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Tareko on 27.07.2017.
 */
public class CloudSlotPlugin extends JavaPlugin {

    @Override
    public void onEnable()
    {
        getConfig().options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(new CloudSlotListener(), this);

    }
}