package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.world.StructureGreenHouse;
import com.infinityraider.agricraft.world.StructureGreenHouseIrrigated;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.List;
import java.util.Random;

public class VillageCreationHandler {
    public static class GreenHouseHandler implements VillagerRegistry.IVillageCreationHandler {
        @Override
        public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i) {
            return new StructureVillagePieces.PieceWeight(StructureGreenHouse.class, AgriCraftConfig.greenhouseWeight, AgriCraftConfig.greenhouseLimit);
        }

        @Override
        public Class<?> getComponentClass() {
            return StructureGreenHouse.class;
        }

        @Override
        public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
            return StructureGreenHouse.buildComponent(startPiece, pieces, random, p1, p2, p3, facing, p5);
        }
    }

    public static class GreenHouseIrrigatedHandler implements VillagerRegistry.IVillageCreationHandler {
        @Override
        public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i) {
            return new StructureVillagePieces.PieceWeight(StructureGreenHouseIrrigated.class, AgriCraftConfig.greenhouseIrrigatedWeight, AgriCraftConfig.greenhouseIrrigatedLimit);
        }

        @Override
        public Class<?> getComponentClass() {
            return StructureGreenHouseIrrigated.class;
        }

        @Override
        public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
            return StructureGreenHouseIrrigated.buildComponent(startPiece, pieces, random, p1, p2, p3, facing, p5);
        }
    }
}
