package de.dytanic.cloudnet.logging.command;

import de.dytanic.cloudnet.lib.interfaces.Nameable;

/**
 * Created by Tareko on 23.05.2017.
 */
public interface CommandSender
        extends Nameable {

    void sendMessage(String...message);
    boolean hasPermission(String permission);

}
