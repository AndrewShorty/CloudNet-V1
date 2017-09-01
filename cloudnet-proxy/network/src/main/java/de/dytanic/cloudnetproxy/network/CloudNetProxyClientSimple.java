package de.dytanic.cloudnetproxy.network;

import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 02.06.2017.
 */
@Getter
@AllArgsConstructor
public class CloudNetProxyClientSimple
            extends SimpleChannelInboundHandler<Packet>
            implements PacketSender{

    private Channel channel;
    private CloudNetProxyServer cloudNetProxyServer;

    @Override
    public String getName()
    {
        return "Unknown-Connection";
    }

    @Override
    public void sendPacket(Packet... packets)
    {
        if(channel != null)
        {
            for(Packet packet : packets)
            {
                sendPacket(packet);
            }
        }
    }

    @Override
    public void sendPacket(Packet packet)
    {
        if(channel != null)
        {
            if(channel.eventLoop().inEventLoop())
            {
                channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
            else
            {
                channel.eventLoop().execute(new Runnable() {
                    @Override
                    public void run()
                    {
                        channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                    }
                });
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        if((!channel.isActive() || !channel.isOpen() || !channel.isWritable()))
        {
            channel.close().syncUninterruptibly();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception
    {
        if(packet.getId() == 2)
        {
            Packet readablePacket = CloudNetProxy.getInstance().getPacketPool().findHandler(packet.getId());
            if(readablePacket != null)
            {
                readablePacket.handleInput(packet.getData(), this);
            }
        }
    }
}
