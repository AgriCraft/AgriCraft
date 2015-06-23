package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockWaterPad extends Block {
    public BlockWaterPad() {
        super(Material.ground);
        this.setStepSound(soundTypeGravel);
        this.maxY = Constants.unit*8;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fX, float fY, float fZ) {
        ItemStack stack = player.getCurrentEquippedItem();
        if(stack == null || stack.getItem() == null) {
            return false;
        }
        if(stack.getItem() == Items.water_bucket) {
            if(world.getBlockMetadata(x, y, z) == 0) {
                if(!world.isRemote) {
                    world.setBlockMetadataWithNotify(x, y, z, 1, 3);
                }
                if(!player.capabilities.isCreativeMode) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    player.inventory.addItemStackToInventory(new ItemStack(Items.bucket));
                }
            }
            return true;
        }
        else if(stack.getItem() == Items.bucket) {
            if(world.getBlockMetadata(x, y, z) == 1) {
                if(!world.isRemote) {
                    world.setBlockMetadataWithNotify(x, y, z, 0, 3);
                }
                if(!player.capabilities.isCreativeMode) {
                    int newSize = stack.stackSize -1;
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, newSize == 0 ? null : new ItemStack(Items.bucket, newSize));
                    player.inventory.addItemStackToInventory(new ItemStack(Items.water_bucket));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float f, int i) {
        if(!world.isRemote) {
            ItemStack drop = new ItemStack(Blocks.dirt, 1);
            this.dropBlockAsItem(world, x, y, z, drop);
        }
    }

    //creative item picking
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        return new ItemStack(Blocks.dirt);
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }

    //render methods
    //--------------
    @Override
    public int getRenderType() {return AgriCraft.proxy.getRenderId(Constants.waterPadId);}                 //get the correct renderId

    @Override
    public boolean isOpaqueCube() {return false;}           //tells minecraft that this is not a block (no levers can be placed on it, it's transparent, ...)

    @Override
    public boolean renderAsNormalBlock() {return false;}    //tells minecraft that this has custom rendering

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int i) {return true;}

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.dirt.getIcon(side, meta);
    }

    //register icons
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        //NOOP
    }

    public static class ItemBlock extends net.minecraft.item.ItemBlock {
        public ItemBlock(Block block) {
            super(block);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
            list.add(StatCollector.translateToLocal("agricraft_tooltip.waterPadDry"));
            if(stack.getItemDamage()==1) {
                list.add(StatCollector.translateToLocal("agricraft_tooltip.waterPadWet"));
            }
        }
    }


}
