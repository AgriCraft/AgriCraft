package com.InfinityRaider.AgriCraft.compatibility.mobdropcrops;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlantGeneric;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.pam.mobdropcrops.mobdropcrops;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;

public class CropPlantSlime extends CropPlantGeneric {
    public CropPlantSlime() {
        super((ItemSeeds) mobdropcrops.slimeseedItem);
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage;
    }

    @Override
    public int tier() {
        return 4;
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> fruits = new ArrayList<ItemStack>();
        fruits.add(new ItemStack(Items.slime_ball));
        return fruits;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsFlower() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getPlantIcon(int growthStage) {
        //for the Vanilla SeedItem class the arguments for this method are not used
        return mobdropcrops.pamslimepadCrop.getIcon(0, transformMeta(growthStage));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        String name = getSeed().getUnlocalizedName();
        int start = name.indexOf('.')+1;
        int stop = name.indexOf("seedItem");
        name = name.substring(start, stop);
        return "agricraft_journal.wf_"+Character.toUpperCase(name.charAt(0))+name.substring(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        int meta = world.getBlockMetadata(x, y, z);
        renderer.setRenderBounds(0, 0, 0, 1, Constants.unit*(1+meta), 1);
        renderer.renderStandardBlock(mobdropcrops.pamslimepadCrop, x, y, z);
    }
}
