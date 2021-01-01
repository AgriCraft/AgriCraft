package com.infinityraider.agricraft.config;

import com.infinityraider.infinitylib.config.ConfigurationHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public abstract class Config implements ConfigurationHandler.SidedModConfig {
    private Config(ForgeConfigSpec.Builder builder) {}

    public static class Common extends Config {
        public Common(ForgeConfigSpec.Builder builder) {
            super(builder);
        }

        @Override
        public ModConfig.Type getSide() {
            return ModConfig.Type.COMMON;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static final class Client extends Common {
        public Client(ForgeConfigSpec.Builder builder) {
            super(builder);
        }

        @Override
        public ModConfig.Type getSide() {
            return ModConfig.Type.CLIENT;
        }
    }
}
