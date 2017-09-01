package de.dytanic.cloudnet.bukkitproxy.command;

import de.dytanic.cloudnet.bukkitproxy.api.CloudProxy;
import de.dytanic.cloudnet.lib.document.Document;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Tareko on 01.06.2017.
 */
public class CommandAlert
        extends Command {

    public CommandAlert()
    {
        super("alert", "bungeecord.command.alert");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {
        if (args.length > 0)
        {
            StringBuilder builder = new StringBuilder().append(CloudProxy.getInstance().getProxyLayout().getPrefix());
            for (short i = 0; i < args.length; i++)
            {
                builder.append(ChatColor.translateAlternateColorCodes('&', args[i])).append(" ");
            }
            CloudProxy.getInstance().sendCustomProxyMessage(
                    "broadcast_message",
                    new Document().append("message", builder.substring(0))
            );
        } else
        {
            commandSender.sendMessage("§c/alert <message>");
        }
    }

    @Override
    public String[] getAliases()
    {
        return new String[]{"bc", "broadcast"};
    }
}
