package com.rocketnotfound.rnf.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rocketnotfound.rnf.RNF;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public enum RNFGuiTextures implements ScreenElement {
    SLOT("widgets", 0, 0, 18, 18, 72, 72),
    X("widgets", 54, 0, 18, 18, 72, 72),
    STAR("widgets", 54, 16, 18, 18, 72, 72),
    POINT_LEFT("widgets", 18, 0, 18, 18, 72, 72),
    POINT_RIGHT("widgets", 36, 0, 18, 18, 72, 72),
    POINT_UP("widgets", 0, 54, 18, 18, 72, 72),
    POINT_DOWN("widgets", 36, 54, 18, 18, 72, 72),
    UP_FROM_RIGHT("widgets", 0, 18,18, 18, 72, 72),
    CONNECT("widgets", 18, 18,18, 18, 72, 72),
    DOWN_CONNECT("widgets", 18, 36,18, 18, 72, 72),
    UP_FROM_LEFT("widgets", 0, 36,18, 18, 72, 72),
    DOWN_TO_RIGHT("widgets", 36, 36,18, 18, 72, 72),
    DOWN_TO_LEFT("widgets", 36, 18,18, 18, 72, 72);

    public final Identifier location;
    public int width, height;
    public int startX, startY;
    public int textureWidth, textureHeight;

    RNFGuiTextures(String location, int startX, int startY, int width, int height, int textureWidth, int textureHeight) {
        this(RNF.MOD_ID, location, startX, startY, width, height, textureWidth, textureHeight);
    }

    RNFGuiTextures(String namespace, String location, int startX, int startY, int width, int height, int textureWidth, int textureHeight) {
        this.location = new Identifier(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public void bind() {
        RenderSystem.setShaderTexture(0, location);
    }

    @Override
    public void render(MatrixStack ms, int x, int y) {
        bind();
        DrawableHelper.drawTexture(ms, x, y, 0, startX, startY, width, height, textureWidth, textureHeight);
    }
}
