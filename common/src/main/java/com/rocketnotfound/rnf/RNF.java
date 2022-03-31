package com.rocketnotfound.rnf;

import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.blockentity.RNFBlockEntities;
import com.rocketnotfound.rnf.config.ClientConfig;
import com.rocketnotfound.rnf.config.ServerConfig;
import com.rocketnotfound.rnf.data.recipes.RNFRecipes;
import com.rocketnotfound.rnf.network.RNFNetwork;
import com.rocketnotfound.rnf.network.SyncConfigMessage;
import com.rocketnotfound.rnf.particle.RNFParticleTypes;
import com.rocketnotfound.rnf.item.RNFItems;
import com.rocketnotfound.rnf.proxy.IProxy;
import com.rocketnotfound.rnf.proxy.ServerProxy;
import com.rocketnotfound.rnf.sound.RNFSounds;
import com.rocketnotfound.rnf.util.RitualFrameConnectionHandler;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class RNF {
    public static final String MOD_ID = "rnf";
    public static final String MOD_NAME = "RNF";
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);
    public static IProxy PROXY;

    private static ConfigHolder<ServerConfig> localServerConfig;
    private static ServerConfig syncedServerConfig;
    private static ConfigHolder<ClientConfig> clientConfig;

    public static ClientConfig clientConfig() {
        return clientConfig.get();
    }

    public static ServerConfig serverConfig() {
        return Optional.ofNullable(syncedServerConfig).orElseGet(localServerConfig);
    }

    public static void setSyncedConfig(ServerConfig config) {
        syncedServerConfig = config;
    }

    public static void init() {
        // Configs
        localServerConfig = AutoConfig.register(ServerConfig.class, Toml4jConfigSerializer::new);
        clientConfig = AutoConfig.register(ClientConfig.class, Toml4jConfigSerializer::new);

        if (PROXY == null) PROXY = new ServerProxy();
        RNFBlocks.BLOCKS.register();
        RNFBlockEntities.BLOCK_ENTITIES.register();
        RNFItems.ITEMS.register();
        RNFParticleTypes.PARTICLES.register();
        RNFSounds.SOUND_EVENTS.register();
        RNFRecipes.register();

        RNFNetwork.init();

        // Lifecycle events
        LifecycleEvent.SERVER_LEVEL_LOAD.register((world) -> {
            RitualFrameConnectionHandler.invalidateCache();
        });

        // Sync Server Configs
        PlayerEvent.PLAYER_JOIN.register(player ->
            RNFNetwork.CHANNEL.sendToPlayer(player, new SyncConfigMessage(RNF.serverConfig()))
        );
    }

    public static Identifier createIdentifier(String path) {
        return new Identifier(MOD_ID, path);
    }
}
