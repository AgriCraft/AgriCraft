package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.api.v2.ISeedStats;
import com.InfinityRaider.AgriCraft.api.v2.ITrowel;
import com.InfinityRaider.AgriCraft.farming.PlantStats;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.items.RenderItemBase;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemTrowel extends ItemAgricraft implements ITrowel {
    private IIcon[] icons = new IIcon[2];

    public ItemTrowel() {
        super();
        this.maxStackSize=1;
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.trowel;
    }

    //I'm overriding this just to be sure
    @Override
    public boolean canItemEditBlocks() {return true;}

    //this is called when you right click with this item in hand
    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        return false;   //return false or else no other use methods will be called (for instance "onBlockActivated" on the crops block)
    }

    @Override
    public boolean hasSeed(ItemStack trowel) {
        if(trowel==null || trowel.getItem()==null || trowel.stackTagCompound==null) {
            return false;
        }
        return CropPlantHandler.readPlantFromNBT(trowel.stackTagCompound.getCompoundTag(Names.NBT.seed)) != null;
    }

    @Override
    public boolean isSeedAnalysed(ItemStack trowel) {
        if(!this.hasSeed(trowel)) {
            return false;
        }
        return trowel.stackTagCompound.hasKey(Names.NBT.analyzed) && trowel.stackTagCompound.getBoolean(Names.NBT.analyzed);
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
        seed.stackTagCompound = seedTag;
        return seed;
    }

    @Override
    public int getGrowthStage(ItemStack trowel) {
        if(!this.hasSeed(trowel)) {
            return -1;
        }
        return trowel.stackTagCompound.getShort(Names.NBT.materialMeta);
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
    public void registerIcons(IIconRegister reg) {
        LogHelper.debug("registering icon for: " + this.getUnlocalizedName());
        icons[0] = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.')+1)+"_empty");
        icons[1] = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.')+1)+"_full");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderItemBase getItemRenderer() {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        if(meta<=1) {
            return this.icons[meta];
        }
        return null;
    }
}
