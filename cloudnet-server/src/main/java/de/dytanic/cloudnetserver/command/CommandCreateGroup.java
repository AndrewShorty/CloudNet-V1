package de.dytanic.cloudnetserver.command;

import de.dytanic.cloudnet.logging.CloudNetLogging;
import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.setup.SetupServerGroup;

/**
 * Created by Tareko on 25.05.2017.
 */
public class CommandCreateGroup
            extends Command {

    public CommandCreateGroup()
    {
        super("creategroup", "cloudnet.server.command.creategroup", "cg");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        new SetupServerGroup().run(((CloudNetLogging)CloudNetServer.getInstance().getLogger()).getReader());
    }
}
