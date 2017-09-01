package de.dytanic.cloudnet.bukkitproxy.command;

import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.bukkitproxy.api.CloudProxy;
import de.dytanic.cloudnet.network.ServerInfo;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;
import java.util.Random;

/**
 * Created by Tareko on 01.06.2017.
 */
public class CommandHub
            extends Command{

    public CommandHub()
    {
        super("hub");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {

        if(!(commandSender instanceof ProxiedPlayer)) return;

        ProxiedPlayer player = ((ProxiedPlayer)commandSender);

            if(CloudProxy.getInstance().fallback().contains(player.getServer().getInfo().getName()))
            {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CloudProxy.getInstance().getProxyLayout().getAlreadyOnHubMessage()));
                return;
            }

        ServerInfo fallback = CloudProxy.getInstance().calcFallback(CloudNetAPI.getInstance().getOnlinePlayer(((ProxiedPlayer)commandSender).getUniqueId()));

        if(fallback != null)
        {
            ((ProxiedPlayer)commandSender).connect(ProxyServer
                        .getInstance().getServerInfo(fallback.getName()));
            commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getHubCommandMessage());
            return;
        }
        else
        {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CloudProxy.getInstance().getProxyLayout().getAlreadyOnHubMessage()));
            return;
        }
    }

    @Override
    public String[] getAliases()
    {
        return new String[]{"l", "lobby", "leave", "quit", "game"};
    }
}
