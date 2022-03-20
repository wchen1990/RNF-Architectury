package com.rocketnotfound.rnf.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class ServerProxy implements IProxy {
    @Override
    public void init() {}

    @Override
    public void sendOverlayMessage(Text text, boolean bl) {
        throw new IllegalStateException("Only run on the client");
    }

    @Override
    public World getClientWorld() {
        throw new IllegalStateException("Only run on the client");
    }

    @Override
    public PlayerEntity getClientPlayer() {
        throw new IllegalStateException("Only run on the client");
    }
}
