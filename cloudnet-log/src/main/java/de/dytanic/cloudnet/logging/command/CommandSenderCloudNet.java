package de.dytanic.cloudnet.logging.command;

import lombok.Getter;

/**
 * Created by Tareko on 23.05.2017.
 */
@Getter
public class CommandSenderCloudNet
            implements CommandSender{

    @Override
    public String getName()
    {
        return "CloudNet";
    }

    @Override
    public void sendMessage(String... message)
    {
        for(String m : message)
        {
            System.out.println(m);
        }
    }

    @Override
    public boolean hasPermission(String perm)
    {
        return true;
    }
}
