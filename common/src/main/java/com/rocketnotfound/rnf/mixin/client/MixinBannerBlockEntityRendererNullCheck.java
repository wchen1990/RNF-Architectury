package com.rocketnotfound.rnf.mixin.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BannerBlockEntityRenderer.class)
public class MixinBannerBlockEntityRendererNullCheck {
    @Inject(
            method = "Lnet/minecraft/client/render/block/entity/BannerBlockEntityRenderer;renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLjava/util/List;Z)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void nullCheck(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, ModelPart modelPart, SpriteIdentifier spriteIdentifier, boolean bl, List<Pair<BannerPattern, DyeColor>> list, boolean bl2, CallbackInfo ci) {
        modelPart.render(matrixStack, spriteIdentifier.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid, bl2), i, j);

        for(int k = 0; k < 17 && k < list.size(); ++k) {
            Pair<BannerPattern, DyeColor> pair = (Pair)list.get(k);
            float[] fs = ((DyeColor)pair.getSecond()).getColorComponents();
            BannerPattern bannerPattern = (BannerPattern)pair.getFirst();
            SpriteIdentifier spriteIdentifier2 = bl ? TexturedRenderLayers.getBannerPatternTextureId(bannerPattern) : TexturedRenderLayers.getShieldPatternTextureId(bannerPattern);
            if (spriteIdentifier2 != null) {
                modelPart.render(matrixStack, spriteIdentifier2.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityNoOutline), i, j, fs[0], fs[1], fs[2], 1.0F);
            }
        }

        ci.cancel();
    }
}
