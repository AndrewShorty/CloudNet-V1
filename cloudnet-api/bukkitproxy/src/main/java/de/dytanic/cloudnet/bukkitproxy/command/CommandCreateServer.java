package de.dytanic.cloudnet.bukkitproxy.command;

import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.bukkitproxy.api.CloudProxy;
import de.dytanic.cloudnet.bukkitproxy.api.event.proxy.ProxyServerInfoUpdateEvent;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.servergroup.SimpleServerGroup;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tareko on 06.07.2017.
 */
public class CommandCreateServer
        extends Command implements Listener {

    private List<String> onlineServers = new LinkedList<>();

    public CommandCreateServer()
    {
        super("createserver", "cloudnet.command.createserver");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {
        if (!(commandSender instanceof ProxiedPlayer)) return;

        switch (args.length)
        {
            case 1:
            {
                if (!CloudNetAPI.getInstance().getCloudNetwork().getGroups().containsKey(args[0]) || ((CloudNetAPI.getInstance().getCloudNetwork().getGroups().containsKey(args[0]) && !CloudNetAPI.getInstance().getCloudNetwork().getGroups().get(args[0]).isCreateServerEnabled())))
                    return;

                CloudNetAPI.getInstance().startHidedServer(CloudNetAPI.getInstance().getGroupData(args[0]), new Document().append("createdServerUUID",
                        ((ProxiedPlayer) commandSender).getUniqueId().toString()
                ), true);

                onlineServers.add(((ProxiedPlayer) commandSender).getUniqueId().toString());
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CloudProxy.getInstance().getProxyLayout().getCreateServerCommandProperties().getExecuteMessage()));

            }
            break;
            default:
                StringBuilder builder = new StringBuilder();
                for (SimpleServerGroup simpleServerGroup : CloudNetAPI.getInstance().getCloudNetwork().getGroups().values())
                {
                    if (simpleServerGroup.isCreateServerEnabled())
                    {
                        builder.append(simpleServerGroup.getName()).append(", ");
                    }
                }
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CloudProxy.getInstance().getProxyLayout().getCreateServerCommandProperties().getListMessage().replace("%groups%", builder.substring(0))));
                commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/createserver <game>");
                break;
        }

    }

    @EventHandler
    public void handleServerInfoUpdate(ProxyServerInfoUpdateEvent e)
    {
        if (e.getServerInfo().getProperties().contains("createdServerUUID") &&
                onlineServers.contains(e.getServerInfo().getProperties().getString("createdServerUUID")) && e.getServerInfo().isOnline())
        {
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(UUID.fromString(e.getServerInfo().getProperties().getString("createdServerUUID")));
            if (proxiedPlayer != null)
            {
                proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo(e.getServerInfo().getName()));
            }
            this.onlineServers.remove(e.getServerInfo().getProperties().getString("createdServerUUID"));
        }
    }

   public void handleQuit(PlayerDisconnectEvent e)
    {
        this.onlineServers.remove(e.getPlayer().getUniqueId().toString());
    }

}