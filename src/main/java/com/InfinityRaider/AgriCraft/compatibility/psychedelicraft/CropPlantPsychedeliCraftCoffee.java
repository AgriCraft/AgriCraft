package com.InfinityRaider.AgriCraft.compatibility.psychedelicraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemSeeds;
import net.minecraft.util.IIcon;

public class CropPlantPsychedeliCraftCoffee extends CropPlantPsychedeliCraft {
    public CropPlantPsychedeliCraftCoffee(ItemSeeds seed) {
        super(seed);
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage<0?0:growthStage>7?7:growthStage;
    }

    @Override
    public int maxMetaBottomBlock() {
        return 3;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getBottomIcon(int growthStage) {
        growthStage = transformMeta(growthStage);
        return getBlock().getIcon(0, growthStage * 2);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getPlantIcon(int growthStage) {
        if(growthStage> maxMetaBottomBlock()) {
            growthStage = transformMeta(growthStage);
            return getBlock().getIcon(0, 2*(growthStage-(maxMetaBottomBlock()+1))+1);
        } else {
            return getBottomIcon(growthStage);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        String name = getSeed().getUnlocalizedName();
        name = name.substring(name.indexOf('.')+1);
        return "agricraft_journal.pc_"+name;
    }
}
