package de.dytanic.cloudnetserver.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnet.servergroup.ServerGroup;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.network.NetworkConnector;
import de.dytanic.cloudnetserver.network.packets.PacketOutServerData;
import de.dytanic.cloudnetserver.util.ProxyConfig;

import java.util.List;

/**
 * Created by Tareko on 25.05.2017.
 */
public class CommandReload
            extends Command{

    public CommandReload()
    {
        super("reload", "cloudnet.server.commmand.reload", "rl");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        
        CloudNetServer.getInstance().getConfig().reloadConfig();
        List<ServerGroup> groups = CloudNetServer.getInstance().getConfig().loadGroups();
        CloudNetServer.getInstance().getGroups().clear();

        for(ServerGroup group : groups)
        {
            CloudNetServer.getInstance().getGroups().put(group.getName(), group);
        }
        CloudNetServer.getInstance()
                .setProxyConfig(CloudNetServer.getInstance().getConfig().getConfig().getObject("proxy", ProxyConfig.class));
        CloudNetServer.getInstance()
                .setMaxMemory(Integer.parseInt(CloudNetServer.getInstance().getConfig().getProperties().getProperty("maxmemory")));

        if(NetworkConnector.getInstance() != null)
        {
            NetworkConnector.getInstance().sendPacket(new PacketOutServerData());
        }

        sender.sendMessage("The config is reloaded and send to the cnp-root.");
    }
}