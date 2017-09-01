package de.dytanic.cloudnetserver.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.server.MinecraftServer;
import de.dytanic.cloudnetserver.server.ProxyServer;

/**
 * Created by Tareko on 25.06.2017.
 */
public class CommandList
            extends Command{

    public CommandList()
    {
        super("list", "cloudnet.server.command.list");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        System.out.println("All servers on CNS:");

        for(MinecraftServer minecraftServer : CloudNetServer.getInstance().getServers().values())
        {
            System.out.println("Server " + minecraftServer.getServerId() + "/" + minecraftServer.getGroup().getName() + " " + minecraftServer.getMemory() + ":" + ((CloudNetServer.getInstance().getCloudNetwork().getServers().containsKey(minecraftServer.getServerId()) &&
                    CloudNetServer.getInstance().getCloudNetwork().getServers().get(minecraftServer.getServerId()).isOnline()) ? "online" : "offline"));
        }

        for(ProxyServer minecraftServer : CloudNetServer.getInstance().getProxys().values())
        {
            System.out.println("Server " + minecraftServer.getServerId() + "/" + minecraftServer.getMemory() + ":" + ((CloudNetServer.getInstance().getCloudNetwork().getProxys().containsKey(minecraftServer.getServerId()) &&
                    CloudNetServer.getInstance().getCloudNetwork().getProxys().get(minecraftServer.getServerId()).isOnline()) ? "online" : "offline"));
        }

    }
}