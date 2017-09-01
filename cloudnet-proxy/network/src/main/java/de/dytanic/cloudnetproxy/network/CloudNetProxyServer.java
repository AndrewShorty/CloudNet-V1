package de.dytanic.cloudnetproxy.network;

import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnet.network.packet.*;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.components.CNS;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;

import java.util.List;

/**
 * Created by Tareko on 26.05.2017.
 */
@Getter
public class CloudNetProxyServer
        extends ChannelInitializer<Channel> {

    private SslContext sslContext;
    private boolean binded = false;
    private ChannelFuture channelFuture;

    private ChannelGroup
            proxyGroup = new DefaultChannelGroup("proxyGroup", GlobalEventExecutor.INSTANCE),
            serverGroup = new DefaultChannelGroup("serverGroup", GlobalEventExecutor.INSTANCE),
            cnsGroup = new DefaultChannelGroup("cnsGroup", GlobalEventExecutor.INSTANCE);

    private EventLoopGroup workerGroup = NetworkUtils.eventLoopGroup(), bossGroup = NetworkUtils.eventLoopGroup();

    public CloudNetProxyServer(List<String> arguments, String hostName, int port)
    {

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
        try
        {


            if (arguments.contains("--ssl"))
            {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslContext = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
            }
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .option(ChannelOption.IP_TOS, 24)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.AUTO_READ, true)

                    .channel(NetworkUtils.serverSocketChannel())

                    .childOption(ChannelOption.IP_TOS, 24)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.AUTO_READ, true)
                    .childHandler(this);

            System.out.println("Binding to " + CloudNetProxy.getInstance().getConfig().getConfiguration().getString("hostName") + ":" + port + "...");
            channelFuture = serverBootstrap.bind(CloudNetProxy.getInstance().getConfig().getConfiguration().getString("hostName"), port).sync();

            System.out.println("Proxy is starting up and now online!");
            System.out.println("This Proxy is listning on " + CloudNetProxy.getInstance().getConfig().getConfiguration().getString("hostName") + ":" + port);
            binded = true;

            CloudNetProxy.getInstance().getProxyServer().add(this);

            for (CNS cns : CloudNetProxy.getInstance().getCnsSystems().values())
            {
                System.out.println("Waiting for [" + cns.getName() + "] /" + cns.getNetworkInfo().getHostName() + " ...");
            }

            CloudNetProxy.getInstance().getProxyServer().add(this);

            channelFuture.channel().closeFuture().sync();

        } catch (Exception ex)
        {

            ex.printStackTrace();
            binded = false;
            System.exit(0);

        } finally
        {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void sendAllPacket(Packet packet)
    {
        CloudNetProxy.getInstance().getScheduler().getExecutorService().execute(new Runnable() {
            @Override
            public void run()
            {
                for (Channel channel : proxyGroup)
                {
                    if (!channel.eventLoop().inEventLoop())
                    {
                        channel.eventLoop().execute(new Runnable() {
                            @Override
                            public void run()
                            {
                                channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                            }
                        });
                    } else
                    {
                        channel.writeAndFlush(packet);
                    }
                }
                for (Channel channel : serverGroup)
                {
                    if (!channel.eventLoop().inEventLoop())
                    {
                        channel.eventLoop().execute(new Runnable() {
                            @Override
                            public void run()
                            {
                                channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                            }
                        });
                    } else
                    {
                        channel.writeAndFlush(packet);
                    }
                }
                for (Channel channel : cnsGroup)
                {
                    if (!channel.eventLoop().inEventLoop())
                    {
                        channel.eventLoop().execute(new Runnable() {
                            @Override
                            public void run()
                            {
                                channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                            }
                        });
                    } else
                    {
                        channel.writeAndFlush(packet);
                    }
                }
            }
        });
    }

    public void sendProxyPacket(Packet packet)
    {
        if (proxyGroup.size() != 0)
            for(Channel channel : proxyGroup)
            {
                if (!channel.eventLoop().inEventLoop())
                {
                    channel.eventLoop().execute(() ->
                    {
                        channel.writeAndFlush(packet);
                    });
                } else
                {
                    channel.writeAndFlush(packet);
                }
            }
    }

    @Override
    protected void initChannel(Channel channel) throws Exception
    {
        System.out.println("Channel [" + channel.remoteAddress().toString() + "] connecting...");

        String[] address = channel.remoteAddress().toString().split(":");
        if (!CloudNetProxy.getInstance().getWhitelist().contains(address[0].replaceFirst("/", "")))
        {
            channel.close().syncUninterruptibly();
            return;
        }

        if (sslContext != null)
        {
            channel.pipeline().addLast(sslContext.newHandler(channel.alloc()));
        }

        channel
                .pipeline().addLast(
                new PacketLengthDeserializer(),
                new PacketInDecoder(),
                new PacketLengthSerializer(),
                new PacketOutEncoder())
        ;
        channel.pipeline().addLast("client", new CloudNetProxyClientSimple(channel, this));

    }
}