package com.InfinityRaider.AgriCraft.apiimpl.v2;

import com.InfinityRaider.AgriCraft.api.APIStatus;
import com.InfinityRaider.AgriCraft.api.v2.*;
import com.InfinityRaider.AgriCraft.apiimpl.v1.APIimplv1;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.PlantStats;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirement;
import com.InfinityRaider.AgriCraft.farming.mutation.statcalculator.StatCalculator;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.statstringdisplayer.StatStringDisplayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class APIimplv2 extends APIimplv1 implements APIv2 {
    public APIimplv2(int version, APIStatus status) {
        super(version, status);
    }

    @Override
    public ISeedStats getSeedStats(ItemStack seed) {
        if (!isHandledByAgricraft(seed)) {
            return null;
        }
        if (seed.stackTagCompound != null && seed.stackTagCompound.hasKey(Names.NBT.growth) && seed.stackTagCompound.getBoolean(Names.NBT.analyzed)) {
            return PlantStats.readFromNBT(seed.stackTagCompound);
        } else {
            return new PlantStats(-1, -1, -1);
        }
    }

    @Override
    public void analyze(ItemStack seed) {
        if(CropPlantHandler.isValidSeed(seed)) {
            if(seed.hasTagCompound()) {
                NBTTagCompound tag = seed.getTagCompound();
                String[] keys = {Names.NBT.growth, Names.NBT.gain, Names.NBT.strength};
                for(String key:keys) {
                    if (!tag.hasKey(key)) {
                        tag.setShort(key, (short) 1);
                    }
                }
                tag.setBoolean(Names.NBT.analyzed, true);
            } else {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setShort(Names.NBT.growth, (short) 1);
                tag.setShort(Names.NBT.gain, (short) 1);
                tag.setShort(Names.NBT.strength, (short) 1);
                tag.setBoolean(Names.NBT.analyzed, true);
                seed.setTagCompound(tag);
            }
        }
    }

    @Override
    public ICrop getCrop(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te instanceof ICrop) {
            return (ICrop) te;
        }
        return null;
    }

    @Override
    public void setStatCalculator(IStatCalculator calculator) {
        StatCalculator.setStatCalculator(calculator);
    }

    @Override
    public IGrowthRequirementBuilder createGrowthRequirementBuilder() {
        return new GrowthRequirement.Builder();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setStatStringDisplayer(IStatStringDisplayer displayer) {
        StatStringDisplayer.setStatStringDisplayer(displayer);
    }
}
