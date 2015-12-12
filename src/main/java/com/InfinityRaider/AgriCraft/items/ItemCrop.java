package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.items.RenderItemBase;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemCrop extends ItemAgricraft {
    @Override
    protected String getInternalName() {
        return Names.Objects.crops+"Item";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderItemBase getItemRenderer() {
        return null;
    }

    //I'm overriding this just to be sure
    @Override
    public boolean canItemEditBlocks() {return true;}

    //this is called when you right click with this item in hand
    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (GrowthRequirementHandler.isSoilValid(world, x, y, z) && world.getBlock(x, y + 1, z).getMaterial()== Material.air && side == 1) {
                world.setBlock(x, y + 1, z, Blocks.blockCrop);
                int use = 1;
                if(player.isSneaking() && (player.capabilities.isCreativeMode || stack.stackSize>=2)) {
                    TileEntity tile = world.getTileEntity(x, y+1, z);
                    if(tile != null && (tile instanceof TileEntityCrop)) {
                        ((TileEntityCrop) tile).setCrossCrop(true);
                        use = 2;
                    }
                }
                world.playSoundEffect((double)((float) x + 0.5F), (double)((float) y + 0.5F), (double)((float) z + 0.5F), net.minecraft.init.Blocks.leaves.stepSound.func_150496_b(), (net.minecraft.init.Blocks.leaves.stepSound.getVolume() + 1.0F) / 2.0F, net.minecraft.init.Blocks.leaves.stepSound.getPitch() * 0.8F);
                stack.stackSize = player.capabilities.isCreativeMode ? stack.stackSize : stack.stackSize - use;
                return false;
            }
        }
        return false;   //return false or else no other use methods will be called (for instance "onBlockActivated" on the crops block)
    }
}
