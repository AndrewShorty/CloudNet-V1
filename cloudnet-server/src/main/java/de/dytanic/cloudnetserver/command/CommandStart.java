package de.dytanic.cloudnetserver.command;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnet.logging.command.TabCompleteable;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tareko on 25.05.2017.
 */
public class CommandStart
            extends Command implements TabCompleteable{

    public CommandStart()
    {
        super("start", "cloudnet.server.command.start", "st");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 1:
                if(args[0].equalsIgnoreCase("proxy") || args[0].equalsIgnoreCase("-p"))
                {
                    CloudNetServer.getInstance().startProxy();
                }
                break;
            case 2:
                if(args[0].equalsIgnoreCase("proxy") || args[0].equalsIgnoreCase("-p"))
                {
                    if(Utils.checkAsNumber(args[1]))
                    {
                        for(short i = 0; i < Short.parseShort(args[1]); i++)
                        {

                            CloudNetServer.getInstance().startProxy();
                            try
                            {
                                Thread.sleep(1000);
                            } catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    else
                    {
                        CloudNetServer.getInstance().startProxy();
                    }
                }
                if(args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("-s"))
                {
                    if(CloudNetServer.getInstance().getGroups().containsKey(args[1]))
                    {
                        CloudNetServer.getInstance().getScheduler().runTaskSync(new Runnable() {
                            @Override
                            public void run()
                            {
                                CloudNetServer.getInstance().startServer(CloudNetServer.getInstance().getGroups().get(args[1]), new Document(), false, false);
                            }
                        });
                    }
                    else
                    {
                        sender.sendMessage("The group is not registered on this CNS.");
                    }
                }
                break;
            case 3:
                if(args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("-s"))
                {
                    if(CloudNetServer.getInstance().getGroups().containsKey(args[1]))
                    {
                        if(Utils.checkAsNumber(args[2]))
                        {
                            for (short i = 0; i < Short.parseShort(args[2]); i++)
                            {
                                CloudNetServer.getInstance().startServer(CloudNetServer.getInstance().getGroups().get(args[1]), new Document(), false, false);
                            }
                        }
                        else
                        {
                            CloudNetServer.getInstance().startServer(CloudNetServer.getInstance().getGroups().get(args[1]), new Document(), false, false);
                        }
                    }
                    else
                    {
                        sender.sendMessage("The group is not registered on this CNS.");
                    }
                }
                else
                if(args[0].equalsIgnoreCase("static") || args[0].equalsIgnoreCase("-sta"))
                {
                    if(CloudNetServer.getInstance().getGroups().containsKey(args[2]))
                    {
                        CloudNetServer.getInstance().getScheduler().runTaskSync(new Runnable() {
                            @Override
                            public void run()
                            {
                                CloudNetServer.getInstance().startStaticServer(CloudNetServer.getInstance().getGroups().get(args[2]), args[1], new Document(), false ,
                                        CloudNetServer.getInstance().getGroups().get(args[2]).getMemory());
                            }
                        });
                    }
                    else
                    {
                        sender.sendMessage("The group doesn't exists");
                    }
                }
                break;
            default:
                sender.sendMessage("start proxy");
                sender.sendMessage("start server <group> <count>");
                sender.sendMessage("start static <serverId> <group>");
                break;
        }
    }

    @Override
    public List<String> onTab(long argsLength, String lastWord)
    {
        List<String> groups = new ArrayList<>();
        groups.addAll(CloudNetServer.getInstance().getGroups().keySet());
        return groups;
    }
}
