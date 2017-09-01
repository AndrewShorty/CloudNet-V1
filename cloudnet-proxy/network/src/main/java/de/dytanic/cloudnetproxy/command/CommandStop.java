package de.dytanic.cloudnetproxy.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;

import java.util.concurrent.TimeUnit;

/**
 * Created by Tareko on 26.05.2017.
 */
public class CommandStop
            extends Command {

    public CommandStop()
    {
        super("stop", "cloudnet.proxy.command.stop");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {

        System.out.println("Please wait a second...");

        System.out.println("Thanks for using this software :)");

        System.exit(0);
    }
}
