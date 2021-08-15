package com.infinityraider.agricraft.compat.enderio;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.infinitylib.utility.WorldHelper;
import crazypants.enderio.api.farm.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;

public class AgriFarmerJoe extends AbstractFarmerJoe {

    public boolean canPlant(ItemStack stack) {
        return false;
    }

    public IFarmerJoe.Result tryPrepareBlock(IFarmer farm, BlockPos pos, IBlockState state) {
        return IFarmerJoe.Result.NEXT;
    }

    public boolean canHarvest(IFarmer farm, BlockPos pos, IBlockState state) {
        return WorldHelper.getTile(farm.getWorld(), pos, IAgriCrop.class)
                .map(IAgriCrop::canBeHarvested)
                .orElse(false);
    }

    public IHarvestResult harvestBlock(IFarmer farm, BlockPos pos, IBlockState state) {
        if (!canHarvest(farm, pos, state) || !farm.checkAction(FarmingAction.HARVEST, IFarmingTool.Tools.HOE)) {
            return null;
        }

        HarvestResult result = new HarvestResult();
        result.getHarvestedBlocks().add(pos);

        WorldHelper.getTile(farm.getWorld(), pos, IAgriCrop.class)
                .ifPresent(tile -> {
                    tile.onHarvest(stack -> {
                        result.addDrop(pos, stack);
                    }, farm.getFakePlayer());
                });

        farm.registerAction(FarmingAction.HARVEST, IFarmingTool.Tools.HOE);

        return result;
    }
    
    private static class HarvestResult implements IHarvestResult {
        private final NonNullList<Pair<BlockPos, ItemStack>> drops = NonNullList.create();
        private final NonNullList<BlockPos> harvestedBlocks = NonNullList.create();
        
        @SuppressWarnings("unchecked")
        @Override
        public NonNullList<Pair<BlockPos, ItemStack>> getDrops() {
            return drops;
        }

        @Override
        public void addDrop(BlockPos pos, ItemStack stack) {
            drops.add(Pair.of(pos, stack));
        }

        @Override
        public NonNullList<BlockPos> getHarvestedBlocks() {
            return harvestedBlocks;
        }
    }
}
