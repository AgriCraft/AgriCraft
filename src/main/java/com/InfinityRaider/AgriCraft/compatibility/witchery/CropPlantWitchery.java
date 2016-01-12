package com.InfinityRaider.AgriCraft.compatibility.witchery;

import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlantGeneric;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;

public class CropPlantWitchery extends CropPlantGeneric {
    protected static final Item BELLADONNA_SEED = (Item) Item.itemRegistry.getObject("witchery:seedsbelladonna");
    private final int tier;

    public CropPlantWitchery(ItemSeeds seed) {
        this(seed, 3);
    }

    public CropPlantWitchery(ItemSeeds seed, int tier) {
        super(seed);
        this.setTier(tier);
        this.tier = tier;
    }

    @Override
    protected boolean modSpecificFruits() {
        return false;
    }

    @Override
    public int tier() {
        return tier;
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage==7?4:(int) (((float) growthStage)/2.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsFlower() {
        return getSeed().getItem()!= BELLADONNA_SEED;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        String name = getSeed().getUnlocalizedName();
        name = name.substring(name.indexOf(':')+1);
        int index = name.indexOf("seeds");
        name = index<0?name:name.substring(index+"seeds".length());
        return "agricraft_journal.wi_"+Character.toUpperCase(name.charAt(0))+name.substring(1);
    }
}
