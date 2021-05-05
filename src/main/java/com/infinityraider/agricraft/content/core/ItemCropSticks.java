package com.infinityraider.agricraft.content.core;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.item.BlockItemBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
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

        // Try placing on same block
        if(this.canPlaceCropSticksOnPlant(world, pos)) {
            // On plant
            return this.placeCropSticksOnPlant(world, pos, context.getPlayer(), context.getHand());
        }

        // Try placing on block above
        final BlockPos cropPos = pos.offset(side);
        if (world.isAirBlock(cropPos)) {
            // On soil
            return this.placeCropSticksOnSoil(world, cropPos, new BlockItemUseContext(context));
        } else if(this.canPlaceCropSticksOnPlant(world, cropPos)) {
            // On plant
            return this.placeCropSticksOnPlant(world, cropPos, context.getPlayer(), context.getHand());
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
        PlayerEntity player = context.getPlayer();
        if(player != null) {
            ItemStack stack = player.getHeldItem(context.getHand());
            if(!player.isCreative()) {
                stack.shrink(1);
            }
        }

        // Play placement sound.
        this.playPlacementSound(world, pos);

        // Action was a success.
        return ActionResultType.SUCCESS;

    }

    protected boolean canPlaceCropSticksOnPlant(World world, BlockPos pos) {
        // Check the block state
        BlockState state = world.getBlockState(pos);
        TileEntity tile = world.getTileEntity(pos);
        return (state.getBlock() instanceof BlockCropPlant) && (tile instanceof TileEntityCropPlant);
    }

    protected ActionResultType placeCropSticksOnPlant(World world, BlockPos pos, PlayerEntity player, Hand hand) {
        // Fetch state and tile
        TileEntityCropPlant plant = (TileEntityCropPlant) world.getTileEntity(pos);
        BlockState state = world.getBlockState(pos);

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

        // Remove the crop used from the stack.
        if(player != null) {
            ItemStack stack = player.getHeldItem(hand);
            if(!player.isCreative()) {
                stack.shrink(1);
            }
        }

        // Play placement sound.
        this.playPlacementSound(world, pos);

        // Action was a success.
        return ActionResultType.SUCCESS;
    }

    protected void playPlacementSound(World world, BlockPos pos) {
        SoundType type = this.getVariant().getSound();
        world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5F, pos.getZ() + 0.5F, type.getPlaceSound(),
                SoundCategory.PLAYERS, (type.getVolume() + 1.0F) / 4.0F, type.getPitch() * 0.8F);
    }
}
