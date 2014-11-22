package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityTank;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemBlockChannel extends ItemBlock {
    public ItemBlockChannel(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        NBTTagCompound tag = stack.stackTagCompound;
        LogHelper.debug("Setting water tank: ");
        if(tag.hasKey(Names.material) && tag.hasKey(Names.materialMeta)) {
            LogHelper.debug("Material: " + tag.getString(Names.material) + ":" + tag.getInteger(Names.materialMeta));
        }
        else {
            LogHelper.debug("Material: " + Block.blockRegistry.getNameForObject(Blocks.planks) + ":0");
        }
        if (!world.setBlock(x, y, z, field_150939_a, metadata, 3)) {
            return false;
        }
        if (world.getBlock(x, y, z) == field_150939_a) {
            field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
            field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
            LogHelper.debug("Looking for TileEntity");
            if(world.getTileEntity(x, y, z)!=null && world.getTileEntity(x, y, z) instanceof TileEntityTank) {
                TileEntityTank tank = (TileEntityTank) world.getTileEntity(x, y, z);
                tank.setMaterial(tag);
            }
        }
        return true;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName()+"."+stack.getItemDamage();
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }
}