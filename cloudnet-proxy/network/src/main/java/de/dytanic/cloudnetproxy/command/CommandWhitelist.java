package de.dytanic.cloudnetproxy.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.packets.PacketOutUpdateNetwork;

/**
 * Created by Tareko on 26.05.2017.
 */
public class CommandWhitelist
            extends Command {

    public CommandWhitelist()
    {
        super("whitelist", "cloudnet.proxy.command.whitelist", "wl");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 1:
                if(args[0].equalsIgnoreCase("list"))
                {
                    sender.sendMessage("Whitelist:");
                    for(String list : CloudNetProxy.getInstance().getWhitelist())
                    {
                        sender.sendMessage("- " + list);
                    }
                }
                break;
            case 2:
                if(args[0].equalsIgnoreCase("add"))
                {
                    CloudNetProxy.getInstance().getWhitelist().add(args[1]);
                    CloudNetProxy.getInstance().getConfig().addWhitelist(args[1]);
                    CloudNetProxy.getInstance().sendAllPacketOnNetwork(new PacketOutUpdateNetwork(CloudNetProxy.getInstance().getCloudNetwork().getGroups(), PacketOutUpdateNetwork.UpdateType.UPDATE_GROUPS));

                    sender.sendMessage("The HostName " + args[1] + " is added.");
                }
                else
                if(args[0].equalsIgnoreCase("remove"))
                {
                    CloudNetProxy.getInstance().getWhitelist().remove(args[1]);
                    CloudNetProxy.getInstance().getConfig().removeWhitelist(args[1]);
                    CloudNetProxy.getInstance().sendAllPacketOnNetwork(new PacketOutUpdateNetwork(CloudNetProxy.getInstance().getCloudNetwork().getGroups(), PacketOutUpdateNetwork.UpdateType.UPDATE_GROUPS));

                    sender.sendMessage("The HostName " + args[1] + " is removed.");
                }
                break;
            default:
                sender.sendMessage("whitelist <add : remove : list> <\"hostName\">");
                break;
        }
    }
}
