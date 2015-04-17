package com.InfinityRaider.AgriCraft.compatibility.mobdropcrops;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlantGeneric;
import com.pam.mobdropcrops.BlockPamMobCrop;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.ArrayList;

public class CropPlantMobDropCrop extends CropPlantGeneric {
    private final BlockPamMobCrop plant;
    public CropPlantMobDropCrop(ItemSeeds seed, Block plant) {
        super(seed);
        this.plant = (BlockPamMobCrop) plant;
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage;
    }

    @Override
    public int tier() {
        return 4;
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> fruits = new ArrayList<ItemStack>();
        fruits.add(new ItemStack(plant.getItemDropped(7, null, 0)));
        return fruits;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getPlantIcon(int growthStage) {
        //for the Vanilla SeedItem class the arguments for this method are not used
        return plant.getIcon(0, transformMeta(growthStage));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsFlower() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        String name = getSeed().getUnlocalizedName();
        int start = name.indexOf('.')+1;
        int stop = name.indexOf("seedItem");
        name = name.substring(start, stop);
        return "agricraft_journal.wf_"+Character.toUpperCase(name.charAt(0))+name.substring(1);
    }
}
