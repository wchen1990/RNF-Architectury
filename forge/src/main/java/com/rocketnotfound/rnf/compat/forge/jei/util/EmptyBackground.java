package com.rocketnotfound.rnf.compat.forge.jei.util;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.util.math.MatrixStack;

public class EmptyBackground implements IDrawable {

    private int width;
    private int height;

    public EmptyBackground(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void draw(MatrixStack matrixStack, int xOffset, int yOffset) {}

}