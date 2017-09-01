package de.dytanic.cloudnet.api.network.packets;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.CloudNetwork;
import de.dytanic.cloudnet.ServerLayout;
import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.CNSInfo;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnet.network.SimpleProxyInfo;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import de.dytanic.cloudnet.servergroup.SimpleServerGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Tareko on 28.05.2017.
 */
public class PacketInUpdateNetwork
                extends Packet {

    //201
    public PacketInUpdateNetwork() {}


    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        switch (UpdateType.valueOf(document.getString("type")))
        {
            case COMPLETE_NET:
                CloudNetAPI.getInstance().setCloudNetwork(document.getObject("cloudnetwork", CloudNetwork.class));
                break;
            case REMOVE_PROXY:
                CloudNetAPI.getInstance().getCloudNetwork().getProxys().remove(document.getString("proxy"));
                break;
            case REMOVE_SERVER:
                CloudNetAPI.getInstance().getCloudNetwork().getServers().remove(document.getObject("serverinfo", ServerInfo.class).getName());
                break;
            case SERVER_LAYOUT:
                CloudNetAPI.getInstance().getCloudNetwork().setServerLayout(document.getObject("serverlayout", ServerLayout.class));
                break;
            case UPDATE_PROXY:
            {
                SimpleProxyInfo proxyInfo = document.getObject("proxy", SimpleProxyInfo.class);
                if(proxyInfo != null)
                CloudNetAPI.getInstance().getCloudNetwork().getProxys().put(proxyInfo.getName(), proxyInfo);
            }
                break;
            case UPDATE_SERVER:
                ServerInfo serverInfo = document.getObject("serverinfo", ServerInfo.class);
                CloudNetAPI.getInstance().getCloudNetwork().getServers().put(serverInfo.getName(), serverInfo);
                break;
            case ONLINE_COUNT:
            {
                CloudNetAPI.getInstance().getCloudNetwork().setOnlineCount(document.getInt("onlinecount"));

                java.util.Map<String, PlayerWhereAmI> playerWhereAmIHashMap = document.getObject("players", new TypeToken<Map<String, PlayerWhereAmI>>(){}.getType());
                CloudNetAPI.getInstance().getCloudNetwork().setOnlinePlayers(playerWhereAmIHashMap);
            }
                break;
            case UPDATE_GROUPS:
                CloudNetAPI.getInstance().getCloudNetwork().setGroups(document.getObject("groups", new TypeToken<java.util.Map<String, SimpleServerGroup>>(){}.getType()));
                CloudNetAPI.getInstance().getCloudNetwork().setCloudNetServers(document.getObject("cns", new TypeToken<ArrayList<CNSInfo>>(){}.getType()));
                CloudNetAPI.getInstance().getCloudNetwork().setIpwhitelist(document.getObject("whitelist", new TypeToken<ArrayList<String>>(){}.getType()));
                break;
        }
    }

    public enum UpdateType
    {
        COMPLETE_NET,
        UPDATE_SERVER,
        UPDATE_PROXY,
        REMOVE_PROXY,
        SERVER_LAYOUT,
        REMOVE_SERVER,
        ONLINE_COUNT,
        UPDATE_GROUPS;
        ;
    }
}