package de.dytanic.cloudnetproxy.command;

import de.dytanic.cloudnet.lib.threading.Callback;
import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnet.player.PlayerMetaData;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.components.CNS;
import de.dytanic.cloudnetproxy.network.components.ProxyServer;

/**
 * Created by Tareko on 01.06.2017.
 */
public class CommandGroup
                extends Command {

    public CommandGroup()
    {
        super("group", "cloudnet.proxy.command.group", "g");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 3:
                if(args[0].equalsIgnoreCase("set"))
                {
                    CloudNetProxy.getInstance().handlePlayer(args[1],
                            new Callback<PlayerMetaData>() {
                                @Override
                                public void call(PlayerMetaData playerMetaData)
                                {

                                    playerMetaData.getPermissionEntity().setPermissionGroup(args[2]);
                                    playerMetaData.getPermissionEntity().setTimeOut(0L);
                                    CloudNetProxy.getInstance().getPlayerBackend().updatePermissionEntity(playerMetaData.getUniqueId(),
                                            playerMetaData.getPermissionEntity());
                                    System.out.println("The group is added from " + playerMetaData.getName() + "/" + playerMetaData.getUniqueId() + ".");

                                    if(CloudNetProxy.getInstance().getCloudNetwork().getOnlinePlayers().containsKey(playerMetaData.getUniqueId().toString()))
                                    {

                                        for(CNS cns : CloudNetProxy.getInstance().getCnsSystems().values())
                                        {
                                            for(ProxyServer proxy : cns.getProxys().values())
                                            {
                                                if(proxy.getProxyInfo().getPlayers().containsKey(playerMetaData.getUniqueId().toString()))
                                                {

                                                    proxy.getProxyInfo().getPlayers().get(playerMetaData.getUniqueId().toString()).setPlayerMetaData(playerMetaData);

                                                    CloudNetProxy.getInstance().updateOnlineCount();
                                                    return;
                                                }
                                            }
                                        }

                                    }

                                }
                            }
                    );
                }
                else
                if(args[0].equalsIgnoreCase("removeperm"))
                {
                    CloudNetProxy.getInstance().handlePlayer(args[1],
                            new Callback<PlayerMetaData>() {
                                @Override
                                public void call(PlayerMetaData playerMetaData)
                                {

                                    playerMetaData.getPermissionEntity().getPermissions().remove(args[2]);
                                    CloudNetProxy.getInstance().getPlayerBackend().updatePermissionEntity(playerMetaData.getUniqueId(),
                                            playerMetaData.getPermissionEntity());

                                    System.out.println("The permission " + args[2] + " is now removed from " + playerMetaData.getName() + ".");


                                    if(CloudNetProxy.getInstance().getCloudNetwork().getOnlinePlayers().containsKey(playerMetaData.getUniqueId().toString()))
                                    {

                                        for(CNS cns : CloudNetProxy.getInstance().getCnsSystems().values())
                                        {
                                            for(ProxyServer proxy : cns.getProxys().values())
                                            {
                                                   if(proxy.getProxyInfo().getPlayers().containsKey(playerMetaData.getUniqueId().toString()))
                                                   {

                                                       proxy.getProxyInfo().getPlayers().get(playerMetaData.getUniqueId().toString()).setPlayerMetaData(playerMetaData);

                                                       CloudNetProxy.getInstance().updateOnlineCount();
                                                       return;
                                                   }
                                            }
                                        }

                                    }

                                }
                            }
                    );
                }
                break;
            case 4:
                if(args[0].equalsIgnoreCase("addperm"))
                {
                    CloudNetProxy.getInstance().handlePlayer(args[1],
                            new Callback<PlayerMetaData>() {
                                @Override
                                public void call(PlayerMetaData playerMetaData)
                                {
                                    playerMetaData.getPermissionEntity().getPermissions().put(args[2], args[3].equalsIgnoreCase("true"));
                                    CloudNetProxy.getInstance().getPlayerBackend().updatePermissionEntity(playerMetaData.getUniqueId(),
                                            playerMetaData.getPermissionEntity());
                                    System.out.println("The Player " + playerMetaData.getName() + "/" + playerMetaData.getUniqueId() + " added the permission \""
                                            + args[2] + "\" with the value " + args[3].equalsIgnoreCase("true") + ".");

                                    if(CloudNetProxy.getInstance().getCloudNetwork().getOnlinePlayers().containsKey(playerMetaData.getUniqueId().toString()))
                                    {

                                        for(CNS cns : CloudNetProxy.getInstance().getCnsSystems().values())
                                        {
                                            for(ProxyServer proxy : cns.getProxys().values())
                                            {
                                                if(proxy.getProxyInfo().getPlayers().containsKey(playerMetaData.getUniqueId().toString()))
                                                {

                                                    proxy.getProxyInfo().getPlayers().get(playerMetaData.getUniqueId().toString()).setPlayerMetaData(playerMetaData);

                                                    CloudNetProxy.getInstance().updateOnlineCount();
                                                    return;
                                                }
                                            }
                                        }

                                    }

                                }
                            }
                    );
                }
                break;
            default:
                sender.sendMessage("group set <name> <group>");
                sender.sendMessage("group addperm <name> <permission> <true : false>");
                sender.sendMessage("group removeperm <name> <permission>");
                break;
        }
    }
}
