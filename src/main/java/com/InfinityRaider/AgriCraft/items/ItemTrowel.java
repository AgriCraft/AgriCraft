package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.api.v1.ISeedStats;
import com.infinityraider.agricraft.api.v1.ITrowel;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.farming.cropplant.CropPlant;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.utility.RegisterHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemTrowel extends ItemBase implements ITrowel {
	
	private static final String[] VARIENTS = { "empty", "full" };
	
    public ItemTrowel() {
        super("trowel");
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
        return CropPlantHandler.readPlantFromNBT(trowel.getTagCompound().getCompoundTag(AgriCraftNBT.SEED)) != null;
    }

    @Override
    public boolean isSeedAnalysed(ItemStack trowel) {
        if(!this.hasSeed(trowel)) {
            return false;
        }
        return trowel.getTagCompound().hasKey(AgriCraftNBT.ANALYZED) && trowel.getTagCompound().getBoolean(AgriCraftNBT.ANALYZED);
    }

    @Override
    public void analyze(ItemStack trowel) {
        if(this.hasSeed(trowel)) {
            if(trowel.hasTagCompound()) {
                NBTTagCompound tag = trowel.getTagCompound();
                tag.setBoolean(AgriCraftNBT.ANALYZED, true);
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
        CropPlant plant = CropPlantHandler.readPlantFromNBT(tag.getCompoundTag(AgriCraftNBT.SEED));
        if(plant == null) {
            return null;
        }
        short growth = tag.getShort(AgriCraftNBT.GROWTH);
        short gain = tag.getShort(AgriCraftNBT.GAIN);
        short strength = tag.getShort(AgriCraftNBT.STRENGTH);
        boolean analysed = tag.getBoolean(AgriCraftNBT.ANALYZED);
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
        return trowel.getTagCompound().getShort(AgriCraftNBT.MATERIAL_META);
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
        short growth = (initialised && seedTag.hasKey(AgriCraftNBT.GROWTH))?seedTag.getShort(AgriCraftNBT.GROWTH): Constants.DEFAULT_GROWTH;
        short gain = (initialised && seedTag.hasKey(AgriCraftNBT.GAIN))?seedTag.getShort(AgriCraftNBT.GAIN): Constants.DEFAULT_GAIN;
        short strength = (initialised && seedTag.hasKey(AgriCraftNBT.STRENGTH))?seedTag.getShort(AgriCraftNBT.STRENGTH): Constants.DEFAULT_STRENGTH;
        boolean analysed = (initialised && seedTag.hasKey(AgriCraftNBT.ANALYZED)) && seedTag.getBoolean(AgriCraftNBT.ANALYZED);
        NBTTagCompound tag = new NBTTagCompound();
        CropPlantHandler.setSeedNBT(tag, growth, gain, strength, analysed);
        tag.setTag(AgriCraftNBT.SEED, CropPlantHandler.writePlantToNBT(plant));
        tag.setShort(AgriCraftNBT.MATERIAL_META, (short) growthStage);
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
	public void registerItemRenderer() {
		RegisterHelper.registerItemRenderer(this, VARIENTS);
	}

}
