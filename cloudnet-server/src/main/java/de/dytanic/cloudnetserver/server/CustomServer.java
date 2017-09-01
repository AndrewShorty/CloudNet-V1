package de.dytanic.cloudnetserver.server;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.lib.threading.ScheduledTask;
import de.dytanic.cloudnet.servergroup.*;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.network.NetworkConnector;
import de.dytanic.cloudnetserver.network.packets.PacketAddServer;
import de.dytanic.cloudnetserver.network.packets.PacketOutRemoveServer;
import de.dytanic.cloudnetserver.server.handlers.ServerPriority;
import de.dytanic.cloudnetserver.util.FileCopy;
import de.dytanic.cloudnetserver.util.Utils;

import java.io.*;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Tareko on 07.07.2017.
 */
public class CustomServer
        extends MinecraftServer {

    private String path;
    private int maxplayers;
    private long startup;

    public CustomServer(String serverId, int port, ServerGroup group, Document properties, boolean priorityStop, int memory, boolean hide, int maxplayers)
    {
        super(new AtomicBoolean(false), serverId, UUID.randomUUID(), port, group, null,
                new Document()
                        .append("created", Utils.SIMPLE_DATE_FORMAT.format(new Date()))
                        .append("group", group.getName())
                        .append("port", port)
                        .append("prioritystop", priorityStop)
                        .append("properties", properties)
                        .append("serverid", serverId)
                , properties, priorityStop, null, memory, false, hide,
                new ServerGroupProfile("default"));
        this.maxplayers = maxplayers;
    }

    @Override
    public void runCommand(String command)
    {
        if (instance == null) return;
        try
        {

            instance.getOutputStream().write((command + "\n").getBytes());
            instance.getOutputStream().flush();

        } catch (Exception ex)
        {
        }
    }

    @Override
    public boolean bootstrap() throws Exception
    {

        if (!new File("database/templates/" + group.getName()).exists())
        {
            System.out.println("Template not found");
            return false;
        }

        new File("static/custom").mkdir();

        path = "static/custom" + serverId;

        File tmpDir = new File(path);

        if (!tmpDir.exists())
        {
            tmpDir.mkdir();

            FileCopy.copyFilesInDirectory(new File("database/templates/" + group.getName() + "/global"), tmpDir);

            File server_icon = new File("database/server-icon.png");
            if (server_icon.exists())
            {
                FileCopy.copyFileToDirectory(server_icon, tmpDir);
            }

            if (!new File(path + "server.properties").exists())
            {
                FileCopy.insertData("files/server.properties", path + "/server.properties");
            }

            if (!new File(path + "/bukkit.yml").exists())
            {
                FileCopy.insertData("files/bukkit.yml", path + "/bukkit.yml");
            }

            if (!new File(path + "/spigot.yml").exists())
            {
                FileCopy.insertData("files/spigot.yml", path + "/spigot.yml");
            }

            if (group.getServerType().equals(ServerGroupType.BUKKIT) && !new File(path + "/spigot.jar").exists()
                    && new File("database/spigot.jar").exists())
            {
                FileCopy.copyFileToDirectory(new File("database/spigot.jar"), new File(path));
            }

            new File(path + "/plugins").mkdir();
            FileCopy.insertData("files/CloudNetAPI.jar", path + "/plugins/CloudNetAPI.jar");

            try
            {

                FileCopy.copyFilesInDirectory(new File("database/global"), tmpDir);

            } catch (Exception ex)
            {
            }

            if (group.getServerType().equals(ServerGroupType.CAULDRON))
            {
                File file = new File(path + "/eula.txt");
                try (FileWriter writer = new FileWriter(file))
                {
                    writer.write("eula=true" + "\n");
                    writer.flush();
                }
            }
        }

        {

            try (FileInputStream stream = new FileInputStream(new File(path + "/server.properties")))
            {
                Properties properties = new Properties();
                properties.load(stream);
                properties.setProperty("server-port", port + "");
                properties.setProperty("server-ip", group.getHostName());
                properties.setProperty("max-players", this.maxplayers + "");
                properties.setProperty("server-name", serverId);
                properties.setProperty("online-mode", "false");
                serverMap = new ServerMap(properties.getProperty("level-name"), maxplayers);

                properties.save(new FileOutputStream(new File(path + "/server.properties")), "Edit by CNS");
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }

        new Document()
                .append("serverId", serverId)
                .append("uniqueId", uniqueId + "")
                .append("cloudid", CloudNetServer.getInstance().getCloudId())
                .append("group", group.getName())
                .append("netconnectorbind", group.getHostName() + ":" + (port - 1))
                .append("netconnectorremote", NetworkConnector.getInstance().getProxyHostName() + ":" + NetworkConnector.getInstance().getProxyPort())
                .append("properties", properties)
                .append("memory", group.getMemory())
                .append("mode", ServerGroupMode.STATIC)
                .append("map", serverMap)
                .append("profile", new ServerGroupProfile("default"))
                .append("beta", betaStatic)
                .append("hide", hide)
                .append("ssl", CloudNetServer.getInstance().getArguments().contains("--ssl"))
                .saveAsConfig(new File(path + "/cloudnet.json"));

        String[] command = new String[]{
                "java",
                "-XX:+UseG1GC"/*"-XX:+UseParallelGC"*/, "-XX:MaxGCPauseMillis=50", "-Xmn2M", "-XX:MaxPermSize=256M", "-Dcom.mojang.eula.agree=true",
                "-Xmx" + group.getMemory() + "M",
                "-jar", (group.getServerType().equals(ServerGroupType.CAULDRON) ? "cauldron.jar" : "spigot.jar"), "nogui"
        };

        CloudNetServer.getInstance().getServers().put(serverId, this);

        ProcessBuilder builder = new ProcessBuilder(command).directory(tmpDir);
        this.instance = builder.start();
        this.startup = System.currentTimeMillis();
        if (priorityStop && group.getPriorityStopTime() != -1 && group.getPriorityStopTime() != 0)
        {
            ServerPriority priority = new ServerPriority(serverId, uniqueId, group);
            ScheduledTask task = CloudNetServer.getInstance().getServerPriorityScheduler().runTaskRepeatSync(priority, 0, 10);
            priority.setScheduledTask(task);
        }

        if (NetworkConnector.getInstance() != null && NetworkConnector.getInstance().getChannel().isOpen())
        {
            NetworkConnector.getInstance().sendPacket(new PacketAddServer(this, port - 1));
        }

        this.running.set(true);

        System.out.println(CloudNetServer.getInstance().getMessages().getProperty("startServer").replace("%server%", toString()));

        return false;
    }

    @Override
    public boolean shutdown()
    {
        if (instance != null)
        {
            if (instance.isAlive())
            {
                try
                {
                    instance.getOutputStream().write("stop\n".getBytes());
                    instance.getOutputStream().flush();
                    Thread.sleep(1000);
                } catch (IOException | InterruptedException e)
                {
                }
            }

            CloudNetServer.getInstance().getScreenSystem().checkAndRemove(this);

            instance.destroy();

            if (!this.running.get())
            {
                return false;
            }

            metaData.append("stopTime", Utils.SIMPLE_DATE_FORMAT.format(new Date()));
            metaData.append("uuid", uniqueId.toString());

            if (CloudNetServer.getInstance().getConfig().getProperties().getProperty("saving_records").equalsIgnoreCase("true"))
            {
                File dir = new File("database/records/" + group.getName() + "#" + uniqueId.toString());
                dir.mkdir();
                if (new File(path + "/logs").exists())
                {
                    try
                    {
                        FileCopy.copyFilesInDirectory(new File(path + "/logs"), dir);
                    } catch (Exception ex)
                    {
                    }

                }

                File file = new File("database/records/" + group.getName() + "#" + uniqueId.toString() + "/metadata.cloudnet");
                metaData.saveAsConfig(file);
            }

        }

        if (NetworkConnector.getInstance() != null)
        {
            NetworkConnector.getInstance().sendPacket(new PacketOutRemoveServer(serverId));
        }

        CloudNetServer.getInstance().getScreenSystem().checkAndRemove(this);
        CloudNetServer.getInstance().getServers().remove(serverId);
        this.running.set(false);
        System.out.println(CloudNetServer.getInstance().getMessages().getProperty("stopServer").replace("%server%", toString()));
        return false;
    }

    @Override
    public boolean isAlive()
    {

        if((startup + 2000) > System.currentTimeMillis()) return true;

        return instance != null && instance.isAlive();
    }

    @Override
    public void copy()
    {
    }

    @Override
    public String toString()
    {
        return "[" + serverId + "/uuid=" + uniqueId + "/port=" + port + "/memory=" + memory + "]";
    }
}
