package com.InfinityRaider.AgriCraft.compatibility.bluepower;

import net.minecraft.item.ItemSeeds;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlantTallGeneric;
import com.InfinityRaider.AgriCraft.reference.Constants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CropPlantBluePower extends CropPlantTallGeneric {
    public CropPlantBluePower(ItemSeeds seed) {
        super(seed);
    }
    @Override
    public int transformMeta(int growthStage) {
        switch(growthStage) {
            case 0:return 0;
            case 1: return 2;
            case 2: return 3;
            case 3: return 5;
            case 4: return 7;
            case 5: return 2;
            case 6: return 3;
        }
        return 8;
    }

    @Override
    public int maxMetaBottomBlock() {
        return 4;
    }

    @Override
    public boolean renderAsFlower() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getHeight(int meta) {
        return (meta>maxMetaBottomBlock()?2:1)* Constants.UNIT*16;
    }

    @Override
    public String getInformation() {
        String name = getSeed().getUnlocalizedName();
        name = name.substring(name.indexOf(':')+1);
        return "agricraft_journal.bp_"+name;
    }
}
