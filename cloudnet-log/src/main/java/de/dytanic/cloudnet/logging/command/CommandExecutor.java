package de.dytanic.cloudnet.logging.command;

/**
 * Created by Tareko on 23.05.2017.
 */
public interface CommandExecutor
{
    void onExecuteCommand(CommandSender sender, String[] args);
}
