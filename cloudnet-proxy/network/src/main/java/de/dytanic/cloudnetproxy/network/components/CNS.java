package de.dytanic.cloudnetproxy.network.components;

import de.dytanic.cloudnet.network.CNSInfo;
import de.dytanic.cloudnet.servergroup.ServerGroup;
import de.dytanic.cloudnet.servergroup.SimpleServerGroup;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.CloudNetProxyServer;
import de.dytanic.cloudnetproxy.network.NetworkInfo;
import de.dytanic.cloudnetproxy.network.packets.PacketOutUpdateNetwork;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tareko on 26.05.2017.
 */
@Getter
public final class CNS
        implements INetworkComponent {

    @Setter
    private Channel channel;
    @Setter
    private CNSInfo cnsInfo;

    private NetworkInfo networkInfo;

    private final java.util.Map<String, ProxyServer> proxys = new ConcurrentHashMap<>();
    private final java.util.Map<String, MinecraftServer> servers = new ConcurrentHashMap<>();
    private final java.util.Map<String, ServerGroup> groups = new ConcurrentHashMap<>();

    @Setter
    private int maxMemory = 0;

    private String serverId;

    public CNS(NetworkInfo networkInfo)
    {
        this.serverId = networkInfo.getServerId();
        this.networkInfo = networkInfo;
    }

    @Override
    public String getName()
    {
        return serverId;
    }

    @Override
    public CNS getCloudNetServer()
    {
        return this;
    }

    public int getUsedMemory()
    {
        int mem = 0;
        for (ProxyServer proxyServer : proxys.values())
        {
            mem = mem + proxyServer.getProxyInfo().getMemory();
        }

        for (MinecraftServer proxyServer : servers.values())
        {
            mem = mem + proxyServer.getServerInfo().getMemory();
        }
        return mem;
    }

    public void disconnctFromThisCNS()
    {
        this.groups.clear();
        this.cnsInfo = null;
        for (MinecraftServer minecraftServer : servers.values())
        {
            minecraftServer.disconnect();
        }

        for (ProxyServer minecraftServer : proxys.values())
        {
            minecraftServer.disconnect();
        }

        java.util.Map<String, SimpleServerGroup> groups = new HashMap<>();
        for (CNS cns_ : CloudNetProxy.getInstance().getCnsSystems().values())
        {
            for (ServerGroup group : cns_.getGroups().values())
            {
                groups.put(group.getName(), group.toSimple());
            }
        }

        servers.clear();
        proxys.clear();

        CloudNetProxy.getInstance().updateNetwork(false);
        for(CloudNetProxyServer cloudNetProxyServer : CloudNetProxy.getInstance().getProxyServer())
                cloudNetProxyServer.sendAllPacket(new PacketOutUpdateNetwork(groups, PacketOutUpdateNetwork.UpdateType.UPDATE_GROUPS));
    }

    public void stopServer(MinecraftServer minecraftServer)
    {
        
    }

}