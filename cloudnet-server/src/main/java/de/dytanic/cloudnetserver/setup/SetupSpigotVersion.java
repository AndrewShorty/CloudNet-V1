package de.dytanic.cloudnetserver.setup;

import de.dytanic.cloudnet.lib.threading.Runnabled;
import de.dytanic.cloudnetserver.CloudNetServer;
import jline.console.ConsoleReader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Tareko on 25.05.2017.
 */
public class SetupSpigotVersion
        implements Runnabled<ConsoleReader> {

    private final Runnabled<String> download = new Runnabled<String>() {
        @Override
        public void run(String url)
        {
            try
            {
                System.out.println("Downloading spigot.jar...");
                URLConnection connection = new URL(url).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                connection.connect();
                FileUtils.copyToFile(connection.getInputStream(), new File("database/spigot.jar"));
                System.out.println("Downloading Complete!");
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void run(ConsoleReader reader)
    {
        System.out.println(CloudNetServer.getInstance().getMessages().getProperty("spigotNotFound"));
        System.out.println(CloudNetServer.getInstance().getMessages().getProperty("spigotVersion"));

        String answer = null;

        String input;

        while (answer == null)
        {
            try
            {
                input = reader.readLine();
                switch (input.toLowerCase())
                {
                    case "taco":
                        answer = "taco";
                        break;
                    case "spigot":
                        answer = "spigot";
                        break;
                    case "paper":
                        answer = "paper";
                        break;
                    case "hose":
                        answer = "hose";
                        break;
                    default:
                        System.out.println(CloudNetServer.getInstance().getMessages().getProperty("versionNotFound"));
                        continue;
                }

            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        switch (answer)
        {
            case "taco":
                System.out.println(CloudNetServer.getInstance().getMessages().getProperty("tacoVersion"));
                while (true)
                {
                    try
                    {
                        switch (reader.readLine().toLowerCase())
                        {
                            case "1.8.8":
                                download.run("https://yivesmirror.com/files/tacospigot/TacoSpigot-1.8.8.jar");
                                return;
                            case "1.11.2":
                                download.run("https://yivesmirror.com/files/tacospigot/TacoSpigot-1.11.2-b102.jar");
                                return;
                            case "1.12":
                                download.run("https://yivesmirror.com/files/tacospigot/TacoSpigot-1.12-b104.jar");
                                return;
                            default:
                                System.out.println(CloudNetServer.getInstance().getMessages().getProperty("versionNotFound"));
                                break;
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            case "spigot":
                System.out.println(CloudNetServer.getInstance().getMessages().getProperty("defaultSpigotVersion"));
                while (true)
                {
                    try
                    {
                        switch (reader.readLine().toLowerCase())
                        {
                            case "1.7.10":
                                download.run("https://yivesmirror.com/files/spigot/spigot-1.7.10-SNAPSHOT-b1657.jar");
                                return;
                            case "1.8.8":
                                download.run("https://yivesmirror.com/files/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar");
                                return;
                            case "1.9.4":
                                download.run("https://yivesmirror.com/files/spigot/spigot-1.9.4-R0.1-SNAPSHOT.jar");
                                return;
                            case "1.11.2":
                                download.run("https://yivesmirror.com/files/spigot/spigot-1.11.2-R0.1-SNAPSHOT.jar");
                                return;
                            case "1.10.2":
                                download.run("https://yivesmirror.com/files/spigot/spigot-1.10.2-R0.1-SNAPSHOT.jar");
                                return;
                            case "1.12":
                                download.run("https://yivesmirror.com/files/spigot/spigot-1.12-R0.1-SNAPSHOT-b1334.jar");
                                return;
                            default:
                                System.out.println(CloudNetServer.getInstance().getMessages().getProperty("versionNotFound"));
                                break;
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            case "paper":
                System.out.println(CloudNetServer.getInstance().getMessages().getProperty("paperVersion"));
                while (true)
                {
                    try
                    {
                        switch (reader.readLine().toLowerCase())
                        {
                            case "1.8.8":
                                download.run("https://yivesmirror.com/files/paperspigot/PaperSpigot-1.8.8-R0.1-SNAPSHOT-latest.jar");
                                return;
                            case "1.11.2":
                                download.run("https://yivesmirror.com/files/paperspigot/PaperSpigot-1.11.2-b1104.jar");
                                return;
                            case "1.12":
                                download.run("https://yivesmirror.com/files/paperspigot/PaperSpigot-latest.jar");
                                return;
                            default:
                                System.out.println(CloudNetServer.getInstance().getMessages().getProperty("versionNotFound"));
                                break;
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            case "hose":
                System.out.println(CloudNetServer.getInstance().getMessages().getProperty("hoseVersion"));
                while (true)
                {
                    try
                    {
                        switch (reader.readLine().toLowerCase())
                        {
                            case "1.8.8":
                                download.run("https://yivesmirror.com/files/hose/hose-1.8.8.jar");
                                return;
                            case "1.9.4":
                                download.run("https://yivesmirror.com/files/hose/hose-1.9.4.jar");
                                return;
                            case "1.10.2":
                                download.run("https://yivesmirror.com/files/hose/hose-1.10.2.jar");
                                return;
                            case "1.11.2":
                                download.run("https://yivesmirror.com/files/hose/hose-1.11.2.jar");
                                return;
                            default:
                                System.out.println(CloudNetServer.getInstance().getMessages().getProperty("versionNotFound"));
                                break;
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
        }
    }
}
