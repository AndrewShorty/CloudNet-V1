package de.dytanic.cloudnetserver.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnetserver.CloudNetServer;

/**
 * Created by Tareko on 30.05.2017.
 */
public class CommandScreen
                extends Command{

    public CommandScreen()
    {
        super("screen", "cloudnet.server.command.screen", "sc");
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
                            CloudNetServer.getInstance().getScreenSystem().joinNewScreen(CloudNetServer.getInstance().getServers().get(args[1]));

                            try
                            {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {}
                            sender.sendMessage("You are now in the screen session [" + args[1] + "]");
                            sender.sendMessage("You can writing the commands for the server with \"minecraft:\" or \"bukkit:\".");
                            sender.sendMessage("Use the command \"leave\" for left the screen session");
                        }
                        else
                        {
                            sender.sendMessage("This server doesn't exist.");
                        }
                    }
                    else
                        if(args[0].equalsIgnoreCase("proxy") || args[0].equalsIgnoreCase("-p"))
                        {
                            if(CloudNetServer.getInstance().getProxys().containsKey(args[1]))
                            {
                                CloudNetServer.getInstance().getScreenSystem().joinNewScreen(CloudNetServer.getInstance().getProxys().get(args[1]));

                                try
                                {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {}
                                sender.sendMessage("You are now in the screen session [" + args[1] + "]");
                                sender.sendMessage("You can writing the commands for the server with \"minecraft:\" or \"bukkit:\".");
                                sender.sendMessage("Use the command \"leave\" for left the screen session");
                            }
                            else
                            {
                                sender.sendMessage("This proxy doesn't exist.");
                            }
                        }
                break;
            default:
                sender.sendMessage("screen <server(-s) : proxy (-p)> <name>");
                sender.sendMessage("leave for leaving");
                break;
        }
    }
}