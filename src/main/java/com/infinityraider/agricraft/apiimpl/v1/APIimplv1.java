package com.infinityraider.agricraft.apiimpl.v1;

import com.infinityraider.agricraft.api.v1.util.BlockWithMeta;
import com.infinityraider.agricraft.api.v1.requirment.IGrowthRequirement;
import com.infinityraider.agricraft.api.v1.requirment.IGrowthRequirementBuilder;
import com.infinityraider.agricraft.api.API;
import com.infinityraider.agricraft.api.APIBase;
import com.infinityraider.agricraft.api.APIStatus;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.farming.mutation.statcalculator.StatCalculator;
import com.infinityraider.agricraft.init.AgriBlocks;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.APIv1;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.api.v1.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizerRegistry;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlantRegistry;
import com.infinityraider.agricraft.api.v1.seed.IAgriSeedRegistry;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatCalculator;

public class APIimplv1 implements APIv1 {
	
    public static final int API_VERSION = 1;
	
	private static final APIv1 INSTANCE = new APIimplv1(APIStatus.OK);
	
    private final APIStatus status;
	
	private final IAgriSeedRegistry seedRegistry;
	private final IAgriPlantRegistry plantRegistry;
	private final IAgriMutationRegistry mutationRegistry;
	private final IAgriFertilizerRegistry fertilizerRegistry;

    private APIimplv1(APIStatus status) {
        this.status = status;
		this.seedRegistry = new SeedRegistry();
		this.plantRegistry = new PlantRegistry();
		this.mutationRegistry = new MutationRegistry();
		this.fertilizerRegistry = new FertilizerRegistry();
    }
	
	public static APIv1 getInstance() {
		return INSTANCE;
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
	public IAgriSeedRegistry getSeedRegistry() {
		return seedRegistry;
	}
	
	@Override
	public IAgriPlantRegistry getPlantRegistry() {
		return plantRegistry;
	}
	
	@Override
	public IAgriMutationRegistry getMutationRegistry() {
		return mutationRegistry;
	}

	@Override
	public IAgriFertilizerRegistry getFertilizerRegistry() {
		return fertilizerRegistry;
	}

    @Override
    public boolean isNativePlantingDisabled(ItemStack seed) {
        return AgriCraftConfig.disableVanillaFarming;
    }
	
	@Override
    public boolean isCrop(World world, BlockPos pos) {
        return world.getTileEntity(pos) instanceof IAgriCrop;
    }

    @Override
    public boolean registerDefaultSoil(BlockWithMeta soil) {
        return GrowthRequirementHandler.addDefaultSoil(soil);
    }

    @Override
    public boolean isRakeRequiredForWeeding() {
        return AgriItems.enableHandRake;
    }

    @Override
    public List<ItemStack> harvest(World world, BlockPos pos) {
        if (world.isRemote) {
            return null;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            if(crop.canHarvest()) {
                crop.getWorld().setBlockState(pos, world.getBlockState(pos).withProperty(AgriProperties.GROWTHSTAGE, 2), 2);
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
        List<ItemStack> result = AgriBlocks.blockCrop.getDrops(world, pos, world.getBlockState(pos), 0);
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
    public IAgriCrop getCrop(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof IAgriCrop) {
            return (IAgriCrop) te;
        }
        return null;
    }

    @Override
    public void setStatCalculator(IAgriStatCalculator calculator) {
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
        if(journal == null || journal.getItem() == null || !(journal.getItem() instanceof IAgriJournalItem)) {
            return false;
        }
        return ((IAgriJournalItem) journal.getItem()).isSeedDiscovered(journal, plant);
    }

    @Override
    public void setPlantDiscovered(ItemStack journal, IAgriPlant plant, boolean discovered) {
        if(journal == null || journal.getItem() == null || !(journal.getItem() instanceof IAgriJournalItem)) {
            return;
        }
        ((IAgriJournalItem) journal.getItem()).addEntry(journal, plant);
    }

    @Override
    public List<IAgriPlant> getPlantsDiscovered(ItemStack journal) {
        if(journal == null || journal.getItem() == null || !(journal.getItem() instanceof IAgriJournalItem)) {
            return new ArrayList<>();
        }
        return ((IAgriJournalItem) journal.getItem()).getDiscoveredSeeds(journal);
    }

}
