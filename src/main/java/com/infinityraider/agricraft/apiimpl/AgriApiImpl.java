package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.api.util.BlockWithMeta;
import com.infinityraider.agricraft.api.requirment.IGrowthRequirement;
import com.infinityraider.agricraft.api.requirment.IGrowthRequirementBuilder;
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
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.api.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.mutation.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.plant.IAgriPlantRegistry;
import com.infinityraider.agricraft.api.stat.IAgriStatCalculator;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.api.adapter.IAgriAdapterRegistry;
import com.infinityraider.agricraft.api.AgriAPI;

public class AgriApiImpl implements AgriAPI {
	
	private static final AgriAPI INSTANCE = new AgriApiImpl();
	
	public static AgriAPI getInstance() {
		return INSTANCE;
	}
	
	private final IAgriAdapterRegistry<IAgriFertilizer> fertilizerRegistry;
	private final IAgriAdapterRegistry<IAgriStat> statRegistry;
	private final IAgriAdapterRegistry<AgriSeed> seedRegistry;
	private final IAgriPlantRegistry plantRegistry;
	private final IAgriMutationRegistry mutationRegistry;

    private AgriApiImpl() {
		this.fertilizerRegistry = new AdapterRegistry<>();
		this.statRegistry = new AdapterRegistry<>();
		this.seedRegistry = new AdapterRegistry<>();
		this.plantRegistry = new PlantRegistry();
		this.mutationRegistry = new MutationRegistry();
		this.fertilizerRegistry.registerAdapter(new FertilizerRegistry.BonemealWrapper());
    }

    @Override
    public boolean isActive(World world) {
        return true;
    }
	
	@Override
	public IAgriAdapterRegistry<IAgriStat> getStatRegistry() {
		return statRegistry;
	}

	@Override
	public IAgriAdapterRegistry<AgriSeed> getSeedRegistry() {
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
	public IAgriAdapterRegistry<IAgriFertilizer> getFertilizerRegistry() {
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
        return AgriItems.HAND_RAKE.isEnabled();
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
        List<ItemStack> result = AgriBlocks.CROP.getDrops(world, pos, world.getBlockState(pos), 0);
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

}
