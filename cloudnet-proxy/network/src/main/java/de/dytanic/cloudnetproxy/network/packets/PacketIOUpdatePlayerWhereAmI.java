package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.ProxyInfo;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.components.CNS;
import de.dytanic.cloudnetproxy.network.components.ProxyServer;

/**
 * Created by Tareko on 01.06.2017.
 */
public class PacketIOUpdatePlayerWhereAmI
                extends Packet {

    public PacketIOUpdatePlayerWhereAmI()
    {
        super(null);
    }

    private PacketIOUpdatePlayerWhereAmI(Document document)
    {
        super(document);
    }

    @Override
    public void handleInput(Document document, PacketSender sender)
    {

        for(CNS cns : CloudNetProxy.getInstance().getCnsSystems().values())
        {
            if(cns.getProxys().containsKey(document.getString("proxy")))
            {
                ProxyServer proxyServer = cns.getProxys().get(document.getString("proxy"));
                ProxyInfo old = proxyServer.getProxyInfo();
                if(document.contains("onlinecount"))
                {
                    ProxyInfo proxyInfo = new ProxyInfo(
                            old.getName(),
                            old.getHost(),
                            old.getPort(),
                            old.getCloudId(),
                            old.getUniqueId(),
                            old.isOnline(),
                            old
                                    .getPlayers(),
                            old.getMemory(),
                            document.getInt("onlinecount"),
                            old.getFallback()
                    );
                    proxyServer.setProxyInfo(proxyInfo);
                }

                PlayerWhereAmI playerWhereAmI = document.getObject("pwai", PlayerWhereAmI.class);

                if(playerWhereAmI.getPlayerMetaData() == null)
                {
                    if(!CloudNetProxy.getInstance().getPlayerBackend().containsPlayer(playerWhereAmI.getUniqueId()))
                    {
                        CloudNetProxy.getInstance().getPlayerBackend()
                                .registerPlayer(playerWhereAmI, CloudNetProxy
                                        .getInstance()
                                .getPermissionPool());

                    }
                    playerWhereAmI.setPlayerMetaData(CloudNetProxy.getInstance().getPlayerBackend().getPlayer(playerWhereAmI.getUniqueId()));
                    playerWhereAmI.getPlayerMetaData().setName(playerWhereAmI.getName());

                    if((playerWhereAmI.getPlayerMetaData().getPermissionEntity().getTimeOut() != 0
                            && playerWhereAmI.getPlayerMetaData().getPermissionEntity().getTimeOut() <= System.currentTimeMillis()))
                    {
                        playerWhereAmI.getPlayerMetaData().setPermissionEntity(
                                CloudNetProxy.getInstance().getPermissionPool().getNewPermissionEntity(playerWhereAmI)
                        );
                    }
                    CloudNetProxy.getInstance().getPlayerBackend().updatePlayerMetaData(playerWhereAmI.getPlayerMetaData());

                }

                if(!CloudNetProxy.getInstance().getPermissionPool().getGroups().containsKey(playerWhereAmI.getPlayerMetaData().getPermissionEntity().getPermissionGroup()))
                {
                    playerWhereAmI.getPlayerMetaData().setPermissionEntity(
                            CloudNetProxy.getInstance().getPermissionPool().getNewPermissionEntity(playerWhereAmI)
                    );
                    CloudNetProxy.getInstance().getPlayerBackend().updatePermissionEntity(
                            playerWhereAmI.getUniqueId(),
                            playerWhereAmI.getPlayerMetaData().getPermissionEntity()
                    );
                }

                if(document.contains("update_group"))
                {
                    CloudNetProxy.getInstance().getPlayerBackend().updatePermissionEntity(
                            playerWhereAmI.getUniqueId(),
                            playerWhereAmI.getPlayerMetaData().getPermissionEntity()
                    );
                    playerWhereAmI.setPlayerMetaData(playerWhereAmI.getPlayerMetaData());
                }

                if(document.contains("update_playermeta"))
                {
                    CloudNetProxy.getInstance().getPlayerBackend().updatePlayerMetaData(playerWhereAmI.getPlayerMetaData());
                }

                proxyServer.getProxyInfo().getPlayers().put(playerWhereAmI.getUniqueId().toString(), playerWhereAmI);

                CloudNetProxy.getInstance().updateOnlineCount();
                return;
            }
        }
    }
}