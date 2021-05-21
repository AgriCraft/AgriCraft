package com.infinityraider.agricraft.reference;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import net.minecraft.util.text.*;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

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
    public static final ITextComponent JOURNAL_USE_1 = new TranslationTextComponent("agricraft.tooltip.journal_use_1");
    public static final ITextComponent JOURNAL_USE_2 = new TranslationTextComponent("agricraft.tooltip.journal_use_2");
    public static final ITextComponent JOURNAL_SEEDS = new TranslationTextComponent("agricraft.tooltip.journal_seeds");
    public static final ITextComponent MAGNIFYING_GLASS = new TranslationTextComponent("agricraft.tooltip.magnifying_glass");
    public static final ITextComponent RAKE = new TranslationTextComponent("agricraft.tooltip.rake");
    public static final ITextComponent TROWEL = new TranslationTextComponent("agricraft.tooltip.trowel");
    public static final ITextComponent SEED_BAG_ACTIVE = new TranslationTextComponent("agricraft.tooltip.seed_bag_active");
    public static final ITextComponent SEED_BAG_CONTENTS = new TranslationTextComponent("agricraft.tooltip.seed_bag_contents");
    public static final ITextComponent SEED_BAG_EMPTY = new TranslationTextComponent("agricraft.tooltip.seed_bag_empty");
    public static final ITextComponent SEED_BAG_SORTER = new TranslationTextComponent("agricraft.tooltip.seed_bag_sorter");
    public static final ITextComponent SEED_BAG_SORTER_DEFAULT = new TranslationTextComponent("agricraft.tooltip.seed_bag_sorter_default");
    public static final ITextComponent SEED_BAG_MAIN_HAND = new TranslationTextComponent("agricraft.tooltip.seed_bag_main_hand");
    public static final ITextComponent SEED_BAG_OFF_HAND = new TranslationTextComponent("agricraft.tooltip.seed_bag_off_hand");
    public static final ITextComponent SEED_BAG_SCROLLING = new TranslationTextComponent("agricraft.tooltip.seed_bag_scrolling");
    public static final ITextComponent SEED_BAG_INACTIVE_1 = new TranslationTextComponent("agricraft.tooltip.seed_bag_inactive_1");
    public static final ITextComponent SEED_BAG_INACTIVE_2 = new TranslationTextComponent("agricraft.tooltip.seed_bag_inactive_2");

    public static final ITextComponent SEED_ANALYZER_L1 = new TranslationTextComponent("agricraft.tooltip.analyzer.l1");
    public static final ITextComponent SEED_ANALYZER_L2 = new TranslationTextComponent("agricraft.tooltip.analyzer.l2");

    public static final ITextComponent GRATE_L1 = new TranslationTextComponent("agricraft.tooltip.grate.l1");
    public static final ITextComponent GRATE_L2 = new TranslationTextComponent("agricraft.tooltip.grate.l2");

    public static final ITextComponent TANK_L1 = new TranslationTextComponent("agricraft.tooltip.tank.l1");

    public static final ITextComponent LIGHT = new TranslationTextComponent("agricraft.tooltip.light");

    public static final ITextComponent MSG_ANALYZER_VIEW_BLOCKED = new TranslationTextComponent("agricraft.message.analyzer_view_blocked");
    public static final ITextComponent MSG_CLIPPING_IMPOSSIBLE = new TranslationTextComponent("agricraft.message.clipping_impossible");
    public static final ITextComponent MSG_TROWEL_WEED = new TranslationTextComponent("agricraft.message.trowel_weed");
    public static final ITextComponent MSG_TROWEL_PLANT = new TranslationTextComponent("agricraft.message.trowel_plant");
    public static final ITextComponent MSG_TROWEL_NO_PLANT = new TranslationTextComponent("agricraft.message.trowel_no_plant");

    public static final ITextComponent SNEAK_INFO = new TranslationTextComponent("agricraft.tooltip.sneak_info");
    public static final ITextComponent EMPTY_LINE = new StringTextComponent("");

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
                .append(genePair.getGene().getGeneDescription())
                .append(new StringTextComponent(": "))
                .append(genePair.getDominant().getTooltip())
                .append(new StringTextComponent( " - "))
                .append(genePair.getRecessive().getTooltip());
    }

    public static ITextComponent collect(Stream<ITextComponent> stream, String separator) {
        return stream.collect(collector(separator));
    }

    private static Collector<ITextComponent, IFormattableTextComponent, ITextComponent> collector(String separator) {
        return new Collector<ITextComponent, IFormattableTextComponent, ITextComponent>() {

            @Override
            public Supplier<IFormattableTextComponent> supplier() {
                return () -> new StringTextComponent("");
            }

            @Override
            public BiConsumer<IFormattableTextComponent, ITextComponent> accumulator() {
                return (combined, toAdd) -> {
                    if(!combined.getString().isEmpty()) {
                        combined.append(new StringTextComponent(separator));
                    }
                    combined.append(toAdd);
                };
            }

            @Override
            public BinaryOperator<IFormattableTextComponent> combiner() {
                return (a, b) -> {
                    if(a.getString().isEmpty()) {
                        return a.append(new StringTextComponent(separator)).append(b);
                    } else {
                        return a.append(b);
                    }
                };
            }

            @Override
            public Function<IFormattableTextComponent, ITextComponent> finisher() {
                return text -> text;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return ImmutableSet.of();
            }
        };
    }
}
