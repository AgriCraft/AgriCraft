package com.InfinityRaider.AgriCraft.compatibility.thaumictinkerer;

import com.InfinityRaider.AgriCraft.farming.CropOverride;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirements;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumic.tinkerer.common.ThaumicTinkerer;
import thaumic.tinkerer.common.block.BlockInfusedGrain;
import thaumic.tinkerer.common.item.ItemInfusedSeeds;

import java.util.Random;

public class ThaumicTinkererOverride extends CropOverride {
    private static final String NBT_MAIN_ASPECT = "mainAspect";
    private static final String NBT_ASPECT_TENDENCIES = "aspectTendencies";
    private static IIcon[][] icons;

    private TileEntityCrop crop;
    private Aspect aspect;
    private AspectList tendencies;

    public ThaumicTinkererOverride(TileEntityCrop crop) {
        this.crop = crop;
        this.tendencies = new AspectList();
        ThaumicTinkererHelper.overrides.put(crop, this);
    }

    @Override
    public void onSeedPlanted(EntityPlayer player) {
        ItemStack stack = player.getCurrentEquippedItem();
        if(stack!=null && stack.getItem()!=null && stack.getItem() instanceof ItemInfusedSeeds && stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            aspect = ((ItemInfusedSeeds) crop.seed).getAspect(stack);
            if(tag.hasKey(NBT_ASPECT_TENDENCIES)) {
                tendencies.readFromNBT(tag.getCompoundTag(NBT_ASPECT_TENDENCIES));
            }
        }
    }

    @Override
    public boolean hasDefaultGrowth() {
        return false;
    }

    @Override
    public void increaseGrowth() {
        //growth
        applyGrowthTick();
        //aspect exchange
        exchangeAspects();
    }

    @Override
    public boolean hasDefaultBonemeal() {
        return true;
    }

    @Override
    public void applyBonemeal() {

    }

    @Override
    public boolean hasDefaultHarvesting() {
        return false;
    }

    @Override
    public void onHarvest() {

    }

    @Override
    public boolean hasDefaultBreaking() {
        return false;
    }

    @Override
    public void onBreak() {
        ThaumicTinkererHelper.overrides.remove(crop);
    }

    @Override
    public boolean immuneToWeed() {
        return false;
    }

    @Override
    public IIcon getIcon() {
        IIcon icon = null;
        int meta = crop.getWorldObj().getBlockMetadata(crop.xCoord, crop.yCoord, crop.zCoord);
        //TODO: figure out how to get the latest version of Thaumcraft & Thaumic Tinkerer working in my dev env
        /*
        int index = BlockInfusedGrain.getNumberFromAspectForTexture(aspect);
        if (meta < 7) {
            if (meta == 6) {
                meta = 5;
            }
            return this.icons[index][meta >> 1];
        } else {
            return this.icons[index][3];
        }
         */
        if(icon==null) {
            icon = SeedHelper.getPlant(crop.seed).getIcon(0, meta);
        }
        return icon;
    }

    @Override
    public NBTTagCompound saveToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound aspectTag = new NBTTagCompound();
        NBTTagCompound tendencyTag = new NBTTagCompound();
        new AspectList().add(aspect, 1).writeToNBT(aspectTag);
        tendencies.writeToNBT(tendencyTag);
        tag.setTag(NBT_MAIN_ASPECT, aspectTag);
        tag.setTag(NBT_ASPECT_TENDENCIES, tendencyTag);
        return tag;
    }

    @Override
    public void loadFromNBT(NBTTagCompound tag) {
        AspectList aspectList = new AspectList();
        aspectList.readFromNBT(tag.getCompoundTag(NBT_MAIN_ASPECT));
        aspect = aspectList.getAspects()[0];
        tendencies.readFromNBT(tag.getCompoundTag(NBT_ASPECT_TENDENCIES));
    }

    //apply a growth tick
    private void applyGrowthTick() {
        int meta = crop.getWorldObj().getBlockMetadata(crop.xCoord, crop.yCoord, crop.zCoord);
        if(meta<7) {
            if (GrowthRequirements.getGrowthRequirement(crop.seed, crop.seedMeta).canGrow(crop.getWorldObj(), crop.xCoord, crop.yCoord, crop.zCoord)) {
                meta++;
                crop.getWorldObj().setBlockMetadataWithNotify(crop.xCoord, crop.yCoord, crop.zCoord, meta, 3);
            }
        }
    }


    //exchanges aspect between neighbouring crops
    private void exchangeAspects() {
        if (crop.getWorldObj().rand.nextInt((2550 - ((int) Math.pow(tendencies.getAmount(Aspect.AIR), 2))) * 10) == 0 && !aspect.isPrimal()) {
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                TileEntity entity = crop.getWorldObj().getTileEntity(crop.xCoord + dir.offsetX, crop.yCoord + dir.offsetY, crop.zCoord + dir.offsetZ);
                if (entity!=null && entity instanceof TileEntityCrop) {
                    //Exchange aspect
                    TileEntityCrop neighbor = (TileEntityCrop) entity;
                    if(neighbor.hasOverride() && neighbor.getOverride() instanceof ThaumicTinkererOverride) {
                        ThaumicTinkererOverride override = (ThaumicTinkererOverride) neighbor.getOverride();
                        Aspect neighbourAspect = override.aspect;
                        if (neighbourAspect.isPrimal()) {
                            if (tendencies.getAmount(neighbourAspect) < 5) {
                                tendencies.add(neighbourAspect, 1);
                                reduceSaturatedAspects();
                                crop.markForUpdate();
                                if (crop.getWorldObj().isRemote) {
                                    for (int i = 0; i < 50; i++) {
                                        ThaumicTinkerer.tcProxy.essentiaTrailFx(crop.getWorldObj(), crop.xCoord + dir.offsetX, crop.yCoord + dir.offsetY, crop.zCoord + dir.offsetZ, crop.xCoord, crop.yCoord, crop.zCoord, 50, neighbourAspect.getColor(), 1F);
                                    }
                                }
                                return;
                            }
                        } else {
                            AspectList targetList = override.tendencies;
                            if (targetList.getAspects().length == 0 || targetList.getAspects()[0] == null) {
                                return;
                            }
                            neighbourAspect = targetList.getAspects()[crop.getWorldObj().rand.nextInt(targetList.getAspects().length)];
                            if (targetList.getAmount(neighbourAspect) >= tendencies.getAmount(neighbourAspect)) {
                                tendencies.add(neighbourAspect, 1);
                                targetList.reduce(neighbourAspect, 1);
                                reduceSaturatedAspects();
                                if (crop.getWorldObj().isRemote) {
                                    for (int i = 0; i < 50; i++) {
                                        ThaumicTinkerer.tcProxy.essentiaTrailFx(crop.getWorldObj(), crop.xCoord + dir.offsetX, crop.yCoord + dir.offsetY, crop.zCoord + dir.offsetZ, crop.xCoord, crop.yCoord, crop.zCoord, 50, neighbourAspect.getColor(), 1F);
                                    }
                                }
                                crop.markForUpdate();
                            }
                            return;
                        }
                    }
                }
            }
        }
    }


    //Ensures that the farmland only holds a maximum of 20 aspect (copied from TT source code)
    public void reduceSaturatedAspects() {
        int sum = 0;
        for (Integer i: tendencies.aspects.values()) {
            sum += i;
        }
        if (sum > 20) {
            int toRemove = sum - 20;
            while (toRemove > 0) {
                Random rand = new Random();
                Aspect target = tendencies.getAspects()[rand.nextInt(tendencies.getAspects().length)];
                tendencies.remove(target, 1);
                toRemove--;
            }
        }
    }
}