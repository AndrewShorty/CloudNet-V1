package de.dytanic.cloudnet.api.network;

import de.dytanic.cloudnet.api.network.packets.PacketOutKeepAlive;
import de.dytanic.cloudnet.lib.threading.Scheduler;
import de.dytanic.cloudnet.network.*;
import de.dytanic.cloudnet.network.packet.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.base64.Base64Decoder;
import io.netty.handler.codec.base64.Base64Encoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.Getter;

import java.net.InetSocketAddress;

/**
 * Created by Tareko on 28.05.2017.
 */
@Getter
public class CloudNetConnector
        extends ChannelInitializer<SocketChannel> implements PacketSender, Runnable {

    private String remoteHost;
    private int portBind;

    private SslContext sslContext;
    private Channel channel;
    private boolean connected = false;
    private PacketPool packetPool;
    private PacketHandleProcessor.PacketHandlerAbstract packetHandlerAbstract;
    private Runnable shutdown;

    private EventLoopGroup workerLoop = NetworkUtils.eventLoopGroup(2);

    public CloudNetConnector(Scheduler scheduler, boolean ssl, PacketPool packetPool,
                             String connectionRemote, int portBind, PacketHandleProcessor.PacketHandlerAbstract packetHandlerAbstract, Runnable shutdown)
    {
        this.remoteHost = connectionRemote;
        this.portBind = portBind;
        this.packetPool = packetPool;
        this.packetHandlerAbstract = packetHandlerAbstract;
        this.shutdown = shutdown;

        boolean value = false;
        try
        {
            if (ssl) sslContext = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);

            Bootstrap bootstrap = new Bootstrap()
                    .group(workerLoop)
                    .option(ChannelOption.AUTO_READ, true)
                    .option(ChannelOption.IP_TOS, 24)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .channel(NetworkUtils.socketChannel())
                    .handler(this);

            channel = bootstrap
                    .connect(new InetSocketAddress(connectionRemote.split(":")[0],
                            Integer.valueOf(connectionRemote.split(":")[1]))).sync().channel();

            connected = true;

        } catch (Exception ex)
        {
            ex.printStackTrace();
            value = true;
        } finally
        {
            if (value)
                workerLoop.shutdownGracefully();
        }
    }

    @Override
    public void run()
    {
        sendPacket(new PacketOutKeepAlive());
    }

    @Override
    public String getName()
    {
        return "CloudNet-Connector CoreAPI";
    }

    @Override
    public void sendPacket(Packet... packets)
    {
        for (Packet packet : packets)
        {
            sendPacket(packet);
        }
    }

    @Override
    public void sendPacket(Packet packet)
    {
        if (!channel.eventLoop().inEventLoop())
        {
            channel.eventLoop().execute(new Runnable() {
                @Override
                public void run()
                {
                    channel.writeAndFlush(packet);
                }
            });
        } else
        {
            channel.writeAndFlush(packet);
        }
    }

    public void sendPacketSync(Packet packet)
    {
        if (!channel.eventLoop().inEventLoop())
        {
            channel.eventLoop().execute(new Runnable() {
                @Override
                public void run()
                {
                    channel.writeAndFlush(packet).syncUninterruptibly();
                }
            });
        } else
        {
            channel.writeAndFlush(packet).syncUninterruptibly();
        }
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception
    {

        if (sslContext != null)
        {
            socketChannel.pipeline().addFirst(sslContext.newHandler(socketChannel.alloc(), remoteHost.split(":")[0], Integer.valueOf(remoteHost.split(":")[1])));
        }
        socketChannel.pipeline()
                .addLast(
                        new PacketLengthDeserializer(),
                        new PacketInDecoder(),
                        new PacketLengthSerializer(),
                        new PacketOutEncoder(),
                        new CloudNetConnectorChannelHandler()
                );
    }

    private class CloudNetConnectorChannelHandler
            extends SimpleChannelInboundHandler<Packet> {

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception
        {
            if (!ctx.channel().isOpen() || !ctx.channel().isWritable() || !ctx.channel().isActive())
            {
                ctx.close();
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
        {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
        {
            cause.printStackTrace();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception
        {
            Packet packet1 = packetPool.findHandler(packet.getId());
            if (packet1 != null)
            {
                packet1.handleInput(packet.getData(), CloudNetConnector.this);
            }

            if (packetHandlerAbstract != null)
            {
                packetHandlerAbstract.handleIncomingPacket(packet, CloudNetConnector.this);
            }

        }
    }

}
