package com.rocketnotfound.rnf.fabric;

import com.rocketnotfound.rnf.RNFExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class RNFExpectPlatformImpl {
    /**
     * This is our actual method to {@link RNFExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
