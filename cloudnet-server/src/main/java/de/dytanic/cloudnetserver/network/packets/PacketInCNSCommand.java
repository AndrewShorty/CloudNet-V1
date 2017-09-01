package de.dytanic.cloudnetserver.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.logging.command.CommandPool;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;

/**
 * Created by Tareko on 10.07.2017.
 */
public class PacketInCNSCommand
                extends Packet {

    public PacketInCNSCommand()
    {
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender)
    {
        if(data.contains("command"))
        {
            CommandPool.newCommandPoolHandler().dispatchCommand(data.getString("command"));
        }
    }
}