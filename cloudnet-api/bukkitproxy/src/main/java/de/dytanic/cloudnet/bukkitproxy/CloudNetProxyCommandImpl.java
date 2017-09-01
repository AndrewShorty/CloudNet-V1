package de.dytanic.cloudnet.bukkitproxy;

import com.google.common.collect.ImmutableSet;
import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.bukkitproxy.api.CloudProxy;
import de.dytanic.cloudnet.bukkitproxy.api.command.GlobalTabExecutor;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;

/**
 * Created by Tareko on 05.07.2017.
 */
public final class CloudNetProxyCommandImpl {

    private CloudNetProxyCommandImpl() {}

    public static class CNPCommandFind
            extends Command implements GlobalTabExecutor {

        public CNPCommandFind()
        {
            super("find", "bungeecord.command.find");
        }

        @Override
        public void execute(CommandSender sender, String[] args)
        {
            if (args.length != 1)
            {
                sender.sendMessage(ChatColor.RED + "Please follow this command by a user name");
            } else
            {
                PlayerWhereAmI player = CloudNetAPI.getInstance().getOnlinePlayer(args[0]);
                if (player == null || player.getServer() == null)
                {
                    sender.sendMessage(ChatColor.RED + "That user is not online");
                } else
                {
                    sender.sendMessage(ChatColor.GREEN + args[0] + " is online at " + player.getServer() + " @§c" + player.getProxy());
                }
            }
        }
    }

    public static class CNPCommandIP
            extends Command implements GlobalTabExecutor {

        public CNPCommandIP()
        {
            super("ip", "bungeecord.command.ip");
        }

        @Override
        public void execute(CommandSender sender, String[] args)
        {
            if (args.length < 1)
            {
                sender.sendMessage(ChatColor.RED + "Please follow this command by a user name");
                return;
            }
            PlayerWhereAmI user = CloudNetAPI.getInstance().getOnlinePlayer(args[0]);
            if (user == null)
            {
                sender.sendMessage(ChatColor.RED + "That user is not online");
            } else
            {
                sender.sendMessage(ChatColor.BLUE + "IP of " + args[0] + " is " + user.getIp());
            }
        }
    }

    public static class CNPCommandList extends Command {

        public CNPCommandList()
        {
            super("glist", "bungeecord.command.list");
        }

        @Override
        public void execute(CommandSender sender, String[] args)
        {
            for (ServerInfo server : CloudNetAPI.getInstance().getServerInfos())
            {
                List<String> players = server.getPlayers();
                Collections.sort(players, String.CASE_INSENSITIVE_ORDER);
                sender.sendMessage(ProxyServer.getInstance().getTranslation("command_list", server.getName(), players.size(), Util.format(players, ChatColor.RESET + ", ")));
            }

            sender.sendMessage(ProxyServer.getInstance().getTranslation("total_players", CloudNetAPI.getInstance().getCloudNetwork().getOnlineCount()));
        }
    }

    public static class CNPCommandSend extends Command implements TabExecutor {

        public CNPCommandSend()
        {
            super("send", "bungeecord.command.send");
        }

        @Override
        public void execute(CommandSender sender, String[] args)
        {
            if (args.length != 2)
            {
                sender.sendMessage(ChatColor.RED + "Not enough arguments, usage: /send <server|player|all|current> <target>");
                return;
            }
            ServerInfo target = CloudNetAPI.getInstance().getServerInfo(args[1]);
            if (target == null)
            {
                sender.sendMessage(ProxyServer.getInstance().getTranslation("no_server"));
                return;
            }

            if (args[0].equalsIgnoreCase("all"))
            {
                for (PlayerWhereAmI p : CloudNetAPI.getInstance().getCloudNetwork().getOnlinePlayers().values())
                {
                    summon(p, target, sender);
                }
            } else if (args[0].equalsIgnoreCase("current"))
            {
                if (!(sender instanceof ProxiedPlayer))
                {
                    sender.sendMessage(ChatColor.RED + "Only in game players can use this command");
                    return;
                }
                ProxiedPlayer player = (ProxiedPlayer) sender;
                for (ProxiedPlayer p : player.getServer().getInfo().getPlayers())
                {
                    PlayerWhereAmI playerWhereAmI = CloudNetAPI.getInstance().getOnlinePlayer(p.getUniqueId());
                    if (playerWhereAmI != null)
                        summon(playerWhereAmI, target, sender);
                }
            } else
            {
                // If we use a server name, send the entire server. This takes priority over players.
                ServerInfo serverTarget = CloudNetAPI.getInstance().getServerInfo(args[0]);
                if (serverTarget != null)
                {
                    for (String p : serverTarget.getPlayers())
                    {
                        PlayerWhereAmI playerWhereAmI = CloudNetAPI.getInstance().getOnlinePlayer(p);
                        if (playerWhereAmI != null)
                            summon(playerWhereAmI, target, sender);
                    }
                } else
                {
                    PlayerWhereAmI player = CloudNetAPI.getInstance().getOnlinePlayer(args[0]);
                    if (player == null)
                    {
                        sender.sendMessage(ChatColor.RED + "That player is not online");
                        return;
                    }
                    summon(player, target, sender);
                }
            }
            sender.sendMessage(ChatColor.GREEN + "Successfully summoned player(s)");
        }

        private void summon(PlayerWhereAmI player, ServerInfo target, CommandSender sender)
        {
            if (player.getServer() != null && player.getServer() != null && !player.getServer().equals(target.getName()))
            {
                CloudProxy.getInstance().sendPlayer(player.getUniqueId(), target);
            }
        }

        @Override
        public Iterable<String> onTabComplete(CommandSender sender, String[] args)
        {
            if (args.length > 2 || args.length == 0)
            {
                return ImmutableSet.of();
            }

            Set<String> matches = new HashSet<>();
            if (args.length == 1)
            {
                String search = args[0].toLowerCase();
                for (PlayerWhereAmI player : CloudNetAPI.getInstance().getCloudNetwork().getOnlinePlayers().values())
                {
                    if (player.getName() != null && player.getName().toLowerCase().startsWith(search))
                    {
                        matches.add(player.getName());
                    }
                }
                if ("all".startsWith(search))
                {
                    matches.add("all");
                }
                if ("current".startsWith(search))
                {
                    matches.add("current");
                }
            } else
            {
                String search = args[1].toLowerCase();
                for (String server : ProxyServer.getInstance().getServers().keySet())
                {
                    if (server.toLowerCase().startsWith(search))
                    {
                        matches.add(server);
                    }
                }
            }
            return matches;
        }
    }
}