package de.dytanic.cloudnetproxy.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.NetworkInfo;

/**
 * Created by Tareko on 27.05.2017.
 */
public class CommandAdd
            extends Command {

    public CommandAdd()
    {
        super("add", "cloudnet.proxy.command.addcns", "cns", "addcns", "ccns", "cc");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 2:
                    CloudNetProxy.getInstance().getConfig().addCNS(new NetworkInfo(args[0], args[1], 0));
                    System.out.println("The CNS [" + args[0] + "] is now added on [" + args[1] + "]");
                break;
            default:
                sender.sendMessage(
                        "add <id> <host>"
                );
                break;
        }
    }
}