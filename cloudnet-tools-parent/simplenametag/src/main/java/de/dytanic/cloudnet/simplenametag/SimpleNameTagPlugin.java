package de.dytanic.cloudnet.simplenametag;

        import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Tareko on 10.06.2017.
 */
public class SimpleNameTagPlugin
        extends JavaPlugin {

    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(new SimpleNameTagListener(), this);
    }
}