package com.infinityraider.agricraft.config;

import com.agricraft.agricore.config.AgriConfigAdapter;
import com.infinityraider.agricraft.api.v1.config.IAgriConfig;
import com.infinityraider.agricraft.impl.v1.genetics.GeneStat;
import com.infinityraider.agricraft.impl.v1.requirement.SeasonPlugin;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.config.ConfigurationHandler;
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
        private final ForgeConfigSpec.ConfigValue<Boolean> generateMissingDefaults;
        private final ForgeConfigSpec.ConfigValue<Boolean> enableJsonWriteBack;
        private final ForgeConfigSpec.ConfigValue<Boolean> plantOffCropSticks;
        private final ForgeConfigSpec.ConfigValue<Boolean> onlyFertileCropsSpread;
        private final ForgeConfigSpec.ConfigValue<Boolean> fertilizerMutations;
        private final ForgeConfigSpec.ConfigValue<Boolean> overrideVanillaFarming;
        private final ForgeConfigSpec.ConfigValue<Double> growthMultiplier;
        private final ForgeConfigSpec.ConfigValue<Boolean> onlyMatureSeedDrops;
        private final ForgeConfigSpec.ConfigValue<Boolean> disableWeeds;
        private final ForgeConfigSpec.ConfigValue<Boolean> matureWeedsKillPlants;
        private final ForgeConfigSpec.ConfigValue<Boolean> weedSpreading;
        private final ForgeConfigSpec.ConfigValue<Boolean> weedsDestroyCropSticks;
        private final ForgeConfigSpec.ConfigValue<Boolean> rakingDropsItems;
        private final ForgeConfigSpec.ConfigValue<Double> seedCompostValue;
        private final ForgeConfigSpec.ConfigValue<Boolean> animalAttraction;
        private final ForgeConfigSpec.ConfigValue<Integer> seedBagEnchantCost;

        // stats
        private final ForgeConfigSpec.ConfigValue<String> statTraitLogic;
        private final ForgeConfigSpec.ConfigValue<Integer> minGain;
        private final ForgeConfigSpec.ConfigValue<Integer> maxGain;
        private final ForgeConfigSpec.ConfigValue<Boolean> hiddenGain;
        private final ForgeConfigSpec.ConfigValue<Integer> minGrowth;
        private final ForgeConfigSpec.ConfigValue<Integer> maxGrowth;
        private final ForgeConfigSpec.ConfigValue<Boolean> hiddenGrowth;
        private final ForgeConfigSpec.ConfigValue<Integer> minStrength;
        private final ForgeConfigSpec.ConfigValue<Integer> maxStrength;
        private final ForgeConfigSpec.ConfigValue<Boolean> hiddenStrength;
        private final ForgeConfigSpec.ConfigValue<Integer> minResistance;
        private final ForgeConfigSpec.ConfigValue<Integer> maxResistance;
        private final ForgeConfigSpec.ConfigValue<Boolean> hiddenResistance;
        private final ForgeConfigSpec.ConfigValue<Integer> minFertility;
        private final ForgeConfigSpec.ConfigValue<Integer> maxFertility;
        private final ForgeConfigSpec.ConfigValue<Boolean> hiddenFertility;
        private final ForgeConfigSpec.ConfigValue<Integer> minMutativity;
        private final ForgeConfigSpec.ConfigValue<Integer> maxMutativity;
        private final ForgeConfigSpec.ConfigValue<Boolean> hiddenMutativity;

        // irrigation
        private final ForgeConfigSpec.ConfigValue<Integer> tankCapacity;
        private final ForgeConfigSpec.ConfigValue<Integer> channelCapacity;
        private final ForgeConfigSpec.ConfigValue<Integer> rainFillRate;
        private final ForgeConfigSpec.ConfigValue<Boolean> spawnWaterBlock;
        private final ForgeConfigSpec.ConfigValue<Integer> sprinkleInterval;
        private final ForgeConfigSpec.ConfigValue<Double> sprinkleGrowthChance;
        private final ForgeConfigSpec.ConfigValue<Integer> sprinklerWaterConsumption;

        // decoration
        private final ForgeConfigSpec.ConfigValue<Boolean> climbableGrate;

        // compat
        private final ForgeConfigSpec.ConfigValue<Boolean> progressiveJEI;
        private final ForgeConfigSpec.ConfigValue<String> seasonLogic;
        private final ForgeConfigSpec.ConfigValue<Boolean> topControlledByMagnifyingGlass;
        private final ForgeConfigSpec.ConfigValue<Boolean> enableBloodMagicCompat;
        private final ForgeConfigSpec.ConfigValue<Boolean> enableBotanyPotsCompat;
        private final ForgeConfigSpec.ConfigValue<Boolean> enableCreateCompat;
        private final ForgeConfigSpec.ConfigValue<Boolean> enableIndustrialForegoingCompat;
        private final ForgeConfigSpec.ConfigValue<Boolean> enableImmersiveEngineeringCompat;
        private final ForgeConfigSpec.ConfigValue<Boolean> enableStrawGolemRebornCompat;

        public Common(ForgeConfigSpec.Builder builder) {
            super();

            builder.push("debug");
            this.debug = builder.comment("\nSet to true to enable debug mode")
                    .define("debug", false);
            this.enableLogging = builder.comment("\nSet to true to enable logging on the ${log} channel.")
                    .define("Enable Logging", true);
            builder.pop();

            builder.push("core");
            this.generateMissingDefaults = builder.comment("\nSet to false to disable the generation of missing default jsons")
                    .define("Generate missing default JSONs", true);
            this.enableJsonWriteBack = builder.comment("\nSet to false to disable automatic JSON writeback.")
                    .define("Enable JSON write back", true);
            this.plantOffCropSticks = builder.comment("\nSet to false to disable planting of (agricraft) seeds outside crop sticks")
                    .define("Plant outside crop sticks", true);
            this.onlyFertileCropsSpread = builder.comment("\nSet to true to allow only fertile plants to be able to cause, participate in, or contribute to a spreading / mutation action\n" +
                    "(note that this may cause issues with obtaining some specific plants)")
                    .define("Only fertile crops mutate", false);
            this.fertilizerMutations = builder.comment("\nSet to false if to disable triggering of mutations by using fertilizers on a cross crop.")
                    .define("Fertilizer mutations", true);
            this.overrideVanillaFarming = builder.comment("\nSet to true to override vanilla farming, meaning vanilla seeds will be converted to agricraft seeds on planting.")
                    .define("Override vanilla farming", true);
            this.growthMultiplier = builder.comment("\nThis is a global growth rate multiplier for crops planted on crop sticks.")
                    .defineInRange("Growth rate multiplier", 1.0, 0.0, 3.0);
            this.onlyMatureSeedDrops = builder.comment("\nSet this to true to make only mature crops drop seeds (to encourage trowel usage).")
                    .define("Only mature crops drop seeds", false);
            this.disableWeeds = builder.comment("\nSet to true to completely disable the spawning of weeds")
                    .define("Disable weeds", false);
            this.matureWeedsKillPlants = builder.comment("\nSet to false to disable mature weeds killing plants")
                    .define("Mature weeds kill plants", true);
            this.weedSpreading = builder.comment("\nSet to false to disable the spreading of weeds")
                    .define("Weeds can spread", true);
            this.weedsDestroyCropSticks = builder.comment("\nSet this to true to have weeds destroy the crop sticks when they are broken with weeds (to encourage rake usage).")
                    .define("Weeds destroy crop sticks", false);
            this.rakingDropsItems = builder.comment("\nSet to false if you wish to disable drops from raking weeds.")
                    .define("Raking weeds drops items", true);
            this.seedCompostValue = builder.comment("\nDefines the seed compost value, if set to zero, seeds will not be compostable")
                    .defineInRange("Seed compost value", 0.3, 0, 1.0);
            this.animalAttraction = builder.comment("\nSet to false to disable certain animals eating certain crops (prevents auto-breeding)")
                    .define("animal attracting crops", true);
            this.seedBagEnchantCost = builder.comment("\nEnchantment cost in player levels to enchant the seed bag")
                    .defineInRange("seed bag enchant cost", 10, 0, 30);
            builder.pop();

            builder.push("stats");
            this.statTraitLogic = builder.comment("\nLogic to calculate stats from gene pairs, accepted values are: \"min\", \"min\", and \"mean\"")
                    .defineInList("Stat calculation logic", "max", GeneStat.getLogicOptions());
            this.minGain = builder.comment("\nMinimum allowed value of the Gain stat (setting min and max equal will freeze the stat to that value in crop breeding)")
                    .defineInRange("Gain stat min", 1, 1, 10);
            this.maxGain = builder.comment("\nMaximum allowed value of the Gain stat (setting min and max equal will freeze the stat to that value in crop breeding)")
                    .defineInRange("Gain stat max", 10, 1, 10);
            this.hiddenGain = builder.comment("\nSet to true to hide the Gain stat (hidden stats will not show up in tooltips or seed analysis)\n" +
                    "setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
                    .define("hide Gain stat", false);
            this.minGrowth = builder.comment("\nMinimum allowed value of the Growth stat (setting min and max equal will freeze the stat to that value in crop breeding)")
                    .defineInRange("Growth stat min", 1, 1, 10);
            this.maxGrowth = builder.comment("\nMaximum allowed value of the Growth stat (setting min and max equal will freeze the stat to that value in crop breeding)")
                    .defineInRange("Growth stat max", 10, 1, 10);
            this.hiddenGrowth = builder.comment("\nSet to true to hide the Growth stat (hidden stats will not show up in tooltips or seed analysis)\n" +
                    "setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
                    .define("hide Growth stat", false);
            this.minStrength = builder.comment("\nMinimum allowed value of the Strength stat (setting min and max equal will freeze the stat to that value in crop breeding)")
                    .defineInRange("Strength stat min", 1, 1, 10);
            this.maxStrength = builder.comment("\nMaximum allowed value of the Strength stat (setting min and max equal will freeze the stat to that value in crop breeding)")
                    .defineInRange("Strength stat max", 10, 1, 10);
            this.hiddenStrength = builder.comment("\nSet to true to hide the Strength stat (hidden stats will not show up in tooltips or seed analysis)\n" +
                    "setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
                    .define("hide Strength stat", false);
            this.minResistance = builder.comment("\nMinimum allowed value of the Resistance stat (setting min and max equal will freeze the stat to that value in crop breeding)")
                    .defineInRange("Resistance stat min", 1, 1, 10);
            this.maxResistance = builder.comment("\nMaximum allowed value of the Resistance stat (setting min and max equal will freeze the stat to that value in crop breeding)")
                    .defineInRange("Resistance stat max", 10, 1, 10);
            this.hiddenResistance = builder.comment("\nSet to true to hide the Resistance stat (hidden stats will not show up in tooltips or seed analysis)\n" +
                    "setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
                    .define("hide Resistance stat", false);
            this.minFertility = builder.comment("\nMinimum allowed value of the Fertility stat (setting min and max equal will freeze the stat to that value in crop breeding)")
                    .defineInRange("Fertility stat min", 1, 1, 10);
            this.maxFertility = builder.comment("\nMaximum allowed value of the Fertility stat (setting min and max equal will freeze the stat to that value in crop breeding)")
                    .defineInRange("Fertility stat max", 10, 1, 10);
            this.hiddenFertility = builder.comment("\nSet to true to hide the Fertility stat (hidden stats will not show up in tooltips or seed analysis)\n" +
                    "setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
                    .define("hide Fertility stat", false);
            this.minMutativity = builder.comment("\nMinimum allowed value of the Mutativity stat (setting min and max equal will freeze the stat to that value in crop breeding)")
                    .defineInRange("Mutativity stat min", 1, 1, 10);
            this.maxMutativity = builder.comment("\nMaximum allowed value of the Mutativity stat (setting min and max equal will freeze the stat to that value in crop breeding)")
                    .defineInRange("Mutativity stat max", 10, 1, 10);
            this.hiddenMutativity = builder.comment("\nSet to true to hide the Mutativity stat (hidden stats will not show up in tooltips or seed analysis)\n" +
                    "setting min and max equal and hiding a stat effectively disables it, with its behaviour at the defined value for min and max.")
                    .define("hide Mutativity stat", false);

            builder.pop();

            builder.push("irrigation");
            this.tankCapacity = builder.comment("\nConfigures the capacity (in mB) of one tank block")
                    .defineInRange("Tank capacity", 8000, 1000, 40000);
            this.channelCapacity = builder.comment("\nConfigures the capacity (in mB) of one channel block")
                    .defineInRange("Channel capacity", 500, 50, 2000);
            this.rainFillRate = builder.comment("\nConfigures the rate (in mB/t) at which tanks accrue water while raining (0 disables filling from rainfall)")
                    .defineInRange("Rain fill rate", 5, 0, 50);
            this.spawnWaterBlock = builder.comment("\nSet to false to disable water tanks spawning a water block when broken when sufficiently full")
                    .define("Tanks spawn water block", true);
            this.sprinkleInterval = builder.comment("\nThe minimum number of ticks between successive starts of irrigation.")
                    .defineInRange("Sprinkler growth interval", 40, 1, 1200);
            this.sprinkleGrowthChance = builder.comment("\nEvery loop, each unobscured plant in sprinkler range has this chance to get a growth tick from the sprinkler.")
                    .defineInRange("Sprinkler growth chance", 0.2, 0, 1.0);
            this.sprinklerWaterConsumption = builder.comment("\nDefined in terms of mB per second. The irrigation loop progress will pause when there is insufficient water.")
                    .defineInRange("Sprinkler water usage", 10, 0, 1000);
            builder.pop();

            builder.push("decoration");
            this.climbableGrate = builder.comment("\nWhen true, entities will be able to climb on grates")
                    .define("Grates always climbable", true);
            builder.pop();

            builder.push("compat");
            this.progressiveJEI = builder.comment("\nSet to false if you want all mutations to be shown in JEI all the time instead of having to research them")
                    .define("Progressive JEI", true);
            this.seasonLogic = builder.comment("\nDefines the mod controlling season logic in case multiple are installed\naccepted values are: " + SeasonPlugin.getConfigComment())
                    .defineInList("season logic", Names.Mods.SERENE_SEASONS, SeasonPlugin.SEASON_MODS);
            this.topControlledByMagnifyingGlass = builder.comment("\nDefines wether or not additional The One Probe data is rendered only when the magnifying glass is being used")
                    .define("TOP only with magnifying glass", true);
            this.enableBloodMagicCompat = builder.comment("\nSet to false to disable compatibility with Blood Magic (in case things break)")
                    .define("Enable Blood Magic compat", true);
            this.enableBotanyPotsCompat = builder.comment("\nSet to false to disable compatibility with Botany Pots (in case things break)")
                    .define("Enable Botany Pots compat", true);
            this.enableCreateCompat = builder.comment("\nSet to false to disable compatibility with Create (in case things break)")
                    .define("Enable Create compat", true);
            this.enableIndustrialForegoingCompat = builder.comment("\nSet to false to disable compatibility with Industrial Foregoing (in case things break)")
                    .define("Enable Industrial Foregoing compat", true);
            this.enableImmersiveEngineeringCompat = builder.comment("\nSet to false to disable compatibility with Immersive Engineering (in case things break)")
                    .define("Enable Immersive Engineering compat", true);
            this.enableStrawGolemRebornCompat = builder.comment("\nSet to false to disable compatibility with Straw Golem Reborn (in case things break)")
                    .define("Enable Straw Golem Reborn compat", true);
            builder.pop();
        }

        @Override
        public boolean debugMode() {
            return this.debug.get();
        }

        @Override
        public boolean allowPlantingOutsideCropSticks() {
            return this.plantOffCropSticks.get();
        }

        @Override
        public boolean onlyFertileCropsCanSpread() {
            return this.onlyFertileCropsSpread.get();
        }

        @Override
        public boolean allowFertilizerMutations() {
            return this.fertilizerMutations.get();
        }

        @Override
        public boolean overrideVanillaFarming() {
            return this.overrideVanillaFarming.get();
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
        public float seedCompostValue() {
            return this.seedCompostValue.get().floatValue();
        }

        @Override
        public boolean enableAnimalAttractingCrops() {
            return this.animalAttraction.get();
        }

        @Override
        public int seedBagEnchantCost() {
            return this.seedBagEnchantCost.get();
        }

        @Override
        public String getStatTraitLogic() {
            return this.statTraitLogic.get();
        }

        @Override
        public int getGainStatMinValue() {
            return this.minGain.get();
        }

        @Override
        public int getGainStatMaxValue() {
            return this.maxGain.get();
        }

        @Override
        public  boolean isGainStatHidden() {
            return this.hiddenGain.get();
        }

        @Override
        public int getGrowthStatMinValue() {
            return this.minGrowth.get();
        }

        @Override
        public int getGrowthStatMaxValue() {
            return this.maxGrowth.get();
        }

        @Override
        public boolean isGrowthStatHidden() {
            return this.hiddenGrowth.get();
        }

        @Override
        public int getStrengthStatMinValue() {
            return this.minStrength.get();
        }

        @Override
        public int getStrengthStatMaxValue() {
            return this.maxStrength.get();
        }

        @Override
        public boolean isStrengthStatHidden() {
            return this.hiddenStrength.get();
        }

        @Override
        public int getResistanceStatMinValue() {
            return this.minResistance.get();
        }

        @Override
        public int getResistanceStatMaxValue() {
            return this.maxResistance.get();
        }

        @Override
        public boolean isResistanceStatHidden() {
            return this.hiddenResistance.get();
        }

        @Override
        public int getFertilityStatMinValue() {
            return this.minFertility.get();
        }

        @Override
        public int getFertilityStatMaxValue() {
            return this.maxFertility.get();
        }

        @Override
        public boolean isFertilityStatHidden() {
            return this.hiddenFertility.get();
        }

        @Override
        public int getMutativityStatMinValue() {
            return this.minMutativity.get();
        }

        @Override
        public int getMutativityStatMaxValue() {
            return this.maxMutativity.get();
        }

        @Override
        public boolean isMutativityStatHidden() {
            return this.hiddenMutativity.get();
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
        public boolean tankSpawnWaterBlockOnBreak() {
            return this.spawnWaterBlock.get();
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
        public boolean generateMissingDefaultJsons() {
            return this.generateMissingDefaults.get();
        }

        @Override
        public boolean enableJsonWriteback() {
            return this.enableJsonWriteBack.get();
        }

        @Override
        public boolean enableLogging() {
            return this.enableLogging.get();
        }

        @Override
        public boolean progressiveJEI() {
            return this.progressiveJEI.get();
        }

        @Override
        public String getSeasonLogicMod() {
            return this.seasonLogic.get();
        }

        @Override
        public boolean doesMagnifyingGlassControlTOP() {
            return this.topControlledByMagnifyingGlass.get();
        }

        @Override
        public boolean enableBloodMagicCompat() {
            return this.enableBloodMagicCompat.get();
        }

        @Override
        public boolean enableBotanyPotsCompat() {
            return this.enableBotanyPotsCompat.get();
        }

        @Override
        public boolean enableCreateCompat() {
            return this.enableCreateCompat.get();
        }

        @Override
        public boolean enableIndustrialForegoingCompat() {
            return this.enableIndustrialForegoingCompat.get();
        }

        @Override
        public boolean enableImmersiveEngineeringCompat() {
            return this.enableImmersiveEngineeringCompat.get();
        }

        @Override
        public boolean enableStrawGolemRebornCompat() {
            return this.enableStrawGolemRebornCompat.get();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static final class Client extends Common {
        //debug
        private final ForgeConfigSpec.ConfigValue<Boolean> registryTooltips;
        private final ForgeConfigSpec.ConfigValue<Boolean> tagTooltips;
        private final ForgeConfigSpec.ConfigValue<Boolean> nbtTooltips;

        //irrigation
        private final ForgeConfigSpec.ConfigValue<Boolean> disableParticles;

        public Client(ForgeConfigSpec.Builder builder) {
            super(builder);

            builder.push("debug");
            this.registryTooltips = builder.comment("\nSet to true to add Registry information to itemstack tooltips (Client only)")
                    .define("Registry tooltips", false);
            this.tagTooltips = builder.comment("\nSet to true to add Tag information to itemstack tooltips (Client only)")
                    .define("Tag tooltips", false);
            this.nbtTooltips = builder.comment("\nSet to true to add NBT information to itemstack tooltips (Client only)")
                    .define("NBT tooltips", false);
            builder.pop();

            builder.push("irrigation");
            this.disableParticles = builder.comment("\nSet to true to disable particles (Client only)")
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
        public boolean disableParticles() {
            return false;
        }

        @Override
        public ModConfig.Type getSide() {
            return ModConfig.Type.COMMON;
        }
    }
}
