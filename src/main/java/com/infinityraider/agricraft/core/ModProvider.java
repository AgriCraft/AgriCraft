/*
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.config.AgriConfigAdapter;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.reference.Reference;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 *
 *
 */
public class ModProvider implements AgriConfigAdapter {

    private final Configuration config;

    public ModProvider(Configuration config) {
        this.config = config;
    }

    @Override
    public void load() {
        this.config.load();
    }

    @Override
    public void save() {
        this.config.save();
    }

    @Override
    public boolean getBoolean(String name, String category, boolean defaultValue, String comment) {
        return config.getBoolean(name, category, defaultValue, comment);
    }

    @Override
    public int getInt(String name, String category, int defaultValue, int minValue, int maxValue, String comment) {
        return config.getInt(name, category, defaultValue, minValue, maxValue, comment);
    }

    @Override
    public float getFloat(String name, String category, float defaultValue, float minValue, float maxValue, String comment) {
        return config.getFloat(name, category, defaultValue, minValue, maxValue, comment);
    }

    @Override
    public double getDouble(String name, String category, double defaultValue, double minValue, double maxValue, String comment) {
        return config.getFloat(name, category, (float) defaultValue, (float) minValue, (float) maxValue, comment);
    }

    @Override
    public String getString(String name, String category, String defaultValue, String comment) {
        return config.getString(name, category, defaultValue, comment);
    }

    @Override
    public String getLocation() {
        return config.toString();
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Reference.MOD_ID)) {
            AgriCore.getConfig().save();
            AgriCore.getConfig().load();
            AgriCore.getLogger("agricraft").debug("Configuration reloaded.");
        }
    }

}
