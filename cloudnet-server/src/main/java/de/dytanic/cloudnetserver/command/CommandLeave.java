package de.dytanic.cloudnetserver.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnetserver.CloudNetServer;

import java.io.IOException;

/**
 * Created by Tareko on 23.06.2017.
 */
public class CommandLeave
            extends Command{

    public CommandLeave()
    {
        super("leave", "cloudnet.server.command.leave");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        try {CloudNetServer.getInstance().getLogger().getReader().clearScreen();} catch (IOException e) {}

        CloudNetServer.getInstance().getScreenSystem().closeScreen();
        System.out.println(NetworkUtils.header((short) 1));

    }
}