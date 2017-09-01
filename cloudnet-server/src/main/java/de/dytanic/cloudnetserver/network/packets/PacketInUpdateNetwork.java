package de.dytanic.cloudnetserver.network.packets;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.CloudNetwork;
import de.dytanic.cloudnet.ServerLayout;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnet.network.SimpleProxyInfo;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnet.player.PlayerWhereAmI;
import de.dytanic.cloudnet.servergroup.ServerState;
import de.dytanic.cloudnet.servergroup.SimpleServerGroup;
import de.dytanic.cloudnetserver.CloudNetServer;

import java.util.Map;

/**
 * Created by Tareko on 28.05.2017.
 */
public class PacketInUpdateNetwork
                extends Packet {

    public PacketInUpdateNetwork() {}

    @Override
    public void handleInput(Document document, PacketSender sender)
    {
        switch (UpdateType.valueOf(document.getString("type")))
        {
            case UPDATE_SERVER:
            {
                ServerInfo serverInfo = document.getObject("serverinfo", ServerInfo.class);

                if(CloudNetServer.getInstance().getServers().containsKey(serverInfo.getName()) &&
                        CloudNetServer.getInstance().getCloudNetwork().getServers().containsKey(serverInfo.getName())
                        && CloudNetServer.getInstance().getCloudNetwork().getServers().get(serverInfo.getName()).getServerState().equals(ServerState.OFFLINE) &&
                        serverInfo.getServerState().equals(ServerState.LOBBY))
                {
                    System.out.println(CloudNetServer.getInstance().getMessages().getProperty("onlineServer").replace("%server%",
                            CloudNetServer.getInstance().getServers().get(serverInfo.getName()).toString()));
                }

                CloudNetServer.getInstance().getCloudNetwork().getServers().put(serverInfo.getName(), serverInfo);

            }
                break;
            case UPDATE_PROXY:


            {
                SimpleProxyInfo proxyInfo = document.getObject("proxy", SimpleProxyInfo.class);
                if(CloudNetServer.getInstance().getCloudNetwork().getProxys().containsKey(proxyInfo.getName())
                        && CloudNetServer.getInstance().getProxys().containsKey(proxyInfo.getName()) && (
                                !CloudNetServer.getInstance().getCloudNetwork().getProxys().get(proxyInfo.getName()).isOnline() &&
                                        proxyInfo.isOnline()
                        ))
                {
                        System.out.println(CloudNetServer.getInstance().getMessages().getProperty("onlineProxy").replace("%proxy%",
                                CloudNetServer.getInstance().getProxys().get(proxyInfo.getName()).toString()));
                }
                CloudNetServer.getInstance().getCloudNetwork().getProxys().put(proxyInfo.getName(), proxyInfo);

            }
                break;
            case REMOVE_SERVER:
                ServerInfo serverInfo = document.getObject("serverinfo", ServerInfo.class);
                CloudNetServer.getInstance().getCloudNetwork().getServers().remove(serverInfo.getName());
                break;
            case REMOVE_PROXY:
                CloudNetServer.getInstance().getCloudNetwork().getProxys().remove(document.getString("proxy"));
                break;
            case COMPLETE_NET:
                CloudNetServer.getInstance().setCloudNetwork(document.getObject("cloudnetwork", CloudNetwork.class));
                break;
            case ONLINE_COUNT:
                CloudNetServer.getInstance().getCloudNetwork().setOnlineCount(document.getInt("onlinecount"));

                Map<String, PlayerWhereAmI> playerWhereAmIHashMap = document.getObject("players", new TypeToken<Map<String, PlayerWhereAmI>>(){}.getType());
                CloudNetServer.getInstance().getCloudNetwork().setOnlinePlayers(playerWhereAmIHashMap);
                break;
            case SERVER_LAYOUT:
                CloudNetServer.getInstance().getCloudNetwork().setServerLayout(document.getObject("serverlayout", ServerLayout.class));
                break;
            case UPDATE_GROUPS:
                CloudNetServer.getInstance().getCloudNetwork().setGroups(document.getObject("groups", new TypeToken<Map<String, SimpleServerGroup>>(){}.getType()));
                break;
        }
    }

    public enum UpdateType
    {
        COMPLETE_NET,
        UPDATE_SERVER,
        UPDATE_PROXY,
        SERVER_LAYOUT,
        REMOVE_PROXY,
        REMOVE_SERVER,
        ONLINE_COUNT,
        UPDATE_GROUPS,
        ;
    }
}