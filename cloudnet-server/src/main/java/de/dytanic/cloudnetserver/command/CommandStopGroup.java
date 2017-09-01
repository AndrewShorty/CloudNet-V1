package de.dytanic.cloudnetserver.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnet.logging.command.TabCompleteable;
import de.dytanic.cloudnetserver.CloudNetServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tareko on 11.06.2017.
 */
public class CommandStopGroup
            extends Command implements TabCompleteable {

    public CommandStopGroup()
    {
        super("stopgroup", "cloudnet.command.stopgroup", "sg");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 1:
                sender.sendMessage("please wait...");
                for(String server : CloudNetServer.getInstance().getServersByName(args[0]))
                {
                    CloudNetServer.getInstance().getServers().get(server).shutdown();
                }
                sender.sendMessage("All servers from " + args[0] + " stops successfully");
                break;
            default:
                sender.sendMessage("stopgroup <name>");
                break;
        }
    }

    @Override
    public List<String> onTab(long argsLength, String lastWord)
    {
        List<String> groups = new ArrayList<>();
        groups.addAll(CloudNetServer.getInstance().getGroups().keySet());
        return groups;
    }
}