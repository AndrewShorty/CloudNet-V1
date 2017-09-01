package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.lib.threading.Callback;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnet.player.PlayerMetaData;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.components.CNS;
import de.dytanic.cloudnetproxy.network.components.ProxyServer;

/**
 * Created by Tareko on 02.06.2017.
 */
public class PacketInUpdateGroupMember
            extends Packet {

    public PacketInUpdateGroupMember() {}

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        CloudNetProxy.getInstance().handlePlayer(document.getString("name"), new Callback<PlayerMetaData>() {
            @Override
            public void call(PlayerMetaData playerMetaData)
            {
                playerMetaData.getPermissionEntity().setTimeOut(document.getLong("timeOut"));
                playerMetaData.getPermissionEntity().setPermissionGroup(document.getString("group"));
                CloudNetProxy.getInstance().getPlayerBackend().updatePermissionEntity(playerMetaData.getUniqueId(), playerMetaData.getPermissionEntity());

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
        });
    }
}
