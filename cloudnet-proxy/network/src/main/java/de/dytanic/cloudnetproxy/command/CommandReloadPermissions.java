package de.dytanic.cloudnetproxy.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.CloudNetProxyServer;
import de.dytanic.cloudnetproxy.network.packets.PacketOutUpdatePermissionPool;
import de.dytanic.cloudnetproxy.utils.Utils;

/**
 * Created by Tareko on 01.06.2017.
 */
public class CommandReloadPermissions
            extends Command {

    public CommandReloadPermissions()
    {
        super("rlp", "cloudnet.proxy.command.perms", "reloadpermissions", "reloadpermission", "rperm");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        CloudNetProxy.getInstance().getPermissionPool().getGroups().clear();
        CloudNetProxy.getInstance().getPermissionPool().setAvailable(CloudNetProxy.getInstance().getConfig().isPermissionSystemEnabled());
        Utils.addAll(CloudNetProxy.getInstance().getPermissionPool().getGroups(), CloudNetProxy.getInstance().getPermissionBackend().loadPermissions());
        CloudNetProxy.getInstance().getCloudNetwork().setPermissionPool(
                CloudNetProxy.getInstance().getPermissionPool()
        );
        PacketOutUpdatePermissionPool packetOutUpdatePermissionPool = new PacketOutUpdatePermissionPool();
        for(CloudNetProxyServer proxyServer : CloudNetProxy.getInstance().getProxyServer())
        {
            proxyServer.sendAllPacket(packetOutUpdatePermissionPool);
        }
        sender.sendMessage("The Permissions are reloaded.");
    }
}