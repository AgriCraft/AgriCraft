package com.infinityraider.agricraft.reference;

import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class AgriToolTips {
    public static final TranslationTextComponent UNKNOWN = new TranslationTextComponent("agricraft.tooltip.unknown");

    public static final ITextComponent GENOME = new TranslationTextComponent("agricraft.tooltip.genome")
            .mergeStyle(TextFormatting.DARK_GREEN, TextFormatting.BOLD);

    public static final ITextComponent SEED = new TranslationTextComponent("agricraft.tooltip.seed");
    public static final ITextComponent PLANT = new TranslationTextComponent("agricraft.tooltip.plant");
    public static final ITextComponent NO_PLANT = new TranslationTextComponent("agricraft.tooltip.no_plant");
    public static final ITextComponent GROWTH = new TranslationTextComponent("agricraft.tooltip.growth");
    public static final ITextComponent MATURE = new TranslationTextComponent("agricraft.tooltip.mature");

    public static final ITextComponent WEED = new TranslationTextComponent("agricraft.tooltip.weed");
    public static final ITextComponent NO_WEED = new TranslationTextComponent("agricraft.tooltip.no_weed");
    public static final ITextComponent WEED_GROWTH = new TranslationTextComponent("agricraft.tooltip.weed_growth");

    public static final ITextComponent FERTILE = new TranslationTextComponent("agricraft.tooltip.fertile");
    public static final ITextComponent NOT_FERTILE = new TranslationTextComponent("agricraft.tooltip.not_fertile");

    public static final ITextComponent SOIL = new TranslationTextComponent("agricraft.tooltip.soil");
    public static final ITextComponent CLIPPER = new TranslationTextComponent("agricraft.tooltip.clipper");
    public static final ITextComponent MAGNIFYING_GLASS = new TranslationTextComponent("agricraft.tooltip.magnifying_glass");
    public static final ITextComponent RAKE = new TranslationTextComponent("agricraft.tooltip.rake");
    public static final ITextComponent TROWEL = new TranslationTextComponent("agricraft.tooltip.trowel");

    public static final ITextComponent SEED_ANALYZER_L1 = new TranslationTextComponent("agricraft.tooltip.analyzer.l1");
    public static final ITextComponent SEED_ANALYZER_L2 = new TranslationTextComponent("agricraft.tooltip.analyzer.l2");

    public static final ITextComponent GRATE_L1 = new TranslationTextComponent("agricraft.tooltip.grate.l1");
    public static final ITextComponent GRATE_L2 = new TranslationTextComponent("agricraft.tooltip.grate.l2");

    public static final ITextComponent MSG_CLIPPING_IMPOSSIBLE = new TranslationTextComponent("agricraft.message.clipping_impossible");
    public static final ITextComponent MSG_TROWEL_WEED = new TranslationTextComponent("agricraft.message.trowel_weed");
    public static final ITextComponent MSG_TROWEL_PLANT = new TranslationTextComponent("agricraft.message.trowel_plant");
    public static final ITextComponent MSG_TROWEL_NO_PLANT = new TranslationTextComponent("agricraft.message.trowel_no_plant");

    public static ITextComponent getSeedTooltip(AgriSeed seed) {
        return new StringTextComponent("")
                .append(SEED)
                .append(new StringTextComponent(": "))
                .append(new TranslationTextComponent(seed.getPlant().getId()));
    }

    public static ITextComponent getPlantTooltip(IAgriPlant plant) {
        return new StringTextComponent("")
                .append(PLANT)
                .append(new StringTextComponent(": "))
                .append(plant.getPlantName());
    }

    public static ITextComponent getWeedTooltip(IAgriWeed weed) {
        return new StringTextComponent("")
                .append(WEED)
                .append(new StringTextComponent(": "))
                .append(weed.getWeedName());
    }

    public static ITextComponent getGrowthTooltip(IAgriGrowthStage growth) {
        if (growth.isFinal()) {
            return new StringTextComponent("")
                    .append(GROWTH)
                    .append(new StringTextComponent(": "))
                    .append(MATURE);
        } else {
            return new StringTextComponent("")
                    .append(GROWTH)
                    .append(new StringTextComponent(": " + ((int) (100 * growth.growthPercentage()) + "%")));
        }
    }

    public static ITextComponent getWeedGrowthTooltip(IAgriGrowthStage growth) {
        return new StringTextComponent("")
                .append(WEED_GROWTH)
                .append(new StringTextComponent(": " + ((int) (100 * growth.growthPercentage()) + "%")));
    }

    public static ITextComponent getSoilTooltip(IAgriSoil soil) {
        return new StringTextComponent("")
                .append(SOIL)
                .append(new StringTextComponent(": "))
                .append(soil.getName());
    }

    public static ITextComponent getUnknownTooltip(ITextComponent source) {
        return new StringTextComponent("")
                .append(source)
                .append(new StringTextComponent(": "))
                .append(UNKNOWN);
    }

    public static ITextComponent getGeneTooltip(IAgriGenePair<?> genePair) {
        return new StringTextComponent("")
                .append(genePair.getGene().getDescription())
                .append(new StringTextComponent(": "))
                .append(genePair.getDominant().getTooltip())
                .append(new StringTextComponent( " - "))
                .append(genePair.getRecessive().getTooltip());
    }
}
