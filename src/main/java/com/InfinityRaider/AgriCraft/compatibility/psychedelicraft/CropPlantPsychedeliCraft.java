package com.InfinityRaider.AgriCraft.compatibility.psychedelicraft;

import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlantTallGeneric;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemSeeds;
import net.minecraft.world.IBlockAccess;

public class CropPlantPsychedeliCraft extends CropPlantTallGeneric {
    public CropPlantPsychedeliCraft(ItemSeeds seed) {
        super(seed);
    }

    @Override
    public int transformMeta(int growthStage) {
        if(growthStage<=maxMetaBottomBlock()) {
            return growthStage * 4;
        } else if(growthStage!=7) {
            return(growthStage-2)*4;
        }
        return 11;
    }

    @Override
    public int maxMetaBottomBlock() {
        return 3;
    }

    @Override
    protected IGrowthRequirement initGrowthRequirement() {
        return GrowthRequirementHandler.getNewBuilder().build();
    }

    @Override
    public boolean isMature(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z)>=7;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsFlower() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        String name = getSeed().getUnlocalizedName();
        name = name.substring(name.indexOf('.')+1);
        name = name.substring(0, name.indexOf("Seeds"));
        return "agricraft_journal.pc_"+name;
    }
}
