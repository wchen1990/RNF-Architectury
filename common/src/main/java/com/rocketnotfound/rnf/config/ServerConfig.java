package com.rocketnotfound.rnf.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "rnf-common")
public class ServerConfig implements ConfigData {
    @ConfigEntry.Category("Ritual")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public WorldGenConfig WORLD_GEN = new WorldGenConfig();

    @ConfigEntry.Category("Ritual")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public RitualConfig RITUAL = new RitualConfig();

    @ConfigEntry.Category("Infusion")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public InfusionConfig INFUSE = new InfusionConfig();

    @ConfigEntry.Category("Transcribe")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public TranscribeConfig TRANSCRIBE = new TranscribeConfig();

    public static class WorldGenConfig {
        public int MOONSTONE_ORE_VEIN_SIZE = 5;
        public int MOONSTONE_VEINS_PER_CHUNK = 2;
        public int MOONSTONE_SPAWN_MAX_Y = 32;
        public int MOONSTONE_SPAWN_MIN_Y = -16;
    }

    public static class RitualConfig {
        public int MAX_RANGE = 16;
        public int CHECK_REQUIREMENTS_INTERVAL_TICKS = 60;
        public int ACTION_DELAY_TICKS = 60;
        public int ACTION_TICKS_PER_FRAME = 30;
        public int ACTION_COOLDOWN = 60;
    }

    public static class InfusionConfig {
        public int INFUSING_RADIUS = 2;
        public int INFUSE_PER_LUNA = 8;
        public int CHECK_REQUIREMENTS_INTERVAL_TICKS = 60;
        public int ACTION_COMPLETION_TICKS = 60;
    }

    public static class TranscribeConfig {
        public int SEARCH_LIMIT = 16;
        public int PRIMED_TRIGGER_DISTANCE = 3;
        public int CHECK_REQUIREMENTS_INTERVAL_TICKS = 60;
        public int ACTION_TICKS_PER_LENGTH = 10;
        public int ACTION_COOLDOWN = 60;
    }
}
