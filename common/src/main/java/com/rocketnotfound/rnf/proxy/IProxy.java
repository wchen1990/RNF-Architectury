package com.rocketnotfound.rnf.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public interface IProxy {
    void init();
    void sendOverlayMessage(Text text, boolean bl);
    World getClientWorld();
    PlayerEntity getClientPlayer();
}
