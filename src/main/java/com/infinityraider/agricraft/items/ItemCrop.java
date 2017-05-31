package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.init.AgriBlocks;
import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import com.infinityraider.infinitylib.item.IItemWithModel;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.utility.IRecipeRegister;
import com.infinityraider.infinitylib.utility.WorldHelper;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ItemCrop extends ItemBase implements IItemWithModel, IRecipeRegister {

    public ItemCrop() {
        super("crop_sticks");
        this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
    }

    //I'm overriding this just to be sure
    @Override
    public boolean canItemEditBlocks() {
        return true;
    }

    // This is called when you right click with this item in hand.
    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        // Skip if remote, or clicked wrong face of block.
        if (world.isRemote || side != EnumFacing.UP) {
            return EnumActionResult.PASS;
        }

        // Fetch information.
        final BlockPos cropPos = pos.up();

        // Test if placement is valid.
        if (!world.isAirBlock(cropPos)) {
            return EnumActionResult.FAIL;
        }
        
        // Test if soil is valid.
        if (!AgriApi.getSoilRegistry().contains(world.getBlockState(pos))) {
            return EnumActionResult.FAIL;
        }

        // Set the block to a crop.
        world.setBlockState(pos.up(), AgriBlocks.getInstance().CROP.getDefaultState());

        // Remove the crop used from the stack.
        stack.stackSize = player.capabilities.isCreativeMode ? stack.stackSize : stack.stackSize - 1;

        // Handle sneak placing of crosscrops.
        if (player.isSneaking() && stack.stackSize > 0) {
            WorldHelper
                    .getTile(world, pos.add(0, 1, 0), TileEntityCrop.class)
                    .ifPresent(c -> {
                        c.setCrossCrop(true);
                        stack.stackSize = player.capabilities.isCreativeMode ? stack.stackSize : stack.stackSize - 1;
                    });
        }

        // Play placement sound.
        SoundType type = Blocks.LEAVES.getSoundType();
        world.playSound(null, (double) ((float) cropPos.getX() + 0.5F), (double) ((float) cropPos.getY() + 0.5F), (double) ((float) cropPos.getZ() + 0.5F), type.getPlaceSound(), SoundCategory.PLAYERS, (type.getVolume() + 1.0F) / 4.0F, type.getPitch() * 0.8F);

        // Action was a success.
        return EnumActionResult.SUCCESS;
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this, AgriCraftConfig.cropsPerCraft), "ss", "ss", 's', "stickWood"));
    }

}
