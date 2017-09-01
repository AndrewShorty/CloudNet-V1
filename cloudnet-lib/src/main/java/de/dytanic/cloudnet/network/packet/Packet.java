package de.dytanic.cloudnet.network.packet;

import de.dytanic.cloudnet.lib.document.Document;
import lombok.Getter;

/**
 * Packet objective
 */
public class Packet {

    @Getter protected int id;
    @Getter protected Document data;

    public Packet() {}

    public Packet(int id) {
        this.id = id;
        this.data = new Document();
    }

    public Packet(Document data)
    {
        this.data = data;
        this.id = 0;
    }

    public Packet(int id, Document data)
    {
        this.id = id;
        this.data = data;
    }

    public void handleInput(Document data, PacketSender packetSender) {}
}