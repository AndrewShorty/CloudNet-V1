package de.dytanic.cloudnet.network.packet;
import lombok.Getter;

/**
 * Created by Tareko on 30.05.2017.
 */
@Getter
public final class PacketHandleProcessor {


    //Removing resource on this instance

    public static abstract class PacketHandlerAbstract
    {
        public abstract void handleIncomingPacket(Packet packet, PacketSender packetSender);
    }
}