package com.InfinityRaider.AgriCraft.blocks;


import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vazkii.botania.api.item.IGrassHornExcempt;

import java.util.ArrayList;

public class BlockCropBotania extends BlockCrop implements IGrassHornExcempt {

    //Botania horn of the wild support
    @Override
    public boolean canUproot(World world, int x, int y, int z) {
        if(!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if(te!=null && te instanceof TileEntityCrop) {
                TileEntityCrop crop = (TileEntityCrop) te;
                if(crop.hasPlant()) {
                    ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
                    if(crop.isMature()) {
                        drops.addAll(SeedHelper.getPlantFruits((ItemSeeds) crop.seed, world, x, y, z, crop.gain, crop.seedMeta));
                    }
                    drops.add(crop.getSeedStack());
                    for (ItemStack drop : drops) {
                        this.dropBlockAsItem(world, x, y, z, drop);
                    }
                }
                crop.clearPlant();
            }
        }
        return false;
    }
}
