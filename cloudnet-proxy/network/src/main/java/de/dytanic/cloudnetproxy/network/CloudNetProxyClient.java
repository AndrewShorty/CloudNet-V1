package de.dytanic.cloudnetproxy.network;

import com.google.gson.JsonParser;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.servergroup.ServerGroupMode;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.components.CNS;
import de.dytanic.cloudnetproxy.network.components.INetworkComponent;
import de.dytanic.cloudnetproxy.network.components.MinecraftServer;
import de.dytanic.cloudnetproxy.network.components.ProxyServer;
import de.dytanic.cloudnetproxy.network.packets.PacketOutUpdateNetwork;
import de.dytanic.cloudnetproxy.network.packets.PacketInUpdateProxyLayout;
import de.dytanic.cloudnetproxy.network.packets.PacketOutUpdateSignsSystem;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;

import java.io.IOException;

/**
 * Created by Tareko on 27.05.2017.
 */
@Getter
public class CloudNetProxyClient
            extends SimpleChannelInboundHandler{

    private static final JsonParser jsonParser = new JsonParser();

    private Channel channel;
    private INetworkComponent networkComponent;

    public CloudNetProxyClient(INetworkComponent iNetworkComponent, Channel channel)
    {
        this.networkComponent = iNetworkComponent;
        this.channel = channel;

        System.out.println("Channel connected [" + channel.remoteAddress().toString() + "/serverId=" + networkComponent.getServerId() + "]");
        if(networkComponent instanceof CNS)
        {
            System.out.println("CloudNetServer [" + networkComponent.getServerId() + "] is connected.");
        }

        if(networkComponent instanceof ProxyServer)
        {
            networkComponent.sendPacket(new PacketOutUpdateNetwork(CloudNetProxy.getInstance().getCloudNetwork(), PacketOutUpdateNetwork.UpdateType.COMPLETE_NET),
                    new PacketInUpdateProxyLayout(CloudNetProxy.getInstance().getProxyLayout()));
        }
        else
        {
            networkComponent.sendPacket(new PacketOutUpdateNetwork(CloudNetProxy.getInstance().getCloudNetwork(), PacketOutUpdateNetwork.UpdateType.COMPLETE_NET));
            if(networkComponent instanceof MinecraftServer)
            {
                MinecraftServer minecraftServer = (MinecraftServer)networkComponent;
                if(minecraftServer.getGroupMode().equals(ServerGroupMode.LOBBY))
                {
                    networkComponent.sendPacket(new PacketOutUpdateSignsSystem(
                            CloudNetProxy.getInstance().getSignBackend().signs(),
                            CloudNetProxy.getInstance().getSignGroupLayoutsBackend().load()
                    ));
                }

            }
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception
    {
        for(CloudNetProxyServer cloudNetProxyServer : CloudNetProxy.getInstance().getProxyServer())
        {
            cloudNetProxyServer.getProxyGroup().remove(channel);
            cloudNetProxyServer.getServerGroup().remove(channel);
            cloudNetProxyServer.getCnsGroup().remove(channel);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        this.channel = ctx.channel();

        if(networkComponent instanceof ProxyServer)
        {
            networkComponent.sendPacket(new PacketOutUpdateNetwork(CloudNetProxy.getInstance().getCloudNetwork(), PacketOutUpdateNetwork.UpdateType.COMPLETE_NET));
            networkComponent.sendPacket(new PacketInUpdateProxyLayout(CloudNetProxy.getInstance().getProxyLayout()));
        }
        else
        {
            networkComponent.sendPacket(new PacketOutUpdateNetwork(CloudNetProxy.getInstance().getCloudNetwork(), PacketOutUpdateNetwork.UpdateType.COMPLETE_NET));
            if(networkComponent instanceof MinecraftServer)
            {
                MinecraftServer minecraftServer = (MinecraftServer)networkComponent;
                if(minecraftServer.getGroupMode().equals(ServerGroupMode.LOBBY))
                {
                    networkComponent.sendPacket(new PacketOutUpdateSignsSystem(
                            CloudNetProxy.getInstance().getSignBackend().signs(),
                            CloudNetProxy.getInstance().getSignGroupLayoutsBackend().load()
                    ));
                }

            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        if((!channel.isActive() || !channel.isOpen() || !channel.isWritable()))
        {
            System.out.println("Channel disconnected [" + channel.remoteAddress().toString() + "/serverId=" + networkComponent.getServerId() + "]");
            ctx.close().syncUninterruptibly();
            if(networkComponent instanceof CNS)
            {
                ((CNS)networkComponent).disconnctFromThisCNS();
            }
            networkComponent.setChannel(null);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        if(!(cause instanceof IOException))
        {
            cause.printStackTrace();
        }
        //TODO:

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
    {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object obj) throws Exception
    {

        if(!(obj instanceof Packet)) return;

        Packet packet = (Packet)obj;

            Packet packet1 = CloudNetProxy.getInstance().getPacketPool().findHandler(packet.getId());
            if(packet1 != null)
            {
                packet1.handleInput(packet.getData(), networkComponent);
            }
    }
}
