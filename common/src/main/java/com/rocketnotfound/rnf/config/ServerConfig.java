package com.rocketnotfound.rnf.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "rnf-common")
public class ServerConfig implements ConfigData {
    @ConfigEntry.Category("ritual")
    public int MAX_RANGE = 16;

    @ConfigEntry.Category("ritual")
    public int CHECK_RECIPE_INTERVAL_TICKS = 60;

    @ConfigEntry.Category("ritual")
    public int RECIPE_CRAFTING_DELAY_TICKS = 60;

    @ConfigEntry.Category("ritual")
    public int CRAFTING_TICKS_PER_FRAME = 30;

    @ConfigEntry.Category("ritual")
    public int CRAFTING_COOLDOWN = 60;

    @ConfigEntry.Category("ritual")
    public int INFUSING_RADIUS = 2;

    @ConfigEntry.Category("ritual")
    public int INFUSE_PER_LUNA = 8;

    @ConfigEntry.Category("ritual")
    public int CHECK_INFUSING_TARGET_INTERVAL_TICKS = 60;

    @ConfigEntry.Category("ritual")
    public int INFUSING_COMPLETION_TICKS = 60;

    @ConfigEntry.Category("ritual")
    public String RUNE_ENGRAVING_RECIPE = "[" +
        "{\"item\":\"rnf:rune_block\"}," +
        "{\"item\":\"rnf:luna\"}," +
        "{\"item\":\"minecraft:amethyst_shard\"}," +
        "{\"item\":\"minecraft:blaze_rod\"}" +
    "]";
}
