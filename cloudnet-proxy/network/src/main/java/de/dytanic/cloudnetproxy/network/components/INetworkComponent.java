package de.dytanic.cloudnetproxy.network.components;

import de.dytanic.cloudnet.network.packet.Packet;
import de.dytanic.cloudnet.network.packet.PacketSender;
import de.dytanic.cloudnetproxy.network.NetworkInfo;
import io.netty.channel.Channel;

/**
 * Created by Tareko on 27.05.2017.
 */
public interface INetworkComponent extends PacketSender {

    String getServerId();

    NetworkInfo getNetworkInfo();

    CNS getCloudNetServer();

    Channel getChannel();

    void setChannel(Channel channel);

    default void sendPacket(Packet packet)
    {
        if (getChannel() == null) return;
        if (getChannel().eventLoop().inEventLoop())
        {
            getChannel().writeAndFlush(packet);
        } else
        {
            getChannel().eventLoop().execute(new Runnable() {
                @Override
                public void run()
                {
                    getChannel().writeAndFlush(packet);
                }
            });
        }
    }

    default void sendPacket(Packet... packets)
    {
        for (Packet packet : packets)
        {
            sendPacket(packet);
        }
    }
}