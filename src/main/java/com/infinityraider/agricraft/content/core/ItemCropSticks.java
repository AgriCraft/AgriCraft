package com.infinityraider.agricraft.content.core;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.util.CropHelper;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.item.BlockItemBase;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemCropSticks extends BlockItemBase {
    private final CropStickVariant variant;

    public ItemCropSticks(CropStickVariant variant) {
        super(variant.getBlock(), new Properties().tab(AgriTabs.TAB_AGRICRAFT));
        this.variant = variant;
    }

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
        BlockEntity tile = world.getBlockEntity(pos);
        BlockState state = world.getBlockState(pos);

        // Try placing on same block
        if(tile instanceof TileEntityCropPlant) {
            // Place on existing plant
            return this.placeCropSticksOnPlant(world, pos, context.getPlayer(), context.getHand(), (TileEntityCropPlant) tile, state);
        } else if(tile instanceof TileEntityCropSticks) {
            // Make cross crop
            return this.tryMakeCrossCrop(world, pos, context.getPlayer(), context.getHand(), (TileEntityCropSticks) tile);
        }

        // Fetch pos, tile, and state above
        pos = pos.relative(context.getClickedFace());
        tile = world.getBlockEntity(pos);
        state = world.getBlockState(pos);
        if (state.isAir() || state.getFluidState().getType() == Fluids.WATER) {
            // Place on soil
            return this.placeCropSticksOnSoil(world, pos, new BlockPlaceContext(context));
        } else if(tile instanceof TileEntityCropPlant) {
            // Place on existing plant
            return this.placeCropSticksOnPlant(world, pos, context.getPlayer(), context.getHand(), (TileEntityCropPlant) tile, state);
        } else if(tile instanceof TileEntityCropSticks) {
            // Make cross crop
            return this.tryMakeCrossCrop(world, pos, context.getPlayer(), context.getHand(), (TileEntityCropSticks) tile);
        }
        return InteractionResult.FAIL;
    }

    protected InteractionResult placeCropSticksOnSoil(Level world, BlockPos pos, BlockPlaceContext context) {
        // Test if the placement is valid
        BlockState newState = this.getVariant().getBlock().getStateForPlacement(context);
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
        this.consumeItem(context.getPlayer(), context.getHand());

        // Play placement sound.
        CropHelper.playPlantingSound(world, pos, context.getPlayer());

        // Action was a success.
        return InteractionResult.SUCCESS;
    }

    protected InteractionResult placeCropSticksOnPlant(Level world, BlockPos pos, @Nullable Player player, InteractionHand hand,
                                                       TileEntityCropPlant plant, BlockState state) {
        // Attempt to convert to crop sticks
        BlockState newState = this.getVariant().getBlock().defaultBlockState();
        newState = BlockCropBase.PLANT.mimic(state, newState);
        newState = BlockCropBase.LIGHT.mimic(state, newState);
        newState = InfProperty.Defaults.fluidlogged().mimic(state, newState);
        world.setBlock(pos, newState, 3);

        // If there was trouble, reset and abort.
        BlockEntity tile = world.getBlockEntity(pos);
        if(!(tile instanceof TileEntityCropSticks)) {
            world.setBlock(pos, state, 3);
            return InteractionResult.FAIL;
        }

        // Mimic plant and weed
        ((TileEntityCropSticks) tile).mimicFrom(plant);

        // Remove the crop used from the stack
        this.consumeItem(player, hand);

        // Play placement sound.
        CropHelper.playPlantingSound(world, pos, player);

        // Action was a success.
        return InteractionResult.SUCCESS;
    }

    protected InteractionResult tryMakeCrossCrop(Level world, BlockPos pos,  @Nullable Player player, InteractionHand hand,
                                                TileEntityCropSticks crop) {
        if(!crop.hasPlant() && !crop.hasWeeds() & !crop.isCrossCrop() && ((BlockCropSticks) crop.getBlockState().getBlock()).isVariant(this)) {
            crop.setCrossCrop(true);
            this.consumeItem(player, hand);
            this.playCropStickSound(world, pos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    protected void consumeItem(@Nullable Player player, InteractionHand hand) {
        if(player != null) {
            ItemStack stack = player.getItemInHand(hand);
            if(!player.isCreative()) {
                stack.shrink(1);
            }
        }

    }

    protected void playCropStickSound(Level world, BlockPos pos) {
        SoundType type = this.getVariant().getSound();
        world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5F, pos.getZ() + 0.5F, type.getPlaceSound(),
                SoundSource.BLOCKS, (type.getVolume() + 1.0F) / 4.0F, type.getPitch() * 0.8F);
    }
}
