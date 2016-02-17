package com.InfinityRaider.AgriCraft.farming.cropplant;

import com.InfinityRaider.AgriCraft.api.v1.RenderMethod;
import com.InfinityRaider.AgriCraft.reference.Constants;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Random;

/**
 * Generic abstract implementation of CropPlantTall for two-blocks tall plants
 */
public abstract class CropPlantTallGeneric extends CropPlantGeneric {

    public CropPlantTallGeneric(ItemSeeds seed) {
        super(seed);
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        int amount = (int) (Math.ceil((gain + 0.00) / 3));
        ArrayList<ItemStack> list = new ArrayList<>();
        while(amount>0) {
            list.add(getRandomFruit(rand));
            amount--;
        }
        return list;
    }

    @Override
    public boolean canBonemeal() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getHeight(int meta) {
        return (getSecondaryPlantTexture(meta) != null ? 2 : 1)*Constants.UNIT*13;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderMethod getRenderMethod() {
        return getBaseRenderMethod() == RenderMethod.CROSSED ? RenderMethod.TALL_CROSSED : RenderMethod.TALL_HASHTAG;
    }

    protected abstract RenderMethod getBaseRenderMethod();
}
