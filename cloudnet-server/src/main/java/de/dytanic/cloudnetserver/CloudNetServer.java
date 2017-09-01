package de.dytanic.cloudnetserver;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.CloudNetwork;
import de.dytanic.cloudnet.Version;
import de.dytanic.cloudnet.lib.Return;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.lib.interfaces.Executeable;
import de.dytanic.cloudnet.lib.threading.Runnabled;
import de.dytanic.cloudnet.lib.threading.Scheduler;
import de.dytanic.cloudnet.logging.CloudNetLogging;
import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnet.servergroup.ServerGroup;
import de.dytanic.cloudnet.servergroup.ServerGroupMode;
import de.dytanic.cloudnet.servergroup.ServerGroupType;
import de.dytanic.cloudnetserver.network.NetworkConnector;
import de.dytanic.cloudnetserver.server.*;
import de.dytanic.cloudnetserver.server.handlers.GroupPriorityStartup;
import de.dytanic.cloudnetserver.server.handlers.GlobalPriorityStartup;
import de.dytanic.cloudnetserver.server.screen.ScreenService;
import de.dytanic.cloudnetserver.setup.SetupProxyVersion;
import de.dytanic.cloudnetserver.setup.SetupServerGroup;
import de.dytanic.cloudnetserver.setup.SetupSpigotVersion;
import de.dytanic.cloudnetserver.util.ProxyConfig;
import de.dytanic.cloudnetserver.util.Utils;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tareko on 21.05.2017.
 */
@Getter
public final class CloudNetServer
        implements Executeable, Runnabled<CloudNetServer>, Runnable {

    public static volatile boolean IS_RUNNING = false;

    @Getter
    private static CloudNetServer instance;

    //Normal instances
    private final Scheduler scheduler = new Scheduler();
    private final Scheduler serverPriorityScheduler = new Scheduler();
    private final Scheduler stopServerScheduler = new Scheduler();

    private final Version version = new Version(CloudNetServer.class.getPackage().getImplementationVersion());

    private final Map<String, MinecraftServer> servers = new ConcurrentHashMap<>();
    private final Map<String, ProxyServer> proxys = new ConcurrentHashMap<>();
    private final Map<String, ServerGroup> groups = new ConcurrentHashMap<>();

    //OTHERS
    @Setter
    private ProxyConfig proxyConfig;
    @Setter
    private int maxMemory;
    @Setter
    private Thread consoleThread;
    @Setter
    private CloudNetwork cloudNetwork = NetworkUtils.cloudNetwork();

    /**
     * Constructor
     */
    private final CloudNetLogging logger;
    private final CloudNetServerConfig config;
    private final List<String> arguments;
    private final String cloudId;

    private ScreenService screenSystem = new ScreenService();

    /**
     * ServiceShutdown Thread
     */
    private CloudNetServiceShutdown serviceShutdown;

    private Properties messages;

    @Deprecated
    public CloudNetServer(List<String> arguments, CloudNetLogging logger, CloudNetServerConfig config, Properties messages) throws Exception
    {
        this.instance = this;

        this.arguments = arguments;
        this.logger = logger;
        this.config = config;
        this.cloudId = config.getProperties().getProperty("cnsId");
        this.proxyConfig = config.getConfig().getObject("proxy", new TypeToken<ProxyConfig>() {
        }.getType());
        this.maxMemory = Integer.parseInt(config.getProperties().getProperty("maxmemory"));
        this.messages = messages;

        this.serviceShutdown = new CloudNetServiceShutdown(logger);
    }

    @Override
    public boolean bootstrap() throws Exception
    {

        System.out.println(messages.getProperty("startup"));

        List<ServerGroup> groups = config.loadGroups();
        for (ServerGroup group : groups)
        {
            System.out.println(messages.getProperty("groupLoading").replace("%group%", group.getName()));
            this.groups.put(group.getName(), group);
        }

        new NetworkConnector(arguments, config.getProperties(), config.getMessages());

        {
            Thread schedulerThread = new Thread(scheduler);
            schedulerThread.setDaemon(true);
            schedulerThread.start();
        }

        {
            Thread schedulerThread = new Thread(serverPriorityScheduler);
            schedulerThread.setDaemon(true);
            schedulerThread.start();
        }

        {
            Thread schedulerThread = new Thread(stopServerScheduler);
            schedulerThread.setDaemon(true);
            schedulerThread.start();
        }

        if (!new File("database/proxy/BungeeCord.jar").exists()
                && !arguments.contains("--disallow_proxy_download"))
        {
            new SetupProxyVersion().run(logger.getReader());
        }

        if (!new File("database/spigot.jar").exists()
                && !arguments.contains("--disallow_spigot_download"))
        {
            new SetupSpigotVersion().run(logger.getReader());
        }

        scheduler.runTaskRepeatSync(this, 0, 5);
        scheduler.runTaskRepeatSync(new GlobalPriorityStartup(), 0, 10);
        scheduler.runTaskRepeatSync(new GroupPriorityStartup(), 0, 7);

        stopServerScheduler.runTaskRepeatSync(new StopHandler(), 0, 2);

        IS_RUNNING = true;

        if (groups.size() == 0 && !arguments.contains("--disable_autogroup_setup"))
        {
            new SetupServerGroup().run(logger.getReader());
        }

        if (config.isWasNewCreated())
        {
            System.out.println(" ");
            System.out.println(messages.getProperty("newConfigCreatedFirst"));
            System.out.println(messages.getProperty("newConfigCreatedSecond"));
            System.out.println(" ");
        }

        return true;
    }

    public boolean startServer(ServerGroup group, Document properties, boolean priorityStop, boolean hide)
    {
        int use = getUsedMemory();
        List<String> servers = getServersByName(group.getName());
        int memory = calcMemory(group.getMemory(), group.getDynamicMemory(), servers.size(), use);
        if ((memory + use) > maxMemory)
        {
            if (group.getMemory() + use > maxMemory)
            {
                System.out.println(messages.getProperty("notEnoughMemory"));
                return false;
            } else
            {
                memory = group.getMemory();
            }
        }

        if (group.isMaintenance() && servers.size() != 0)
        {
            return false;
        }
        if (group.getGroupMode().equals(ServerGroupMode.STATIC))
        {
            return startStaticServer(group, properties, priorityStop, group.getMemory());
        }

        boolean beta = false;
        if (group.isMaintenance())
        {
            beta = true;
        }

        Return<String, Integer> data = doA(group);
        MinecraftServer minecraftServer = null;

        switch (group.getServerType())
        {
            case SPONGE:
                minecraftServer = new SpigotServer(data.getFirst(), group, memory, data.getSecond(), properties, priorityStop, beta, hide);
                break;
            default:
                minecraftServer = new SpigotServer(data.getFirst(), group, memory, data.getSecond(), properties, priorityStop, beta, hide);
                break;
        }

        try
        {
            minecraftServer.bootstrap();
            return true;
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
    }

    public boolean startStaticServer(ServerGroup group, Document properties, boolean priorityStop, int memory)
    {
        if (!group.getGroupMode().equals(ServerGroupMode.STATIC)) return false;

        int use = getUsedMemory();
        List<String> servers = getServersByName(group.getName());
        if ((memory + use) > maxMemory)
        {
            if (group.getMemory() + use > maxMemory)
            {
                System.out.println(messages.getProperty("notEnoughMemory"));
                return false;
            } else
            {
                memory = group.getMemory();
            }
        }

        if (group.isMaintenance() && servers.size() != 0)
        {
            return false;
        }

        new File("static/" + group.getName()).mkdir();

        Return<String, Integer> result = doA(group);

        StaticSpigotServer staticServer = new StaticSpigotServer(result.getFirst(), group, result.getSecond(), properties, priorityStop, memory, false);
        try
        {
            return staticServer.bootstrap();
        } catch (Exception exc)
        {
            exc.printStackTrace();
        }
        return false;
    }

    public boolean startCustomServer(ServerGroup serverGroup, String serverId, Document properties, boolean priorityStop, int maxplayers, int memory, boolean hide)
    {

        if (memory + getUsedMemory() > maxMemory)
        {
            System.out.println(messages.getProperty("notEnoughMemory"));
            return false;
        }

        Return<String, Integer> result = doA(serverGroup);

        CustomServer staticServer = new CustomServer(serverId, result.getSecond(), serverGroup, properties, priorityStop, memory, hide, maxplayers);
        try
        {
            return staticServer.bootstrap();
        } catch (Exception exc)
        {
            exc.printStackTrace();
        }
        return false;
    }

    public boolean startStaticServer(ServerGroup group, String serverId, Document properties, boolean priorityStop, int memory)
    {
        if (!group.getGroupMode().equals(ServerGroupMode.STATIC)) return false;

        if (memory + getUsedMemory() > maxMemory)
        {
            System.out.println(messages.getProperty("notEnoughMemory"));
            return false;
        }

        new File("static/" + group.getName()).mkdir();

        Return<String, Integer> result = doA(group);

        StaticSpigotServer staticServer = new StaticSpigotServer(serverId, group, result.getSecond(), properties, priorityStop, memory, false);
        try
        {
            return staticServer.bootstrap();
        } catch (Exception exc)
        {
            exc.printStackTrace();
        }
        return false;
    }

    public void startProxy()
    {
        if ((getUsedMemory() + proxyConfig.getMemory()) >= maxMemory)
        {
            System.out.println(messages.getProperty("notEnoughMemory"));
            return;
        }

        Return<String, Integer> data = doA();
        ProxyServer proxyServer = new ProxyServer(data.getFirst(), data.getSecond(), proxyConfig);
        try
        {
            proxyServer.bootstrap();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public List<String> getServersByName(String group)
    {
        List<String> servers = new ArrayList<>();

        for (MinecraftServer minecraftServer : this.servers.values())
        {
            if (minecraftServer.getGroup().getName().equals(group) ||
                    (minecraftServer.getGroup().getParentGroup() != null && minecraftServer.getGroup().getParentGroup().equals(group)))
            {
                servers.add(minecraftServer.getServerId());
            }
        }

        return servers;
    }

    @Override
    public boolean shutdown()
    {
        System.exit(0);
        return true;
    }

    @Override
    public void run(CloudNetServer value)
    {
        if (IS_RUNNING)
        {
            value.shutdown();
        }
    }

    @Override
    public void run()
    {
        for (ServerGroup group : groups.values())
        {

            if ((group.getMemory() + getUsedMemory()) > maxMemory)
            {
                continue;
            }
            List<String> servers = getServersByName(group.getName());

            if (group.isMaintenance() && servers.size() == 0 && group.getStartup() > 0)
            {
                if (!group.getGroupMode().equals(ServerGroupMode.STATIC))
                {
                    startServer(group, new Document(), false, false);
                    continue;
                } else
                {
                    startStaticServer(group, new Document(), false, group.getMemory());
                    continue;
                }
            }

            if (servers.size() < group.getStartup())
            {
                if (!group.getGroupMode().equals(ServerGroupMode.STATIC))
                {
                    startServer(group, new Document(), false, false);
                    continue;
                } else
                {
                    startServer(group, new Document(), false, false);
                    continue;
                }
            }
        }

        if ((proxyConfig.isStaticProxyMode() && proxys.size() == 0) || (proxyConfig.getStartup() > proxys.size() && !proxyConfig.isStaticProxyMode()))
        {
            startProxy();
        }
    }

    public int getUsedMemory()
    {
        int m = 0;

        for (MinecraftServer minecraftServer : servers.values())
        {
            m = m + minecraftServer.getMemory();
        }

        for (ProxyServer proxyServer : proxys.values())
        {
            m = m + proxyServer.getMemory();
        }

        return m;
    }

    /*========================================================================*/
    //Utils

    private Return<String, Integer> doA()
    {
        short i = 1;
        int port = proxyConfig.getStartPort();
        while (proxys.containsKey("Proxy-" + i) || cloudNetwork.getProxys().containsKey("Proxy-" + i)) i++;

        Collection<Integer> bindedPorts = new HashSet<>();
        for (ProxyServer proxyServer : proxys.values())
        {
            bindedPorts.add(proxyServer.getPort());
        }

        if (bindedPorts.size() != 0)
            while (bindedPorts.contains(port))
            {
                port++;
                port++;
            }

        return new Return<>("Proxy-" + i, port);
    }

    private Return<String, Integer> doA(ServerGroup group)
    {
        short i = 1;
        int port = Integer.parseInt(config.getProperties().getProperty("startport"));
        while (servers.containsKey(group.getName() + "-" + i) || cloudNetwork.getServers().containsKey(group.getName() + "-" + i))
            i++;

        Collection<Integer> bindedPorts = new HashSet<>();
        for (MinecraftServer proxyServer : servers.values())
        {
            bindedPorts.add(proxyServer.getPort());
        }

        if (bindedPorts.size() != 0)
            while (bindedPorts.contains(port))
            {
                port = port + Utils.RANDOM.nextInt(servers.size() * 10) + 1;
            }
        return new Return<>(group.getName() + "-" + i, port);
    }

    public String getHostName()
    {
        try
        {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex)
        {
            return "127.0.0.1";
        }
    }

    //6172, groupMemory = 512, groupDynamicMemory = 1024, 3 online, 1536, 2048
    public int calcMemory(int groupMemory, int groupDynmamicMemory, int onlineFromGroup, int globaluse)
    {
        if (groupMemory < 0 || groupDynmamicMemory < 0) return groupMemory < 0 ? 512 : groupMemory;
        if (groupDynmamicMemory <= groupMemory) return groupMemory;
        if (onlineFromGroup > 9) return groupMemory;
        if (onlineFromGroup == 0) return groupDynmamicMemory;

        //return groupDynmamicMemory - groupMemory / 100 * (10 - onlineFromGroup * 10 - (maxMemory - globaluse * 100 / maxMemory / 2)) + groupMemory;
        //return ((groupDynmamicMemory - groupMemory) / 100) * (((10 - onlineFromGroup) * 10 + ((globaluse * 100 / maxMemory / 2)))) + groupMemory;
        return ((groupDynmamicMemory - groupMemory) / 100) * (((10 - onlineFromGroup) * 10)) + groupMemory;
    }

    private class StopHandler
            implements Runnable {

        @Override
        public void run()
        {
            for (MinecraftServer minecraftServer : servers.values())
            {
                try
                {
                    if (!minecraftServer.isAlive())
                    {
                        minecraftServer.shutdown();
                    }
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }

            for (ProxyServer minecraftServer : proxys.values())
            {
                try
                {
                    if (!minecraftServer.isAlive())
                    {
                        minecraftServer.shutdown();
                    }
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

}