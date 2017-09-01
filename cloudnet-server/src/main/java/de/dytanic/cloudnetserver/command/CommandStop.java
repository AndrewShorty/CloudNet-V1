package de.dytanic.cloudnetserver.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;

/**
 * Created by Tareko on 24.05.2017.
 */
public class CommandStop
            extends Command{

    public CommandStop()
    {
        super("stop", "cloudnet.server.command.stop");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        System.exit(0);
    }
}