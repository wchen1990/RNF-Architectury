package com.rocketnotfound.rnf.forge.item;

import com.rocketnotfound.rnf.forge.client.renderer.item.RitualFrameItemRenderer;
import com.rocketnotfound.rnf.forge.client.renderer.item.RitualStaffItemRenderer;
import com.rocketnotfound.rnf.item.RitualFrameItem;
import com.rocketnotfound.rnf.item.RitualStaffItem;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;

public class ForgeRitualStaffItem extends RitualStaffItem {
    public ForgeRitualStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            private final BuiltinModelItemRenderer renderer = new RitualStaffItemRenderer();

            @Override
            public BuiltinModelItemRenderer getItemStackRenderer() {
                return renderer;
            }
        });
    }
}
