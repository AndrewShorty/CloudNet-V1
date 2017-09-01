package de.dytanic.cloudnetproxy.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.CloudNetProxyServer;
import de.dytanic.cloudnetproxy.network.packets.PacketOutUpdateNetwork;
import de.dytanic.cloudnetproxy.network.packets.PacketOutUpdateSignsSystem;

/**
 * Created by Tareko on 31.05.2017.
 */
public class CommandReload
                extends Command {

    public CommandReload()
    {
        super("reloadproxylayout", "cloudnet.proxy.command.rpl", "rl", "rpl", "reload");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        CloudNetProxy.getInstance().getConfig().reloadConfig();
        CloudNetProxy.getInstance().updateProxyLayout();


        CloudNetProxy.getInstance().sendAllLobbys(new PacketOutUpdateSignsSystem(
                CloudNetProxy.getInstance().getSignBackend().signs(),
                CloudNetProxy.getInstance().getSignGroupLayoutsBackend().load()
        ));
        CloudNetProxy.getInstance().setServerLayout(CloudNetProxy.getInstance().getConfig().loadServerLayout());

        PacketOutUpdateNetwork packetOutUpdateNetwork = new PacketOutUpdateNetwork(CloudNetProxy.getInstance().getServerLayout());
        for(CloudNetProxyServer proxyServer : CloudNetProxy.getInstance().getProxyServer())
        {
            proxyServer.sendAllPacket(packetOutUpdateNetwork);
        }

        sender.sendMessage("Loading and sending proxylayout, serverlayout, and signlayout!");
    }
}