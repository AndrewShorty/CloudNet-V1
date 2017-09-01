package de.dytanic.cloudnetserver.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.server.MinecraftServer;
import de.dytanic.cloudnetserver.server.ProxyServer;

/**
 * Created by Tareko on 25.05.2017.
 */
public class CommandCmd
            extends Command{

    public CommandCmd()
    {
        super("command", "cloudnet.server.command.cmd", "cmd", "c");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        if(args.length > 2)
        {
            if(args[1].equalsIgnoreCase("all"))
            {
                StringBuilder b = new StringBuilder();

                for(short i = 2; i < args.length; i++)
                {
                    b.append(args[i]).append(" ");
                }

                if(!args[0].equalsIgnoreCase("server") && !args[0].equalsIgnoreCase("-s"))
                {
                    for(ProxyServer minecraftServer : CloudNetServer.getInstance().getProxys().values())
                    {
                        System.out.println("Sending command to " + minecraftServer.getServerId());
                        minecraftServer.runCommand(b.substring(0, b.length() - 1));
                    }
                }
                else
                {
                    for(MinecraftServer minecraftServer : CloudNetServer.getInstance().getServers().values())
                    {
                        System.out.println("Sending command to " + minecraftServer.getServerId());
                        minecraftServer.runCommand(b.substring(0, b.length() - 1));
                    }
                }
            }
            else
            if((args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("-s")) &&
                    CloudNetServer.getInstance().getServers().containsKey(args[1]))
            {
                StringBuilder b = new StringBuilder();

                for(short i = 2; i < args.length; i++)
                {
                    b.append(args[i]).append(" ");
                }
                System.out.println("Sending command to " + args[1]);
                CloudNetServer.getInstance().getServers().get(args[1]).runCommand(b.substring(0, b.length() - 1));
            }
            else
            if((args[0].equalsIgnoreCase("proxy") || args[0].equalsIgnoreCase("-p")) &&
                    CloudNetServer.getInstance().getProxys().containsKey(args[1]))
            {
                StringBuilder b = new StringBuilder();

                for(short i = 2; i < args.length; i++)
                {
                    b.append(args[i]).append(" ");
                }
                System.out.println("Sending command to " + args[1]);
                CloudNetServer.getInstance().getProxys().get(args[1]).runCommand(b.substring(0, b.length() - 1));
            }
            else
            {
                sender.sendMessage("Server cannot be found.");
            }
        }
        else
        {
            sender.sendMessage("command <server(-s) : proxy (-p)> <all : id> <command>");
        }
    }
}