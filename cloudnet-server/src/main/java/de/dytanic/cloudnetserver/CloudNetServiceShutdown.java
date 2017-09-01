package de.dytanic.cloudnetserver;

import de.dytanic.cloudnet.logging.CloudNetLogging;
import de.dytanic.cloudnetserver.network.NetworkConnector;
import de.dytanic.cloudnetserver.server.MinecraftServer;
import de.dytanic.cloudnetserver.server.ProxyServer;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Created by Tareko on 23.05.2017.
 */
public final class CloudNetServiceShutdown
            extends Thread {

    private final CloudNetLogging logging;

    public CloudNetServiceShutdown(CloudNetLogging logging)
    {
        this.logging = logging;
    }

    @Override
    public void run()
    {
        if(CloudNetServer.IS_RUNNING) CloudNetServer.IS_RUNNING = false;

        if(CloudNetServer.getInstance() != null)
        {
            CloudNetServer.getInstance().getScheduler().cancelAllTasks();
            CloudNetServer.getInstance().getStopServerScheduler().cancelAllTasks();
            CloudNetServer.getInstance().getServerPriorityScheduler().cancelAllTasks();

            for(ProxyServer proxyServer : CloudNetServer.getInstance().getProxys().values())
            {
                proxyServer.shutdown();
            }

            for(MinecraftServer minecraftServer : CloudNetServer.getInstance().getServers().values())
            {
                minecraftServer.shutdown();
            }

            CloudNetServer.getInstance().getScreenSystem().shutdown();

            if(NetworkConnector.getInstance() != null)
            {

                if(NetworkConnector.getInstance().getChannel().isOpen())
                NetworkConnector.getInstance().getChannel().close().syncUninterruptibly();
                NetworkConnector.getInstance().getWorkerLoop().shutdownGracefully();

            }

        }
        try{ FileUtils.deleteDirectory(new File("tmp")); }catch (Exception ex) {}

        System.out.println("Thanks for using this software :)");

        logging.shutdownAll();

    }
}