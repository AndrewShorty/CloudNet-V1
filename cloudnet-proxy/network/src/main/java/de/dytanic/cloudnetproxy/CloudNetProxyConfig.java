package de.dytanic.cloudnetproxy;

import de.dytanic.cloudnet.*;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnetproxy.database.yaml.Configuration;
import de.dytanic.cloudnetproxy.database.yaml.ConfigurationProvider;
import de.dytanic.cloudnetproxy.database.yaml.YamlConfiguration;
import de.dytanic.cloudnetproxy.network.NetworkInfo;
import de.dytanic.cloudnetproxy.network.components.CNS;
import jline.console.ConsoleReader;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

/**
 * Created by Tareko on 26.05.2017.
 */
@Getter
public final class CloudNetProxyConfig {

    private Configuration configuration;
    private final File file = new File("config.yml");
    private final File prop = new File("config.properties");

    private boolean created = false;
    private Properties properties = new Properties();

    public CloudNetProxyConfig(ConsoleReader reader) throws Exception
    {

        if (!file.exists())
        {
            file.createNewFile();
            String hostName = NetworkUtils.getHostName();
            if (hostName.equalsIgnoreCase("127.0.0.1") || hostName.equalsIgnoreCase("127.0.1.1") || hostName.split(".").length == 4)
            {
                String input;
                System.out.println("Your IP address where located is 127.0.0.1 please write your server ip");
                while ((input = reader.readLine()) != null && (input.equalsIgnoreCase("127.0.0.1") && input.split(".").length == 4))
                {
                    hostName = input;
                    System.out.println("Please write your really ip address :)");
                }

            }

            String key = new ServiceKey().newKey();
            System.out.println(" ");
            System.out.println("ServiceKey: \"" + key + "\"");
            System.out.println("Please copy the service key for the CNS connection authentication");
            System.out.println(" ");

            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

            Configuration layouts = new Configuration();

            layouts.set("prefix", "§bCloud§fNet §8❘ §7");
            layouts.set("disableLayoutFunction", false);
            layouts.set("disableMaintenanceFunction", false);

            Configuration tablistsettings = new Configuration();
            tablistsettings.set("enabled", true);
            tablistsettings.set("header", "§7Your §bCloud§fNet §7system for §afree! ");
            tablistsettings.set("footer", "§8■ §7Your own network §acloud software §8■");

            layouts.set("tablist", tablistsettings);

            Configuration bx = new Configuration();
            bx.set("firstLine", "    §b§lCloud§fNet §8► §7your cloud-system §8● §fversion §8➟§8【§aRELEASE§8】");
            bx.set("secondLine", " §aNEWS §8» §7Cloudsystem for §efree! §8▼");
            layouts.set("motd", bx);

            Configuration bxx = new Configuration();
            bxx.set("firstLine", "    §b§lCloud§fNet §8► §7your cloud-system §8● §fversion §8➟§8【§aRELEASE§8】");
            bxx.set("secondLine", "§cMaintenance mode &8▼");
            layouts.set("maintenanceMotd", bxx);

            layouts.set("maintenance", false);
            layouts.set("maxOnlineCount", 32);
            layouts.set("autoSlot", false);
            layouts.set("maintenanceDesign", "&cMaintenance §8【§c✘§8】");
            layouts.set("fallback", "Lobby");
            layouts.set("notifySystem", true);

            layouts.set("maintenanceMessage", "&cThe network is in maintenance mode!");
            layouts.set("hubCommandMessage", "&7You're connecting to the lobby...");
            layouts.set("alreadyOnHubMessage", "&7You are already on a hub server");
            layouts.set("serverFullMessage", "&7The Network is full! Buy Premium under our server ip :)");
            layouts.set("fallbackNotFoundMessage", "&7We don't have any fallback server from cloudnet.");

            Configuration values = new Configuration();
            values.set("noPermissionToEnterServerMessage", "&cYou don't have permission to enter this server!");
            values.set("signSendToServerMessage", "&eYou will send to the server %server%!");
            values.set("signFullServerHide", true);

            java.util.Map<String, Object> netInfoFromFirstCNS = new HashMap<>();
            netInfoFromFirstCNS.put("CNS-1", hostName);

            layouts.set("createservercommand", new Configuration()
                    .set("enabled", true)
                    .set("listMessage", "The the following games is available: %groups%")
                    .set("executeMessage", "Your server starting...")
            );

            configuration
                    .set("autoupdate", true)
                    .set("hostName", hostName)
                    .set("port", Arrays.asList(1417))
                    .set("servicekey", key)
                    .set("permissions_system", true)
                    .set("proxyLayout", layouts)
                    .set("serverLayout", values)

                    .set("ipwhitelist", Arrays.asList(hostName, "127.0.0.1"))
                    .set("cloudnetserver", netInfoFromFirstCNS);

            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        }
        this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

        if (this.configuration.get("whitelist") == null)
        {
            this.configuration.set("whitelist", Arrays.asList("md_5"));
            saveAndReloadConfig();
        }
    }

    public void setProxyLayout(ProxyLayout proxyLayout)
    {
        Configuration layouts = new Configuration();

        layouts.set("prefix", proxyLayout.getPrefix());
        layouts.set("disableLayoutFunction", proxyLayout.isDisableLayoutFunction());
        layouts.set("disableMaintenanceFunction", proxyLayout.isDisableMaintenanceFunction());

        Configuration tablistsettings = new Configuration();
        tablistsettings.set("enabled", proxyLayout.getTabList().isEnabled());
        tablistsettings.set("header", proxyLayout.getTabList().getHeader());
        tablistsettings.set("footer", proxyLayout.getTabList().getFooter());

        layouts.set("tablist", tablistsettings);

        Configuration bx = new Configuration();
        bx.set("firstLine", proxyLayout.getDefaultMotd().getFirstLine());
        bx.set("secondLine", proxyLayout.getDefaultMotd().getSecondLine());
        layouts.set("motd", bx);

        Configuration bxx = new Configuration();
        bxx.set("firstLine", proxyLayout.getMaintenanceMotd().getFirstLine());
        bxx.set("secondLine", proxyLayout.getMaintenanceMotd().getSecondLine());
        layouts.set("maintenanceMotd", bxx);

        layouts.set("maintenance", proxyLayout.isMaintenance());
        layouts.set("maxOnlineCount", proxyLayout.getMaxOnlineCount());
        layouts.set("autoSlot", proxyLayout.isAutoSlot());
        layouts.set("maintenanceDesign", proxyLayout.getMaintenanceDesign());
        layouts.set("fallback", proxyLayout.getFallback());
        layouts.set("notifySystem", proxyLayout.isNotifySystem());

        layouts.set("maintenanceMessage", proxyLayout.getMaintenanceMessage());
        layouts.set("hubCommandMessage", proxyLayout.getHubCommandMessage());
        layouts.set("alreadyOnHubMessage", proxyLayout.getAlreadyOnHubMessage());
        layouts.set("serverFullMessage", proxyLayout.getServerFullMessage());
        layouts.set("fallbackNotFoundMessage", proxyLayout.getFallbackNotFoundMessage());

        layouts.set("createservercommand", new Configuration()
                .set("enabled", proxyLayout.getCreateServerCommandProperties().isEnabled())
                .set("listMessage", proxyLayout.getCreateServerCommandProperties().getListMessage())
                .set("executeMessage", proxyLayout.getCreateServerCommandProperties().getExecuteMessage())
        );

        this.configuration.set("proxyLayout", layouts);
        this.configuration.set("whitelist", proxyLayout.getPlayerWhitelist());
        saveAndReloadConfig();
    }

    public List<String> loadWhitelist()
    {
        return configuration.getStringList("ipwhitelist");
    }

    public void addWhitelist(String hostName)
    {
        List<String> ips = loadWhitelist();
        ips.add(hostName);
        configuration.set("ipwhitelist", ips);
        saveAndReloadConfig();
    }

    public void addCNS(NetworkInfo networkInfo)
    {
        if (!CloudNetProxy.getInstance().getCnsSystems().containsKey(networkInfo.getServerId()))
        {
            configuration.set("cloudnetserver." + networkInfo.getServerId(), networkInfo.getHostName());

            saveAndReloadConfig();

            CNS cns = new CNS(networkInfo);
            CloudNetProxy.getInstance().getCnsSystems().put(cns.getServerId(), cns);
        }
    }

    public void removeWhitelist(String hostName)
    {
        List<String> ips = loadWhitelist();
        ips.remove(hostName);
        configuration.set("ipwhitelist", ips);
        saveAndReloadConfig();
    }

    public void reloadConfig()
    {
        try
        {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ProxyLayout loadProxyLayout()
    {
        return new ProxyLayout(configuration.getString("proxyLayout.prefix"), configuration.getBoolean("proxyLayout.disableLayoutFunction"),
                configuration.getBoolean("proxyLayout.disableMaintenanceFunction"),
                new TabList(configuration.getBoolean("proxyLayout.tablist.enabled"),
                        configuration.getString("proxyLayout.tablist.header"),
                        configuration.getString("proxyLayout.tablist.footer")),
                configuration.getStringList("whitelist")
                , new Motd(
                configuration.getString("proxyLayout.motd.firstLine"),
                configuration.getString("proxyLayout.motd.secondLine")),
                new Motd(
                        configuration.getString("proxyLayout.maintenanceMotd.firstLine"),
                        configuration.getString("proxyLayout.maintenanceMotd.secondLine")
                ), configuration.getBoolean("proxyLayout.maintenance"), configuration.getInt("proxyLayout.maxOnlineCount"),
                configuration.getBoolean("proxyLayout.autoSlot"), configuration.getBoolean("proxyLayout.notifySystem"),
                configuration.getString("proxyLayout.maintenanceDesign"),
                configuration.getString("proxyLayout.fallback"), configuration.getString("proxyLayout.maintenanceMessage"),
                configuration.getString("proxyLayout.hubCommandMessage"),
                configuration.getString("proxyLayout.alreadyOnHubMessage"),
                configuration.getString("proxyLayout.serverFullMessage"),
                configuration.getString("proxyLayout.fallbackNotFoundMessage"),
                new ProxyLayout.CreateServerCommandProperties(
                        configuration.getBoolean("proxyLayout.createservercommand.enabled"),
                        configuration.getString("proxyLayout.createservercommand.listMessage"),
                        configuration.getString("proxyLayout.createservercommand.executeMessage"))
        );
    }

    public boolean isPermissionSystemEnabled()
    {
        return configuration.getBoolean("permissions_system");
    }

    public String getKey()
    {
        return configuration.getString("servicekey");
    }

    public ServerLayout loadServerLayout()
    {
        return new ServerLayout(
                configuration.getString("serverLayout.noPermissionToEnterServerMessage"),
                configuration.getString("serverLayout.signSendToServerMessage"),
                configuration.getBoolean("serverLayout.signFullServerHide")
        );
    }

    public void saveAndReloadConfig()
    {
        try
        {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        reloadConfig();
    }

    public List<CNS> loadServers()
    {
        List<CNS> list = new ArrayList<>();

        Configuration se = configuration.getSection("cloudnetserver");
        for (String key : se.getKeys())
        {
            String hostName = se.getString(key);
            list.add(new CNS(new NetworkInfo(key, hostName, 0)));
        }
        return list;
    }
}