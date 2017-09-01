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
public class SetupProxyVersion
            implements Runnabled<ConsoleReader> {

    @Override
    public void run(ConsoleReader value)
    {
        boolean access = false;
        String input = null;
        if(CloudNetServer.getInstance().getArguments().contains("--autodownload_proxy"))
        {
            access = true;
            input = "default";
        }else
        {
            System.out.println(CloudNetServer.getInstance().getMessages().getProperty("proxyNotFound"));
            System.out.println(CloudNetServer.getInstance().getMessages().getProperty("proxyVersionChose"));

            while (!access)
            {
                try
                {
                    input = value.readLine();
                    switch (input.toLowerCase())
                    {
                        case "waterfall":
                            access = true;
                            break;
                        case "default":
                            access = true;
                            break;
                        case "hexacord":
                            access = true;
                            break;
                        case "travertine":
                            access = true;
                            break;
                        default:
                            System.out.println(CloudNetServer.getInstance().getMessages().getProperty("proxyVersionNotFound"));
                            continue;
                    }

                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        String url;

        switch (input)
        {
            case "waterfall":
                //url = "https://ci.destroystokyo.com/job/Waterfall/lastSuccessfulBuild/artifact/Waterfall-Proxy/bootstrap/target/Waterfall.jar";
                url = "https://yivesmirror.com/files/waterfall/Waterfall-131.jar";
                break;
            case "hexacord":
                url = "https://yivesmirror.com/files/hexacord/HexaCord-v154.jar";
                break;
            case "travertine":
                url = "https://yivesmirror.com/files/travertine/Travertine-latest.jar";
                break;
            default:
                url = "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar";
                break;
        }

        try
        {
            System.out.println("Downloading BungeeCord.jar...");
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            FileUtils.copyToFile(connection.getInputStream(), new File("database/proxy/BungeeCord.jar"));
            System.out.println("Downloading Complete!");
            System.out.println(" ");
            System.out.println(CloudNetServer.getInstance().getMessages().getProperty("proxyDir"));
            System.out.println(" ");
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
