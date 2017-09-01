package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.ProxyInfo;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.components.CNS;
import de.dytanic.cloudnetproxy.network.components.ProxyServer;

/**
 * Created by Tareko on 01.06.2017.
 */
public class PacketIORemovePlayerWhereAmI
                extends Packet {

    public PacketIORemovePlayerWhereAmI()
    {
        super(null);
    }

    private PacketIORemovePlayerWhereAmI(Document data)
    {
        super(data);
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
                proxyServer
                        .getProxyInfo().getPlayers().remove(document.getString("uuid"));
                CloudNetProxy.getInstance().updateOnlineCount();
                return;
            }
        }
    }
}
