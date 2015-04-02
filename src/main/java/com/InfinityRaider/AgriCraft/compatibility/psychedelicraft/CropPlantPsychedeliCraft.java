package com.InfinityRaider.AgriCraft.compatibility.psychedelicraft;

import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlantTallGeneric;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemSeeds;
import net.minecraft.world.World;

public class CropPlantPsychedeliCraft extends CropPlantTallGeneric {
    public CropPlantPsychedeliCraft(ItemSeeds seed) {
        super(seed);
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage;
    }

    @Override
    public int maxMetaBottomBlock() {
        return 3;
    }

    @Override
    public boolean isMature(World world, int x, int y, int z) {
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
        return "agricraft_journal.pc_"+getSeed().getUnlocalizedName();
    }
}
