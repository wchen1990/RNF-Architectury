package com.rocketnotfound.rnf.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "rnf-common")
public class ServerConfig implements ConfigData {
    @ConfigEntry.Category("ritual_frame")
    public int MAX_RANGE = 16;

    @ConfigEntry.Category("ritual_frame")
    public int CHECK_RECIPE_INTERVAL_TICKS = 60;

    @ConfigEntry.Category("ritual_frame")
    public int RECIPE_CRAFTING_DELAY_TICKS = 60;

    @ConfigEntry.Category("ritual_frame")
    public int CRAFTING_TICKS_PER_FRAME = 30;

    @ConfigEntry.Category("ritual_frame")
    public int CRAFTING_COOLDOWN = 60;
}
