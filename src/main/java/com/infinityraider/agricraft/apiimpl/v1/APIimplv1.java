package com.infinityraider.agricraft.apiimpl.v1;

import com.infinityraider.agricraft.api.API;
import com.infinityraider.agricraft.api.APIBase;
import com.infinityraider.agricraft.api.APIStatus;
import com.infinityraider.agricraft.api.v1.*;
import com.infinityraider.agricraft.api.v1.ISeedStats;
import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.farming.mutation.MutationHandler;
import com.infinityraider.agricraft.farming.mutation.statcalculator.StatCalculator;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.init.AgriCraftItems;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.tileentity.TileEntityCrop;
import com.infinityraider.agricraft.utility.statstringdisplayer.StatStringDisplayer;
import com.google.common.collect.Lists;
import com.infinityraider.agricraft.handler.config.AgriCraftConfig;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.infinityraider.agricraft.reference.AgriCraftProperties;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;

public class APIimplv1 implements APIv1 {
    private final int version;
    private final APIStatus status;

    public APIimplv1(int version, APIStatus status) {
        this.version = version;
        this.status = status;
    }

    @Override
    public APIBase getAPI(int maxVersion) {
        if (maxVersion == version && status == APIStatus.OK) {
            return this;
        } else {
            return API.getAPI(maxVersion);
        }
    }

    @Override
    public APIStatus getStatus() {
        return status;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public boolean isActive(World world) {
        return true;
    }

    @Override
    public List<ItemStack> getCropsItems() {
        return Lists.newArrayList(new ItemStack(AgriCraftItems.crops));
    }

    @Override
    public List<ItemStack> getRakeItems() {
        return Lists.newArrayList(new ItemStack(AgriCraftItems.handRake, 1, 0), new ItemStack(AgriCraftItems.handRake, 1, 1));
    }

    @Override
    public List<Block> getCropsBlocks() {
        return Lists.newArrayList((Block) AgriCraftBlocks.blockCrop);
    }

    @Override
    public boolean isNativePlantingDisabled(ItemStack seed) {
        return AgriCraftConfig.disableVanillaFarming;
    }

    @Override
    public boolean isHandledByAgricraft(ItemStack seed) {
        return CropPlantHandler.isValidSeed(seed);
    }

    @Override
    public ISeedStats getSeedStats(ItemStack seed) {
        if (!isHandledByAgricraft(seed)) {
            return null;
        }
        if (seed.getTagCompound() != null && seed.getTagCompound().hasKey(AgriCraftNBT.GROWTH) && seed.getTagCompound().getBoolean(AgriCraftNBT.ANALYZED)) {
            return PlantStats.readFromNBT(seed.getTagCompound());
        } else {
            return new PlantStats(-1, -1, -1);
        }
    }

    @Override
    public void registerCropPlant(IAgriCraftPlant plant) {
        CropPlantHandler.addCropToRegister(plant);
    }

    @Override
    public IAgriCraftPlant getCropPlant(ItemStack seed) {
        return CropPlantHandler.getPlantFromStack(seed);
    }

    @Override
    public boolean registerDefaultSoil(BlockWithMeta soil) {
        return GrowthRequirementHandler.addDefaultSoil(soil);
    }

    @Override
    public IGrowthRequirement getGrowthRequirement(ItemStack seed) {
        if(!CropPlantHandler.isValidSeed(seed)) {
            return null;
        }
        return CropPlantHandler.getGrowthRequirement(seed);
    }

    @Override
    public boolean canPlaceCrops(World world, BlockPos pos, ItemStack crops) {
        if (crops == null || crops.getItem() == null || crops.getItem() != AgriCraftItems.crops) {
            return false;
        } else if (GrowthRequirementHandler.isSoilValid(world, pos.add(0, -1, 0)) && world.isAirBlock(pos)) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean placeCrops(World world, BlockPos pos, ItemStack crops) {
        if (canPlaceCrops(world, pos, crops) && crops.stackSize >= 1) {
            if (!world.isRemote) {
                world.setBlockState(pos, AgriCraftBlocks.blockCrop.getDefaultState().withProperty(AgriCraftProperties.GROWTHSTAGE, 0), 3);
                crops.stackSize--;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isCrops(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == AgriCraftBlocks.blockCrop;
    }

    @Override
    public boolean isMature(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            return crop.hasPlant() && crop.isMature();
        }
        return false;
    }

    @Override
    public boolean isWeeds(World world, BlockPos pos) {
        if (!AgriCraftConfig.enableWeeds) {
            return false;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            return crop.hasWeed();
        }
        return false;
    }

    @Override
    public boolean isEmpty(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            return !(crop.isCrossCrop() || crop.hasWeed() || crop.hasPlant());
        }
        return false;
    }

    @Override
    public boolean isCrossCrops(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            return crop.isCrossCrop();
        }
        return false;
    }

    @Override
    public ItemStack getPlantedSeed(World world, BlockPos pos) {
        if(!isCrops(world, pos)) {
            return null;
        }
        return ((TileEntityCrop) world.getTileEntity(pos)).getSeedStack();
    }

    @Override
    public Block getPlantedBlock(World world, BlockPos pos) {
        if(!isCrops(world, pos)) {
            return null;
        }
        return ((TileEntityCrop) world.getTileEntity(pos)).getPlantBlock();
    }

    @Override
    public IAgriCraftPlant getCropPlant(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if(te==null || !(te instanceof TileEntityCrop)) {
            return null;
        }
        return ((TileEntityCrop) te).getPlant();
    }

    @Override
    public boolean canGrow(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            return crop.hasPlant() && crop.isFertile();
        }
        return false;
    }

    @Override
    public boolean isAnalyzed(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if(te==null || !(te instanceof TileEntityCrop)) {
            return false;
        }
        TileEntityCrop crop = (TileEntityCrop) te;
        return crop.hasPlant() && crop.isAnalyzed();
    }

    @Override
    public ISeedStats getStats(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if(te==null || !(te instanceof TileEntityCrop)) {
            return new PlantStats(-1, -1, -1);
        }
        TileEntityCrop crop = (TileEntityCrop) te;
        return crop.getStats();
    }

    @Override
    public boolean isRakeRequiredForWeeding() {
        return AgriCraftItems.enableHandRake;
    }

    @Override
    public boolean removeWeeds(World world, BlockPos pos, boolean byHand) {
        if (!AgriCraftConfig.enableWeeds || (byHand && AgriCraftItems.enableHandRake)) {
            return false;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            if (!crop.hasWeed()) {
                return false;
            }
            if (!world.isRemote) {
                crop.updateWeed(0);
            }
            return true;
        }
        return false;
    }

    private static final Random random = new Random();

    @Override
    public boolean removeWeeds(World world, BlockPos pos, ItemStack rake) {
        if(world.isRemote) {
            return false;
        }
        if (!AgriCraftConfig.enableWeeds) {
            return false;
        }
        if(rake == null || rake.getItem() == null || !(rake.getItem() instanceof IRake)) {
            return false;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            if (!crop.hasWeed()) {
                return false;
            }
            return ((IRake) rake.getItem()).removeWeeds(world, pos, world.getBlockState(pos), crop, rake);
        }
        return false;
    }

    @Override
    public boolean placeCrossCrops(World world, BlockPos pos, ItemStack crops) {
        if (world.isRemote) {
            return false;
        }
        if (crops == null || crops.getItem() == null || crops.getItem() != AgriCraftItems.crops || crops.stackSize < 1) {
            return false;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            if(!crop.hasWeed() && !crop.isCrossCrop() && !crop.hasPlant()) {
                crop.setCrossCrop(true);
                crops.stackSize--;
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack removeCrossCrops(World world, BlockPos pos) {
        if (world.isRemote) {
            return null;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            if(crop.isCrossCrop()) {
                crop.setCrossCrop(false);
                return new ItemStack(AgriCraftItems.crops, 1);
            }
        }
        return null;
    }

    @Override
    public SeedRequirementStatus canApplySeeds(World world, BlockPos pos, ItemStack seed) {
        if (CropPlantHandler.isValidSeed(seed)) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityCrop) {
                TileEntityCrop crop = (TileEntityCrop) te;
                if (crop.isCrossCrop() || crop.hasPlant() || crop.hasWeed()) {
                    return SeedRequirementStatus.BAD_LOCATION;
                }
                IGrowthRequirement growthRequirement = CropPlantHandler.getGrowthRequirement(seed);
                if(!growthRequirement.isValidSoil(world, pos.add(0, -1, 0))) {
                    return SeedRequirementStatus.WRONG_SOIL;
                }
                if (!growthRequirement.canGrow(world, pos)) {
                    return SeedRequirementStatus.MISSING_REQUIREMENTS;
                }
                return SeedRequirementStatus.CAN_APPLY;
            } else {
                return SeedRequirementStatus.BAD_LOCATION;
            }
        } else {
            return SeedRequirementStatus.BAD_SEED;
        }
    }

    @Override
    public boolean applySeeds(World world, BlockPos pos, ItemStack seed) {
        if(!world.isRemote) {
            if (CropPlantHandler.isValidSeed(seed)) {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TileEntityCrop) {
                    TileEntityCrop crop = (TileEntityCrop) te;
                    if (crop.isCrossCrop() || crop.hasPlant() || crop.hasWeed() || !CropPlantHandler.getGrowthRequirement(seed).canGrow(world, pos)) {
                        return false;
                    }
                    NBTTagCompound tag = seed.getTagCompound();
                    if (tag != null && tag.hasKey(AgriCraftNBT.GROWTH)) {
                        crop.setPlant(tag.getInteger(AgriCraftNBT.GROWTH), tag.getInteger(AgriCraftNBT.GAIN), tag.getInteger(AgriCraftNBT.STRENGTH), tag.getBoolean(AgriCraftNBT.ANALYZED), seed.getItem(), seed.getItemDamage());
                    } else {
                        crop.setPlant(Constants.DEFAULT_GROWTH, Constants.DEFAULT_GAIN, Constants.DEFAULT_STRENGTH, false, seed.getItem(), seed.getItemDamage());
                    }
                    seed.stackSize--;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<ItemStack> harvest(World world, BlockPos pos) {
        if (world.isRemote) {
            return null;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            if(crop.allowHarvest(null)) {
                crop.getWorld().setBlockState(pos, world.getBlockState(pos).withProperty(AgriCraftProperties.GROWTHSTAGE, 2), 2);
                return crop.getPlant().getFruitsOnHarvest(crop.getGain(), world.rand);
            }
        }
        return null;
    }

    @Override
    public List<ItemStack> destroy(World world, BlockPos pos) {
        if (world.isRemote || !isCrops(world, pos)) {
            return null;
        }
        List<ItemStack> result = AgriCraftBlocks.blockCrop.getDrops(world, pos, world.getBlockState(pos), 0);
        world.setBlockToAir(pos);
        world.removeTileEntity(pos);
        return result;
    }

    @Override
    public boolean isSupportedFertilizer(ItemStack fertilizer) {
        if (fertilizer == null || fertilizer.getItem() == null) {
            return false;
        }
        if (fertilizer.getItem() == net.minecraft.init.Items.dye && fertilizer.getItemDamage() == 15) {
            return true;
        }
        if (fertilizer.getItem() instanceof IFertiliser) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isValidFertilizer(World world, BlockPos pos, ItemStack fertilizer) {
        if (fertilizer == null || fertilizer.getItem() == null) {
            return false;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            if (fertilizer.getItem() == net.minecraft.init.Items.dye && fertilizer.getItemDamage() == 15) {
                return crop.canBonemeal();
            } else if (fertilizer.getItem() instanceof IFertiliser) {
                return crop.allowFertiliser((IFertiliser) fertilizer.getItem());
            }
        }
        return false;
    }

    @Override
    public boolean applyFertilizer(World world, BlockPos pos, IBlockState state, ItemStack fertilizer) {
        if (world.isRemote || !isValidFertilizer(world, pos, fertilizer)) {
            return false;
        }
        if (fertilizer.getItem() == net.minecraft.init.Items.dye && fertilizer.getItemDamage() == 15) {
            ((BlockCrop) AgriCraftBlocks.blockCrop).grow(world, random, pos, state);
            fertilizer.stackSize--;
            world.playAuxSFX(2005, pos, 0);
            return true;
        } else if (fertilizer.getItem() instanceof IFertiliser) {
            ((TileEntityCrop) world.getTileEntity(pos)).applyFertiliser((IFertiliser) fertilizer.getItem(), world.rand);
            fertilizer.stackSize--;
            world.playAuxSFX(2005, pos, 0);
            return true;
        }
        return false;
    }

    @Override
    public IMutation[] getRegisteredMutations() {
        return MutationHandler.getMutations();
    }

    @Override
    public IMutation[] getRegisteredMutationsForParent(ItemStack parent) {
        return MutationHandler.getMutationsFromParent(parent);
    }

    @Override
    public IMutation[] getRegisteredMutationsForChild(ItemStack child) {
        return MutationHandler.getMutationsFromChild(child);
    }

    @Override
    public boolean registerMutation(ItemStack result, ItemStack parent1, ItemStack parent2) {
        MutationHandler.add(new Mutation(result, parent1, parent2));
        return false;
    }

    @Override
    public boolean registerMutation(ItemStack result, ItemStack parent1, ItemStack parent2, double d) {
        MutationHandler.add(new Mutation(result, parent1, parent2, d));
        return true;
    }

    @Override
    public boolean removeMutation(ItemStack result) {
        MutationHandler.removeMutationsByResult(result);
        return true;
    }

    @Override
    public boolean registerValidSoil(BlockWithMeta soil) {
        GrowthRequirementHandler.addSoil(soil);
        return true;
    }

    @Override
    public short getStatCap() {
        return (short) AgriCraftConfig.cropStatCap;
    }

    @Override
    public void analyze(ItemStack seed) {
        if(CropPlantHandler.isValidSeed(seed)) {
            if(seed.hasTagCompound()) {
                NBTTagCompound tag = seed.getTagCompound();
                String[] keys = {AgriCraftNBT.GROWTH, AgriCraftNBT.GAIN, AgriCraftNBT.STRENGTH};
                for(String key:keys) {
                    if (!tag.hasKey(key)) {
                        tag.setShort(key, (short) 1);
                    }
                }
                tag.setBoolean(AgriCraftNBT.ANALYZED, true);
            } else {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setShort(AgriCraftNBT.GROWTH, (short) 1);
                tag.setShort(AgriCraftNBT.GAIN, (short) 1);
                tag.setShort(AgriCraftNBT.STRENGTH, (short) 1);
                tag.setBoolean(AgriCraftNBT.ANALYZED, true);
                seed.setTagCompound(tag);
            }
        }
    }

    @Override
    public ICrop getCrop(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
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
        return GrowthRequirementHandler.getNewBuilder();
    }

    @Override
    public IGrowthRequirement createDefaultGrowthRequirement() {
        return GrowthRequirementHandler.getNewBuilder().build();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setStatStringDisplayer(IStatStringDisplayer displayer) {
        StatStringDisplayer.setStatStringDisplayer(displayer);
    }

    @Override
    public boolean isSeedDiscoveredInJournal(ItemStack journal, ItemStack seed) {
        if(journal == null || journal.getItem() == null || !(journal.getItem() instanceof IJournal)) {
            return false;
        }
        return ((IJournal) journal.getItem()).isSeedDiscovered(journal, seed);
    }

    @Override
    public void addEntryToJournal(ItemStack journal, ItemStack seed) {
        if(journal == null || journal.getItem() == null || !(journal.getItem() instanceof IJournal)) {
            return;
        }
        ((IJournal) journal.getItem()).addEntry(journal, seed);
    }

    @Override
    public List<ItemStack> getDiscoveredSeedsFromJournal(ItemStack journal) {
        if(journal == null || journal.getItem() == null || !(journal.getItem() instanceof IJournal)) {
            return new ArrayList<>();
        }
        return ((IJournal) journal.getItem()).getDiscoveredSeeds(journal);
    }

}
