package de.dytanic.cloudnet.network.packet;

import de.dytanic.cloudnet.network.NetworkUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * Created by Tareko on 31.05.2017.
 */
public class PacketOutEncoder
            extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf outPut) throws Exception
    {
        if(!(o instanceof Packet)) return;
        Packet packet = (Packet) o;

        byte[] values = NetworkUtils.GSON.toJson(packet).getBytes(StandardCharsets.UTF_8);
        writeVarInt(values.length, outPut);
        outPut.writeBytes(values);
    }

    public static void writeVarInt(int value, ByteBuf out) {
        do {
            byte temp = (byte)(value & 0b01111111);
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            out.writeByte(temp);
        } while (value != 0);
    }
}