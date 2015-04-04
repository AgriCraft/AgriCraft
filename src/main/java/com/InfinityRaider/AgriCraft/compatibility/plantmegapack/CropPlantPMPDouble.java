package com.InfinityRaider.AgriCraft.compatibility.plantmegapack;
;
import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlantTallGeneric;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemSeeds;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class CropPlantPMPDouble extends CropPlantTallGeneric {

    public CropPlantPMPDouble(ItemSeeds seed) {
        super(seed);
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage;
    }

    @Override
    public int maxMetaBottomBlock() {
        return 1;
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
        return "agricraft_journal.pmp_"+getSeed().getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getBottomIcon(int growthStage) {
        if(growthStage<2) {
            return getPlantIcon(growthStage);
        }
        return getPlantIcon(2);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderTopLayer(int growthStage) {
        return growthStage>1;
    }

}
