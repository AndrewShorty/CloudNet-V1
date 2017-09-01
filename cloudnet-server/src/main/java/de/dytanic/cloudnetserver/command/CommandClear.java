package de.dytanic.cloudnetserver.command;

import de.dytanic.cloudnet.logging.CloudNetLogging;
import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;
import de.dytanic.cloudnet.network.NetworkUtils;
import de.dytanic.cloudnetserver.CloudNetServer;

import java.io.IOException;

/**
 * Created by Tareko on 05.06.2017.
 */
public class CommandClear
        extends Command{

   public CommandClear()
   {
           super("clear", "cloudnet.server.command.clear");
   }

   @Override
   public void onExecuteCommand(CommandSender sender, String[] args)
   {
           try
           {
                   ((CloudNetLogging)CloudNetServer.getInstance().getLogger())
                           .getReader().clearScreen();
               System.out.println(NetworkUtils.header((short) 1));
           } catch (IOException e) {}
   }
}
