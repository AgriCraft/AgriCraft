package com.infinityraider.agricraft.farming.cropplant;

import com.infinityraider.agricraft.api.v1.IMutation;
import com.infinityraider.agricraft.api.v1.RenderMethod;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemSeeds;
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
    public TextureAtlasSprite getPrimaryPlantTexture(int growthStage) {
        return null;
    }

    @Override
    public TextureAtlasSprite getSecondaryPlantTexture(int growthStage) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        return "agricraft_journal."+getSeed().getUnlocalizedName();
    }
}
