package com.infinityraider.agricraft.content.world;

import com.agricraft.agricore.templates.AgriPlant;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.content.world.IWorldGenPlantManager;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class WorldGenPlantManager implements IWorldGenPlantManager {
    private static final IWorldGenPlantManager INSTANCE = new WorldGenPlantManager();

    public static IWorldGenPlantManager getInstance() {
        return INSTANCE;
    }

    private final Map<ResourceLocation, StructureSettings> settings;

    private WorldGenPlantManager() {
        this.settings = Maps.newHashMap();
    }

    @Override
    public Optional<IAgriGenome> generateGenomeFor(ResourceLocation structure, Random rand) {
        return Optional.ofNullable(this.settings.get(structure))
                .flatMap(settings -> Optional.ofNullable(settings.generate(rand)));
    }

    @Override
    public void registerGenerator(int weight, Function<Random, IAgriGenome> generator, ResourceLocation structure) {
        this.settings.computeIfAbsent(structure, StructureSettings::new).addRule(weight, generator);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onServerClosed(ServerStoppedEvent event) {
        this.settings.clear();
    }

    private static class StructureSettings {
        private final List<PlantRule> rules;
        private int totalWeight;

        private StructureSettings(ResourceLocation structure) {
            this.rules = Lists.newArrayList();
            this.totalWeight = 0;
        }

        @Nullable
        public IAgriGenome generate(Random random) {
            int roll = random.nextInt(this.totalWeight);
            for (PlantRule rule : rules) {
                if (rule.getWeight() > roll) {
                    return rule.generate(random);
                } else {
                    roll -= rule.getWeight();
                }
            }
            return null;
        }

        public void addRule(int weight, Function<Random, IAgriGenome> factory) {
            PlantRule rule = new PlantRule(weight, factory);
            this.rules.add(rule);
            this.totalWeight += rule.getWeight();
        }
    }

    private static class PlantRule {
        private final int weight;
        private final Function<Random, IAgriGenome> factory;

        private PlantRule(int weight, Function<Random, IAgriGenome> factory) {
            this.weight = weight;
            this.factory = factory;
        }

        public int getWeight() {
            return this.weight;
        }

        public IAgriGenome generate(Random random) {
            return this.factory.apply(random);
        }
    }

    public static void registerJsonRule(IAgriPlant plant, AgriPlant json) {
        json.getStructureGenSettings().forEach(settings ->
                settings.getStructures().stream()
                        .map(ResourceLocation::new)
                        .forEach(structure -> getInstance().registerGenerator(
                                settings.getWeight(),
                                plant,
                                (stat) -> settings.getStatsMin(),
                                (stat) -> settings.getStatsMax(), structure)
                        )
        );
    }
}
