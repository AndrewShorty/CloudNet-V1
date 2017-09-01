package de.dytanic.cloudnet.chat;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Tareko on 15.06.2017.
 */
public class ChatPlugin
            extends JavaPlugin{

    @Override
    public void onEnable()
    {
        getConfig().options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(new ChatListener(), this);

    }
}