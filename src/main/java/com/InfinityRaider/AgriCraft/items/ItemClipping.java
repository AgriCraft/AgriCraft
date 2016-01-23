package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.api.v2.ISeedStats;
import com.InfinityRaider.AgriCraft.farming.PlantStats;
import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.items.RenderItemBase;
import com.InfinityRaider.AgriCraft.renderers.items.RenderItemClipping;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemClipping extends ItemAgricraft {
    public ItemClipping() {
        super();
        this.setCreativeTab(null);
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.clipping;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderItemBase getItemRenderer() {
        return new RenderItemClipping(this);
    }

    @Override
    public boolean canItemEditBlocks() {return true;}

    //this is called when you right click with this item in hand
    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if(world.isRemote) {
            return false;
        }
        if(stack==null || stack.getItem()==null || stack.stackTagCompound==null) {
            return false;
        }
        Block block = world.getBlock(x, y, z);
        if(!(block instanceof BlockCrop)) {
            return false;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if(te == null || !(te instanceof TileEntityCrop)) {
            return true;
        }
        TileEntityCrop crop = (TileEntityCrop) te;
        BlockCrop blockCrop = (BlockCrop) block;
        if(crop.isCrossCrop()) {
            blockCrop.harvest(world, x, y, z, player, crop);
        }
        if(!crop.canPlant()) {
            return true;
        }
        ItemStack seed = ItemStack.loadItemStackFromNBT(stack.getTagCompound());
        ISeedStats stats = PlantStats.getStatsFromStack(seed);
        if(stats == null) {
            return false;
        }
        if(world.rand.nextInt(10)<=stats.getStrength()) {
            blockCrop.plantSeed(seed, world, x, y, z);
        }
        stack.stackSize = stack.stackSize-1;
        return true;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String text = StatCollector.translateToLocal("item.agricraft:clipping.name");
        if(stack==null || stack.stackTagCompound==null) {
            return text;
        }
        ItemStack seed = ItemStack.loadItemStackFromNBT(stack.stackTagCompound);
        if(seed==null || seed.getItem()==null || !CropPlantHandler.isValidSeed(seed)) {
            return text;
        }
        CropPlant plant = CropPlantHandler.getPlantFromStack(seed);
        if(plant == null) {
            return text;
        }
        ItemStack fruit = plant.getAllFruits().get(0);
        return fruit.getDisplayName() + " " + text;
    }
}
