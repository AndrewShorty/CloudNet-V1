package de.dytanic.cloudnetserver.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnet.servergroup.ServerGroup;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.server.MinecraftServer;
import de.dytanic.cloudnetserver.server.ProxyServer;

import java.lang.management.ManagementFactory;

/**
 * Created by Tareko on 24.05.2017.
 */
public class CommandHelp
            extends Command{

    public CommandHelp()
    {
        super("help", "cloudnet.server.command.help", "ask", "question");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        sender.sendMessage(
                " ",
                "CloudNet-Server Version: 1.0",
                "stop: \"Stop the CloudNet-Server instance\"",
                "start: \"Start a proxy or spigot instance\"",
                "stopserver: \"Stop a proxy or spigot instance",
                "command: \"Write a command to a proxy or spigot instance\"",
                "reload: \"Reload the config and groups\"",
                "creategroup: \"Start the simple-setup group creator\"",
                "deletegroup: \"Deletes a server group\"",
                "screen: \"Enter the console of a existing proxy or spigot instance",
                "leave: \"Left the active screen session",
                "stopgroup: \"Stop the existing servers from one group\"",
                "copy: \"Copied a online server into the group template\""
        );

        StringBuilder builder = new StringBuilder();
        for(ServerGroup group : CloudNetServer.getInstance().getGroups().values())
        {
            builder.append(group.getName()).append(", ");
        }

        sender.sendMessage(" ","This CNS has the following groups:", builder.substring(0), " ");

        int i = 0;
        for(MinecraftServer minecraftServer : CloudNetServer.getInstance().getServers().values())
        {
            i = i + minecraftServer.getMemory();
        }

        for(ProxyServer server : CloudNetServer.getInstance().getProxys().values())
        {
            i = i + server.getMemory();
        }

        sender.sendMessage("This CNS use " + i + "/" + CloudNetServer.getInstance().getMaxMemory() + "MB for all servers");
        sender.sendMessage("This CNS self " + (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L) + "/" + (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1048576L) + "MB");
    }
}
