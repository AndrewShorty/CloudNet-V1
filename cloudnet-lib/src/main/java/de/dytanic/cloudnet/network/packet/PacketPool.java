package de.dytanic.cloudnet.network.packet;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tareko on 22.05.2017.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PacketPool {

    private final java.util.Map<Integer, Class<? extends Packet>> pool = new ConcurrentHashMap<>();

    public PacketPool append(int id, Class<? extends Packet> packetClass)
    {
        pool.put(id, packetClass);
        return this;
    }

    public Packet findHandler(int id)
    {
        if(pool.containsKey(id))
        {
            try
            {
                return pool.get(id).newInstance();
            } catch (InstantiationException e)
            {
                return null;
            } catch (IllegalAccessException e)
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    public static PacketPool newSimpledPacketPool()
    {
        return new PacketPool();
    }

}
