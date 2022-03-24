package com.rocketnotfound.rnf.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "rnf-common")
public class ServerConfig implements ConfigData {
    @ConfigEntry.Category("ritual_frame")
    public int MAX_DISTANCE = 16;
}
