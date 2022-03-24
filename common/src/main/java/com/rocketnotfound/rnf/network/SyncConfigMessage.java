package com.rocketnotfound.rnf.network;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.config.ServerConfig;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.minecraft.network.PacketByteBuf;

import java.util.function.Supplier;

public class SyncConfigMessage {
    private final byte[] configData;
    private static final ByteConfigSerializer<ServerConfig> serializer = new ByteConfigSerializer<>();

    public SyncConfigMessage(ServerConfig config) {
        this(serializer.serialize(config));
    }

    public SyncConfigMessage(byte[] configData) {
        this.configData = configData;
    }

    public static void encode(SyncConfigMessage message, PacketByteBuf buf) {
        buf.writeByteArray(message.configData);
    }

    public static SyncConfigMessage decode(PacketByteBuf buf) {
        return new SyncConfigMessage(buf.readByteArray());
    }

    public static void handle(SyncConfigMessage message, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext context = contextSupplier.get();
        context.queue(() -> {
            if (context.getEnv() == EnvType.CLIENT) {
                serializer.deserialize(message.configData).ifPresent(RNF::setSyncedConfig);
            }
        });
    }
}
