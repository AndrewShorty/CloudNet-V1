package de.dytanic.cloudnetserver.network.packets;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnet.servergroup.ServerGroup;
import de.dytanic.cloudnetserver.CloudNetServer;
import de.dytanic.cloudnetserver.network.NetworkConnector;

/**
 * Created by Tareko on 07.07.2017.
 */
public class PacketInManageGroups
        extends Packet {

    public PacketInManageGroups()
    {
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender)
    {
        if(data.contains("group"))
        {
            ServerGroup group = data.getObject("group", new TypeToken<ServerGroup>(){}.getType());
            switch (UpdateType.valueOf(data.getString("type")))
            {
                case UPDATE:
                    CloudNetServer.getInstance().getConfig().addNewGroup(group);
                    NetworkConnector.getInstance().sendPacket(new PacketOutServerData());
                    break;
                case REMOVE:
                    CloudNetServer.getInstance().getConfig().removeGroup(group);
                    NetworkConnector.getInstance().sendPacket(new PacketOutServerData());
                    break;
                case CREATE:
                    CloudNetServer.getInstance().getConfig().addNewGroup(group);
                    NetworkConnector.getInstance().sendPacket(new PacketOutServerData());
                    break;
            }
        }
    }

    public enum UpdateType {
        REMOVE,
        UPDATE,
        CREATE;
    }

}