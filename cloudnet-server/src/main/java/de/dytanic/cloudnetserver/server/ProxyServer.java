package de.dytanic.cloudnetserver.server;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.lib.interfaces.Executeable;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.network.NetworkConnector;
import de.dytanic.cloudnetserver.network.packets.PacketAddProxy;
import de.dytanic.cloudnetserver.network.packets.PacketOutRemoveProxy;
import de.dytanic.cloudnetserver.server.screen.IScreen;
import de.dytanic.cloudnetserver.util.FileCopy;
import de.dytanic.cloudnetserver.util.ProxyConfig;
import de.dytanic.cloudnetserver.util.Utils;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Tareko on 24.05.2017.
 */
@Getter
public class ProxyServer
            implements Executeable, IScreen{

    private volatile Process instance;
    private String serverId;
    private UUID uniqueId;
    private int port;
    private ProxyConfig config;
    private String path;
    private int memory;

    private Document metaData;

    public ProxyServer(String serverId, int port, ProxyConfig proxyConfig)
    {
        this.serverId = serverId;
        this.uniqueId = UUID.randomUUID();
        this.port = port;
        this.config = proxyConfig;
        this.memory = proxyConfig.getMemory();

        this.metaData = new Document()
                .append("created", Utils.SIMPLE_DATE_FORMAT.format(new Date()))
                .append("uuid", uniqueId.toString())
                .append("memory", memory)
                .append("port", port)
                .append("serverId", serverId)
                .append("proxyconfig", proxyConfig);
    }

    @Override
    public boolean bootstrap() throws Exception
    {
        if(new File("database/proxy").exists())
        {
            if(!config.isStaticProxyMode())
            {
                new File("tmp/Proxy").mkdir();
                path = "tmp/Proxy/" + serverId;
            }
            else
            {
                path = "database/proxy";
            }
            File pathFile = new File(path);
            pathFile.mkdir();
            new File(path + "/plugins").mkdir();

            if(!config.isStaticProxyMode())
            FileCopy.copyFilesInDirectory(new File("database/proxy"), pathFile);

            //External
            {
                File server_icon = new File("database/server-icon.png");
                if(server_icon.exists())
                {
                    FileCopy.copyFileToDirectory(server_icon, pathFile);
                }
                if(!new File(path + "/config.yml").exists())
                {
                    FileCopy.insertData("files/config.yml", path + "/config.yml");
                }
                if(CloudNetServer.getInstance().getConfig().getProperties().getProperty("ip").equalsIgnoreCase("127.0.0.1") || CloudNetServer.getInstance().getConfig().getProperties().getProperty("ip").equalsIgnoreCase("127.0.1.1"))
                {
                    FileCopy.rewriteFileUtils(new File(path + "/config.yml"),  "0.0.0.0:" + port);
                }
                else
                {
                    FileCopy.rewriteFileUtils(new File(path + "/config.yml"), CloudNetServer.getInstance().getConfig().getProperties().getProperty("ip") + ":" + port);
                }
            }

            FileCopy.insertData("files/CloudNetAPI.jar", path + "/plugins/CloudNetAPI.jar");

            new Document()
                    .append("serverId", serverId)
                    .append("cloudid", CloudNetServer.getInstance().getCloudId())
                    .append("uniqueId", uniqueId+"")
                    .append("fallback", config.getFallback())
                    .append("host", CloudNetServer.getInstance().getConfig().getProperties().getProperty("ip"))
                    .append("port", port)
                    .append("memory", config.getMemory())
                    .append("netconnectorbind", CloudNetServer.getInstance().getHostName() + ":" + (port - 1))
                    .append("netconnectorremote", NetworkConnector.getInstance().getProxyHostName() + ":" + NetworkConnector.getInstance().getProxyPort())
                    .append("ssl", CloudNetServer.getInstance().getArguments().contains("--ssl"))
                    .saveAsConfig(new File(path + "/cloudnet.json"));

            String[] command = new String[] {

                    "java",
                    "-XX:+UseG1GC","-XX:MaxGCPauseMillis=50", "-Djline.terminal=jline.UnsupportedTerminal","-Xmn2M","-XX:MaxPermSize=256M", "-DIReallyKnowWhatIAmDoingISwear=true",
                    "-Xmx" + config.getMemory() + "M",
                    "-jar", (new File(path + "/Waterfall.jar").exists() ? "Waterfall.jar" : "BungeeCord.jar"), "-o", "true", "-p"

            };
            this.instance = new ProcessBuilder(command).directory(pathFile).start();
            CloudNetServer.getInstance().getProxys().put(serverId, this);

            if(NetworkConnector.getInstance() != null)
            {
                NetworkConnector.getInstance().sendPacket(new PacketAddProxy(this));
            }

            System.out.println(CloudNetServer.getInstance().getMessages().getProperty("startProxy").replace("%proxy%", this.toString()));
        }

        return false;
    }

    @Override
    public void runCommand(String command)
    {
        if(instance == null) return;
        String x = command + "\n";
        try{
            instance.getOutputStream().write(x.getBytes());
            instance.getOutputStream().flush();
        }catch (IOException ex)
        {
        }
    }

    public boolean isAlive()
    {
        try
        {
            return instance != null && instance.isAlive() && instance.getInputStream().available() != -1;
        } catch (IOException e)
        {
            return false;
        }
    }

    @Override
    public boolean shutdown()
    {
        if(instance != null)
        {
            if(instance.isAlive())
            {
                try
                {
                    instance.getOutputStream().write("end\n".getBytes());
                    instance.getOutputStream().flush();
                    Thread.sleep(1000);
                }catch (IOException | InterruptedException ex)
                {
                    ex.printStackTrace();
                }
            }

            CloudNetServer.getInstance().getScreenSystem().checkAndRemove(this);
            instance.destroy();

            if(CloudNetServer.getInstance().getConfig().getConfig().getBoolean("saving_server_meta"))
            {
                File dir = new File("database/records/" + serverId + "#" + uniqueId.toString());
                dir.mkdir();

                try{ FileCopy.copyFilesInDirectory(new File(path + "/proxy.logging.0"), dir); }catch (Exception ex) {}

                File file = new File("database/records/" + serverId + "#" + uniqueId.toString() + "/metadata.cloudnet");
                metaData.saveAsConfig(file);

            }

            if(!config.isStaticProxyMode())
            {
                try {FileUtils.deleteDirectory(new File(path));} catch (IOException e) {}
                new File("tmp/Proxy").delete();
            }

            if(NetworkConnector.getInstance() != null && NetworkConnector.getInstance().getChannel().isOpen())
            {
                NetworkConnector.getInstance().sendPacket(new PacketOutRemoveProxy(serverId));
            }

            CloudNetServer.getInstance().getScreenSystem().checkAndRemove(this);

            System.out.println(CloudNetServer.getInstance().getMessages().getProperty("stopProxy").replace("%proxy%", this.toString()));
        }

        CloudNetServer.getInstance().getProxys().remove(serverId);

        return false;
    }

    @Override
    public String toString()
    {
        return "[" + serverId + "/uuid=" + uniqueId + "/port=" + port + "/memory=" + memory + "]";
    }
}