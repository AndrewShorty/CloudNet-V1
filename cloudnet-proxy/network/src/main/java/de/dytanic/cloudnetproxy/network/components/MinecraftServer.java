package de.dytanic.cloudnetproxy.network.components;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.ServerInfo;
import de.dytanic.cloudnet.servergroup.ServerGroupProfile;
import de.dytanic.cloudnet.servergroup.ServerMap;
import de.dytanic.cloudnet.servergroup.ServerGroup;
import de.dytanic.cloudnet.servergroup.ServerGroupMode;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.CloudNetProxyServer;
import de.dytanic.cloudnetproxy.network.NetworkInfo;
import de.dytanic.cloudnetproxy.utils.NullServerInfo;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Created by Tareko on 26.05.2017.
 */
@Getter
public final class MinecraftServer
        implements INetworkComponent {

    private String serverId;
    private UUID uniqueId;
    private NetworkInfo networkInfo;
    private ServerGroup serverGroup;
    private CNS cloudNetServer;
    private Document properties;
    private NetworkInfo connectionInfo;
    private ServerGroupMode groupMode;

    @Setter
    private ServerInfo serverInfo;
    @Setter
    private Channel channel;
    @Setter
    private long updatePacketTime;

    public MinecraftServer(

            CNS cnsSystem,
            ServerGroup group,
            UUID uniqueId, NetworkInfo networkInfo, NetworkInfo connectionInfo, Document properties, ServerGroupMode groupMode, ServerMap serverMap, ServerGroupProfile profile, boolean hide)
    {
        this.serverId = networkInfo.getServerId();
        this.cloudNetServer = cnsSystem;
        this.serverGroup = group;
        this.uniqueId = uniqueId;
        this.networkInfo = networkInfo;
        this.connectionInfo = connectionInfo;
        this.properties = properties;
        this.groupMode = groupMode;
        this.updatePacketTime = 0;

        this.serverInfo = new NullServerInfo(
                serverId, group.getName(),
                networkInfo.getHostName(),
                networkInfo.getPort(),
                cnsSystem.getServerId(),
                uniqueId,
                serverMap, profile, hide);
    }

    public void disconnect()
    {
        if (this.channel != null)
        {
            for(CloudNetProxyServer proxyServer : CloudNetProxy.getInstance().getProxyServer())
                proxyServer.getServerGroup().remove(channel);
            this.channel.close().syncUninterruptibly();
        }
    }

    @Override
    public String getName()
    {
        return serverId;
    }
}