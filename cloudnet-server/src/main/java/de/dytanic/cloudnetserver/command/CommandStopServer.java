package de.dytanic.cloudnetserver.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnet.logging.command.TabCompleteable;
import de.dytanic.cloudnetserver.CloudNetServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tareko on 15.06.2017.
 */
public class CommandStopServer
            extends Command implements TabCompleteable{

    public CommandStopServer()
    {
        super("stopserver", "cloudnet.server.command.stopserver", "sts");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 2:
                if(args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("-s"))
                {
                    if(CloudNetServer.getInstance().getServers().containsKey(args[1]))
                    {
                        CloudNetServer.getInstance().getServers().get(args[1]).shutdown();
                    }
                    else
                    {
                        sender.sendMessage("The server doesn't exists");
                    }
                }
                else
                if(args[0].equalsIgnoreCase("proxy") || args[0].equalsIgnoreCase("-p"))
                {
                    if(CloudNetServer.getInstance().getProxys().containsKey(args[1]))
                    {
                        CloudNetServer.getInstance().getProxys().get(args[1]).shutdown();
                    }
                    else
                    {
                        sender.sendMessage("The server doesn't exists");
                    }
                }
                break;
            default:
                sender.sendMessage(
                        "stopserver <proxy (-p) | server (-s)> <name>"
                );
                break;
        }
    }

    @Override
    public List<String> onTab(long argsLength, String lastWord)
    {
        List<String> groups = new ArrayList<>();

        if(argsLength == 1 || argsLength == 2)
        {
            if(lastWord.equalsIgnoreCase("server") || lastWord.equalsIgnoreCase("-s"))
            {
                groups.addAll(CloudNetServer.getInstance().getServers().keySet());
            }

            if(lastWord.equalsIgnoreCase("proxy") || lastWord.equalsIgnoreCase("-p"))
            {
                groups.addAll(CloudNetServer.getInstance().getProxys().keySet());
            }
        }

        return groups;
    }
}
