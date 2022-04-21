package com.rocketnotfound.rnf.client.gui;

import net.minecraft.client.util.math.MatrixStack;

public interface ScreenElement {
    void render(MatrixStack ms, int x, int y);
}
