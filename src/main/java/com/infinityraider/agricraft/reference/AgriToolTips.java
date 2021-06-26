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

    // Crop tooltips
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
    public static final ITextComponent LIGHT = new TranslationTextComponent("agricraft.tooltip.light");

    // Valve tooltips
    public static final ITextComponent VALVE_INFO_OPEN = new TranslationTextComponent("agricraft.tooltip.valve.open");
    public static final ITextComponent VALVE_INFO_CLOSED = new TranslationTextComponent("agricraft.tooltip.valve.closed");

    // Item tooltips
    public static final ITextComponent CLIPPER
            = new TranslationTextComponent("agricraft.tooltip.clipper").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent JOURNAL_USE_1
            = new TranslationTextComponent("agricraft.tooltip.journal_use_1").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent JOURNAL_USE_2
            = new TranslationTextComponent("agricraft.tooltip.journal_use_2").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent JOURNAL_SEEDS
            = new TranslationTextComponent("agricraft.tooltip.journal_seeds").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent MAGNIFYING_GLASS
            = new TranslationTextComponent("agricraft.tooltip.magnifying_glass").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent RAKE
            = new TranslationTextComponent("agricraft.tooltip.rake").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent TROWEL
            = new TranslationTextComponent("agricraft.tooltip.trowel").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent SEED_BAG_ACTIVE
            = new TranslationTextComponent("agricraft.tooltip.seed_bag_active").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent SEED_BAG_CONTENTS
            = new TranslationTextComponent("agricraft.tooltip.seed_bag_contents").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent SEED_BAG_EMPTY
            = new TranslationTextComponent("agricraft.tooltip.seed_bag_empty").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent SEED_BAG_SORTER
            = new TranslationTextComponent("agricraft.tooltip.seed_bag_sorter").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent SEED_BAG_SORTER_DEFAULT
            = new TranslationTextComponent("agricraft.tooltip.seed_bag_sorter_default").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent SEED_BAG_MAIN_HAND
            = new TranslationTextComponent("agricraft.tooltip.seed_bag_main_hand").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent SEED_BAG_OFF_HAND
            = new TranslationTextComponent("agricraft.tooltip.seed_bag_off_hand").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent SEED_BAG_SCROLLING
            = new TranslationTextComponent("agricraft.tooltip.seed_bag_scrolling").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent SEED_BAG_INACTIVE_1
            = new TranslationTextComponent("agricraft.tooltip.seed_bag_inactive_1").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent SEED_BAG_INACTIVE_2
            = new TranslationTextComponent("agricraft.tooltip.seed_bag_inactive_2").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent SEED_ANALYZER_L1
            = new TranslationTextComponent("agricraft.tooltip.analyzer.l1").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent SEED_ANALYZER_L2
            = new TranslationTextComponent("agricraft.tooltip.analyzer.l2").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent GRATE_L1
            = new TranslationTextComponent("agricraft.tooltip.grate.l1").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent GRATE_L2
            = new TranslationTextComponent("agricraft.tooltip.grate.l2").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent TANK_L1
            = new TranslationTextComponent("agricraft.tooltip.tank.l1").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent SPRINKLER
            = new TranslationTextComponent("agricraft.tooltip.sprinkler.l1").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent VALVE_L1
            = new TranslationTextComponent("agricraft.tooltip.valve.l1").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent VALVE_L2
            = new TranslationTextComponent("agricraft.tooltip.valve.l2").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent VALVE_L3
            = new TranslationTextComponent("agricraft.tooltip.valve.l3").mergeStyle(TextFormatting.DARK_GRAY);

    public static final ITextComponent SNEAK_INFO
            = new TranslationTextComponent("agricraft.tooltip.sneak_info").mergeStyle(TextFormatting.DARK_GRAY);
    public static final ITextComponent EMPTY_LINE = new StringTextComponent("");

    // Feedback messages
    public static final ITextComponent MSG_ANALYZER_VIEW_BLOCKED = new TranslationTextComponent("agricraft.message.analyzer_view_blocked");
    public static final ITextComponent MSG_CLIPPING_IMPOSSIBLE = new TranslationTextComponent("agricraft.message.clipping_impossible");
    public static final ITextComponent MSG_SEED_BAG_SHAKE = new TranslationTextComponent("agricraft.message.seed_bag_shake");
    public static final ITextComponent MSG_SEED_BAG_EMPTY = new TranslationTextComponent("agricraft.message.seed_bag_empty");
    public static final ITextComponent MSG_SEED_BAG_DEFAULT_SORTER = new TranslationTextComponent("agricraft.message.seed_bag_sorter.default");
    public static final ITextComponent MSG_TROWEL_WEED = new TranslationTextComponent("agricraft.message.trowel_weed");
    public static final ITextComponent MSG_TROWEL_PLANT = new TranslationTextComponent("agricraft.message.trowel_plant");
    public static final ITextComponent MSG_TROWEL_NO_PLANT = new TranslationTextComponent("agricraft.message.trowel_no_plant");

    public static ITextComponent getPlantTooltip(IAgriPlant plant) {
        return new StringTextComponent("")
                .appendSibling(PLANT)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(plant.getPlantName());
    }

    public static ITextComponent getWeedTooltip(IAgriWeed weed) {
        return new StringTextComponent("")
                .appendSibling(WEED)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(weed.getWeedName());
    }

    public static ITextComponent getGrowthTooltip(IAgriGrowthStage growth) {
        return getGrowthTooltip(growth, growth.growthPercentage());
    }

    public static ITextComponent getGrowthTooltip(IAgriGrowthStage growth, double precise) {
        if (growth.isFinal()) {
            return new StringTextComponent("")
                    .appendSibling(GROWTH)
                    .appendSibling(new StringTextComponent(": "))
                    .appendSibling(MATURE);
        } else {
            return new StringTextComponent("")
                    .appendSibling(GROWTH)
                    .appendSibling(new StringTextComponent(": " + ((int) (100 * precise) + "%")));
        }
    }

    public static ITextComponent getWeedGrowthTooltip(double growth) {
        return new StringTextComponent("")
                .appendSibling(WEED_GROWTH)
                .appendSibling(new StringTextComponent(": " + ((int) (100 * growth) + "%")));
    }

    public static ITextComponent getSoilTooltip(IAgriSoil soil) {
        return new StringTextComponent("")
                .appendSibling(SOIL)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(soil.getName());
    }

    public static ITextComponent getUnknownTooltip(ITextComponent source) {
        return new StringTextComponent("")
                .appendSibling(source)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(UNKNOWN);
    }

    public static ITextComponent getGeneTooltip(IAgriGenePair<?> genePair) {
        return new StringTextComponent("")
                .appendSibling(genePair.getGene().getGeneDescription())
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(genePair.getDominant().getTooltip())
                .appendSibling(new StringTextComponent( " - "))
                .appendSibling(genePair.getRecessive().getTooltip());
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
                        combined.appendSibling(new StringTextComponent(separator));
                    }
                    combined.appendSibling(toAdd);
                };
            }

            @Override
            public BinaryOperator<IFormattableTextComponent> combiner() {
                return (a, b) -> {
                    if(a.getString().isEmpty()) {
                        return a.appendSibling(new StringTextComponent(separator)).appendSibling(b);
                    } else {
                        return a.appendSibling(b);
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
