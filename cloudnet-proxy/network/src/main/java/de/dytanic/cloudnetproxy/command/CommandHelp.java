package de.dytanic.cloudnetproxy.command;

import de.dytanic.cloudnet.logging.command.Command;
import de.dytanic.cloudnet.logging.command.CommandSender;

import java.lang.management.ManagementFactory;

/**
 * Created by Tareko on 27.05.2017.
 */
public class CommandHelp
            extends Command {

    public CommandHelp()
    {
        super("help", "cloudnet.proxy.command.help", "h");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        sender.sendMessage(
                " ",
                "CloudNet-Proxy:",
                "stop: \"Stop the proxy\"",
                "whitelist: \"Add or remove an IP the Whitelist-System\"",
                "add: \"Add a CNS system to the existing network\"",
                "group: \"Modify the permissions of the network\"",
                "rl: \"Reload the proxy layout from the config.yml\"",
                "rlp: \"Reload the permission backend\"",
                "list: \"List all servers and proxy on network",
                " ",
                "Usage: " + (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L) + "/" + (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1048576L) + "MB"
        );
    }
}