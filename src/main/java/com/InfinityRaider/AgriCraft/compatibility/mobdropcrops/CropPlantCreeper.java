package com.InfinityRaider.AgriCraft.compatibility.mobdropcrops;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlantStem;
import com.pam.mobdropcrops.mobdropcrops;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.ArrayList;

public class CropPlantCreeper extends CropPlantStem {
    public CropPlantCreeper() {
        super((ItemSeeds) mobdropcrops.creeperseedItem, mobdropcrops.pamcreeperPod);
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
        fruits.add(new ItemStack(Items.gunpowder));
        return fruits;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getPlantIcon(int growthStage) {
        //for the Vanilla SeedItem class the arguments for this method are not used
        return mobdropcrops.pamcreeperVine.getIcon(0, transformMeta(growthStage));
    }

    @SideOnly(Side.CLIENT)
    public IIcon getStemIcon() {
        Block plant = mobdropcrops.pamcreeperVine;
        if(plant instanceof BlockStem) {
            BlockStem stem = (BlockStem) plant;
            return stem.getStemIcon();
        }
        return this.getPlantIcon(7);
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
