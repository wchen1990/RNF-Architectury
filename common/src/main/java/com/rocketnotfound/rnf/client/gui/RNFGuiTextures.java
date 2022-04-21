package com.rocketnotfound.rnf.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rocketnotfound.rnf.RNF;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public enum RNFGuiTextures implements ScreenElement {
    SLOT("widgets", 0, 0, 18, 18, 54, 54),
    POINT_LEFT("widgets", 19, 0, 18, 18, 54, 54),
    POINT_RIGHT("widgets", 37, 0, 18, 18, 54, 54),
    UP_FROM_RIGHT("widgets", 0, 19,18, 18, 54, 54),
    CONNECT("widgets", 19, 19,18, 18, 54, 54),
    DOWN_CONNECT("widgets", 19, 37,18, 18, 54, 54),
    UP_FROM_LEFT("widgets", 0, 37,18, 18, 54, 54),
    DOWN_TO_RIGHT("widgets", 37, 37,18, 18, 54, 54),
    DOWN_TO_LEFT("widgets", 37, 19,18, 18, 54, 54);

    public final Identifier location;
    public int width, height;
    public int startX, startY;
    public int textureWidth, textureHeight;

    private RNFGuiTextures(String location, int width, int height, int textureWidth, int textureHeight) {
        this(location, 0, 0, width, height, textureWidth, textureHeight);
    }

    private RNFGuiTextures(String location, int startX, int startY, int width, int height, int textureWidth, int textureHeight) {
        this(RNF.MOD_ID, location, startX, startY, width, height, textureWidth, textureHeight);
    }

    private RNFGuiTextures(String namespace, String location, int startX, int startY, int width, int height, int textureWidth, int textureHeight) {
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
        DrawableHelper.drawTexture(ms, x, y, 0, startX, startY, width, height, 54, 54);
    }
}
