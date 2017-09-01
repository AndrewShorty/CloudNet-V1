package de.dytanic.cloudnet.bukkitproxy.api.command;

import de.dytanic.cloudnet.api.CloudNetAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.TabExecutor;

/**
 * Created by Tareko on 10.07.2017.
 */
public interface ServerTabExecutor
        extends TabExecutor {

    @Override
    default Iterable<String> onTabComplete(CommandSender commandSender, String[] strings)
    {
        return CloudNetAPI.getInstance().getServers().keySet();
    }
}
