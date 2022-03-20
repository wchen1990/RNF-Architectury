package com.rocketnotfound.rnf.proxy;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {
    @Override
    public void init() {}

    @Override
    public void sendOverlayMessage(Text text, boolean bl) {
        MinecraftClient.getInstance().inGameHud.setOverlayMessage(text, bl);
    }

    @Override
    public World getClientWorld() {
        return MinecraftClient.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return MinecraftClient.getInstance().player;
    }
}
