package com.infinityraider.agricraft.content.core;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.item.BlockItemBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onItemUse(@Nonnull ItemUseContext context) {
        // Skip if remote.
        World world = context.getWorld();
        if (world.isRemote) {
            return ActionResultType.PASS;
        }

        // Fetch target pos, tile, and state
        BlockPos pos = context.getPos();
        TileEntity tile = world.getTileEntity(pos);
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
        pos = pos.offset(context.getFace());
        tile = world.getTileEntity(pos);
        state = world.getBlockState(pos);
        if (state.isAir(world, pos) || state.getFluidState().getFluid() == Fluids.WATER) {
            // Place on soil
            return this.placeCropSticksOnSoil(world, pos, new BlockItemUseContext(context));
        } else if(tile instanceof TileEntityCropPlant) {
            // Place on existing plant
            return this.placeCropSticksOnPlant(world, pos, context.getPlayer(), context.getHand(), (TileEntityCropPlant) tile, state);
        } else if(tile instanceof TileEntityCropSticks) {
            // Make cross crop
            return this.tryMakeCrossCrop(world, pos, context.getPlayer(), context.getHand(), (TileEntityCropSticks) tile);
        }
        return ActionResultType.FAIL;
    }

    protected ActionResultType placeCropSticksOnSoil(World world, BlockPos pos, BlockItemUseContext context) {
        // Test if the placement is valid
        BlockState newState = this.getVariant().getBlock().getStateForPlacement(context);
        if(newState == null) {
            return ActionResultType.FAIL;
        }

        // Set the block to a crop.
        final boolean success = world.setBlockState(pos, newState);

        // If there was trouble, abort.
        if (!success) {
            AgriCore.getCoreLogger().debug("ItemCrop#onItemUse failed to create the BlockCrop!");
            return ActionResultType.FAIL;
        }

        // Remove the crop used from the stack.
        this.consumeItem(context.getPlayer(), context.getHand());

        // Play placement sound.
        this.playPlacementSound(world, pos);

        // Action was a success.
        return ActionResultType.SUCCESS;
    }

    protected ActionResultType placeCropSticksOnPlant(World world, BlockPos pos, @Nullable PlayerEntity player, Hand hand,
                                                      TileEntityCropPlant plant, BlockState state) {
        // Attempt to convert to crop sticks
        BlockState newState = this.getVariant().getBlock().getDefaultState();
        newState = BlockCropBase.PLANT.mimic(state, newState);
        newState = BlockCropBase.LIGHT.mimic(state, newState);
        newState = InfProperty.Defaults.waterlogged().mimic(state, newState);
        world.setBlockState(pos, newState);

        // If there was trouble, reset and abort.
        TileEntity tile = world.getTileEntity(pos);
        if(!(tile instanceof TileEntityCropSticks)) {
            world.setBlockState(pos, state);
            return ActionResultType.FAIL;
        }

        // Mimic plant and weed
        ((TileEntityCropSticks) tile).mimicFrom(plant);

        // Remove the crop used from the stack
        this.consumeItem(player, hand);

        // Play placement sound.
        this.playPlacementSound(world, pos);

        // Action was a success.
        return ActionResultType.SUCCESS;
    }

    protected ActionResultType tryMakeCrossCrop(World world, BlockPos pos,  @Nullable PlayerEntity player, Hand hand,
                                                TileEntityCropSticks crop) {
        if(!crop.hasPlant() && !crop.hasWeeds() & !crop.isCrossCrop() && ((BlockCropSticks) crop.getBlockState().getBlock()).isVariant(this)) {
            crop.setCrossCrop(true);
            this.consumeItem(player, hand);
            this.playPlacementSound(world, pos);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    protected void consumeItem(@Nullable PlayerEntity player, Hand hand) {
        if(player != null) {
            ItemStack stack = player.getHeldItem(hand);
            if(!player.isCreative()) {
                stack.shrink(1);
            }
        }

    }

    protected void playPlacementSound(World world, BlockPos pos) {
        SoundType type = this.getVariant().getSound();
        world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5F, pos.getZ() + 0.5F, type.getPlaceSound(),
                SoundCategory.PLAYERS, (type.getVolume() + 1.0F) / 4.0F, type.getPitch() * 0.8F);
    }
}
