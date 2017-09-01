package de.dytanic.cloudnet.bukkitproxy.api.command;

import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tareko on 30.06.2017.
 */
public interface GlobalTabExecutor
                    extends TabExecutor{

    @Override
    default Iterable<String> onTabComplete(CommandSender commandSender, String[] strings)
    {
        List<String> names = new ArrayList<>();
        for(PlayerWhereAmI key : CloudNetAPI.getInstance().getCloudNetwork().getOnlinePlayers().values())
            names.add(key.getName());
        return names;
    }
}