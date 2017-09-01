package de.dytanic.cloudnet.network;

import com.google.gson.Gson;
import de.dytanic.cloudnet.CloudNetwork;
import de.dytanic.cloudnet.ServerLayout;
import de.dytanic.cloudnet.lib.Acceptable;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.permission.PermissionPool;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Tareko on 24.05.2017.
 */
public final class NetworkUtils {

    private NetworkUtils() {}

    public static final boolean EPOLL = Epoll.isAvailable();

    public static final Gson GSON = new Gson();

    public static Class<? extends SocketChannel> socketChannel()
    {
        return EPOLL ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public static String getHostName()
    {
        try
        {
            return InetAddress.getLocalHost().getHostAddress();
        }catch (Exception ex)
        {
            return "127.0.0.1";
        }
    }

    public static CloudNetwork cloudNetwork()
    {
        return new CloudNetwork(new HashMap<>(), new HashMap<>(), 0, new HashMap<>(),
                new HashMap<>(), new ArrayList<>(), new PermissionPool(), new ServerLayout(
                "&cYou don't have permission to enter this server!",
                "&eYou will send to the server %server%!", true), Arrays.asList("127.0.0.1", "127.0.1.1"));
    }

    public static Class<? extends ServerSocketChannel> serverSocketChannel()
    {
        return EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static EventLoopGroup eventLoopGroup()
    {
        return EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }

    public static EventLoopGroup eventLoopGroup(int threads)
    {
        return EPOLL ? new EpollEventLoopGroup(threads) : new NioEventLoopGroup(threads);
    }

    public static EventLoopGroup eventLoopGroup(ThreadFactory threadFactory)
    {
        return EPOLL ? new EpollEventLoopGroup(0, threadFactory) : new NioEventLoopGroup(0, threadFactory);
    }

    public static EventLoopGroup eventLoopGroup(int threads, ThreadFactory threadFactory)
    {
        return EPOLL ? new EpollEventLoopGroup(threads, threadFactory) : new NioEventLoopGroup(threads, threadFactory);
    }

    public static <T> void addAll(Collection<T> key, Collection<T> value)
    {
        for(T k : value)
        {
            key.add(k);
        }
    }

    public static <T, V> void addAll(java.util.Map<T, V> key, java.util.Map<T, V> value)
    {
        for(T key_ : value.keySet())
        {
            key.put(key_, value.get(key_));
        }
    }

    public static void addAll(Document key, Document value)
    {
        for(String keys : value.keys())
        {
            key.append(keys, value.get(keys));
        }
    }

    public static boolean checkIsNumber(String input)
    {
        try{
            Integer.parseInt(input);
            return true;
        }catch (Exception ex)
        {
            return false;
        }
    }

    public static <T, V> void addAll(java.util.Map<T, V> key, java.util.Map<T, V> value, Acceptable<V> handle)
    {
        for (T key_ : value.keySet())
        {
            if (handle.isAccepted(value.get(key_))) key.put(key_, value.get(key_));
        }
    }

    public static String header(short i)
    {
        return "\n" +
                "      " + "   ___  _                    _      __        _   \n" +
                "      " + "  / __\\| |  ___   _   _   __| |  /\\ \\ \\  ___ | |_ \n" +
                "      " + " / /   | | / _ \\ | | | | / _` | /  \\/ / / _ \\| __|\n" +
                "      " + "/ /___ | || (_) || |_| || (_| |/ /\\  / |  __/| |_ \n" +
                "      " + "\\____/ |_| \\___/  \\__,_| \\__,_|\\_\\ \\/   \\___| \\__|\n" +
                "      " + "_______________________________________________________\n\n" +
                "      " + "By Tarek H. 2017 | Software: " + (i == 0 ? "CloudNet-ProxyDB" : (i == 1 ? "CloudNet-Server" : "CloudNet-API")) + " " + NetworkUtils.class.getPackage().getImplementationVersion() + "-" + NetworkUtils.class.getPackage().getSpecificationVersion() + "\n" +
                "      " + "Discord Support: https://discord.gg/MghDhjM\n";
    }

    public static String headerOut(short i)
    {
        return
                        "      " + "_______________________________________________________\n\n" +
                        "      " + "By Tarek H. 2017 | Software: " + (i == 0 ? "CloudNet-ProxyDB" : (i == 1 ? "CloudNet-Server" : "CloudNet-API")) + " " + NetworkUtils.class.getPackage().getImplementationVersion() + "-" + NetworkUtils.class.getPackage().getSpecificationVersion() + "\n" +
                        "      " + "Discord Support: https://discord.gg/MghDhjM\n";
    }

}