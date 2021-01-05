package com.infinityraider.agricraft.content.core;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.init.AgriTabs;
import com.infinityraider.infinitylib.item.BlockItemBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemCropSticks extends BlockItemBase {
    private final CropStickVariant variant;

    public ItemCropSticks(CropStickVariant variant) {
        super(variant.getBlock(), new Properties().group(AgriTabs.TAB_AGRICRAFT));
        this.variant = variant;
    }

    public CropStickVariant getVariant() {
        return this.variant;
    }

    @Override
    public void onUse(@Nonnull World world, @Nonnull LivingEntity entity, @Nonnull ItemStack stack, int count) {
        super.onUse(world, entity, stack, count);
    }

    @Override
    @Nonnull
    public ActionResultType onItemUse(@Nonnull ItemUseContext context) {
        // Skip if remote.
        World world = context.getWorld();
        if (world.isRemote) {
            return ActionResultType.PASS;
        }

        // Calculate the target position.
        BlockPos pos = context.getPos();
        Direction side = context.getFace();
        final BlockPos cropPos = pos.offset(side);

        // Test if placement is valid.
        if (!world.isAirBlock(cropPos)) {
            return ActionResultType.FAIL;
        }

        // Test if the placement is valid
        BlockState newState = this.getVariant().getBlock().getStateForPlacement(new BlockItemUseContext(context));
        if(newState == null) {
            return ActionResultType.FAIL;
        }

        // Set the block to a crop.
        final boolean success = world.setBlockState(cropPos, newState);

        // If there was trouble, abort.
        if (!success) {
            AgriCore.getCoreLogger().debug("ItemCrop#onItemUse failed to create the BlockCrop!");
            return ActionResultType.FAIL;
        }

        PlayerEntity player = context.getPlayer();
        if(player != null) {
            ItemStack stack = player.getHeldItem(context.getHand());
            // Remove the crop used from the stack.
            if(!player.isCreative()) {
                stack.shrink(1);
            }
        }

        // Play placement sound.
        SoundType type = this.getVariant().getSound();
        world.playSound(null, cropPos.getX() + 0.5, cropPos.getY() + 0.5F, cropPos.getZ() + 0.5F, type.getPlaceSound(),
                SoundCategory.PLAYERS, (type.getVolume() + 1.0F) / 4.0F, type.getPitch() * 0.8F);

        // Action was a success.
        return ActionResultType.SUCCESS;
    }
}
