package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.handler.VillageCreationHandler;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.world.StructureGreenhouse;
import com.infinityraider.agricraft.world.StructureGreenhouseIrrigated;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.Collection;

public class WorldGen {
    private static int villagerId;

    public static void init() {
        if (!ConfigurationHandler.disableWorldGen) {
            //register villagers
            if (ConfigurationHandler.villagerEnabled) {
                Collection<Integer> usedIds = VillagerRegistry.getRegisteredVillagers();
                int id = 5;
                while (usedIds.contains(id)) {
                    id++;
                }
                registerVillager(id);
            }

            //add greenhouses to villages
            MapGenStructureIO.registerStructureComponent(StructureGreenhouse.class, Reference.MOD_ID + ":Greenhouse");
            VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationHandler.GreenhouseHandler());

            //add irrigated greenhouses to villages
            if (!ConfigurationHandler.disableIrrigation) {
                MapGenStructureIO.registerStructureComponent(StructureGreenhouseIrrigated.class, Reference.MOD_ID + ":GreenhouseIrrigated");
                VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationHandler.GreenhouseIrrigatedHandler());
            }
        }
    }

    public static int getVillagerId() {
        return villagerId;
    }

    private static void registerVillager(int id) {
        VillagerRegistry.instance().registerVillagerId(id);
        AgriCraft.proxy.registerVillagerSkin(id, "textures/entities/villager.png");
        villagerId = id;
    }
}
