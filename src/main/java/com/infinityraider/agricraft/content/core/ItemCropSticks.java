package com.infinityraider.agricraft.content.core;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.init.AgriBlockRegistry;
import com.infinityraider.agricraft.init.AgriTabs;
import com.infinityraider.infinitylib.item.BlockItemBase;
import com.infinityraider.infinitylib.utility.WorldHelper;
import net.minecraft.block.SoundType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCropSticks extends BlockItemBase {

    public ItemCropSticks(CropStickVariant variant) {
        super(variant.getBlock(), new Properties().group(AgriTabs.TAB_AGRICRAFT));
    }

    //I'm overriding this just to be sure
    @Override
    public boolean canItemEditBlocks() {
        return true;
    }

    // This is called when you right click with this item in hand.
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        // Skip if remote.
        if (world.isRemote) {
            return EnumActionResult.PASS;
        }

        // Calculate the target position.
        final BlockPos cropPos = pos.offset(side);

        // Test if placement is valid.
        if (!world.isAirBlock(cropPos)) {
            return EnumActionResult.FAIL;
        }

        // Test if soil is valid.
        if (!AgriApi.getSoilRegistry().contains(world.getBlockState(cropPos.down()))) {
            return EnumActionResult.FAIL;
        }

        // Set the block to a crop.
        final Boolean success = world.setBlockState(cropPos, AgriBlockRegistry.getInstance().crop_sticks.getDefaultState());

        // If there was trouble, abort.
        if (!success) {
            AgriCore.getCoreLogger().error("ItemCrop#onItemUse failed to create the BlockCrop!");
            return EnumActionResult.FAIL;
        }

        ItemStack stack = player.getHeldItem(hand);

        // Remove the crop used from the stack.
        StackHelper.decreaseStackSize(player, stack, 1);

        // Handle sneak placing of crosscrops.
        if (player.isSneaking() && stack.getCount() > 0) {
            WorldHelper
                    .getTile(world, cropPos, TileEntityCropSticks.class)
                    .ifPresent(c -> {
                        c.setCrossCrop(true);
                        StackHelper.decreaseStackSize(player, stack, 1);
                    });
        }

        // Play placement sound.
        SoundType type = Blocks.LEAVES.getSoundType();
        world.playSound(null, (double) ((float) cropPos.getX() + 0.5F), (double) ((float) cropPos.getY() + 0.5F), (double) ((float) cropPos.getZ() + 0.5F), type.getPlaceSound(), SoundCategory.PLAYERS, (type.getVolume() + 1.0F) / 4.0F, type.getPitch() * 0.8F);

        // Action was a success.
        return EnumActionResult.SUCCESS;
    }

}
