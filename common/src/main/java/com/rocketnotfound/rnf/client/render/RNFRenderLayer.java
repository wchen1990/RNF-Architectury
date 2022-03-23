package com.rocketnotfound.rnf.client.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public abstract class RNFRenderLayer extends RenderLayer {
    private static final Function<Identifier, RenderLayer> TRANSLUCENT_RNF;

    static {
        TRANSLUCENT_RNF = Util.memoize((identifier) -> {
            MultiPhaseParameters multiPhaseParameters =
                MultiPhaseParameters.builder()
                    .shader(TRANSLUCENT_SHADER)
                    .texture(new Texture(identifier, false, false))
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .cull(DISABLE_CULLING)
                    .lightmap(ENABLE_LIGHTMAP)
                    .overlay(ENABLE_OVERLAY_COLOR)
                    .build(true);

            return of("translucent_rnf",
                VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
                VertexFormat.DrawMode.QUADS,
                2097152,
                true,
                true,
                multiPhaseParameters
            );
        });
    }

    public RNFRenderLayer(String string, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, drawMode, i, bl, bl2, runnable, runnable2);
    }

    public static RenderLayer getTranslucentRNF(Identifier identifier) {
        return TRANSLUCENT_RNF.apply(identifier);
    }
}
