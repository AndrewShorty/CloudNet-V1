import de.dytanic.cloudnet.executions.AutoUpdateService;
import de.dytanic.cloudnet.executions.HeaderFunction;
import de.dytanic.cloudnet.logging.CloudNetLogging;
import de.dytanic.cloudnet.logging.command.CommandPool;
import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.command.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Tareko on 26.05.2017.
 */
public final class Main {

    private Main()
    {
    }

    private static void a(CommandPool pool)
    {
        pool
                .registerCommand(new CommandStop())
                .registerCommand(new CommandWhitelist())
                .registerCommand(new CommandHelp())
                .registerCommand(new CommandReload())
                .registerCommand(new CommandAdd())
                .registerCommand(new CommandReloadPermissions())
                .registerCommand(new CommandClear())
                .registerCommand(new CommandList())
                .registerCommand(new CommandGroup());
    }

    public static void main(String[] args)
    {

        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("file.encoding", "UTF8");
        System.setProperty("io.netty.noPreferDirect", "true");
        System.setProperty("client.encoding.override", "UTF-8");
        System.setProperty("io.netty.maxDirectMemory", "0");

        HeaderFunction headerFunction = new HeaderFunction();
        if (!headerFunction.isExecuted())
            System.out.println(NetworkUtils.header((short) 0));
        else
            System.out.println(NetworkUtils.headerOut((short) 0));

        List<String> arguments = Arrays.asList(args);
        CloudNetLogging logging;
        try
        {
            logging = new CloudNetLogging("ProxyDB");

            CloudNetProxy cloudNetProxy = new CloudNetProxy(arguments, logging);

            Runtime.getRuntime().addShutdownHook(new Thread(cloudNetProxy));

            if (cloudNetProxy.getConfig().getConfiguration().getBoolean("autoupdate"))
                new AutoUpdateService("CloudNet-Proxy", cloudNetProxy.getVersion());

        } catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println("This Software has an error by executing the bootstrap. Please report this error and kill this software with Crtl+C");
            System.out.println("\n     __\n" +
                    " _  / /\n" +
                    "(_)| | \n" +
                    " _ | | \n" +
                    "(_)| | \n" +
                    "    \\_\\\n");
            while (true) try
            {
                Thread.sleep(400000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        if (logging == null) System.exit(0);

        CommandPool commandPool = CommandPool.newCommandPoolHandler();
        a(commandPool);

        String input;
        while (true)
        {
            try
            {
                while ((input = logging.getReader().readLine()) != null)
                {
                    if (!commandPool.dispatchCommand(input))
                    {
                        System.out.println("Commmad not found, pls use \"help\" for help!");
                    }
                }
            } catch (Exception ex)
            {
                ex.getStackTrace();
            }
        }
    }
}
