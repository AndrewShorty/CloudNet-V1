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
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Tareko on 24.05.2017.
 */
@Getter
public class SpigotServer
        extends MinecraftServer {

    private String path;
    private long startup = 0;

    public SpigotServer(String serverId, ServerGroup group, int memory, int port, Document properties, boolean priorityStop, boolean betaStatic, boolean hide)
    {
        super(new AtomicBoolean(false), serverId, UUID.randomUUID(), port, group, null,
                new Document()
                        .append("created", Utils.SIMPLE_DATE_FORMAT.format(new Date()))
                        .append("group", group.getName())
                        .append("port", port)
                        .append("prioritystop", priorityStop)
                        .append("properties", properties)
                        .append("serverid", serverId)
                , properties, priorityStop, null, memory, betaStatic, hide, new ServerGroupProfile("default"));
        this.properties.appendValues(group.getProperties());
    }

    @Override
    public boolean bootstrap() throws Exception
    {
        if (!new File("database/templates/" + group.getName()).exists() || !new File("database/templates/" + group.getName() + "/global").exists())
        {
            System.out.println("Template not found");
            return false;
        }

        File tmpDir;

        if (group.getParentGroup() != null)
        {
            path = "tmp/" + group.getParentGroup() + "/" + group.getName() + "/" + serverId;
            new File("tmp/" + group.getParentGroup()).mkdir();
            new File("tmp/" + group.getParentGroup() + "/" + group.getName()).mkdir();
            new File(path).mkdir();
            tmpDir = new File(path);
            if (group.getParentGroup() != null && new File("database/templates/" + group.getParentGroup()).exists())
            {
                FileCopy.copyFilesInDirectory(new File("database/templates/global" + group.getParentGroup()), tmpDir);
            }

        } else
        {
            path = "tmp/" + group.getName() + "/" + serverId;
            new File("tmp/" + group.getName()).mkdir();
            tmpDir = new File(path);
        }
        tmpDir.mkdir();

        try
        {
            FileCopy.copyFilesInDirectory(new File("database/global"), tmpDir);
            FileCopy.copyFilesInDirectory(new File("database/templates/" + group.getName() + "/global"), tmpDir);
        } catch (Exception ex) {}

        File server_icon = new File("database/server-icon.png");
        if (server_icon.exists())
        {
            FileCopy.copyFileToDirectory(server_icon, tmpDir);
        }

        if (!new File(path + "/server.properties").exists())
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

        if (group.getServerType().equals(ServerGroupType.CAULDRON))
        {
            File file = new File(path + "/eula.txt");
            try (FileWriter writer = new FileWriter(file))
            {
                writer.write("eula=true" + "\n");
                writer.flush();
            }
        }

        boolean profile_use = false;
        if(!group.isMaintenance())
        {
            File profilesDir = new File("database/templates/" + group.getName() + "/profiles");
            if(profilesDir.exists())
            {
                File[] files = profilesDir.listFiles();
                if(files.length > 0)
                {
                    profile_use = true;
                    try{

                        File pf = files[Utils.RANDOM.nextInt(files.length)];
                        profile = new ServerGroupProfile(pf.getName());
                        FileCopy.copyFilesInDirectory(pf, tmpDir);

                    }catch (Exception ex) {}
                }
            }
        }


        //Servers Properties Data edit
        {

            int maxplayers = group.getMaxPlayers();

            ServerMap serverMap = null;

            File mapDir = new File("database/templates/" + group.getName() + "/maps");
            if (mapDir.exists() && mapDir.isDirectory() && mapDir.list().length > 0)
            {
                String[] fileList = mapDir.list((dir, name) -> dir.isDirectory() && name.split("#").length > 1);

                if (fileList.length > 0)
                {
                    String name__ = fileList[Utils.RANDOM.nextInt(fileList.length)];
                    String[] name_x = name__.split("#");
                    File sFile = new File("database/templates/" + group.getName() + "/maps/" + name__);
                    try
                    {
                        FileCopy.copyFilesInDirectory(sFile, new File(path + "/" + name_x[0]));
                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    //new File(path + "/" + name__).renameTo(new File(path));
                    serverMap = new ServerMap(name_x[0], Integer.parseInt(name_x[1]));
                    maxplayers = Integer.parseInt(name_x[1]);
                    this.serverMap = serverMap;
                }
            } else if (group.getParentGroup() != null)
            {

                File pdir = new File("database/templates/" + group.getName() + "/maps");
                if (pdir.exists() && pdir.isDirectory() && mapDir.list().length > 0)
                {
                    String[] fileList = pdir.list((dir, name) -> dir.isDirectory() && name.split("#").length > 1);

                    if (fileList.length > 0)
                    {
                        String name__ = fileList[Utils.RANDOM.nextInt(fileList.length)];
                        String[] name_x = name__.split("#");
                        File sFile = new File("database/templates/" + group.getName() + "/maps/" + name__);
                        try
                        {
                            FileCopy.copyFilesInDirectory(sFile, new File(path + "/" + name_x[0]));
                        }catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        //new File(path + "/" + name__).renameTo(new File(path));
                        serverMap = new ServerMap(name_x[0], Integer.parseInt(name_x[1]));
                        maxplayers = Integer.parseInt(name_x[1]);
                        this.serverMap = serverMap;
                    }
                }

            }

            try (FileInputStream stream = new FileInputStream(new File(path + "/server.properties")))
            {
                Properties properties = new Properties();
                properties.load(stream);
                properties.setProperty("server-port", port + "");
                properties.setProperty("server-ip", group.getHostName());
                if(!profile_use)
                properties.setProperty("max-players", maxplayers + "");
                properties.setProperty("server-name", serverId);
                properties.setProperty("online-mode", "false");

                if (serverMap == null)
                {
                    serverMap = new ServerMap(properties.getProperty("level-name"), maxplayers);
                    this.serverMap = serverMap;
                } else
                {
                    properties.setProperty("level-name", serverMap.getName());
                }

                properties.save(new FileOutputStream(new File(path + "/server.properties")), "Edit by CNS");
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }

        Document document = new Document();
        if (serverMap != null)
        {
            document.append("servermap", serverMap);
        }

        document
                .append("serverId", serverId)
                .append("uniqueId", uniqueId + "")
                .append("cloudid", CloudNetServer.getInstance().getCloudId())
                .append("group", group.getName())
                .append("netconnectorbind", group.getHostName() + ":" + (port - 1))
                .append("netconnectorremote", NetworkConnector.getInstance().getProxyHostName() + ":" + NetworkConnector.getInstance().getProxyPort())
                .append("properties", properties)
                .append("memory", group.getMemory())
                .append("mode", group.getGroupMode().name())
                .append("map", serverMap)
                .append("profile", profile)
                .append("beta", betaStatic)
                .append("hide", hide)
                .append("ssl", CloudNetServer.getInstance().getArguments().contains("--ssl"))
                .saveAsConfig(new File(path + "/cloudnet.json"));

        String[] command = new String[]{
                "java",
                "-XX:+UseG1GC"/*"-XX:+UseParallelGC"*/, "-XX:MaxGCPauseMillis=50", "-Xmn2M", "-XX:MaxPermSize=256M", "-Dcom.mojang.eula.agree=true", "-Djline.terminal=jline.UnsupportedTerminal",
                "-Xmx" + memory + "M",
                "-jar", (group.getServerType().equals(ServerGroupType.CAULDRON) ? "cauldron.jar" : "spigot.jar"), "nogui",
        };

        ProcessBuilder builder = new ProcessBuilder(command).directory(tmpDir);
        this.instance = builder.start();

        this.running.set(true);
        this.startup = System.currentTimeMillis();
        CloudNetServer.getInstance().getServers().put(serverId, this);

        if (NetworkConnector.getInstance() != null && NetworkConnector.getInstance().getChannel().isOpen())
        {
            NetworkConnector.getInstance().sendPacket(new PacketAddServer(this, port - 1));
        }

        if (priorityStop && group.getPriorityStopTime() != -1 && group.getPriorityStopTime() != 0)
        {
            ServerPriority priority = new ServerPriority(serverId, uniqueId, group);
            ScheduledTask task = CloudNetServer.getInstance().getServerPriorityScheduler().runTaskRepeatSync(priority, 0, 10);
            priority.setScheduledTask(task);
        }

        System.out.println(CloudNetServer.getInstance().getMessages().getProperty("startServer").replace("%server%", toString()));
        return true;
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

            if (group.getGroupMode().equals(ServerGroupMode.STATIC) || betaStatic)
            {
                copy();
            }

            try
            {
                FileUtils.deleteDirectory(new File(path));
            } catch (IOException e)
            {
            }
            new File("tmp/" + group.getName()).delete();

        }

        if (NetworkConnector.getInstance() != null)
        {
            NetworkConnector.getInstance().sendPacket(new PacketOutRemoveServer(serverId));
        }

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
        try
        {

            if(isAlive())
            {
                runCommand("save-all");
            }

            File file = new File(path);
            if (file.exists())
            {
                FileCopy.copyFilesInDirectory(file, new File("database/templates/" + group.getName() + "/global"));
                new File("database/templates/" + group.getName() + "/global/plugins/CloudNetAPI.jar").delete();
            }
        } catch (IOException e)
        {
        }

    }

    @Override
    public void runCommand(String command)
    {
        String x = command + "\n";
        if (instance == null) return;
        try
        {
            instance.getOutputStream().write(x.getBytes());
            instance.getOutputStream().flush();
        } catch (IOException ex)
        {
        }
    }


    @Override
    public String toString()
    {
        return "[" + serverId + "/uuid=" + uniqueId + "/port=" + port + "/memory=" + memory + "]";
    }
}
