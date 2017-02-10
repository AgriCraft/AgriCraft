/*
 */
package com.infinityraider.agricraft.handler;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.apiimpl.PlantRegistry;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 *
 * @author Ryan
 */
public final class GrassDropHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void interceptGrassDrop(BlockEvent.HarvestDropsEvent event) {
        
        // Skip silk touch.
        if (event.isSilkTouching()) {
            return;
        }
        
        // Fetch the blockstate.
        final IBlockState state = event.getState();
        
        // Skip Air or Error
        if (state == null || state.getBlock() == null) {
            return;
        }

        // Fetch the world random.
        final Random rand = event.getWorld().rand;
        
        // Log
        AgriCore.getLogger("AgriCraft").debug("Intercepted! Block: {0}", state.getBlock());

        // Add grass drops if grass block.
        if (TypeHelper.isAnyType(state.getBlock(), BlockGrass.class)) {
            // Wipe other drops, if needed.
            if (AgriCraftConfig.wipeGrassDrops) {
                event.getDrops().clear();
            }
            // Log
            AgriCore.getLogger("AgriCraft").debug("Inserting Drops!");
            // Add the drops.
            addGrassDrops(event.getDrops(), rand);
        }
    }

    public static void addGrassDrops(List<ItemStack> drops, Random rand) {
        PlantRegistry
                .getInstance()
                .getPlants()
                .stream()
                .forEach(p -> addGrassDrop(drops, rand, p));
    }

    public static void addGrassDrop(List<ItemStack> drops, Random rand, IAgriPlant plant) {
        if (plant.getGrassDropChance() > rand.nextDouble()) {
            drops.add(plant.getSeed());
        }
    }

}
