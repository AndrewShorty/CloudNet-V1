package de.dytanic.cloudnetserver;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnet.servergroup.ServerGroup;
import de.dytanic.cloudnet.servergroup.ServerGroupMode;
import de.dytanic.cloudnet.servergroup.ServerGroupType;
import de.dytanic.cloudnetserver.setup.SetupLanguage;
import de.dytanic.cloudnetserver.util.FileCopy;
import de.dytanic.cloudnetserver.util.ProxyConfig;
import de.dytanic.cloudnetserver.util.Utils;
import jline.console.ConsoleReader;
import lombok.Getter;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

/**
 * Created by Tareko on 21.05.2017.
 */
@Getter
public class CloudNetServerConfig {

    private final File propertiesFile = new File("config.properties");
    private final File configFile = new File("groups.json");
    private final Properties properties = new Properties();
    private final Document config = new Document();
    private final Properties messages = new Properties();

    private boolean wasNewCreated = false;

    private String hostName;

    public CloudNetServerConfig(ConsoleReader reader) throws Exception
    {

        new File("database").mkdir();
        new File("database/templates").mkdir();
        new File("database/records").mkdir();
        new File("database/global").mkdir();

        new File("database/proxy").mkdir();
        new File("database/proxy/plugins").mkdir();
        new File("static").mkdir();
        new File("tmp").mkdir();

        if (!new File("database/server-icon.png").exists())
        {
            FileCopy.insertData("files/server-icon.png", "database/server-icon.png");
        }

        if (!propertiesFile.exists())
        {

            SetupLanguage setupLanguage = new SetupLanguage();
            String language = setupLanguage.call(reader);

            String hostName = NetworkUtils.getHostName();
            if (hostName.equalsIgnoreCase("127.0.0.1") || hostName.equalsIgnoreCase("127.0.1.1") || hostName.split(".").length == 4)
            {
                String input;
                System.out.println(language.equals("german") ?
                        "Bitte schreibe deine Ip Address von deinem Server, weil die ip 127.0.0.1 gefunden wurde."
                        :
                        "Your IP address where located is 127.0.0.1 please write your server ip");
                while ((input = reader.readLine()) != null && (input.equalsIgnoreCase("127.0.0.1") && input.split(".").length == 4))
                {
                    hostName = input;
                    break;
                }
            }

            System.out.println(language.equals("german") ?
                    "Bitte schreibe deinen servicekey vom CloudNet-Proxy System:"
                    :
                    "Please enter the CloudNet-Proxy service key:");

            String key;
            while ((key = reader.readLine()) != null)
            {
                if (key.split("-").length == 4 && key.length() == 19)
                {
                    break;
                } else
                {
                    System.out.println("Invalid servicekey enter the right servicekey:");
                }
            }

            propertiesFile.createNewFile();
            Properties properties = new Properties();
            properties.setProperty("cnsId", "CNS-1");
            properties.setProperty("autoupdate", "true");
            properties.setProperty("language", language);
            properties.setProperty("maxmemory", "2048");
            properties.setProperty("startport", "37000");
            properties.setProperty("servicekey", key);
            properties.setProperty("ip", hostName);
            properties.setProperty("proxy-host", hostName);
            properties.setProperty("proxy-port", "1417");
            properties.setProperty("saving_records", "false");

            try (FileOutputStream outputStream = new FileOutputStream(propertiesFile))
            {
                properties.save(outputStream, "");
            }
        }

        try (FileInputStream stream = new FileInputStream(propertiesFile))
        {
            this.properties.load(stream);
        }

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("language/" + properties.getProperty("language") + ".properties"))
        {
            this.messages.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        }

        if (!configFile.exists())
        {

            int memory = 512;
            String key;
            System.out.println(this.messages.getProperty("proxyMemory"));
            while ((key = reader.readLine()) != null)
            {
                if (Utils.checkAsNumber(key))
                {
                    memory = Integer.parseInt(key);
                    break;
                }
                else
                {
                    System.out.println("Invalid input");
                }
            }

            this.wasNewCreated = true;
            configFile.createNewFile();

            new Document()
                    .append("proxy", new ProxyConfig(25565, "Lobby", 1, memory, false))
                    .append("groups", Arrays.asList(new ServerGroup(
                                    "Lobby", 356, 512, new HashMap<>(), 0, true, 1, 1, 0 ,
                                    "127.0.0.1", 100, 300, 100, 100, ServerGroupType.BUKKIT,
                                    ServerGroupMode.LOBBY, null
                            ))
                    ).saveAsConfig(configFile);
            new File("database/templates/Lobby").mkdir();
            new File("database/templates/Lobby/global").mkdir();
            new File("database/templates/Lobby/profiles").mkdir();
            new File("database/templates/Lobby/maps").mkdir();
        }

        this.hostName = properties.getProperty("ip");

        this.config.loadToExistingDocument(configFile);
    }

    public List<ServerGroup> loadGroups()
    {
        List<ServerGroup> groups = new ArrayList<>();

        if (config.contains("groups"))
        {
            JsonArray array = config.getArray("groups");
            Gson gson = new Gson();
            for (JsonElement obj : array)
            {
                groups.add(gson.fromJson(obj, new TypeToken<ServerGroup>() {
                }.getType()));
            }
        }

        for (ServerGroup serverGroup : groups)
        {
            if (serverGroup.getParentGroup() != null)
            {
                ServerGroup group = groups.stream().filter(new Predicate<ServerGroup>() {
                    @Override
                    public boolean test(ServerGroup serverGroup)
                    {
                        return serverGroup.getName().equals(serverGroup.getParentGroup());
                    }
                }).findFirst().get();

                serverGroup.handleExtends(group);

            }
        }

        return groups;
    }

    public void addNewGroup(ServerGroup group)
    {
        List<ServerGroup> groups = loadGroups();

        ServerGroup dserverGroup = null;

        for (ServerGroup serverGroup : groups)
        {
            if (serverGroup.getName().equalsIgnoreCase(group.getName()))
            {
                dserverGroup = serverGroup;
            }
        }

        if (dserverGroup != null) groups.remove(dserverGroup);

        groups.add(group);

        config.append("groups", groups);
        config.saveAsConfig(configFile);

        CloudNetServer.getInstance().getGroups().clear();
        for (ServerGroup group_ : groups)
        {
            CloudNetServer.getInstance().getGroups().put(group_.getName(), group_);
        }

    }

    public void removeGroup(ServerGroup group)
    {
        List<ServerGroup> groups = loadGroups();

        ServerGroup dserverGroup = null;

        for (ServerGroup serverGroup : groups)
        {
            if (serverGroup.getName().equalsIgnoreCase(group.getName()))
            {
                dserverGroup = serverGroup;
            }
        }

        if (dserverGroup != null) groups.remove(dserverGroup);

        config.append("groups", groups);
        config.saveAsConfig(configFile);

        CloudNetServer.getInstance().getGroups().clear();
        for (ServerGroup group_ : groups)
        {
            CloudNetServer.getInstance().getGroups().put(group_.getName(), group_);
        }

    }

    public void reloadConfig()
    {
        try (FileInputStream stream = new FileInputStream(propertiesFile))
        {
            this.properties.load(stream);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        this.config.loadToExistingDocument(configFile);

    }

}