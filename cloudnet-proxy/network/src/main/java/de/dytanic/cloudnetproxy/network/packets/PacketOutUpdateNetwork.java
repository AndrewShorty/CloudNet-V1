package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.CloudNetwork;
import de.dytanic.cloudnet.ServerLayout;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.CNSInfo;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import de.dytanic.cloudnet.servergroup.SimpleServerGroup;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.components.CNS;
import de.dytanic.cloudnetproxy.network.components.ProxyServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tareko on 28.05.2017.
 */
public class PacketOutUpdateNetwork
                extends Packet {

    public PacketOutUpdateNetwork(CloudNetwork cloudNetwork, UpdateType type)
    {
        super(201, new Document().append("cloudnetwork", cloudNetwork).append("type", type));

    }

    public PacketOutUpdateNetwork(ProxyServer proxy, UpdateType type)
    {
        super(201, new Document().append("type", type));
        if(type.equals(UpdateType.UPDATE_PROXY))
        this.data.append("proxy", proxy.getProxyInfo().toSimple());
        else
        this.data.append("proxy", proxy.getServerId());
    }

    public PacketOutUpdateNetwork(ServerInfo info, UpdateType type)
    {
        super(201, new Document().append("serverinfo", info).append("type", type));
    }

    public PacketOutUpdateNetwork(int info, java.util.Map<String, PlayerWhereAmI> players, UpdateType type)
    {
        super(201, new Document().append("onlinecount", info).append("players", players).append("type", type));
    }

    public PacketOutUpdateNetwork(java.util.Map<String, SimpleServerGroup> groups, UpdateType type)
    {
        super(201, new Document().append("groups", groups).append("type", type));

        List<CNSInfo> list = new ArrayList<>();
        for(CNS cns : CloudNetProxy.getInstance().getCnsSystems().values())
        {
            if(cns.getCnsInfo() != null)
            list.add(cns.getCnsInfo());
        }
        this.data.append("cns", list);
        this.data.append("whitelist", CloudNetProxy.getInstance().getWhitelist());

    }

    public PacketOutUpdateNetwork(ServerLayout serverLayout)
    {
        super(201, new Document().append("serverlayout", serverLayout).append("type", UpdateType.SERVER_LAYOUT));
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender) {}

    public enum UpdateType
    {
        COMPLETE_NET,
        UPDATE_SERVER,
        UPDATE_PROXY,
        REMOVE_PROXY,
        SERVER_LAYOUT,
        REMOVE_SERVER,
        ONLINE_COUNT,
        UPDATE_GROUPS
        ;
    }
}