package com.InfinityRaider.AgriCraft.farming.cropplant;

import com.InfinityRaider.AgriCraft.api.v1.RenderMethod;
import net.minecraft.item.ItemSeeds;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CropPlantOreDict extends CropPlantGeneric {
    public CropPlantOreDict(ItemSeeds seed) {
        super(seed);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderMethod getRenderMethod() {
        return RenderMethod.HASHTAG;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        return "agricraft_journal."+getSeed().getUnlocalizedName();
    }
}
