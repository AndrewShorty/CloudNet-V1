package de.dytanic.cloudnet.bukkitproxy.command;

import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.api.network.packets.PacketIOHandleStartAndStop;
import de.dytanic.cloudnet.bukkitproxy.CloudNetProxyPlugin;
import de.dytanic.cloudnet.bukkitproxy.api.CloudProxy;
import de.dytanic.cloudnet.bukkitproxy.packet.PacketIOUpdateProxyLayout;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.CNSInfo;
import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnet.network.SimpleProxyInfo;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import de.dytanic.cloudnet.servergroup.SimpleServerGroup;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tareko on 02.06.2017.
 */
public final class CommandCloud
        extends Command implements TabExecutor {

    public CommandCloud()
    {
        super("cloud", "cloudnet.command.cloudnet");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {

        if (args.length > 2)
        {
            if (args[0].equalsIgnoreCase("cmds"))
            {
                if (CloudNetAPI.getInstance().getCloudNetwork().getServers().containsKey(args[1]))
                {
                    StringBuilder builder = new StringBuilder();
                    for (short i = 2; i < args.length; i++)
                    {
                        builder.append(args[i]).append(" ");
                    }

                    CloudNetAPI.getInstance().getCnpConnector()
                            .sendPacket(new PacketIOHandleStartAndStop(new Document().append("serverid", args[1]).append("command", builder.substring(0, builder.length() - 1)),
                                    PacketIOHandleStartAndStop.HandleType.WRITE_COMMAND));
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                            "The update was sent to the cnp-root successfully");
                    return;
                }
            }
            if (args[0].equalsIgnoreCase("cmdp"))
            {
                if (CloudNetAPI.getInstance().getCloudNetwork().getProxys().containsKey(args[1]))
                {
                    StringBuilder builder = new StringBuilder();
                    for (short i = 2; i < args.length; i++)
                    {
                        builder.append(args[i]).append(" ");
                    }

                    CloudProxy.getInstance().sendCustomProxyMessage("command", new Document().append("command", builder.substring(0, builder.length() - 1)));
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                            "The update was sent to the cnp-root successfully");
                    return;
                }
            }
        }

        if (args.length > 1)
        {
            if (args[0].equalsIgnoreCase("cml1"))
            {
                StringBuilder builder = new StringBuilder();
                for (short i = 1; i < args.length; i++)
                {
                    builder.append(args[i]).append(" ");
                }

                CloudProxy.getInstance().getProxyLayout().getMaintenanceMotd().setFirstLine(builder.substring(0));
                CloudNetAPI.getInstance().getCnpConnector().sendPacket(new PacketIOUpdateProxyLayout(
                        CloudProxy.getInstance().getProxyLayout()
                ));

                commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                        "The update was sent to the cnp-root successfully");
                return;
            } else if (args[0].equalsIgnoreCase("cml2"))
            {
                StringBuilder builder = new StringBuilder();
                for (short i = 1; i < args.length; i++)
                {
                    builder.append(args[i]).append(" ");
                }

                CloudProxy.getInstance().getProxyLayout().getMaintenanceMotd().setSecondLine(builder.substring(0));
                CloudNetAPI.getInstance().getCnpConnector().sendPacket(new PacketIOUpdateProxyLayout(
                        CloudProxy.getInstance().getProxyLayout()
                ));

                commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                        "The update was sent to the cnp-root successfully");
                return;
            } else if (args[0].equalsIgnoreCase("motd1"))
            {
                StringBuilder builder = new StringBuilder();
                for (short i = 1; i < args.length; i++)
                {
                    builder.append(args[i]).append(" ");
                }

                CloudProxy.getInstance().getProxyLayout().getDefaultMotd().setFirstLine(builder.substring(0));
                CloudNetAPI.getInstance().getCnpConnector().sendPacket(new PacketIOUpdateProxyLayout(
                        CloudProxy.getInstance().getProxyLayout()
                ));

                commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                        "The update was sent to the cnp-root successfully");
                return;
            } else if (args[0].equalsIgnoreCase("motd2"))
            {
                StringBuilder builder = new StringBuilder();
                for (short i = 1; i < args.length; i++)
                {
                    builder.append(args[i]).append(" ");
                }

                CloudProxy.getInstance().getProxyLayout().getDefaultMotd().setSecondLine(builder.substring(0));
                CloudNetAPI.getInstance().getCnpConnector().sendPacket(new PacketIOUpdateProxyLayout(
                        CloudProxy.getInstance().getProxyLayout()
                ));

                commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                        "The update was sent to the cnp-root successfully");
                return;
            }

        }

        switch (args.length)
        {
            case 1:
                if (args[0].equalsIgnoreCase("help"))
                {
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud toggle autoslot");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud toggle notify");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud toggle maintenance");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud toggle maintenance <time>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud setMaxPlayers <maxonlinecount>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud whitelist <add : remove> <name>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud start <group> <count>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud stop <serverId>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud stopGroup <group>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud listProxys");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud listOnline");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud listServers");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud listGroups");

                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud cml1 <maintenancemotd1>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud cml2 <maintenancemotd2>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud motd1 <motd1>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud motd2 <motd2>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud cmds <server> <command>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud cmdp <proxy> <command>");

                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud rl");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud rl <cns>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud rlperm");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud list");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud copy <server>");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/cloud maintenance <group>");
                    return;
                }
                if (args[0].equalsIgnoreCase("rl"))
                {
                    CloudNetAPI.getInstance().writeCNPCommand("rl");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                            "The update was sent to the cnp-root successfully");
                    return;
                }
                if (args[0].equalsIgnoreCase("list"))
                {
                    commandSender.sendMessage(" ");

                    int maxMemory = 0;
                    int usedMemory = 0;

                    for (CNSInfo cnsInfo : CloudNetAPI.getInstance().getOnlineCloudNetServers())
                    {
                        commandSender.sendMessage("§8[§7" + cnsInfo.getServerId() + "§8/§7" + cnsInfo.getHostName() + "§8] §7Cores: " + cnsInfo.getAvailableProcessors());
                        maxMemory = maxMemory + cnsInfo.getMemory();
                    }
                    commandSender.sendMessage(" ");
                    for (SimpleProxyInfo simpleProxyInfo : CloudNetAPI.getInstance().getProxys())
                    {
                        commandSender.sendMessage("§8[§c" + simpleProxyInfo.getName() + "§8] §8(§e" + simpleProxyInfo.getOnlineCount() + "§8) : §7" + simpleProxyInfo.getMemory() + "MB");
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
                }
                if (args[0].equalsIgnoreCase("rlperm"))
                {
                    CloudNetAPI.getInstance().writeCNPCommand("rlp");
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                            "The update was sent to the cnp-root successfully");
                    return;
                }
                if (args[0].equalsIgnoreCase("listProxys"))
                {
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "Proxys:");
                    for (SimpleProxyInfo proxy : CloudNetAPI.getInstance().getCloudNetwork().getProxys().values())
                    {
                        commandSender.sendMessage("§7- " + (proxy.isOnline() ? "§e" : "§c") + proxy.getName() + " §8(§e" + proxy.getOnlineCount() + "§8) ");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("listServers"))
                {
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "Server:");
                    for (ServerInfo server : CloudNetAPI.getInstance().getCloudNetwork().getServers().values())
                    {
                        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText("§7- " + (server.isOnline() ? "§e" : "§c") + server.getName() + "§8(" + server.getOnlineCount() + "§8) §7State: " + server.getServerState()));
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + server.getName()));
                        commandSender.sendMessage(textComponent);
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("listOnline"))
                {
                    for (PlayerWhereAmI playerWhereAmI : CloudNetAPI.getInstance().getCloudNetwork().getOnlinePlayers().values())
                    {
                        commandSender.sendMessage("§7- §e" + playerWhereAmI.getName() + " §7auf §e" + playerWhereAmI.getServer() + "/" + playerWhereAmI.getProxy());
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("listGroups"))
                {
                    StringBuilder builder = new StringBuilder();

                    for (SimpleServerGroup group : CloudNetAPI.getInstance().getCloudNetwork().getGroups().values())
                    {
                        builder.append((!group.isMaintenance() ? "§e" : "§c")).append(group.getName()).append("§7, ");
                    }

                    commandSender.sendMessage("§7The network has the following groups:");
                    commandSender.sendMessage(builder.substring(0));
                    return;
                }
                break;
            case 2:
                if(args[0].equalsIgnoreCase("copy"))
                {
                    if(CloudNetAPI.getInstance().getServerInfo(args[1]) != null)
                    {
                        ServerInfo serverInfo = CloudNetAPI.getInstance().getServerInfo(args[1]);
                        CloudNetAPI.getInstance().writeCNSCommand(CloudNetAPI.getInstance().getCloudNetServer(serverInfo.getCloudId()), "copy " + serverInfo.getName());
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                                "The update was sent to the cnp-root successfully");
                        return;
                    }
                    return;
                }
                if(args[0].equalsIgnoreCase("maintenance"))
                {
                    if(CloudNetAPI.getInstance().getGroupData(args[1]) != null)
                    {
                        for(CNSInfo cnsInfo : CloudNetAPI.getInstance().getOnlineCloudNetServers())
                        CloudNetAPI.getInstance().writeCNSCommand(cnsInfo, "cmfg " + args[1]);
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                                "The update was sent to the cnp-root successfully");
                        return;
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("toggle"))
                {
                    switch (args[1].toLowerCase())
                    {
                        case "autoslot":
                            CloudProxy.getInstance().getProxyLayout().setAutoSlot(!CloudProxy.getInstance().getProxyLayout().isAutoSlot());
                            CloudNetAPI.getInstance().getCnpConnector().sendPacket(new PacketIOUpdateProxyLayout(
                                    CloudProxy.getInstance().getProxyLayout()
                            ));
                            commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "the autoslot state is updatet.");
                            return;
                        case "maintenance":
                        {
                            CloudProxy.getInstance().getProxyLayout().setMaintenance(!CloudProxy.getInstance().getProxyLayout().isMaintenance());
                            CloudNetAPI.getInstance().getCnpConnector().sendPacket(new PacketIOUpdateProxyLayout(
                                    CloudProxy.getInstance().getProxyLayout()
                            ));
                            commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "the maintenance state is updatet.");
                            return;
                        }
                        case "notify":
                        {
                            CloudProxy.getInstance().getProxyLayout().setNotifySystem(!CloudProxy.getInstance().getProxyLayout().isNotifySystem());
                            CloudNetAPI.getInstance().getCnpConnector().sendPacket(new PacketIOUpdateProxyLayout(
                                    CloudProxy.getInstance().getProxyLayout()
                            ));
                            commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "the notify state is updatet.");
                        }
                            return;
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("rl"))
                {
                    if (CloudNetAPI.getInstance().getCloudNetServer(args[1]) != null)
                    {
                        CloudNetAPI.getInstance().writeCNSCommand(CloudNetAPI.getInstance().getCloudNetServer(args[1]), "rl");
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                                "The update was sent to the cnp-root successfully");
                    }
                }
                if (args[0].equalsIgnoreCase("setMaxPlayers"))
                {
                    if (checkAsNumber(args[1]))
                    {
                        CloudProxy.getInstance().getProxyLayout().setMaxOnlineCount(Integer.parseInt(args[1]));
                        CloudNetAPI.getInstance().getCnpConnector().sendPacket(new PacketIOUpdateProxyLayout(
                                CloudProxy.getInstance().getProxyLayout()
                        ));
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "the max onlinecount is updatet.");
                    } else
                    {
                        commandSender.sendMessage("§7The argument 2 is not a number.");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("start"))
                {
                    if (CloudNetAPI.getInstance().getCloudNetwork().getGroups().containsKey(args[1]))
                    {
                        CloudNetAPI.getInstance().startServer(CloudNetAPI.getInstance().getGroupData(args[1]), new Document(), true);
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                                "The update was sent to the cnp-root successfully");
                    } else
                    {
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The group does'nt exist.");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("stop"))
                {
                    if (CloudNetAPI.getInstance().getCloudNetwork().getServers().containsKey(args[1]))
                    {
                        CloudNetAPI.getInstance().getCnpConnector()
                                .sendPacket(new PacketIOHandleStartAndStop(
                                        new Document().append("serverid", args[1]),
                                        PacketIOHandleStartAndStop.HandleType.STOP_SERVER
                                ));
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                                "The update was sent to the cnp-root successfully");
                    } else
                    {
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The server isn't online.");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("stopGroup"))
                {
                    List<String> servers = CloudNetAPI.getInstance().getServers(args[1]);
                    for (String server : servers)
                    {
                        CloudNetAPI.getInstance().stopServer(server);
                    }
                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                            "The update was sent to the cnp-root successfully");
                    return;
                }
                break;
            case 3:
                if (args[0].equalsIgnoreCase("toggle"))
                {
                    switch (args[1].toLowerCase())
                    {
                        case "maintenance":
                        {
                            if (!NetworkUtils.checkIsNumber(args[2])) return;
                            ProxyServer.getInstance().getScheduler().schedule(CloudNetProxyPlugin.getInstance(), new Runnable() {
                                @Override
                                public void run()
                                {
                                    CloudProxy.getInstance().getProxyLayout().setMaintenance(!CloudProxy.getInstance().getProxyLayout().isMaintenance());
                                    CloudNetAPI.getInstance().getCnpConnector().sendPacket(new PacketIOUpdateProxyLayout(
                                            CloudProxy.getInstance().getProxyLayout()
                                    ));
                                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "the maintenance state is updatet.");
                                }
                            }, Integer.parseInt(args[2]), TimeUnit.SECONDS);
                            commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "the maintenance will be changed at " + args[2] + " seconds");
                            return;
                        }
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("start"))
                {
                    if (CloudNetAPI.getInstance().getCloudNetwork().getGroups().containsKey(args[1]))
                    {
                        if (checkAsNumber(args[2]))
                        {
                            for (short i = 0; i < Integer.parseInt(args[2]); i++)
                            {
                                CloudNetAPI.getInstance().startServer(CloudNetAPI.getInstance().getGroupData(args[1]), new Document(), true);
                            }
                            commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                                    "The update was sent to the cnp-root successfully");
                        } else
                        {
                            CloudNetAPI.getInstance().startServer(CloudNetAPI.getInstance().getGroupData(args[1]), new Document(), true);
                            commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                                    "The update was sent to the cnp-root successfully");
                        }
                    } else
                    {
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "The group does'nt exist.");
                    }
                } else if (args[0].equalsIgnoreCase("whitelist"))
                {
                    if (args[1].equalsIgnoreCase("add"))
                    {
                        CloudProxy.getInstance().getProxyLayout().getPlayerWhitelist().add(args[2]);
                        CloudNetAPI.getInstance().getCnpConnector().sendPacket(new PacketIOUpdateProxyLayout(
                                CloudProxy.getInstance().getProxyLayout()
                        ));
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + " you added " + args[2] + " to the whitelist from the maintenance mode.");
                    } else if (args[1].equalsIgnoreCase("remove"))
                    {
                        CloudProxy.getInstance().getProxyLayout().getPlayerWhitelist().remove(args[2]);
                        CloudNetAPI.getInstance().getCnpConnector().sendPacket(new PacketIOUpdateProxyLayout(
                                CloudProxy.getInstance().getProxyLayout()
                        ));
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + " you removed " + args[2] + " to the whitelist from the maintenance mode.");
                    }
                }
                break;
            default:
                commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "Use /cloud help");
                break;
        }

    }

    private final boolean checkAsNumber(String input)
    {
        try
        {
            Short.parseShort(input);
            return true;
        } catch (NumberFormatException e)
        {
            return false;
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings)
    {
        List<String> names = new ArrayList<>();
        for (PlayerWhereAmI key : CloudNetAPI.getInstance().getCloudNetwork().getOnlinePlayers().values())
            names.add(key.getName());
        return names;
    }

}