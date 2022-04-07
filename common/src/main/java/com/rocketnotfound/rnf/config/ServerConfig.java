package com.rocketnotfound.rnf.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "rnf-common")
public class ServerConfig implements ConfigData {
    @ConfigEntry.Category("Ritual")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public RitualConfig RITUAL = new RitualConfig();

    @ConfigEntry.Category("Infusion")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public InfusionConfig INFUSE = new InfusionConfig();

    public static class RitualConfig {
        public int MAX_RANGE = 16;
        public int CHECK_RECIPE_INTERVAL_TICKS = 60;
        public int RECIPE_CRAFTING_DELAY_TICKS = 60;
        public int CRAFTING_TICKS_PER_FRAME = 30;
        public int CRAFTING_COOLDOWN = 60;
    }

    public static class InfusionConfig {
        public int INFUSING_RADIUS = 2;
        public int INFUSE_PER_LUNA = 8;
        public int CHECK_INFUSING_TARGET_INTERVAL_TICKS = 60;
        public int INFUSING_COMPLETION_TICKS = 60;
    }
}
