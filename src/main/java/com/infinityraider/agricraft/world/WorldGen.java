package com.infinityraider.agricraft.world;

import com.infinityraider.agricraft.handler.VillageCreationHandler;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.reference.Reference;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

public class WorldGen {
    public static void init()
    {
        if (!AgriCraftConfig.disableWorldGen) {
            MapGenStructureIO.registerStructureComponent(StructureGreenHouse.class, Reference.MOD_ID + ":Greenhouse");
            VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationHandler.GreenHouseHandler());

            MapGenStructureIO.registerStructureComponent(StructureGreenHouseIrrigated.class, Reference.MOD_ID + ":GreenhouseIrrigated");
            VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationHandler.GreenHouseIrrigatedHandler());
        }
    }
}
