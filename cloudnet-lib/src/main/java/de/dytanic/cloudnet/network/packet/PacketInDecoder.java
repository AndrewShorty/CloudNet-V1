package de.dytanic.cloudnet.network.packet;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.network.NetworkUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by Tareko on 31.05.2017.
 */
public class PacketInDecoder
                extends ByteToMessageDecoder{
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> output) throws Exception
    {
        int vx = in.readableBytes();
        if(vx != 0)
        {
            int i = readVarInt(in);
            Packet packet = null;
            try
            {
                String input = in.readBytes(i).toString(StandardCharsets.UTF_8);
                packet = NetworkUtils.GSON.fromJson(input, new TypeToken<Packet>(){}.getType());
            } catch (Exception ex)
            {
                ex.printStackTrace();
                in.clear();
            }
            if (packet != null)
                output.add(packet);
        }
    }

    private int readVarInt(ByteBuf in)
    {
        int number = 0;
        int round = 0;
        byte currentByte;

        do {
            currentByte = in.readByte();
            number |= (currentByte & 127) << round++ * 7;

            if (round > 5) {
                throw new RuntimeException();
            }
        } while ((currentByte & 128) == 128);

        return number;
    }
}