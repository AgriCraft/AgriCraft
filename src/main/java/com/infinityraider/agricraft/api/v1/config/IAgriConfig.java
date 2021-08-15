package com.infinityraider.agricraft.api.v1.config;

import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Interface providing access to AgriCraft's config settings as defined by the end user in the configuration file
 * Its instance can be obtained via AgriApi.getAgriConfig()
 */
public interface IAgriConfig {
    /**
     * @return the AgriCraft IAgriConfig instance
     */
    @SuppressWarnings("unused")
    static IAgriConfig getInstance() {
        return AgriApi.getAgriConfig();
    }

    /*
     * --------------------
     * DEBUG CONFIG OPTIONS
     * --------------------
     */

    /**
     * @return true if AgriCraft is in debug  mode
     */
    boolean debugMode();

    /**
     * @return true if registry tooltips should be added to items
     */
    @OnlyIn(Dist.CLIENT)
    boolean registryTooltips();

    /**
     * @return true if tag tooltips should be added to items
     */
    @OnlyIn(Dist.CLIENT)
    boolean tagTooltips();

    /**
     * @return true if nbt tooltips should be added to items
     */
    @OnlyIn(Dist.CLIENT)
    boolean nbtTooltips();


    /*
     * -------------------
     * CORE CONFIG OPTIONS
     * -------------------
     */

    /**
     * @return true if AgriCraft should generate missing default jsons
     */
    boolean generateMissingDefaultJsons();

    /**
     * @return true if the coal nugget item is enabled
     */
    boolean enableCoalNugget();

    /**
     * @return true if the diamond nugget item is enabled
     */
    boolean enableDiamondNugget();

    /**
     * @return true if the emerald nugget item is enabled
     */
    boolean enableEmeraldNugget();

    /**
     * @return true if the quartz nugget item is enabled
     */
    boolean enableQuartzNugget();

    /**
     * @return true if AgriCraft should generate resource crop jsons
     */
    boolean generateResourceCropJsons();

    /**
     * @return if crops may be planted off crop sticks
     */
    boolean allowPlantingOutsideCropSticks();

    /**
     * @return if crop sticks cause collisions
     */
    boolean cropSticksCollide();

    /**
     * @return true if only fertile crops may cause, participate in, or contribute to a spreading / mutation action
     */
    boolean onlyFertileCropsCanSpread();

    /**
     * @return false if fertilizers can not trigger mutations (global override for all IAgriFertilizer instances)
     */
    boolean allowFertilizerMutations();

    /**
     * @return true if mutations are allowed to occur on cloning
     */
    boolean allowCloneMutations();

    /**
     * @return true if vanilla farming is overridden
     */
    boolean overrideVanillaFarming();

    /**
     * @return global growth modifier for all AgriCraft crops
     */
    double growthMultiplier();

    /**
     * @return true if only mature crops will drop seeds when broken (global override for all IAgriPlant instances)
     */
    boolean onlyMatureSeedDrops();

    /**
     * @return true if all weeds should be disabled (global override for all IAgriWeed instances)
     */
    boolean disableWeeds();

    /**
     * @return false if weeds are not allowed to be lethal (global override for all IAgriWeed instances)
     */
    boolean allowLethalWeeds();

    /**
     * @return false if weeds are not allowed to be aggressive (global override for all IAgriWeed instances)
     */
    boolean allowAggressiveWeeds();

    /**
     * @return true to not drop crop sticks when breaking crop sticks with weeds
     */
    boolean weedsDestroyCropSticks();

    /**
     * @return false if raking should not drop items (global override for all IAgriWeed instances)
     */
    boolean rakingDropsItems();

    /**
     * @return the compost value for seeds (between 0 and 1, zero means seeds are not compostable)
     */
    float seedCompostValue();

    /**
     * @return true if genes for animal attraction are enabled
     */
    boolean enableAnimalAttractingCrops();

    /**
     * @return the enchantment cost for enchanting the seed bag
     */
    int seedBagEnchantCost();

    /**
     * @return the seed bag capacity
     */
    int seedBagCapacity();

    /**
     * @return true if AgriCraft grass drop loot modifiers are allowed to reset Vanilla grass drops
     */
    boolean allowGrassDropResets();


    /*
     * --------------------
     * STATS CONFIG OPTIONS
     * --------------------
     */

    /**
     * @return the stat trait calculation logic ("min", "max", or "mean")
     */
    String getStatTraitLogic();

    /**
     * @return the minimum allowed value for the Gain stat (inclusive)
     */
    int getGainStatMinValue();

    /**
     * @return the maximum allowed value for the Gain stat (inclusive)
     */
    int getGainStatMaxValue();

    /**
     * @return true if the Gain stat is hidden
     */
    boolean isGainStatHidden();

    /**
     * @return the minimum allowed value for the Growth stat (inclusive)
     */
    int getGrowthStatMinValue();

    /**
     * @return the maximum allowed value for the Growth stat (inclusive)
     */
    int getGrowthStatMaxValue();

    /**
     * @return true if the Growth stat is hidden
     */
    boolean isGrowthStatHidden();

    /**
     * @return the minimum allowed value for the Strength stat (inclusive)
     */
    int getStrengthStatMinValue();

    /**
     * @return the maximum allowed value for the Strength stat (inclusive)
     */
    int getStrengthStatMaxValue();

    /**
     * @return true if the Strength stat is hidden
     */
    boolean isStrengthStatHidden();

    /**
     * @return the minimum allowed value for the Resistance stat (inclusive)
     */
    int getResistanceStatMinValue();

    /**
     * @return the maximum allowed value for the Resistance stat (inclusive)
     */
    int getResistanceStatMaxValue();

    /**
     * @return true if the Resistance stat is hidden
     */
    boolean isResistanceStatHidden();

    /**
     * @return the minimum allowed value for the Fertility stat (inclusive)
     */
    int getFertilityStatMinValue();

    /**
     * @return the maximum allowed value for the Fertility stat (inclusive)
     */
    int getFertilityStatMaxValue();

    /**
     * @return true if the Fertility stat is hidden
     */
    boolean isFertilityStatHidden();

    /**
     * @return the minimum allowed value for the Mutativity stat (inclusive)
     */
    int getMutativityStatMinValue();

    /**
     * @return the maximum allowed value for the Gain stat (inclusive)
     */
    int getMutativityStatMaxValue();

    /**
     * @return true if the Gain stat is hidden
     */
    boolean isMutativityStatHidden();


    /*
     * -------------------------
     * IRRIGATION CONFIG OPTIONS
     * ------------------------
     */

    /**
     * @return the capacity of water tanks in mB
     */
    int tankCapacity();

    /**
     * @return the capacity of water channels in mB
     */
    int channelCapacity();

    /**
     * @return the rain fill rate of tanks in mB/tick
     */
    int rainFillRate();

    /**
     * @return true if tanks should spawn water blocks when broken when sufficiently full
     */
    boolean tankSpawnWaterBlockOnBreak();

    /**
     * @return the amount of ticks between each sprinkle operation
     */
    int sprinkleInterval();

    /**
     * @return the probability to trigger a growth tick from sprinklers
     */
    double sprinkleGrowthChance();

    /**
     * @return the water consumption of sprinklers in mB/tick
     */
    int sprinklerWaterConsumption();

    /**
     * @return true if particles should be disabled
     */
    @OnlyIn(Dist.CLIENT)
    boolean disableParticles();


    /*
     * -------------------------
     * DECORATION CONFIG OPTIONS
     * ------------------------
     */

    /**
     * @return true if grates can be climbed
     */
    boolean areGratesClimbable();


    /*
     * --------------------
     * WORLD CONFIG OPTIONS
     * --------------------
     */

    /**
     * @return the spawn weight for greenhouses
     */
    int getGreenHouseSpawnWeight();

    /**
     * @return the spawn weight for irrigated greenhouses
     */
    int getIrrigatedGreenHouseSpawnWeight();

    /**
     * @return the maximum block size for greenhouses
     */
    int getGreenHouseBlockSizeLimit();

    /**
     * @return the minimum required fraction of glass in greenhouse ceilings
     */
    double greenHouseCeilingGlassFraction();

    /**
     * @return if greenhouses cause crops inside them to ignore seasons
     */
    boolean greenHouseIgnoresSeasons() ;

    /**
     * @return the growth modifier applied to crops growing in greenhouses
     */
    double greenHouseGrowthModifier();


    /*
     * ----------------------------
     * COMPATIBILITY CONFIG OPTIONS
     * ----------------------------
     */

    /**
     * @return true if JEI is in progressive mode, meaning only mutations which the player has discovered will be shown
     */
    boolean progressiveJEI();

    /**
     * @return the mod controlling the season logic in case multiple are present
     */
    String getSeasonLogicMod();

    /**
     * @return if TOP data is controlled by the magnifying glass or not
     */
    boolean doesMagnifyingGlassControlTOP();

    /**
     * @return if Blood Magic compatibility is enabled
     */
    boolean enableBloodMagicCompat();

    /**
     * @return if Botania compatibility is enabled
     */
    boolean enableBotaniaCompat();

    /**
     * @return if Botany Pots compatibility is enabled
     */
    boolean enableBotanyPotsCompat();

    /**
     * @return if weeds are allowed to spawn on Botany Pots
     */
    boolean allowBotanyPotsWeeds();

    /**
     * @return if Create compatibility is enabled
     */
    boolean enableCreateCompat();

    /**
     * @return if Cyclic compatibility is enabled
     */
    boolean enableCyclicCompat();

    /**
     * @return if Industrial Foregoing compatibility is enabled
     */
    boolean enableIndustrialForegoingCompat();

    /**
     * @return if Immersive Engineering compatibility is enabled
     */
    boolean enableImmersiveEngineeringCompat();

    /**
     * @return if Mystical Agriculture compatibility is enabled
     */
    boolean enableMysticalAgricultureCompat();

    /**
     * @return if Straw Golem Reborn compatibility is enabled
     */
    boolean enableStrawGolemRebornCompat();


    /*
     * ------------------------
     * ANIMATION CONFIG OPTIONS
     * ------------------------
     */

    /**
     * @return the animation duration for snapping the camera to the seed analyzer
     */
    @OnlyIn(Dist.CLIENT)
    int seedAnalyzerAnimationDuration();

    /**
     * @return the animation duration for snapping the camera to the journal
     */
    @OnlyIn(Dist.CLIENT)
    int journalAnimationDuration();
}
