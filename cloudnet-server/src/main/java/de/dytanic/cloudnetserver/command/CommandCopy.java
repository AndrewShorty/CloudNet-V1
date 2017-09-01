package de.dytanic.cloudnetserver.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnet.logging.command.TabCompleteable;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tareko on 14.06.2017.
 */
public class CommandCopy
        extends Command implements TabCompleteable{

    public CommandCopy()
    {
        super("copy", "cloudnet.server.command.copy");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 1:
                if(CloudNetServer.getInstance().getServers().containsKey(args[0]))
                {
                    MinecraftServer minecraftServer = CloudNetServer.getInstance().getServers().get(args[0]);
                    minecraftServer.copy();
                    System.out.println("The server was copied into the template.");
                }
                else
                {
                    sender.sendMessage("The server doesn't exists.");
                }
                break;
            default:
                sender.sendMessage("copy <name>");
                break;
        }
    }

    @Override
    public List<String> onTab(long argsLength, String lastWord)
    {
        List<String> server = new ArrayList<>();
        server.addAll(CloudNetServer.getInstance().getServers().keySet());
        return server;
    }
}
