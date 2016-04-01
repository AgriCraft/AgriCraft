package com.infinityraider.agricraft.farming.cropplant;

import com.infinityraider.agricraft.api.v1.IMutation;
import com.infinityraider.agricraft.api.v1.RenderMethod;
import net.minecraft.item.ItemSeeds;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class CropPlantOreDict extends CropPlantGeneric {
	
    public CropPlantOreDict(ItemSeeds seed) {
        super(seed);
    }

    @Override
    public List<IMutation> getDefaultMutations() {
        return new ArrayList<>();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderMethod getRenderMethod() {
        return RenderMethod.HASHTAG;
    }

    @Override
    public ResourceLocation getPrimaryPlantTexture(int growthStage) {
        return null;
    }

    @Override
    public ResourceLocation getSecondaryPlantTexture(int growthStage) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        return "agricraft_journal."+getSeed().getUnlocalizedName();
    }
}
