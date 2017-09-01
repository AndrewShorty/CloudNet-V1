package de.dytanic.cloudnetproxy.network.components;

import de.dytanic.cloudnet.network.ProxyInfo;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.CloudNetProxyServer;
import de.dytanic.cloudnetproxy.network.NetworkInfo;
import de.dytanic.cloudnetproxy.utils.NullProxyInfo;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Created by Tareko on 26.05.2017.
 */
@Getter
public class ProxyServer
        implements INetworkComponent {

    private String serverId;
    private UUID uniqueId;
    private CNS cloudNetServer;
    private NetworkInfo networkInfo;
    private NetworkInfo connectionInfo;

    @Setter
    private Channel channel;
    @Setter
    private ProxyInfo proxyInfo;

    public ProxyServer(CNS cns, UUID uuid, NetworkInfo networkInfo, NetworkInfo connectionInfo, String fallback, int memory)
    {
        this.cloudNetServer = cns;
        this.serverId = networkInfo.getServerId();
        this.uniqueId = uuid;

        this.networkInfo = networkInfo;
        this.connectionInfo = connectionInfo;

        this.proxyInfo = new NullProxyInfo(serverId, networkInfo.getHostName(), networkInfo.getPort(), cns.getServerId(), uuid, memory, fallback);
    }

    public void disconnect()
    {
        if (this.channel != null)
        {
            for(CloudNetProxyServer proxyServer : CloudNetProxy.getInstance().getProxyServer())
                proxyServer.getProxyGroup().remove(channel);
            this.channel.close().syncUninterruptibly();
        }
    }

    @Override
    public String getName()
    {
        return serverId;
    }
}