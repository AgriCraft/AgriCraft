package com.infinityraider.agricraft.compat.enderio;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.infinitylib.utility.WorldHelper;
import crazypants.enderio.api.farm.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

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

        double dropX = pos.getX() + 0.5;
        double dropY = pos.getY() + 0.5;
        double dropZ = pos.getZ() + 0.5;

        WorldHelper.getTile(farm.getWorld(), pos, IAgriCrop.class)
                .ifPresent(tile -> {
                    tile.onHarvest(stack -> {
                        result.getDrops().add(new EntityItem(farm.getWorld(), dropX, dropY, dropZ, stack));
                    }, farm.getFakePlayer());
                });

        farm.registerAction(FarmingAction.HARVEST, IFarmingTool.Tools.HOE);

        return result;
    }

    private static class HarvestResult implements IHarvestResult {

        final NonNullList<EntityItem> drops = NonNullList.create();
        final NonNullList<BlockPos> harvestedBlocks = NonNullList.create();

        public NonNullList<EntityItem> getDrops() {
            return drops;
        }

        public NonNullList<BlockPos> getHarvestedBlocks() {
            return harvestedBlocks;
        }
    }
}
