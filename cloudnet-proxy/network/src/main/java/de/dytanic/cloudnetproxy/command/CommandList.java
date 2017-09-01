package de.dytanic.cloudnetproxy.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.components.MinecraftServer;
import de.dytanic.cloudnetproxy.network.components.ProxyServer;

/**
 * Created by Tareko on 25.06.2017.
 */
public class CommandList
            extends Command{

    public CommandList()
    {
        super("list", "cloudnet.proxy.command.list");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        CloudNetProxy.getInstance().getCnsSystems().values().forEach(c -> {

            System.out.println("CloudNetServer [" + c.getServerId() + "]:");

            for(MinecraftServer minecraftServer : c.getServers().values())
            {
                System.out.println("Server [" + minecraftServer.getServerInfo().getName() + "/" + minecraftServer.getServerInfo().getGroup()
                         + minecraftServer.getServerInfo().getMemory() + "MB] "
                        + minecraftServer.getServerInfo().getOnlineCount() + "/" + minecraftServer.getServerInfo().getMaxPlayers());
            }

            for(ProxyServer minecraftServer : c.getProxys().values())
            {
                System.out.println("Proxy [" + minecraftServer.getProxyInfo().getName() + "/" + minecraftServer.getProxyInfo().getMemory() + "MB] "
                        + minecraftServer.getProxyInfo().getOnlineCount());
            }

            System.out.println(" ");

        });
    }
}