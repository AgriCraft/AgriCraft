/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.infinityraider.agricraft.compat.actuallyadditions;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.util.MethodResult;
import com.infinityraider.infinitylib.utility.WorldHelper;
import de.ellpeck.actuallyadditions.api.farmer.FarmerResult;
import de.ellpeck.actuallyadditions.api.farmer.IFarmerBehavior;
import de.ellpeck.actuallyadditions.api.internal.IFarmer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Ryan
 */
public class AgriCraftFarmerBehavior implements IFarmerBehavior {

    @Override
    public FarmerResult tryPlantSeed(ItemStack seed, World world, BlockPos pos, IFarmer farmer) {
        // Attempt to resolve seed and crops instance.
        final Optional<AgriSeed> agriSeed = AgriApi.getSeedRegistry().valueOf(seed);
        final Optional<IAgriCrop> agriCrop = WorldHelper.getTile(world, pos, IAgriCrop.class);
        // If both are there, attempt to plant.
        if (agriSeed.isPresent() && agriCrop.isPresent()) {
            // Ensure enough energy.
            if (farmer.getEnergy() < ActuallyAdditionsPlugin.ENERGY_COST) {
                return FarmerResult.STOP_PROCESSING;
            }
            final MethodResult result = agriCrop.get().onApplySeeds(agriSeed.get(), null);
            if (result == MethodResult.SUCCESS) {
                farmer.extractEnergy(250);
                return FarmerResult.SUCCESS;
            }
        }
        // Otherwise fail.
        return FarmerResult.FAIL;
    }

    @Override
    public FarmerResult tryHarvestPlant(World world, BlockPos pos, IFarmer farmer) {
        // Attempt to resolve crops instance.
        final Optional<IAgriCrop> agriCrop = WorldHelper.getTile(world, pos, IAgriCrop.class);
        // If crops are there, do the thing.
        if (agriCrop.isPresent()) {
            // Ensure enough energy.
            if (farmer.getEnergy() < ActuallyAdditionsPlugin.ENERGY_COST) {
                return FarmerResult.STOP_PROCESSING;
            }
            final List<ItemStack> products = new ArrayList<>();
            final MethodResult result = agriCrop.get().onHarvest(products::add, null);
            if (result == MethodResult.SUCCESS) {
                farmer.extractEnergy(ActuallyAdditionsPlugin.ENERGY_COST);
                farmer.addToOutput(products);
                return FarmerResult.SUCCESS;
            }
        }
        // Otherwise fail.
        return FarmerResult.FAIL;
    }

    @Override
    public int getPriority() {
        // Randomly chosen, as I don't know how this works...
        return 10;
    }

}
