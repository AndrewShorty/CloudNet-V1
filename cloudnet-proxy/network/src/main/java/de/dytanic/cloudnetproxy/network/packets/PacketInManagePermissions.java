package de.dytanic.cloudnetproxy.network.packets;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.logging.command.CommandPool;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnet.permission.PermissionEntity;
import de.dytanic.cloudnet.permission.PermissionGroup;
import de.dytanic.cloudnet.player.PlayerMetaData;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.components.CNS;
import de.dytanic.cloudnetproxy.network.components.ProxyServer;
import de.dytanic.cloudnetproxy.utils.Utils;

import java.util.UUID;

/**
 * Created by Tareko on 05.07.2017.
 */
public class PacketInManagePermissions
                extends Packet {

    public PacketInManagePermissions() {}

    @Override
    public void handleInput(Document data, PacketSender packetSender)
    {
        if(data.contains("permissiongroup"))
        {
            switch (PermissionGroupHandle.valueOf(data.getString("handle")))
            {
                case CREATE:
                    CloudNetProxy.getInstance().getPermissionBackend().update(data.getObject("permissiongroup", new TypeToken<PermissionGroup>(){}.getType()));
                    a();
                    break;
                case REMOVE:
                    CloudNetProxy.getInstance().getPermissionBackend().delete(data.getObject("permissiongroup", new TypeToken<PermissionGroup>(){}.getType()));
                    a();
                    break;
                case UPDATE:
                    CloudNetProxy.getInstance().getPermissionBackend().update(data.getObject("permissiongroup", new TypeToken<PermissionGroup>(){}.getType()));
                    a();
                    break;
                default:
                    break;
            }
        }

        if(data.contains("uuid") && data.contains("value") && data.contains("handle") && data.contains("permission"))
        {
            PlayerMetaData playerMetaData = CloudNetProxy.getInstance().getPlayerBackend().getPlayer(UUID.fromString(data.getString("uuid")));
            if(data.getString("handle").toUpperCase().equals(PlayerPermissionHandle.ADD.name()))
            {
                playerMetaData.getPermissionEntity().getPermissions().put(data.getString("permission"), data.getBoolean("value"));
            }
            else
            {
                playerMetaData.getPermissionEntity().getPermissions().remove(data.getString("permission"));
            }

            CloudNetProxy.getInstance().getPlayerBackend().updatePlayerMetaData(playerMetaData);

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

    private void a()
    {
        CloudNetProxy.getInstance().getPermissionPool().getGroups().clear();
        CloudNetProxy.getInstance().getPermissionPool().setAvailable(CloudNetProxy.getInstance().getConfig().isPermissionSystemEnabled());
        Utils.addAll(CloudNetProxy.getInstance().getPermissionPool().getGroups(), CloudNetProxy.getInstance().getPermissionBackend().loadPermissions());
        CloudNetProxy.getInstance().getCloudNetwork().setPermissionPool(
                CloudNetProxy.getInstance().getPermissionPool()
        );
        CloudNetProxy.getInstance().sendAllPacketOnNetwork(new PacketOutUpdatePermissionPool());
        CloudNetProxy.getInstance().getCloudNetwork().setPermissionPool(CloudNetProxy.getInstance().getPermissionPool());
    }

    public enum PermissionGroupHandle {
        CREATE,
        UPDATE,
        REMOVE,
    }

    public enum PlayerPermissionHandle {
        ADD,
        REMOVE;
    }

}