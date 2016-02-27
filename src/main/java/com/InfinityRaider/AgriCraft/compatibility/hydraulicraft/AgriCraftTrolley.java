package com.InfinityRaider.AgriCraft.compatibility.hydraulicraft;

import com.InfinityRaider.AgriCraft.api.v3.ICrop;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlantTall;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import cpw.mods.fml.common.Optional;
import k4unl.minecraft.Hydraulicraft.api.IHarvesterCustomHarvestAction;
import k4unl.minecraft.Hydraulicraft.api.IHarvesterCustomPlantAction;
import k4unl.minecraft.Hydraulicraft.api.IHarvesterTrolley;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;
@Optional.InterfaceList(value = {
        @Optional.Interface(modid = Names.Mods.hydraulicraft, iface = "k4unl.minecraft.Hydraulicraft.api.IHarvesterTrolley"),
        @Optional.Interface(modid = Names.Mods.hydraulicraft, iface = "k4unl.minecraft.Hydraulicraft.api.IHarvesterCustomPlantAction"),
        @Optional.Interface(modid = Names.Mods.hydraulicraft, iface = "k4unl.minecraft.Hydraulicraft.api.IHarvesterCustomHarvestAction")
})
public class AgriCraftTrolley implements IHarvesterTrolley, IHarvesterCustomHarvestAction, IHarvesterCustomPlantAction {
    private static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/misc/AgriCraftTrolley.png");
    @Override
    public String getName() {
        return "AgriTrolley";
    }

    @Override
    public boolean canHarvest(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        return te != null && te instanceof ICrop && ((ICrop) te).isMature();
    }

    @Override
    public boolean canPlant(World world, int x, int y, int z, ItemStack seed) {
        if(!CropPlantHandler.isValidSeed(seed)) {
            return false;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if(te == null || !(te instanceof ICrop) ) {
            return false;
        }
        ICrop crop = (ICrop) te;
        return !crop.hasPlant() && !crop.hasWeed() && !crop.isCrossCrop();
    }

    @Override
    public ArrayList<ItemStack> getHandlingSeeds() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for(CropPlant plant : CropPlantHandler.getPlants()) {
            list.add(plant.getSeed());
        }
        return list;
    }

    @Override
    public Block getBlockForSeed(ItemStack seed) {
        CropPlant plant = CropPlantHandler.getPlantFromStack(seed);
        return plant == null ? null : plant.getBlock();
    }

    @Override
    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public int getPlantHeight(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te == null || !(te instanceof ICrop) ) {
            return 1;
        }
        ICrop crop = (ICrop) te;
        if(!crop.hasPlant()) {
            return 1;
        }
        return crop.getPlant() instanceof CropPlantTall ? 2 : 1;
    }

    @Override
    public ArrayList<ItemStack> doHarvest(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te == null || !(te instanceof TileEntityCrop) ) {
            return new ArrayList<ItemStack>();
        }
        TileEntityCrop crop = (TileEntityCrop) te;
        crop.getWorldObj().setBlockMetadataWithNotify(crop.xCoord, crop.yCoord, crop.zCoord, 2, 2);
        return crop.getFruits();
    }

    @Override
    public void doPlant(World world, int x, int y, int z, ItemStack seed) {
        if(!CropPlantHandler.isValidSeed(seed)) {
            return;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if(te == null || !(te instanceof TileEntityCrop) ) {
            return;
        }
        TileEntityCrop crop = (TileEntityCrop) te;
        if(crop.hasPlant() || crop.hasWeed() || crop.isCrossCrop()) {
            return;
        }
        if (seed.stackTagCompound != null && seed.stackTagCompound.hasKey(Names.NBT.growth)) {
            //NBT data was found: copy data to plant
            crop.setPlant(seed.stackTagCompound.getInteger(Names.NBT.growth), seed.stackTagCompound.getInteger(Names.NBT.gain), seed.stackTagCompound.getInteger(Names.NBT.strength), seed.stackTagCompound.getBoolean(Names.NBT.analyzed), seed.getItem(), seed.getItemDamage());
        } else {
            //NBT data was not initialized: set defaults
            crop.setPlant(Constants.DEFAULT_GROWTH, Constants.DEFAULT_GAIN, Constants.DEFAULT_STRENGTH, false, seed.getItem(), seed.getItemDamage());
        }
    }
}
