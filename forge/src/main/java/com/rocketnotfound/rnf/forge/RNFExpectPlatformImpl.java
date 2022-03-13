package com.rocketnotfound.rnf.forge;

import com.rocketnotfound.rnf.RNFExpectPlatform;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class RNFExpectPlatformImpl {
    /**
     * This is our actual method to {@link RNFExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
