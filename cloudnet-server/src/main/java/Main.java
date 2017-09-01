import de.dytanic.cloudnet.executions.AutoUpdateService;
import de.dytanic.cloudnet.executions.HeaderFunction;
import de.dytanic.cloudnet.logging.CloudNetLogging;
import de.dytanic.cloudnet.logging.command.CommandPool;
import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.CloudNetServerConfig;
import de.dytanic.cloudnetserver.command.*;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tareko on 21.05.2017.
 */
public final class Main {

    private Main(){}

    private static void help()
    {
        System.out.println("\n[CNS] SUB arguments:");
        System.out.println("[CNS] --disallow_proxy_download | disallows to download a BungeeCord.jar");
        System.out.println("[CNS] --disallow_spigot_download | disallows to download a spigot.jar");
        System.out.println("[CNS] --ssl | allows to use Ssl");
        System.out.println("[CNS] --disable_autogroup_setup | disallows to setup a new group on startup if the cns doesn't have one group.");
    }

    private static void registerCommands(CommandPool commandPool)
    {
        commandPool
                .registerCommand(new CommandStop())
                .registerCommand(new CommandHelp())
                .registerCommand(new CommandReload())
                .registerCommand(new CommandStart())
                .registerCommand(new CommandCmd())
                .registerCommand(new CommandCreateGroup())
                .registerCommand(new CommandStopServer())
                .registerCommand(new CommandScreen())
                .registerCommand(new CommandClear())
                .registerCommand(new CommandCopy())
                .registerCommand(new CommandList())
                .registerCommand(new CommandStopGroup())
                .registerCommand(new CommandLeave())
                .registerCommand(new CommandDeleteGroup())
                .registerCommand(new CommandChangeMaintenance())
        ;
    }

    //--groupSetup wert

    public static void main(String[] args)
    {
        System.setProperty( "java.net.preferIPv4Stack", "true" );
        System.setProperty("file.encoding", "UTF8");
        System.setProperty("io.netty.noPreferDirect", "true");
        System.setProperty("client.encoding.override", "UTF-8");
        System.setProperty("io.netty.maxDirectMemory", "0");

        CloudNetLogging logger;
        try{ FileUtils.deleteDirectory(new File("tmp")); }catch (Exception ex){}
        try {
            List<String> arguments = Arrays.asList(args);

            if(arguments.contains("--help"))
            {
                help();
                return;
            }

            HeaderFunction headerFunction = new HeaderFunction();
            if(!headerFunction.isExecuted())
                System.out.println(NetworkUtils.header((short) 1));
            else
                System.out.println(NetworkUtils.headerOut((short)1));

            logger = new CloudNetLogging("Server");

            CloudNetServerConfig config = new CloudNetServerConfig(logger.getReader());
            CloudNetServer cloudNetServer = new CloudNetServer(arguments, logger, config, config.getMessages());
            Runtime.getRuntime().addShutdownHook(cloudNetServer.getServiceShutdown());

            if (!cloudNetServer.bootstrap())
            {
                System.exit(0);
            }

            if(config.getProperties().getProperty("autoupdate").equalsIgnoreCase("true"))
            new AutoUpdateService("CloudNet-Server", cloudNetServer.getVersion());

        }catch (Exception ex)
        {

            ex.printStackTrace();

            System.out.println("This software has an error by executing the Bootstrap. Please report with the " +
                    "logging this exception and kill the cloudnet-server with Ctrl+C");

            System.out.println("\n     __\n" +
                    " _  / /\n" +
                    "(_)| | \n" +
                    " _ | | \n" +
                    "(_)| | \n" +
                    "    \\_\\\n");

            while (true) try
            {
                Thread.sleep(700000);
            } catch (InterruptedException e) {}
        }

        if(logger == null) return; if(logger.getReader() == null) return;

            if(CloudNetServer.getInstance().getArguments().contains("--disable_jline"))
            {
                CommandPool pool = CommandPool.newCommandPoolHandler();
                registerCommands(pool);
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                String input;

                try
                {
                    while (CloudNetServer.IS_RUNNING && (input = reader.readLine()) != null)
                    {
                        if(!pool.dispatchCommand(input))
                        {
                            System.out.println("command not found, use the command \"help\" for more informations.");
                        }
                        return;
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }else
            {
                CommandPool pool = CommandPool.newCommandPoolHandler();
                logger.getReader().addCompleter(pool);
                registerCommands(pool);
                String input;
                System.out.println("Use the command \"help\" for help!");
                while (true)
                {
                    try{
                        while (CloudNetServer.IS_RUNNING && (input = logger.getReader().readLine()) != null)
                        {
                            if(!pool.dispatchCommand(input))
                            {
                                if(CloudNetServer.getInstance() != null)
                                {
                                    if(CloudNetServer.getInstance().getScreenSystem().getScreen() != null)
                                    CloudNetServer.getInstance().getScreenSystem().command(input);
                                    else
                                    System.out.println(CloudNetServer.getInstance().getMessages().getProperty("commandNotFound"));
                                }
                            }
                        }
                    }catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
    }
}