package de.dytanic.cloudnetproxy.network.packets;

import de.dytanic.cloudnet.lib.document.Document;
import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetproxy.CloudNetProxy;
import de.dytanic.cloudnetproxy.network.components.CNS;
import de.dytanic.cloudnetproxy.network.components.MinecraftServer;

/**
 * Created by Tareko on 02.06.2017.
 */
public class PacketIOHandleStartAndStop
            extends Packet {

    public PacketIOHandleStartAndStop()
    {
        super(0);
    }

    public PacketIOHandleStartAndStop(Document handler, HandleType handleType)
    {
        super(209, new Document().append("data", handler).append("type", handleType));
    }

    private PacketIOHandleStartAndStop(Document data)
    {
        super(209, data);
    }

    @Override
    public void handleInput(Document document, PacketSender sender)
    {

        Document handle = document.getDocument("data");
        if(document.contains("type") && document.getString("type").equalsIgnoreCase(HandleType.START_SERVER.name()))
        {
            if(handle.contains("group"))
            {

                CNS active = null;
                int used = 0;

                for(CNS cns : CloudNetProxy.getInstance().getCnsSystems().values())
                {
                    int use = cns.getUsedMemory();
                    if (cns.getGroups().containsKey(handle.getString("group"))
                            && use < cns.getMaxMemory())
                    {
                        if(active != null)
                        {
                            if(use <= used)
                            {
                                active = cns;
                                used = use;
                            }
                        }
                        else
                        {
                            active = cns;
                            used = use;
                        }
                    }
                }

                Packet packet = new PacketIOHandleStartAndStop(document);
                if(active != null) active.sendPacket(packet);
            }
        }
        else
            if(document.contains("type") && document.getString("type").equalsIgnoreCase(HandleType.WRITE_COMMAND.name())
                    && handle.contains("serverid"))
            {

                MinecraftServer minecraftServer = CloudNetProxy.getInstance().getServer(handle.getString("serverid"));
                if(minecraftServer.getChannel() != null)
                {
                    minecraftServer.sendPacket(new PacketIOHandleStartAndStop(handle, HandleType.WRITE_COMMAND));
                }
            }
        else
        {
            Packet packet = new PacketIOHandleStartAndStop(document);
            for(CNS cns : CloudNetProxy.getInstance().getCnsSystems().values())
            {
                cns.sendPacket(packet);
            }
        }
    }

    public enum HandleType
    {
        START_SERVER,
        WRITE_COMMAND,
        STOP_SERVER;
    }
}