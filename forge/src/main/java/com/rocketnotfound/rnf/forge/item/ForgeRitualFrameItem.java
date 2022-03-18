package com.rocketnotfound.rnf.forge.item;

import com.rocketnotfound.rnf.forge.client.renderer.item.RitualFrameItemRenderer;
import com.rocketnotfound.rnf.item.RitualFrameItem;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;

public class ForgeRitualFrameItem extends RitualFrameItem {
    public ForgeRitualFrameItem(Settings settings) {
        super(settings);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            private final BuiltinModelItemRenderer renderer = new RitualFrameItemRenderer();

            @Override
            public BuiltinModelItemRenderer getItemStackRenderer() {
                return renderer;
            }
        });
    }
}
