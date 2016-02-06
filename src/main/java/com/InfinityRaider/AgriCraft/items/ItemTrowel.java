package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.api.v1.ISeedStats;
import com.InfinityRaider.AgriCraft.api.v1.ITrowel;
import com.InfinityRaider.AgriCraft.farming.PlantStats;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.items.RenderItemBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTrowel extends ItemBase implements ITrowel {
    public ItemTrowel() {
        super(Names.Objects.trowel);
        this.maxStackSize=1;
    }

    //I'm overriding this just to be sure
    @Override
    public boolean canItemEditBlocks() {return true;}

    //this is called when you right click with this item in hand
    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        return false;   //return false or else no other use methods will be called (for instance "onBlockActivated" on the crops block)
    }

    @Override
    public boolean hasSeed(ItemStack trowel) {
        if(trowel==null || trowel.getItem()==null || trowel.getTagCompound()==null) {
            return false;
        }
        return CropPlantHandler.readPlantFromNBT(trowel.getTagCompound().getCompoundTag(Names.NBT.seed)) != null;
    }

    @Override
    public boolean isSeedAnalysed(ItemStack trowel) {
        if(!this.hasSeed(trowel)) {
            return false;
        }
        return trowel.getTagCompound().hasKey(Names.NBT.analyzed) && trowel.getTagCompound().getBoolean(Names.NBT.analyzed);
    }

    @Override
    public void analyze(ItemStack trowel) {
        if(this.hasSeed(trowel)) {
            if(trowel.hasTagCompound()) {
                NBTTagCompound tag = trowel.getTagCompound();
                tag.setBoolean(Names.NBT.analyzed, true);
            } else {
                NBTTagCompound tag = new NBTTagCompound();
                CropPlantHandler.setSeedNBT(tag, (short) 1, (short) 1, (short) 1, true);
                trowel.setTagCompound(tag);
            }
        }
    }

    @Override
    public ItemStack getSeed(ItemStack trowel) {
        if(!this.hasSeed(trowel)) {
            return null;
        }
        NBTTagCompound tag = trowel.getTagCompound();
        CropPlant plant = CropPlantHandler.readPlantFromNBT(tag.getCompoundTag(Names.NBT.seed));
        if(plant == null) {
            return null;
        }
        short growth = tag.getShort(Names.NBT.growth);
        short gain = tag.getShort(Names.NBT.gain);
        short strength = tag.getShort(Names.NBT.strength);
        boolean analysed = tag.getBoolean(Names.NBT.analyzed);
        NBTTagCompound seedTag = new NBTTagCompound();
        CropPlantHandler.setSeedNBT(seedTag, growth, gain, strength, analysed);
        ItemStack seed = plant.getSeed();
        seed.setTagCompound(seedTag);
        return seed;
    }

    @Override
    public int getGrowthStage(ItemStack trowel) {
        if(!this.hasSeed(trowel)) {
            return -1;
        }
        return trowel.getTagCompound().getShort(Names.NBT.materialMeta);
    }

    @Override
    public boolean setSeed(ItemStack trowel, ItemStack seed, int growthStage) {
        if(this.hasSeed(trowel)) {
            return false;
        }
        CropPlant plant = CropPlantHandler.getPlantFromStack(seed);
        if(plant == null) {
            return false;
        }
        NBTTagCompound seedTag = seed.getTagCompound();
        boolean initialised = seedTag!=null;
        short growth = (initialised && seedTag.hasKey(Names.NBT.growth))?seedTag.getShort(Names.NBT.growth): Constants.DEFAULT_GROWTH;
        short gain = (initialised && seedTag.hasKey(Names.NBT.gain))?seedTag.getShort(Names.NBT.gain): Constants.DEFAULT_GAIN;
        short strength = (initialised && seedTag.hasKey(Names.NBT.strength))?seedTag.getShort(Names.NBT.strength): Constants.DEFAULT_STRENGTH;
        boolean analysed = (initialised && seedTag.hasKey(Names.NBT.analyzed)) && seedTag.getBoolean(Names.NBT.analyzed);
        NBTTagCompound tag = new NBTTagCompound();
        CropPlantHandler.setSeedNBT(tag, growth, gain, strength, analysed);
        tag.setTag(Names.NBT.seed, CropPlantHandler.writePlantToNBT(plant));
        tag.setShort(Names.NBT.materialMeta, (short) growthStage);
        trowel.setTagCompound(tag);
        trowel.setItemDamage(1);
        return true;
    }

    @Override
    public void clearSeed(ItemStack trowel) {
        trowel.setTagCompound(null);
        trowel.setItemDamage(0);
    }

    @Override
    public ISeedStats getStats(ItemStack trowel) {
        return PlantStats.getStatsFromStack(getSeed(trowel));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderItemBase getItemRenderer() {
        return null;
    }
}
