package com.InfinityRaider.AgriCraft.compatibility.applemilktea;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.AgriCraftPlantGeneric;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class CropPlantAppleMilkTea extends AgriCraftPlantGeneric {

    public CropPlantAppleMilkTea(ItemStack seed, Block plant, int tier, ItemStack...fruits) {
	super(seed, plant, tier, fruits);
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        int amount = (int) (Math.ceil((gain + 0.00) / 3));
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        while(amount>0) {
            list.add(getRandomFruit(rand));
            amount--;
        }
        return list;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getPlantIcon(int growthStage) {
        int meta = 1;
        if(growthStage==0 || growthStage==1) {
            meta = 0;
        } else if(growthStage==5 || growthStage==6) {
            meta = 2;
        } else if(growthStage==7) {
            meta = 3;
        }
        return plant.getIcon(0, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getInformation() {
        return "agricraft.journal_AMT."+seed.getUnlocalizedName()+":"+seed.getItemDamage();
    }
}
