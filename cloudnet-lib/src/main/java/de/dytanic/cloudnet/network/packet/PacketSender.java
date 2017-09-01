package de.dytanic.cloudnet.network.packet;

import de.dytanic.cloudnet.lib.interfaces.Nameable;

/**
 * Created by Tareko on 24.05.2017.
 */
public interface PacketSender
            extends Nameable{

    //Änderungstest
    //DefaultCommit
    void sendPacket(Packet...packets);
    void sendPacket(Packet packet);
}