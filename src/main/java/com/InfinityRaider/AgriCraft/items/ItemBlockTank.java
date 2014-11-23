package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.blocks.BlockWaterTank;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityTank;
import com.InfinityRaider.AgriCraft.utility.NBTHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class ItemBlockTank extends ItemBlock {
    public ItemBlockTank(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        NBTTagCompound tag = stack.stackTagCompound;
        if (!world.setBlock(x, y, z, field_150939_a, metadata, 3)) {
            return false;
        }
        if (world.getBlock(x, y, z) == field_150939_a) {
            field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
            field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
            if(world.getTileEntity(x, y, z)!=null && world.getTileEntity(x, y, z) instanceof TileEntityTank) {
                TileEntityTank tank = (TileEntityTank) world.getTileEntity(x, y, z);
                tank.setMaterial(tag);
            }
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, java.util.List list) {
        list.clear();
        ArrayList<ItemStack> registeredMaterials = new ArrayList<ItemStack>();
        for(ItemStack stack:OreDictionary.getOres("plankWood")) {
            if(stack.getItemDamage()==OreDictionary.WILDCARD_VALUE) {
                ArrayList<ItemStack> subItems = new ArrayList<ItemStack>();
                stack.getItem().getSubItems(stack.getItem(), null, subItems);
                for(ItemStack subItem:subItems) {
                    this.addMaterialToList(subItem, list, 0, registeredMaterials);
                }
            }
            else {
                this.addMaterialToList(stack, list, 0, registeredMaterials);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private boolean hasMaterial(ArrayList<ItemStack> registeredMaterials, ItemStack material) {
        for(ItemStack stack:registeredMaterials) {
            if(material.getItem()==stack.getItem() && material.getItemDamage()==stack.getItemDamage()) {
                return true;
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    private void addMaterialToList(ItemStack stack, List list, int tankMeta, ArrayList<ItemStack> registeredMaterials) {
        if(!this.hasMaterial(registeredMaterials, stack)) {
            ItemStack waterTank = new ItemStack(Blocks.blockWaterTank, 1, tankMeta);
            NBTTagCompound tag = NBTHelper.getMaterialTag(stack);
            if (tag != null) {
                waterTank.setTagCompound(tag);
            }
            list.add(waterTank);
            registeredMaterials.add(stack);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName()+"."+stack.getItemDamage();
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
        if(stack.getItemDamage()==0 && stack.hasTagCompound() && stack.getTagCompound().hasKey(Names.material) && stack.getTagCompound().hasKey(Names.materialMeta)) {
            NBTTagCompound tag = stack.getTagCompound();
            String name = tag.getString(Names.material);
            int meta = tag.getInteger(Names.materialMeta);
            ItemStack material = new ItemStack((Block) Block.blockRegistry.getObject(name), 1, meta);
            list.add("Material: "+ material.getItem().getItemStackDisplayName(material));
        }
    }
}
