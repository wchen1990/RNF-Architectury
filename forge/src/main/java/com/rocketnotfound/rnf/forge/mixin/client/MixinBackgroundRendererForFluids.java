package com.rocketnotfound.rnf.forge.mixin.client;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.util.client.MixinBackgroundRendererForFluidsHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraftforge.fluids.FluidAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRendererForFluids {
    @Shadow
    private static float red;

    @Shadow
    private static float green;

    @Shadow
    private static float blue;

    @Shadow
    private static long lastWaterFogColorUpdateTime = -1L;

    @Inject(method = "render(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld$Properties;getHorizonShadingRatio()F"))
    private static void rnf_render(Camera camera, float j, ClientWorld clientWorld, int l, float i1, CallbackInfo ci) {
        if (RNF.serverConfig().MISC.FORCE_UNTAGGED_AS_WATER) {
            FluidState fluidState = MixinBackgroundRendererForFluidsHelper.getNearbyFluid(camera);
            if (MixinBackgroundRendererForFluidsHelper.matchesCondition(fluidState)) {
                // Scale the brightness of fog but make sure it is never darker than the dimension's min brightness.
                float brightness = (float) Math.max(
                    Math.pow(MixinBackgroundRendererForFluidsHelper.getDimensionBrightnessAtEyes(camera.getFocusedEntity()), 2D),
                    camera.getFocusedEntity().world.getDimension().getBrightness(0)
                );

                Fluid fluid = fluidState.getFluid();
                FluidAttributes attributes = fluid.getAttributes();
                Identifier fluidStill = attributes.getStillTexture();
                if (fluidStill != null) {
                    MinecraftClient minecraft = MinecraftClient.getInstance();
                    Sprite textureAtlasSprite = minecraft.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(fluidStill);
                    int renderColor = textureAtlasSprite.getPixelRGBA(0, 0, 0);
                    int attributeColor = fluid.getAttributes().getColor();

                    red = brightness * Math.min((renderColor >> 16 & 0xFF), (attributeColor >> 16 & 0xFF)) / 255F;
                    green = brightness * Math.min((renderColor >> 8 & 0xFF), (attributeColor >> 8 & 0xFF)) / 255F;
                    blue = Math.min((renderColor & 0xFF), (attributeColor & 0xFF)) / 255F;

                    if (RNF.clientConfig().DEBUG.SHOW_MOD_LOGGER_INFO) {
                        RNF.LOG.info(String.format("--------\nsprite: %s\nattr: %s\nrgb (float): %s, %s, %s", renderColor, attributeColor, red, green, blue));
                    }

                    lastWaterFogColorUpdateTime = -1L;
                }
            }
        }
    }
}
