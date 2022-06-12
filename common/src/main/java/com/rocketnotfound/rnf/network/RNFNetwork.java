package com.rocketnotfound.rnf.network;

import com.rocketnotfound.rnf.RNF;
import dev.architectury.networking.NetworkChannel;
import net.minecraft.util.Identifier;

public class RNFNetwork {
    private static final String version = "1.0";

    public static final NetworkChannel CHANNEL = NetworkChannel.create(new Identifier(RNF.MOD_ID, "network"));

    public static void init() {
        CHANNEL.register(SyncConfigMessage.class, SyncConfigMessage::encode, SyncConfigMessage::decode, SyncConfigMessage::handle);
        CHANNEL.register(SyncRitualManager.class, SyncRitualManager::encode, SyncRitualManager::decode, SyncRitualManager::handle);
        CHANNEL.register(SyncSpellManager.class, SyncSpellManager::encode, SyncSpellManager::decode, SyncSpellManager::handle);
    }
}
