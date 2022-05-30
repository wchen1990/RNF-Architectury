package com.rocketnotfound.rnf.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "rnf-client")
public class ClientConfig implements ConfigData {
    @ConfigEntry.Category("Debug")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public DebugConfig DEBUG = new DebugConfig();

    public static class DebugConfig {
        public boolean SHOW_MOD_LOGGER_INFO = false;
    }
}
