package com.infinityraider.agricraft.handler;

import java.util.List;
import java.util.Random;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.world.StructureGreenhouse;
import com.infinityraider.agricraft.world.StructureGreenhouseIrrigated;

public class VillageCreationHandler {
    public static class GreenhouseHandler implements VillagerRegistry.IVillageCreationHandler {
        //get the weight to spawn the greenhouse
        @Override
        public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i) {
            //args: structure, weight, max spawns
            return new StructureVillagePieces.PieceWeight(StructureGreenhouse.class, AgriCraftConfig.greenhouseWeight, AgriCraftConfig.greenhouseLimit);
        }

        @Override
        public Class<?> getComponentClass() {
            return StructureGreenhouse.class;
        }

        @Override
        public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
            return StructureGreenhouse.buildComponent(startPiece, pieces, random, p1, p2, p3, facing, p5);
        }
    }

    public static class GreenhouseIrrigatedHandler implements VillagerRegistry.IVillageCreationHandler {
        //get the weight to spawn the greenhouse
        @Override
        public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i) {
            //args: structure, weight, max spawns
            return new StructureVillagePieces.PieceWeight(StructureGreenhouseIrrigated.class, AgriCraftConfig.greenhouseIrrigatedWeight, AgriCraftConfig.greenhouseIrrigatedLimit);
        }

        @Override
        public Class<?> getComponentClass() {
            return StructureGreenhouseIrrigated.class;
        }

        @Override
        public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
            return StructureGreenhouseIrrigated.buildComponent(startPiece, pieces, random, p1, p2, p3, facing, p5);
        }
    }
}
