package com.infinityraider.agricraft.config;

import com.agricraft.agricore.config.AgriConfigAdapter;
import com.infinityraider.agricraft.api.v1.config.IAgriConfig;
import com.infinityraider.infinitylib.config.ConfigurationHandler;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public abstract class Config implements IAgriConfig, ConfigurationHandler.SidedModConfig, AgriConfigAdapter {

    private Config() {}

    public static abstract class Common extends Config {
        // debug
        private final ForgeConfigSpec.ConfigValue<Boolean> debug;
        private final ForgeConfigSpec.ConfigValue<Boolean> enableLogging;

        // core
        private final ForgeConfigSpec.ConfigValue<Boolean> enableJsonWriteBack;
        private final ForgeConfigSpec.ConfigValue<Integer> statsMin;
        private final ForgeConfigSpec.ConfigValue<Integer> statsMax;
        private final ForgeConfigSpec.ConfigValue<Boolean> fertilizerMutations;
        private final ForgeConfigSpec.ConfigValue<Boolean> disableVanillaFarming;
        private final ForgeConfigSpec.ConfigValue<Double> growthMultiplier;
        private final ForgeConfigSpec.ConfigValue<Boolean> onlyMatureSeedDrops;
        private final ForgeConfigSpec.ConfigValue<Boolean> overwriteGrassDrops;
        private final ForgeConfigSpec.ConfigValue<Boolean> disableWeeds;
        private final ForgeConfigSpec.ConfigValue<Boolean> matureWeedsKillPlants;
        private final ForgeConfigSpec.ConfigValue<Boolean> weedSpreading;
        private final ForgeConfigSpec.ConfigValue<Boolean> weedsDestroyCropSticks;
        private final ForgeConfigSpec.ConfigValue<Boolean> rakingDropsItems;

        // irrigation
        private final ForgeConfigSpec.ConfigValue<Integer> tankCapacity;
        private final ForgeConfigSpec.ConfigValue<Integer> channelCapacity;
        private final ForgeConfigSpec.ConfigValue<Integer> rainFillRate;
        private final ForgeConfigSpec.ConfigValue<Integer> sprinkleInterval;
        private final ForgeConfigSpec.ConfigValue<Double> sprinkleGrowthChance;
        private final ForgeConfigSpec.ConfigValue<Integer> sprinklerWaterConsumption;

        // decoration
        private final ForgeConfigSpec.ConfigValue<Boolean> climbableGrate;

        public Common(ForgeConfigSpec.Builder builder) {
            super();

            builder.push("debug");
            this.debug = builder.comment("Set to true to enable debug mode")
                    .define("debug", false);
            this.enableLogging = builder.comment("Set to true to enable logging on the ${log} channel.")
                    .define("Enable Logging", true);
            builder.pop();

            builder.push("core");
            this.enableJsonWriteBack = builder.comment("Set to false to disable automatic JSON writeback.")
                    .define("Enable JSON write back", true);
            this.statsMin = builder.comment("Minimum allowed value of stats")
                    .defineInRange("Stats min", 1, 1, 10);
            this.statsMax = builder.comment("Maximum allowed value of stats")
                    .defineInRange("Stats max", 10, 1, 10);
            this.fertilizerMutations = builder.comment("Set to false if to disable triggering of mutations by using fertilizers on a cross crop.")
                    .define("Fertilizer mutations", true);
            this.disableVanillaFarming = builder.comment("Set to true to disable vanilla farming, meaning you can only grow plants on crops.")
                    .define("Disable vanilla farming", true);
            this.growthMultiplier = builder.comment("This is a global growth rate multiplier for crops planted on crop sticks.")
                    .defineInRange("Growth rate multiplier", 1.0, 0.0, 3.0);
            this.onlyMatureSeedDrops = builder.comment("Set this to true to make only mature crops drop seeds (to encourage trowel usage).")
                    .define("Only mature crops drop seeds", false);
            this.overwriteGrassDrops = builder.comment("Determines if AgriCraft should completeley override grass drops with those confiured in the JSON files.")
                    .define("Overwrite Grass Drops", true);
            this.disableWeeds = builder.comment("Set to true to completely disable the spawning of weeds")
                    .define("Disable weeds", false);
            this.matureWeedsKillPlants = builder.comment("Set to false to disable mature weeds killing plants")
                    .define("Mature weeds kill plants", true);
            this.weedSpreading = builder.comment("Set to false to disable the spreading of weeds")
                    .define("Weeds can spread", true);
            this.weedsDestroyCropSticks = builder.comment("Set this to true to have weeds destroy the crop sticks when they are broken with weeds (to encourage rake usage).")
                    .define("Weeds destroy crop sticks", false);
            this.rakingDropsItems = builder.comment("Set to false if you wish to disable drops from raking weeds.")
                    .define("Raking weeds drops items", true);
            builder.pop();

            builder.push("irrigation");
            this.tankCapacity = builder.comment("Configures the capacity (in mB) of one tank block")
                    .defineInRange("Tank capacity", 8000, 1000, 40000);
            this.channelCapacity = builder.comment("Configures the capacity (in mB) of one channel block")
                    .defineInRange("Tank capacity", 50, 500, 2000);
            this.rainFillRate = builder.comment("Configures the rate (in mB/t) at which tanks accrue water while raining (0 disables filling from rainfall)")
                    .defineInRange("Rain fill rate", 5, 0, 50);
            this.sprinkleInterval = builder.comment("The minimum number of ticks between successive starts of irrigation.")
                    .defineInRange("Sprinkler growth interval", 40, 1, 1200);
            this.sprinkleGrowthChance = builder.comment("Every loop, each unobscured plant in sprinkler range has this chance to get a growth tick from the sprinkler.")
                    .defineInRange("Sprinkler growth chance", 0.2, 0, 1.0);
            this.sprinklerWaterConsumption = builder.comment("Defined in terms of mB per second. The irrigation loop progress will pause when there is insufficient water.")
                    .defineInRange("Sprinkler water usage", 10, 0, 1000);
            builder.pop();

            builder.push("decoration");
            this.climbableGrate = builder.comment("When true, entities will be able to climb on grates")
                    .define("Grates always climbable", true);
            builder.pop();
        }

        @Override
        public boolean debugMode() {
            return this.debug.get();
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public int getMinStatsValue() {
            return this.statsMin.get();
        }

        @Override
        public int getMaxStatsValue() {
            return this.statsMax.get();
        }

        @Override
        public boolean allowFertilizerMutations() {
            return this.fertilizerMutations.get();
        }

        @Override
        public boolean disableVanillaFarming() {
            return this.disableVanillaFarming.get();
        }

        @Override
        public double growthMultiplier() {
            return this.growthMultiplier.get();
        }

        @Override
        public boolean onlyMatureSeedDrops() {
            return this.onlyMatureSeedDrops.get();
        }

        @Override
        public boolean overwriteGrassDrops() {
            return this.overwriteGrassDrops.get();
        }

        @Override
        public boolean disableWeeds() {
            return this.disableWeeds.get();
        }

        @Override
        public boolean allowLethalWeeds() {
            return this.matureWeedsKillPlants.get();
        }

        @Override
        public boolean allowAggressiveWeeds() {
            return this.weedSpreading.get();
        }

        @Override
        public boolean weedsDestroyCropSticks() {
            return this.weedsDestroyCropSticks.get();
        }

        @Override
        public boolean rakingDropsItems() {
            return this.rakingDropsItems.get();
        }

        @Override
        public int tankCapacity() {
            return this.tankCapacity.get();
        }

        @Override
        public int channelCapacity() {
            return this.channelCapacity.get();
        }

        @Override
        public int rainFillRate() {
            return this.rainFillRate.get();
        }

        @Override
        public int sprinkleInterval() {
            return this.sprinkleInterval.get();
        }

        @Override
        public double sprinkleGrowthChance() {
            return this.sprinkleGrowthChance.get();
        }

        @Override
        public int sprinklerWaterConsumption() {
            return this.sprinklerWaterConsumption.get();
        }

        @Override
        public boolean areGratesClimbable() {
            return this.climbableGrate.get();
        }

        @Override
        public ModConfig.Type getSide() {
            return ModConfig.Type.COMMON;
        }

        @Override
        public boolean enableJsonWriteback() {
            return this.enableJsonWriteBack.get();
        }

        @Override
        public boolean enableLogging() {
            return this.enableLogging.get();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static final class Client extends Common {
        //debug
        private final ForgeConfigSpec.ConfigValue<Boolean> registryTooltips;
        private final ForgeConfigSpec.ConfigValue<Boolean> tagTooltips;
        private final ForgeConfigSpec.ConfigValue<Boolean> nbtTooltips;

        //core
        private final ForgeConfigSpec.ConfigValue<Boolean> vanillaFarmingWarning;
        private final ForgeConfigSpec.ConfigValue<String> statFormat;
        private final ForgeConfigSpec.ConfigValue<Boolean> disableParticles;

        public Client(ForgeConfigSpec.Builder builder) {
            super(builder);

            builder.push("debug");
            this.registryTooltips = builder.comment("Set to true to add Registry information to itemstack tooltips (Client only)")
                    .define("Registry tooltips", false);
            this.tagTooltips = builder.comment("Set to true to add Tag information to itemstack tooltips (Client only)")
                    .define("Tag tooltips", false);
            this.nbtTooltips = builder.comment("Set to true to add NBT information to itemstack tooltips (Client only)")
                    .define("NBT tooltips", false);
            builder.pop();

            builder.push("core");
            this.vanillaFarmingWarning = builder.comment("Set to false to warn that vanilla farming is disabled when trying to plant vanilla plant (Client only)")
                    .define("Show Disabled Vanilla Farming Warning", true);
            this.statFormat = builder.comment("This defines how to display the stats of plants (Client only)")
                    .define("Stat Format", TextFormatting.GREEN + "- {0}: [{1}/{2}]");
            this.disableParticles = builder.comment("Set to true to disable particles (Client only)")
                    .define("Disable particles", false);
            builder.pop();
        }

        @Override
        public boolean registryTooltips() {
            return this.registryTooltips.get();
        }

        @Override
        public boolean tagTooltips() {
            return this.tagTooltips.get();
        }

        @Override
        public boolean nbtTooltips() {
            return this.nbtTooltips.get();
        }

        @Override
        public boolean vanillaFarmingWarning() {
            return this.vanillaFarmingWarning.get();
        }

        @Override
        public String statDisplayFormat() {
            return this.statFormat.get();
        }

        @Override
        public boolean disableParticles() {
            return this.disableParticles.get();
        }

        @Override
        public ModConfig.Type getSide() {
            return ModConfig.Type.CLIENT;
        }
    }

    public static final class Server extends Common {
        public Server(ForgeConfigSpec.Builder builder) {
            super(builder);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean registryTooltips() {
            return false;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean tagTooltips() {
            return false;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean nbtTooltips() {
            return false;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean vanillaFarmingWarning() {
            return false;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public String statDisplayFormat() {
            return null;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean disableParticles() {
            return false;
        }

        @Override
        public ModConfig.Type getSide() {
            return ModConfig.Type.COMMON;
        }
    }
}
