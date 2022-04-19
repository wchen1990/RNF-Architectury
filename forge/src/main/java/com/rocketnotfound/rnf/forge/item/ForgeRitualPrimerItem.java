package com.rocketnotfound.rnf.forge.item;

import com.rocketnotfound.rnf.forge.client.renderer.item.RitualPrimerItemRenderer;
import com.rocketnotfound.rnf.item.RitualPrimerItem;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;

public class ForgeRitualPrimerItem extends RitualPrimerItem {
    public ForgeRitualPrimerItem(Settings settings) {
        super(settings);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            private final BuiltinModelItemRenderer renderer = new RitualPrimerItemRenderer();

            @Override
            public BuiltinModelItemRenderer getItemStackRenderer() {
                return renderer;
            }
        });
    }
}
