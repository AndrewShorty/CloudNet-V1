package de.dytanic.cloudnet.bukkitproxy.command;

import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.bukkitproxy.api.CloudProxy;
import de.dytanic.cloudnet.bukkitproxy.api.command.GlobalTabExecutor;
import de.dytanic.cloudnet.bukkitproxy.api.command.ServerTabExecutor;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.CNSInfo;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnet.network.SimpleProxyInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Tareko on 09.07.2017.
 */
public class CommandDevCloud
        extends Command implements GlobalTabExecutor {

    public CommandDevCloud()
    {
        super("devcloud", "cloudnet.command.devcloud");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {
        switch (args.length)
        {
            case 1:
                if (args[0].equalsIgnoreCase("reload") && commandSender.hasPermission("cloudnet.command.devcloud.reload"))
                {
                    CloudNetAPI.getInstance().writeCNPCommand("rl");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                            "The update was sent to the cnp-root successfully");
                    return;
                }

                if (args[0].equalsIgnoreCase("list") && commandSender.hasPermission("cloudnet.command.devcloud.list"))
                {
                    commandSender.sendMessage(" ");

                    int maxMemory = 0;
                    int usedMemory = 0;

                    for (CNSInfo cnsInfo : CloudNetAPI.getInstance().getOnlineCloudNetServers())
                    {
                        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText("§8[§7" + cnsInfo.getServerId() + "§8/§7" + cnsInfo.getHostName() + "§8] §7Cores: " + cnsInfo.getAvailableProcessors()));
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + cnsInfo.getServerId()));
                        commandSender.sendMessage(textComponent);
                        maxMemory = maxMemory + cnsInfo.getMemory();
                    }
                    commandSender.sendMessage(" ");
                    for (SimpleProxyInfo simpleProxyInfo : CloudNetAPI.getInstance().getProxys())
                    {
                        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText("§8[§c" + simpleProxyInfo.getName() + "§8] §8(§e" + simpleProxyInfo.getOnlineCount() + "§8) : §7" + simpleProxyInfo.getMemory() + "MB"));
                        commandSender.sendMessage(textComponent);
                        usedMemory = usedMemory + simpleProxyInfo.getMemory();
                    }
                    commandSender.sendMessage(" ");
                    for (ServerInfo simpleProxyInfo : CloudNetAPI.getInstance().getServerInfos())
                    {
                        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText("§8[§c" + simpleProxyInfo.getName() + "§8] §8(§e" + simpleProxyInfo.getOnlineCount() + "§8) §e" + simpleProxyInfo.getServerState().name() + " §8: §7" + simpleProxyInfo.getMemory() + "MB"));
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + simpleProxyInfo.getName()));
                        commandSender.sendMessage(textComponent);
                        usedMemory = usedMemory + simpleProxyInfo.getMemory();
                    }
                    commandSender.sendMessage(" ");

                    commandSender.sendMessage("§7Memory in use: " + usedMemory + "§8/§7" + maxMemory + "MB");

                    return;
                }
                break;
            case 2:
                if (args[0].equalsIgnoreCase("start") && commandSender.hasPermission("cloudnet.command.devcloud.start"))
                {
                    if (CloudNetAPI.getInstance().getGroups().containsKey(args[1]))
                    {
                        CloudNetAPI.getInstance().startServer(CloudNetAPI.getInstance().getGroupData(args[1]), new Document(), true);
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                                "The update was sent to the cnp-root successfully");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("stop") && commandSender.hasPermission("cloudnet.command.devcloud.stop"))
                {
                    if (CloudNetAPI.getInstance().getServers().containsKey(args[1]))
                    {
                        CloudNetAPI.getInstance().stopServer(args[1]);
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                                "The update was sent to the cnp-root successfully");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("jump") && commandSender.hasPermission("cloudnet.command.devcloud.jump"))
                {
                    if (commandSender instanceof ProxiedPlayer && CloudNetAPI.getInstance().getOnlinePlayer(args[1]) != null)
                    {
                        String server = CloudNetAPI.getInstance().getOnlinePlayer(args[1]).getServer();
                        if (server != null)
                            ((ProxiedPlayer) commandSender).connect(ProxyServer.getInstance().getServerInfo(server));
                    }
                    return;
                }
                break;
            default:
                String prefix = CloudProxy.getInstance().getProxyLayout().getPrefix();
                commandSender.sendMessage(prefix + "/devcloud start <group>");
                commandSender.sendMessage(prefix + "/devcloud stop <server>");
                commandSender.sendMessage(prefix + "/devcloud jump <name>");
                commandSender.sendMessage(prefix + "/devcloud list");
                commandSender.sendMessage(prefix + "/devcloud reload");
                break;
        }
    }
}