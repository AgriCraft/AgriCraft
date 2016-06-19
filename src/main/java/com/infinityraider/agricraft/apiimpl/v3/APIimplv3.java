package com.infinityraider.agricraft.apiimpl.v3;

import com.infinityraider.agricraft.api.v3.plant.IPlantRegistry;
import com.infinityraider.agricraft.api.v3.util.BlockWithMeta;
import com.infinityraider.agricraft.api.v3.mutation.IMutationRegistry;
import com.infinityraider.agricraft.api.v3.requirment.IGrowthRequirement;
import com.infinityraider.agricraft.api.v3.stat.IStatCalculator;
import com.infinityraider.agricraft.api.v3.requirment.IGrowthRequirementBuilder;
import com.infinityraider.agricraft.api.v3.items.IJournal;
import com.infinityraider.agricraft.api.API;
import com.infinityraider.agricraft.api.APIBase;
import com.infinityraider.agricraft.api.APIStatus;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.farming.mutation.statcalculator.StatCalculator;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.init.AgriCraftItems;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import com.infinityraider.agricraft.reference.AgriCraftProperties;
import com.infinityraider.agricraft.api.v3.APIv3;
import com.infinityraider.agricraft.api.v3.fertiliser.IFertilizerRegistry;
import com.infinityraider.agricraft.api.v3.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v3.stat.IAgriStat;
import com.infinityraider.agricraft.api.v3.crop.IAgriCrop;

public class APIimplv3 implements APIv3 {
	
    public static final int API_VERSION = 3;
	
    private final APIStatus status;
	private final IMutationRegistry mutationRegistry;
	private final IPlantRegistry plantRegistry;
	private final IFertilizerRegistry fertilizerRegistry;

    public APIimplv3(APIStatus status) {
        this.status = status;
		this.mutationRegistry = new MutationRegistry();
		this.plantRegistry = new PlantRegistry();
		this.fertilizerRegistry = new FertilizerRegistry();
    }

    @Override
    public APIBase getAPI(int maxVersion) {
        if (maxVersion == API_VERSION && status == APIStatus.OK) {
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
        return API_VERSION;
    }

    @Override
    public boolean isActive(World world) {
        return true;
    }

	@Override
	public IMutationRegistry getMutationRegistry() {
		return mutationRegistry;
	}
	
	@Override
	public IPlantRegistry getPlantRegistry() {
		return plantRegistry;
	}

	@Override
	public IFertilizerRegistry getFertilizerRegistry() {
		return fertilizerRegistry;
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
    public boolean isCrop(World world, BlockPos pos) {
        return world.getTileEntity(pos) instanceof IAgriCrop;
    }

    @Override
    public IAgriStat getStats(ItemStack seed) {
        if (isHandledByAgricraft(seed)) {
            return new PlantStats(seed);
        } else {
			return null;   
        }
    }

    @Override
    public boolean registerDefaultSoil(BlockWithMeta soil) {
        return GrowthRequirementHandler.addDefaultSoil(soil);
    }

    @Override
    public boolean isRakeRequiredForWeeding() {
        return AgriCraftItems.enableHandRake;
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
                return crop.getPlant().getFruitsOnHarvest(crop.getStat().getGain(), world.rand);
            }
        }
        return null;
    }

    @Override
    public List<ItemStack> destroy(World world, BlockPos pos) {
        if (world.isRemote || !isCrop(world, pos)) {
            return null;
        }
        List<ItemStack> result = AgriCraftBlocks.blockCrop.getDrops(world, pos, world.getBlockState(pos), 0);
        world.setBlockToAir(pos);
        world.removeTileEntity(pos);
        return result;
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
			NBTTagCompound tag;
			PlantStats stats;
            if(seed.hasTagCompound()) {
				tag = seed.getTagCompound();
				stats = new PlantStats(tag);
            } else {
                tag = new NBTTagCompound();
				stats = new PlantStats();
            }
			stats.analyze();
			stats.writeToNBT(tag);
			seed.setTagCompound(tag);
        }
    }

    @Override
    public IAgriCrop getCrop(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof IAgriCrop) {
            return (IAgriCrop) te;
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
    public boolean isPlantDiscovered(ItemStack journal, IAgriPlant plant) {
        if(journal == null || journal.getItem() == null || !(journal.getItem() instanceof IJournal)) {
            return false;
        }
        return ((IJournal) journal.getItem()).isSeedDiscovered(journal, plant);
    }

    @Override
    public void setPlantDiscovered(ItemStack journal, IAgriPlant plant, boolean discovered) {
        if(journal == null || journal.getItem() == null || !(journal.getItem() instanceof IJournal)) {
            return;
        }
        ((IJournal) journal.getItem()).addEntry(journal, plant);
    }

    @Override
    public List<IAgriPlant> getPlantsDiscovered(ItemStack journal) {
        if(journal == null || journal.getItem() == null || !(journal.getItem() instanceof IJournal)) {
            return new ArrayList<>();
        }
        return ((IJournal) journal.getItem()).getDiscoveredSeeds(journal);
    }

}
