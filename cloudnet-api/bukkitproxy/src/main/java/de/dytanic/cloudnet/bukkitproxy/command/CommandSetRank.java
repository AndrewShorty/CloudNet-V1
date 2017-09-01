package de.dytanic.cloudnet.bukkitproxy.command;

import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.bukkitproxy.api.CloudProxy;
import de.dytanic.cloudnet.api.network.packets.PacketIOUpdatePlayerWhereAmI;
import de.dytanic.cloudnet.api.network.packets.PacketOutUpdateGroupMember;
import de.dytanic.cloudnet.bukkitproxy.api.command.GlobalTabExecutor;
import de.dytanic.cloudnet.bukkitproxy.api.event.proxy.ProxyRankUpdateEvent;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Tareko on 02.06.2017.
 */
public class CommandSetRank
        extends Command implements GlobalTabExecutor {

    public CommandSetRank()
    {
        super("setrank", "cloudnet.command.setrank");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {
        switch (args.length)
        {
            case 3:
            {
                if (CloudNetAPI.getInstance().getCloudNetwork().getPermissionPool().getGroups().containsKey(args[1]))
                {
                    if (args[2].equalsIgnoreCase("lifetime"))
                    {
                        long timeout = 0;

                        ProxyServer.getInstance().getPluginManager().callEvent(new ProxyRankUpdateEvent(args[0], args[1], timeout));

                        for (String uuid : CloudNetAPI.getInstance().getCloudNetwork().getOnlinePlayers().keySet())
                        {
                            PlayerWhereAmI playerWhereAmI = CloudNetAPI.getInstance().getCloudNetwork().getOnlinePlayers().get(uuid);
                            if (playerWhereAmI.getName().equals(args[2]))
                            {
                                if (playerWhereAmI.getPlayerMetaData() != null)
                                {

                                    playerWhereAmI.getPlayerMetaData()
                                            .getPermissionEntity().setPermissionGroup(args[1]);
                                    playerWhereAmI.getPlayerMetaData()
                                            .getPermissionEntity().setTimeOut(timeout);
                                    CloudNetAPI.getInstance()
                                            .getCnpConnector()
                                            .sendPacket(new PacketIOUpdatePlayerWhereAmI(
                                                    playerWhereAmI, ProxyServer.getInstance().getOnlineCount(),
                                                    CloudNetAPI.getInstance().getServerId(), true
                                            ));
                                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                                            "The update was sent to the cnp-root successfully");
                                    return;
                                }
                            }

                        }

                        CloudNetAPI.getInstance()
                                .getCnpConnector().sendPacket(new PacketOutUpdateGroupMember(
                                args[0], args[1], timeout
                        ));
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                                "The update was sent to the cnp-root successfully");
                    } else if (checkAsNumber(args[2]))
                    {
                        long timeout = (Integer.valueOf(args[2]) * 60 * 1000 * 24 * 60) + System.currentTimeMillis();
                        ProxyServer.getInstance().getPluginManager().callEvent(new ProxyRankUpdateEvent(args[0], args[1], timeout));
                        for (String uuid : CloudNetAPI.getInstance().getCloudNetwork().getOnlinePlayers().keySet())
                        {
                            PlayerWhereAmI playerWhereAmI = CloudNetAPI.getInstance().getCloudNetwork().getOnlinePlayers().get(uuid);
                            if (playerWhereAmI.getName().equals(args[2]))
                            {
                                if (playerWhereAmI.getPlayerMetaData() != null)
                                {

                                    playerWhereAmI.getPlayerMetaData()
                                            .getPermissionEntity().setPermissionGroup(args[1]);
                                    playerWhereAmI.getPlayerMetaData()
                                            .getPermissionEntity().setTimeOut(timeout);
                                    CloudNetAPI.getInstance()
                                            .getCnpConnector()
                                            .sendPacket(new PacketIOUpdatePlayerWhereAmI(
                                                    playerWhereAmI, ProxyServer.getInstance().getOnlineCount(),
                                                    CloudNetAPI.getInstance().getServerId(), true
                                            ));
                                    commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                                            "The update was sent to the cnp-root successfully");
                                    return;
                                }
                            }

                        }

                        CloudNetAPI.getInstance()
                                .getCnpConnector().sendPacket(new PacketOutUpdateGroupMember(
                                args[0], args[1], timeout
                        ));
                        commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() +
                                "The update was sent to the cnp-root successfully");
                    }
                } else
                {
                    commandSender.sendMessage("The group doesn't exist.");
                }
            }
            break;
            default:
                commandSender.sendMessage(CloudProxy.getInstance().getProxyLayout().getPrefix() + "/setrank <name> <group> <timeout in days : lifetime>");
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
}