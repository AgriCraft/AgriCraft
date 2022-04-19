package com.infinityraider.agricraft.content.core;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriCropStickItem;
import com.infinityraider.agricraft.content.AgriBlockRegistry;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.util.CropHelper;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemCropSticks extends ItemBase implements IAgriCropStickItem {
    private final CropStickVariant variant;

    public ItemCropSticks(CropStickVariant variant) {
        super(variant.getId(), new Properties().tab(AgriTabs.TAB_AGRICRAFT));
        this.variant = variant;
    }

    @Override
    public CropStickVariant getVariant() {
        return this.variant;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        // Fetch target world, pos, tile, and state
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        BlockState state = world.getBlockState(pos);

        // Place on existing crop block
        if(state.getBlock() == AgriApi.getAgriContent().getBlocks().getCropBlock()) {
            return this.placeCropSticksOnExisting(world, pos, state, context.getPlayer(), context.getHand(), stack);
        }
        // Fetch pos, tile, and state above
        pos = pos.relative(context.getClickedFace());
        state = world.getBlockState(pos);
        if (state.isAir() || state.getFluidState().getType() == Fluids.WATER) {
            // Place on soil
            return this.placeCropSticksOnSoil(world, pos, context.getPlayer(), context.getHand());
        } else if(state.getBlock() == AgriApi.getAgriContent().getBlocks().getCropBlock()) {
            // Place on existing crop block
            return this.placeCropSticksOnExisting(world, pos, state, context.getPlayer(), context.getHand(), stack);
        }
        return InteractionResult.FAIL;
    }

    protected InteractionResult placeCropSticksOnExisting(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, ItemStack stack) {
        BlockCrop.CropState cropState = BlockCrop.STATE.fetch(state);
        CropStickVariant stickType = BlockCrop.VARIANT.fetch(state);
        if(cropState.hasSticks()) {
            // If there are already crop sticks present, attempt making cross crops
            if(cropState.hasPlant()) {
                // Failure: can not create cross crop if a plant is already present
                return InteractionResult.FAIL;
            }
            if(stickType.isSameType(stack)) {
                // Set block state
                state = BlockCrop.STATE.apply(state, BlockCrop.CropState.DOUBLE_STICKS);
                world.setBlock(pos, state, 3);
                // Remove the crop stick used from the stack.
                this.consumeItem(player, hand);
                // Play placement sound.
                stickType.playCropStickSound(world, pos);
                // Action was a success.
                return InteractionResult.SUCCESS;
            } else {
                // Failure: can not create cross crop of different types
                return InteractionResult.FAIL;
            }
        } else {
            // There are not yet crop sticks present, therefore, apply them
            state = BlockCrop.VARIANT.apply(state, CropStickVariant.fromItem(stack));
            world.setBlock(pos, state, 3);
            // Remove the crop stick used from the stack.
            this.consumeItem(player, hand);
            // Play placement sound.
            CropHelper.playPlantingSound(world, pos, player);
            // Action was a success.
            return InteractionResult.SUCCESS;
        }
    }

    protected InteractionResult placeCropSticksOnSoil(Level world, BlockPos pos, Player player, InteractionHand hand) {
        // Test if the placement is valid
        BlockCrop block = AgriBlockRegistry.getInstance().getCropBlock();
        BlockState newState = block.adaptStateForPlacement(block.blockStateCropSticks(this.getVariant()), world, pos);
        if(newState == null) {
            return InteractionResult.FAIL;
        }

        // Set the block to a crop.
        final boolean success = world.setBlock(pos, newState, 3);

        // If there was trouble, abort.
        if (!success) {
            AgriCore.getCoreLogger().debug("ItemCrop#onItemUse failed to create the BlockCrop!");
            return InteractionResult.FAIL;
        }

        // Remove the crop used from the stack.
        this.consumeItem(player, hand);

        // Play placement sound.
        CropHelper.playPlantingSound(world, pos, player);

        // Action was a success.
        return InteractionResult.SUCCESS;
    }

    protected void consumeItem(@Nullable Player player, InteractionHand hand) {
        if(player != null) {
            ItemStack stack = player.getItemInHand(hand);
            if(!player.isCreative()) {
                stack.shrink(1);
            }
        }

    }
}
