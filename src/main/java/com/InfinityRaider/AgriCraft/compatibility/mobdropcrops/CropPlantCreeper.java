package com.InfinityRaider.AgriCraft.compatibility.mobdropcrops;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlantStem;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import com.pam.mobdropcrops.mobdropcrops;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockStem;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;

public class CropPlantCreeper extends CropPlantStem {
    public CropPlantCreeper() {
        super((ItemSeeds) mobdropcrops.creeperseedItem, mobdropcrops.pamcreeperPod);
    }

    @Override
    public int tier() {
        return 4;
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> fruits = new ArrayList<ItemStack>();
        fruits.add(new ItemStack(Items.gunpowder));
        return fruits;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getPlantIcon(int growthStage) {
        if(growthStage<7) {
            //for the Vanilla SeedItem class the arguments for this method are not used
            return mobdropcrops.pamcreeperVine.getIcon(0, transformMeta(growthStage));
        }
        else {
            return getStemIcon();
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getStemIcon() {
        BlockStem plant = (BlockStem) mobdropcrops.pamcreeperVine;
        return plant.getStemIcon();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        int meta = world.getBlockMetadata(x, y, z);
        boolean mature = isMature(world, x, y ,z);
        PlantRenderer.renderStemPlant(x, y, z, renderer, getPlantIcon(meta), meta, mobdropcrops.pamcreeperVine, mobdropcrops.pamcreeperPod, mature);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        String name = getSeed().getUnlocalizedName();
        int start = name.indexOf('.')+1;
        int stop = name.indexOf("seedItem");
        name = name.substring(start, stop);
        return "agricraft_journal.pmd_"+Character.toUpperCase(name.charAt(0))+name.substring(1);
    }
}
