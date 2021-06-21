package com.infinityraider.agricraft.content.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.LegacySingleJigsawPiece;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

public class GreenHouseStructures {
    private static final Map<ResourceLocation, Injector> INJECTORS = Maps.newHashMap();

    public static void init() {
        JigsawPattern.PlacementBehaviour placement = JigsawPattern.PlacementBehaviour.TERRAIN_MATCHING;
        // regular greenhouses
        int weightRegular = AgriCraft.instance.getConfig().getGreenHouseSpawnWeight();
        registerStructureToInject(Targets.DESERT, GreenHouses.DESERT, weightRegular, placement);
        registerStructureToInject(Targets.PLAINS, GreenHouses.PLAINS, weightRegular, placement);
        registerStructureToInject(Targets.SAVANNA, GreenHouses.SAVANNA, weightRegular, placement);
        registerStructureToInject(Targets.SNOWY, GreenHouses.SNOWY, weightRegular, placement);
        registerStructureToInject(Targets.TAIGA, GreenHouses.TAIGA, weightRegular, placement);
        // irrigated greenhouses
        int weightIrrigated = AgriCraft.instance.getConfig().getIrrigatedGreenHouseSpawnWeight();
        // TODO
        /*
        registerStructureToInject(Targets.DESERT, GreenHousesIrrigated.DESERT, weightIrrigated, placement);
        registerStructureToInject(Targets.PLAINS, GreenHousesIrrigated.PLAINS, weightIrrigated, placement);
        registerStructureToInject(Targets.SAVANNA, GreenHousesIrrigated.SAVANNA, weightIrrigated, placement);
        registerStructureToInject(Targets.SNOWY, GreenHousesIrrigated.SNOWY, weightIrrigated, placement);
        registerStructureToInject(Targets.TAIGA, GreenHousesIrrigated.TAIGA, weightIrrigated, placement);
        */
    }

    private static void registerStructureToInject(ResourceLocation target, ResourceLocation structure, int weight, JigsawPattern.PlacementBehaviour placement) {
        INJECTORS.computeIfAbsent(target, Injector::new).addPiece(structure, weight, placement);
    }

    public static void inject(DynamicRegistries registries) {
        INJECTORS.values().forEach(injector -> injector.inject(registries));
    }

    // TODO: migrate this to InfinityLib and streamline it
    private static class Injector {
        private final List<JigsawPiece> pieces;
        private final ResourceLocation target;

        protected Injector(ResourceLocation target) {
            this.pieces = Lists.newArrayList();
            this.target = target;
        }

        public void addPiece(ResourceLocation structure, int weight, JigsawPattern.PlacementBehaviour placement) {
            if(weight > 0) {
                LegacySingleJigsawPiece piece = JigsawPiece.func_242849_a(structure.toString()).apply(placement);
                for (int i = 0; i < weight; i++) {
                    pieces.add(piece);
                }
            }
        }

        @SuppressWarnings("unchecked")
        public void inject(DynamicRegistries registries) {
            JigsawPattern pool = registries.getRegistry(Registry.JIGSAW_POOL_KEY).getOptional(this.target).orElse(null);
            if(pool == null) {
                AgriCraft.instance.getLogger().error("Could not inject structures into {0}, pool not found", this.target);
                return;
            } try {
                // fetch the field
                Field field = ObfuscationReflectionHelper.findField(JigsawPattern.class, "field_214953_e");
                // set accessible
                field.setAccessible(true);
                // remove final modifier
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                // make a copy of the field in a new list
                List<JigsawPiece> pieces = Lists.newArrayList((List<JigsawPiece>) field.get(pool));
                // inject new pieces
                pieces.addAll(this.pieces);
                // set the field
                field.set(pool, pieces);
            } catch(Exception e) {
                AgriCraft.instance.getLogger().error("Failed to inject structures into {0}, exception was thrown", this.target);
                AgriCraft.instance.getLogger().printStackTrace(e);
            }
        }
    }

    public static final class Targets {
        public static final ResourceLocation DESERT = new ResourceLocation("village/desert/houses");
        public static final ResourceLocation PLAINS = new ResourceLocation("village/plains/houses");
        public static final ResourceLocation SAVANNA = new ResourceLocation("village/savanna/houses");
        public static final ResourceLocation SNOWY = new ResourceLocation("village/snowy/houses");
        public static final ResourceLocation TAIGA = new ResourceLocation("village/taiga/houses");

        private Targets() {}
    }

    public static final class GreenHouses {
        public static final ResourceLocation DESERT = new ResourceLocation(AgriCraft.instance.getModId(), "village/desert/greenhouse");
        public static final ResourceLocation PLAINS = new ResourceLocation(AgriCraft.instance.getModId(), "village/plains/greenhouse");
        public static final ResourceLocation SAVANNA = new ResourceLocation(AgriCraft.instance.getModId(), "village/savanna/greenhouse");
        public static final ResourceLocation SNOWY = new ResourceLocation(AgriCraft.instance.getModId(), "village/snowy/greenhouse");
        public static final ResourceLocation TAIGA = new ResourceLocation(AgriCraft.instance.getModId(), "village/taiga/greenhouse");

        private GreenHouses() {}
    }

    public static final class GreenHousesIrrigated {
        public static final ResourceLocation DESERT = new ResourceLocation(AgriCraft.instance.getModId(), "village/desert/greenhouse_irrigated");
        public static final ResourceLocation PLAINS = new ResourceLocation(AgriCraft.instance.getModId(), "village/plains/greenhouse_irrigated");
        public static final ResourceLocation SAVANNA = new ResourceLocation(AgriCraft.instance.getModId(), "village/savanna/greenhouse_irrigated");
        public static final ResourceLocation SNOWY = new ResourceLocation(AgriCraft.instance.getModId(), "village/snowy/greenhouse_irrigated");
        public static final ResourceLocation TAIGA = new ResourceLocation(AgriCraft.instance.getModId(), "village/taiga/greenhouse_irrigated");

        private GreenHousesIrrigated() {}
    }
}
