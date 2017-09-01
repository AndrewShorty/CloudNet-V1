package de.dytanic.cloudnet.bukkitproxy.api.plugin;

import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.bukkitproxy.api.CloudServer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tareko on 21.07.2017.
 */
public abstract class CNGamePlugin extends JavaPlugin {

    private java.util.Map<PluginKey, Object> properties = new ConcurrentHashMap<>();

    public abstract void bootstrap(Server server, CloudServer cloudServer, CloudNetAPI cloudNetAPI);

    public abstract void shutdown(Server server, CloudServer cloudServer, CloudNetAPI cloudNetAPI);

    @Override
    public final void onEnable()
    {
        bootstrap(getServer(), CloudServer.getInstance(), CloudNetAPI.getInstance());
    }

    @Override
    public final void onDisable()
    {
        shutdown(getServer(), CloudServer.getInstance(), CloudNetAPI.getInstance());
    }

    @Deprecated
    private String getVersionX()
    {
        return org.bukkit.Bukkit.getServer().getClass().getPackage()
                .getName().split("\\.")[3];
    }

    public CNGamePlugin registerCommand(Command command)
    {
        try{

            Method method = Class.forName("org.bukkit.craftbukkit." + getVersionX() + ".CraftServer").getMethod("getCommandMap");
            SimpleCommandMap commandMap = (SimpleCommandMap) method.invoke(getServer());
            commandMap.register("cloudnet", command);
        }catch (Exception ex) {
            try
            {
                Method method = Class.forName("org.bukkit.craftbukkit.CraftServer").getMethod("getCommandMap");
                SimpleCommandMap commandMap = (SimpleCommandMap) method.invoke(getServer());
                commandMap.register("cloudnet", command);
            } catch (Exception e)
            {
            }
        }
        return this;
    }

    public CNGamePlugin registerListener(Listener listener)
    {
        getServer().getPluginManager().registerEvents(listener, this);
        return this;
    }

    public Map<PluginKey, Object> getProperties()
    {
        return properties;
    }

    public CNGamePlugin appendProperty(PluginKey pluginKey, Object value)
    {
        this.properties.put(pluginKey, value);
        return this;
    }
}