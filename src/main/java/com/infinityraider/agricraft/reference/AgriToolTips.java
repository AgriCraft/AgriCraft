package com.infinityraider.agricraft.reference;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class AgriToolTips {
    public static final TranslatableComponent UNKNOWN = new TranslatableComponent("agricraft.tooltip.unknown");

    public static final MutableComponent GENOME = new TranslatableComponent("agricraft.tooltip.genome")
            .withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD);

    // Crop tooltips
    public static final MutableComponent PLANT = new TranslatableComponent("agricraft.tooltip.plant");
    public static final MutableComponent NO_PLANT = new TranslatableComponent("agricraft.tooltip.no_plant");
    public static final MutableComponent GROWTH = new TranslatableComponent("agricraft.tooltip.growth");
    public static final MutableComponent MATURE = new TranslatableComponent("agricraft.tooltip.mature");
    public static final MutableComponent WEED = new TranslatableComponent("agricraft.tooltip.weed");
    public static final MutableComponent NO_WEED = new TranslatableComponent("agricraft.tooltip.no_weed");
    public static final MutableComponent WEED_GROWTH = new TranslatableComponent("agricraft.tooltip.weed_growth");
    public static final MutableComponent FERTILE = new TranslatableComponent("agricraft.tooltip.fertile");
    public static final MutableComponent NOT_FERTILE = new TranslatableComponent("agricraft.tooltip.not_fertile");
    public static final MutableComponent SOIL = new TranslatableComponent("agricraft.tooltip.soil");
    public static final MutableComponent LIGHT = new TranslatableComponent("agricraft.tooltip.light");

    // Valve tooltips
    public static final MutableComponent VALVE_INFO_OPEN = new TranslatableComponent("agricraft.tooltip.valve.open");
    public static final MutableComponent VALVE_INFO_CLOSED = new TranslatableComponent("agricraft.tooltip.valve.closed");

    // Item tooltips
    public static final MutableComponent CLIPPER
            = new TranslatableComponent("agricraft.tooltip.clipper").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent GENE_INSPECTOR
            = new TranslatableComponent("agricraft.tooltip.gene_inspector").withStyle(ChatFormatting.BLUE);
    public static final MutableComponent JOURNAL_USE_1
            = new TranslatableComponent("agricraft.tooltip.journal_use_1").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent JOURNAL_USE_2
            = new TranslatableComponent("agricraft.tooltip.journal_use_2").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent JOURNAL_SEEDS
            = new TranslatableComponent("agricraft.tooltip.journal_seeds").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent MAGNIFYING_GLASS
            = new TranslatableComponent("agricraft.tooltip.magnifying_glass").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent RAKE
            = new TranslatableComponent("agricraft.tooltip.rake").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent TROWEL
            = new TranslatableComponent("agricraft.tooltip.trowel").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent SEED_BAG_ACTIVE
            = new TranslatableComponent("agricraft.tooltip.seed_bag_active").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent SEED_BAG_CONTENTS
            = new TranslatableComponent("agricraft.tooltip.seed_bag_contents").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent SEED_BAG_EMPTY
            = new TranslatableComponent("agricraft.tooltip.seed_bag_empty").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent SEED_BAG_SORTER
            = new TranslatableComponent("agricraft.tooltip.seed_bag_sorter").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent SEED_BAG_SORTER_DEFAULT
            = new TranslatableComponent("agricraft.tooltip.seed_bag_sorter_default").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent SEED_BAG_MAIN_HAND
            = new TranslatableComponent("agricraft.tooltip.seed_bag_main_hand").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent SEED_BAG_OFF_HAND
            = new TranslatableComponent("agricraft.tooltip.seed_bag_off_hand").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent SEED_BAG_SCROLLING
            = new TranslatableComponent("agricraft.tooltip.seed_bag_scrolling").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent SEED_BAG_INACTIVE_1
            = new TranslatableComponent("agricraft.tooltip.seed_bag_inactive_1").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent SEED_BAG_INACTIVE_2
            = new TranslatableComponent("agricraft.tooltip.seed_bag_inactive_2").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent SEED_ANALYZER_L1
            = new TranslatableComponent("agricraft.tooltip.analyzer.l1").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent SEED_ANALYZER_L2
            = new TranslatableComponent("agricraft.tooltip.analyzer.l2").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent SEED_ANALYZER_L3
            = new TranslatableComponent("agricraft.tooltip.analyzer.l3").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent GRATE_L1
            = new TranslatableComponent("agricraft.tooltip.grate.l1").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent GRATE_L2
            = new TranslatableComponent("agricraft.tooltip.grate.l2").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent TANK_L1
            = new TranslatableComponent("agricraft.tooltip.tank.l1").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent SPRINKLER
            = new TranslatableComponent("agricraft.tooltip.sprinkler.l1").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent VALVE_L1
            = new TranslatableComponent("agricraft.tooltip.valve.l1").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent VALVE_L2
            = new TranslatableComponent("agricraft.tooltip.valve.l2").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent VALVE_L3
            = new TranslatableComponent("agricraft.tooltip.valve.l3").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent GREENHOUSE_MONITOR_L1
            = new TranslatableComponent("agricraft.tooltip.greenhouse_monitor.l1").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent GREENHOUSE_MONITOR_L2
            = new TranslatableComponent("agricraft.tooltip.greenhouse_monitor.l2").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent GREENHOUSE_MONITOR_L3
            = new TranslatableComponent("agricraft.tooltip.greenhouse_monitor.l3").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent GREENHOUSE_MONITOR_L4
            = new TranslatableComponent("agricraft.tooltip.greenhouse_monitor.l3").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent GREENHOUSE_MONITOR_L5
            = new TranslatableComponent("agricraft.tooltip.greenhouse_monitor.l3").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent GREENHOUSE_MONITOR_L6
            = new TranslatableComponent("agricraft.tooltip.greenhouse_monitor.l3").withStyle(ChatFormatting.DARK_GRAY);

    public static final MutableComponent SNEAK_INFO
            = new TranslatableComponent("agricraft.tooltip.sneak_info").withStyle(ChatFormatting.DARK_GRAY);
    public static final MutableComponent EMPTY_LINE = new TextComponent("");

    // Feedback messages
    public static final MutableComponent MSG_ANALYZER_VIEW_BLOCKED = new TranslatableComponent("agricraft.message.analyzer_view_blocked");
    public static final MutableComponent MSG_CLIPPING_IMPOSSIBLE = new TranslatableComponent("agricraft.message.clipping_impossible");
    public static final MutableComponent MSG_SEED_BAG_SHAKE = new TranslatableComponent("agricraft.message.seed_bag_shake");
    public static final MutableComponent MSG_SEED_BAG_EMPTY = new TranslatableComponent("agricraft.message.seed_bag_empty");
    public static final MutableComponent MSG_SEED_BAG_DEFAULT_SORTER = new TranslatableComponent("agricraft.message.seed_bag_sorter.default");
    public static final MutableComponent MSG_TROWEL_WEED = new TranslatableComponent("agricraft.message.trowel_weed");
    public static final MutableComponent MSG_TROWEL_PLANT = new TranslatableComponent("agricraft.message.trowel_plant");
    public static final MutableComponent MSG_TROWEL_NO_PLANT = new TranslatableComponent("agricraft.message.trowel_no_plant");

    public static MutableComponent getPlantTooltip(IAgriPlant plant) {
        return new TextComponent("")
                .append(PLANT)
                .append(new TextComponent(": "))
                .append(plant.getPlantName());
    }

    public static MutableComponent getWeedTooltip(IAgriWeed weed) {
        return new TextComponent("")
                .append(WEED)
                .append(new TextComponent(": "))
                .append(weed.getWeedName());
    }

    public static MutableComponent getGrowthTooltip(IAgriGrowthStage growth) {
        return getGrowthTooltip(growth, growth.growthPercentage());
    }

    public static MutableComponent getGrowthTooltip(IAgriGrowthStage growth, double precise) {
        if (growth.isFinal()) {
            return new TextComponent("")
                    .append(GROWTH)
                    .append(new TextComponent(": "))
                    .append(MATURE);
        } else {
            return new TextComponent("")
                    .append(GROWTH)
                    .append(new TextComponent(": " + ((int) (100 * precise) + "%")));
        }
    }

    public static MutableComponent getWeedGrowthTooltip(double growth) {
        return new TextComponent("")
                .append(WEED_GROWTH)
                .append(new TextComponent(": " + ((int) (100 * growth) + "%")));
    }

    public static MutableComponent getSoilTooltip(IAgriSoil soil) {
        return new TextComponent("")
                .append(SOIL)
                .append(new TextComponent(": "))
                .append(soil.getName());
    }

    public static MutableComponent getUnknownTooltip(MutableComponent source) {
        return new TextComponent("")
                .append(source)
                .append(new TextComponent(": "))
                .append(UNKNOWN);
    }

    public static MutableComponent getGeneTooltip(IAgriGenePair<?> genePair) {
        return new TextComponent("")
                .append(genePair.getGene().getGeneDescription())
                .append(new TextComponent(": "))
                .append(genePair.getDominant().getTooltip())
                .append(new TextComponent( " - "))
                .append(genePair.getRecessive().getTooltip());
    }

    public static MutableComponent collect(Stream<MutableComponent> stream, String separator) {
        return stream.collect(collector(separator));
    }

    private static Collector<MutableComponent, MutableComponent, MutableComponent> collector(String separator) {
        return new Collector<MutableComponent, MutableComponent, MutableComponent>() {

            @Override
            public Supplier<MutableComponent> supplier() {
                return () -> new TextComponent("");
            }

            @Override
            public BiConsumer<MutableComponent, MutableComponent> accumulator() {
                return (combined, toAdd) -> {
                    if(!combined.getString().isEmpty()) {
                        combined.append(new TextComponent(separator));
                    }
                    combined.append(toAdd);
                };
            }

            @Override
            public BinaryOperator<MutableComponent> combiner() {
                return (a, b) -> {
                    if(a.getString().isEmpty()) {
                        return a.append(new TextComponent(separator)).append(b);
                    } else {
                        return a.append(b);
                    }
                };
            }

            @Override
            public Function<MutableComponent, MutableComponent> finisher() {
                return text -> text;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return ImmutableSet.of();
            }
        };
    }
}
