package com.InfinityRaider.AgriCraft.compatibility.witchery;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.AgriCraftPlantGeneric;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;

public class CropPlantWitchery extends AgriCraftPlantGeneric {
	
    protected static final Item BELLADONNA_SEED = (Item) Item.itemRegistry.getObject("witchery:seedsbelladonna");

    public CropPlantWitchery(ItemSeeds seed) {
        this(seed, 3);
    }

    public CropPlantWitchery(ItemSeeds seed, int tier) {
        super(seed, tier);
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage==7?4:(int) ((growthStage)/2.0F);
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
