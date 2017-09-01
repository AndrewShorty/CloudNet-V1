package de.dytanic.cloudnetserver.network;

import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnet.network.packet.*;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.network.packets.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.ResourceLeakDetector;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Properties;

/**
 * Created by Tareko on 24.05.2017.
 */
@Getter
public class NetworkConnector
        implements PacketSender {

    @Getter
    private static NetworkConnector instance;

    private Channel channel;
    private SslContext sslContext;

    private String proxyHostName;
    private Integer proxyPort;
    private PacketPool packetPool = PacketPool.newSimpledPacketPool();

    private final EventLoopGroup workerLoop = NetworkUtils.eventLoopGroup(2);

    public NetworkConnector(List<String> arguments, Properties cnscProperties, Properties messages)
    {
        a();
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

        boolean error = false;
        try
        {
            if (arguments.contains("--ssl"))
            {
                sslContext = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
            }

            Bootstrap bootstrap = new Bootstrap()
                    .group(workerLoop)
                    .option(ChannelOption.AUTO_READ, true)
                    .option(ChannelOption.IP_TOS, 24)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .channel(NetworkUtils.socketChannel())
                    .handler(new NetworkConnectorChannelInit());

            proxyHostName = cnscProperties.getProperty("proxy-host");
            proxyPort = Integer.parseInt(cnscProperties.getProperty("proxy-port"));
            System.out.println(messages.getProperty("connecting").replace("%host%", proxyHostName).replace("%port%", proxyPort + ""));

            channel = bootstrap.connect(
                    new InetSocketAddress(proxyHostName, proxyPort))
                    .sync().channel()
                    .writeAndFlush(new PacketOutAuth(cnscProperties.getProperty("cnsId"), cnscProperties.getProperty("servicekey"))).sync().channel()
                    .writeAndFlush(new PacketOutServerData()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE).sync().channel();

            System.out.println(messages.getProperty("connectionSuc"));

            instance = this;
        } catch (Exception ex)
        {
            error = true;
            ex.printStackTrace();
        } finally
        {
            if (error)
            {
                workerLoop.shutdownGracefully().syncUninterruptibly();
                System.exit(0);
            }
        }

    }

    private void a()
    {
        packetPool
                //OutIn
                .append(0, PacketInKeepAlive.class)
                .append(1, PacketIOPing.class)
                //In
                .append(201, PacketInUpdateNetwork.class)
                .append(206, PacketInUpdatePlayerWhereAmI.class)
                .append(207, PacketInRemovePlayerWhereAmI.class)
                .append(208, PacketInUpdatePermissionPool.class)
                .append(209, PacketInHandleStartAndStop.class)
                .append(216, PacketInManageGroups.class)
                .append(219, PacketInCNSCommand.class);
    }

    @Override
    public void sendPacket(Packet packet)
    {
        if (channel != null)
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
    }

    @Override
    public void sendPacket(Packet... packets)
    {
        if (channel != null)
        {
            for (Packet packet : packets)
            {
                sendPacket(packet);
            }
        }
    }

    @Override
    public String getName()
    {
        return "CloudNet-Server NetworkConnector";
    }

    private class NetworkConnectorChannelHandler
            extends SimpleChannelInboundHandler<Packet> {

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
        {
            ctx.flush();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception
        {
            if(channel != null)
            {
                if ((!channel.isActive() || !channel.isOpen() || !channel.isWritable()))
                {
                    System.out.println(CloudNetServer.getInstance().getMessages().getProperty("connectionInactive"));
                    channel.unsafe().closeForcibly();

                    if (CloudNetServer.IS_RUNNING)
                        System.exit(0);
                }
            }
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
                packet1.handleInput(packet.getData(), NetworkConnector.this);
            }
        }

    }

    private class NetworkConnectorChannelInit
            extends ChannelInitializer<Channel> {

        @Override
        public void initChannel(Channel socketChannel) throws Exception
        {
            if (sslContext != null)
            {
                socketChannel.pipeline()
                        .addLast(sslContext.newHandler(socketChannel.alloc(), proxyHostName, proxyPort));
            }

            socketChannel.pipeline()
                    .addLast(
                            new PacketLengthDeserializer(),
                            new PacketInDecoder(),
                            new PacketLengthSerializer(),
                            new PacketOutEncoder(),
                            new NetworkConnectorChannelHandler()
                    );
        }
    }
}