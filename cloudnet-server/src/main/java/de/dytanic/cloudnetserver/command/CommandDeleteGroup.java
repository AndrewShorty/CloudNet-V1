package de.dytanic.cloudnetserver.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.network.NetworkConnector;
import de.dytanic.cloudnetserver.network.packets.PacketOutServerData;
import de.dytanic.cloudnetserver.server.MinecraftServer;

/**
 * Created by Tareko on 10.07.2017.
 */
public class CommandDeleteGroup
            extends Command{

    public CommandDeleteGroup()
    {
        super("deletegroup", "cloudnet.command.deletegroup");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 1:
                if(CloudNetServer.getInstance().getGroups().containsKey(args[0]))
                {
                    CloudNetServer.getInstance().getConfig().removeGroup(CloudNetServer.getInstance().getGroups().get(args[0]));
                    for(String minecraftServer : CloudNetServer.getInstance().getServersByName(args[0]))
                    {
                        MinecraftServer x = CloudNetServer.getInstance().getServers().get(minecraftServer);
                        if(x != null) x.shutdown();
                    }
                    if(NetworkConnector.getInstance() != null)
                    {
                        NetworkConnector.getInstance().sendPacket(new PacketOutServerData());
                    }
                    sender.sendMessage("The group is deleted.");
                }
                break;
            default:
                sender.sendMessage("deletegroup <group>");
                break;
        }
    }
}