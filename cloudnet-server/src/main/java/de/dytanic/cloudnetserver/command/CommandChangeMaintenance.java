package de.dytanic.cloudnetserver.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnet.servergroup.ServerGroup;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.network.NetworkConnector;
import de.dytanic.cloudnetserver.network.packets.PacketOutServerData;

/**
 * Created by Tareko on 27.07.2017.
 */
public class CommandChangeMaintenance extends Command {

    public CommandChangeMaintenance()
    {
        super("cmfg", "cloudnet.server.command.changemaintenancefromgroup", "changemaintenance", "togglemaintenance", "groupmaintenance");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 1:
                if(CloudNetServer.getInstance().getGroups().containsKey(args[0]))
                {
                    ServerGroup serverGroup = CloudNetServer.getInstance().getGroups().get(args[0]);
                    serverGroup.setMaintenance(!serverGroup.isMaintenance());
                   CloudNetServer.getInstance().getConfig().addNewGroup(serverGroup);
                    NetworkConnector.getInstance().sendPacket(new PacketOutServerData());
                    sender.sendMessage("Maintenance is toggled for " + serverGroup.getName());
                }
                break;
            default:
                sender.sendMessage("cmfg <group>");
                break;
        }
    }
}
