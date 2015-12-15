package com.InfinityRaider.AgriCraft.apiimpl.v1;

import com.InfinityRaider.AgriCraft.api.API;
import com.InfinityRaider.AgriCraft.api.APIBase;
import com.InfinityRaider.AgriCraft.api.APIStatus;
import com.InfinityRaider.AgriCraft.api.v1.*;
import com.InfinityRaider.AgriCraft.api.v2.IRake;
import com.InfinityRaider.AgriCraft.api.v2.ISeedStats;
import com.InfinityRaider.AgriCraft.farming.PlantStats;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlantAPIv1;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlantAgriCraft;
import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.farming.mutation.Mutation;
import com.InfinityRaider.AgriCraft.farming.mutation.MutationHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.init.Items;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.exception.MissingArgumentsException;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
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
		return Lists.newArrayList(new ItemStack(Items.crops));
	}

	@Override
	public List<ItemStack> getRakeItems() {
		return Lists.newArrayList(new ItemStack(Items.handRake, 1, 0), new ItemStack(Items.handRake, 1, 1));
	}

	@Override
	public List<Block> getCropsBlocks() {
		return Lists.newArrayList((Block) Blocks.blockCrop);
	}

	@Override
	public boolean isNativePlantingDisabled(ItemStack seed) {
		return ConfigurationHandler.disableVanillaFarming;
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
		if (seed.stackTagCompound != null && seed.stackTagCompound.hasKey(Names.NBT.growth) && seed.stackTagCompound.getBoolean(Names.NBT.analyzed)) {
			return PlantStats.readFromNBT(seed.stackTagCompound);
		} else {
			return new PlantStats(-1, -1, -1);
		}
	}

    @Override
    public void registerCropPlant(ICropPlant plant) {
       CropPlantHandler.addCropToRegister(new CropPlantAPIv1(plant));
    }

	@Override
	public ICropPlant getCropPlant(ItemStack seed) {
		return CropPlantHandler.getPlantFromStack(seed);
	}

	@Override
    public void registerCropPlant(IAgriCraftPlant plant) {
        CropPlantHandler.addCropToRegister(new CropPlantAgriCraft(plant));
    }

    @Override
    public boolean registerGrowthRequirement(ItemWithMeta seed, IGrowthRequirement requirement) {
		return CropPlantHandler.setGrowthRequirement(seed, requirement);
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
	public boolean canPlaceCrops(World world, int x, int y, int z, ItemStack crops) {
		if (crops == null || crops.getItem() == null || crops.getItem() != Items.crops) {
			return false;
		} else if (GrowthRequirementHandler.isSoilValid(world, x, y - 1, z) && world.isAirBlock(x, y, z)) {
			return true;
		} else {
			return false;
		}
	}
  

	@Override
	public boolean placeCrops(World world, int x, int y, int z, ItemStack crops) {
		if (canPlaceCrops(world, x, y, z, crops) && crops.stackSize >= 1) {
			if (!world.isRemote) {
				world.setBlock(x, y, z, Blocks.blockCrop, 0 ,3);
				crops.stackSize--;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isCrops(World world, int x, int y, int z) {
		return world.getBlock(x, y, z) == Blocks.blockCrop;
	}

	@Override
	public boolean isMature(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityCrop) {
			TileEntityCrop crop = (TileEntityCrop) te;
			return crop.hasPlant() && crop.isMature();
		}
		return false;
	}

	@Override
	public boolean isWeeds(World world, int x, int y, int z) {
		if (!ConfigurationHandler.enableWeeds) {
			return false;
		}
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityCrop) {
			TileEntityCrop crop = (TileEntityCrop) te;
			return crop.hasWeed();
		}
		return false;
	}

	@Override
	public boolean isEmpty(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityCrop) {
			TileEntityCrop crop = (TileEntityCrop) te;
			return !(crop.isCrossCrop() || crop.hasWeed() || crop.hasPlant());
		}
		return false;
	}

	@Override
	public boolean isCrossCrops(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityCrop) {
			TileEntityCrop crop = (TileEntityCrop) te;
			return crop.isCrossCrop();
		}
		return false;
	}

	@Override
	public ItemStack getPlantedSeed(World world, int x, int y, int z) {
		if(!isCrops(world, x, y, z)) {
			return null;
		}
		return ((TileEntityCrop) world.getTileEntity(x, y, z)).getSeedStack();
	}

	@Override
	public Block getPlantedBlock(World world, int x, int y, int z) {
		if(!isCrops(world, x, y, z)) {
			return null;
		}
		return ((TileEntityCrop) world.getTileEntity(x, y, z)).getPlantBlock();
	}

	@Override
	public ICropPlant getCropPlant(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te==null || !(te instanceof TileEntityCrop)) {
			return null;
		}
		return ((TileEntityCrop) te).getPlant();
	}

	@Override
	public boolean canGrow(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityCrop) {
			TileEntityCrop crop = (TileEntityCrop) te;
			return crop.hasPlant() && crop.isFertile();
		}
		return false;
	}

	@Override
	public boolean isAnalyzed(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te==null || !(te instanceof TileEntityCrop)) {
			return false;
		}
		TileEntityCrop crop = (TileEntityCrop) te;
		return crop.hasPlant() && crop.isAnalyzed();
	}

	@Override
	public ISeedStats getStats(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te==null || !(te instanceof TileEntityCrop)) {
			return new PlantStats(-1, -1, -1);
		}
		TileEntityCrop crop = (TileEntityCrop) te;
		return crop.getStats();
	}

	@Override
	public boolean isRakeRequiredForWeeding() {
		return ConfigurationHandler.enableHandRake;
	}

	@Override
	public boolean removeWeeds(World world, int x, int y, int z, boolean byHand) {
		if (!ConfigurationHandler.enableWeeds || (byHand && ConfigurationHandler.enableHandRake)) {
			return false;
		}
		TileEntity te = world.getTileEntity(x, y, z);
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
	public boolean removeWeeds(World world, int x, int y, int z, ItemStack rake) {
		if(world.isRemote) {
			return false;
		}
		if (!ConfigurationHandler.enableWeeds) {
			return false;
		}
		if(rake == null || rake.getItem() == null || !(rake.getItem() instanceof IRake)) {
			return false;
		}
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityCrop) {
			TileEntityCrop crop = (TileEntityCrop) te;
			if (!crop.hasWeed()) {
				return false;
			}
			return ((IRake) rake.getItem()).removeWeeds(crop, rake);
		}
		return false;
	}

	@Override
	public boolean placeCrossCrops(World world, int x, int y, int z, ItemStack crops) {
		if (world.isRemote) {
			return false;
		}
		if (crops == null || crops.getItem() == null || crops.getItem() != Items.crops || crops.stackSize < 1) {
			return false;
		}
		TileEntity te = world.getTileEntity(x, y, z);
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
	public ItemStack removeCrossCrops(World world, int x, int y, int z) {
		if (world.isRemote) {
			return null;
		}
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityCrop) {
			TileEntityCrop crop = (TileEntityCrop) te;
			if(crop.isCrossCrop()) {
				crop.setCrossCrop(false);
				return new ItemStack(Items.crops, 1);
			}
		}
		return null;
	}

	@Override
	public SeedRequirementStatus canApplySeeds(World world, int x, int y, int z, ItemStack seed) {
		if (CropPlantHandler.isValidSeed(seed)) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TileEntityCrop) {
				TileEntityCrop crop = (TileEntityCrop) te;
				if (crop.isCrossCrop() || crop.hasPlant() || crop.hasWeed()) {
					return SeedRequirementStatus.BAD_LOCATION;
				}
				IGrowthRequirement growthRequirement = CropPlantHandler.getGrowthRequirement(seed);
				if(!growthRequirement.isValidSoil(world, x, y-1, z)) {
					return SeedRequirementStatus.WRONG_SOIL;
				}
				if (!growthRequirement.canGrow(world, x, y, z)) {
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
	public boolean applySeeds(World world, int x, int y, int z, ItemStack seed) {
		if(!world.isRemote) {
			if (CropPlantHandler.isValidSeed(seed)) {
				TileEntity te = world.getTileEntity(x, y, z);
				if (te instanceof TileEntityCrop) {
					TileEntityCrop crop = (TileEntityCrop) te;
					if (crop.isCrossCrop() || crop.hasPlant() || crop.hasWeed() || !CropPlantHandler.getGrowthRequirement(seed).canGrow(world, x, y, z)) {
						return false;
					}
					if (seed.stackTagCompound != null && seed.stackTagCompound.hasKey(Names.NBT.growth)) {
						crop.setPlant(seed.stackTagCompound.getInteger(Names.NBT.growth), seed.stackTagCompound.getInteger(Names.NBT.gain), seed.stackTagCompound.getInteger(Names.NBT.strength), seed.stackTagCompound.getBoolean(Names.NBT.analyzed), seed.getItem(), seed.getItemDamage());
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
	public List<ItemStack> harvest(World world, int x, int y, int z) {
		if (world.isRemote) {
			return null;
		}
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityCrop) {
			TileEntityCrop crop = (TileEntityCrop) te;
			if(crop.allowHarvest(null)) {
				crop.getWorldObj().setBlockMetadataWithNotify(crop.xCoord, crop.yCoord, crop.zCoord, 2, 2);
				return crop.getPlant().getFruitsOnHarvest(crop.getGain(), world.rand);
			}
		}
		return null;
	}

	@Override
	public List<ItemStack> destroy(World world, int x, int y, int z) {
		if (world.isRemote || !isCrops(world, x, y, z)) {
			return null;
		}
		List<ItemStack> result = Blocks.blockCrop.getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
    world.setBlockToAir(x,y,z);
    world.removeTileEntity(x, y, z);
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
	public boolean isValidFertilizer(World world, int x, int y, int z, ItemStack fertilizer) {
		if (fertilizer == null || fertilizer.getItem() == null) {
			return false;
		}
		TileEntity te = world.getTileEntity(x, y, z);
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
	public boolean applyFertilizer(World world, int x, int y, int z, ItemStack fertilizer) {
		if (world.isRemote || !isValidFertilizer(world, x, y, z, fertilizer)) {
			return false;
		}
		if (fertilizer.getItem() == net.minecraft.init.Items.dye && fertilizer.getItemDamage() == 15) {
			((BlockCrop) Blocks.blockCrop).func_149853_b(world, random, x, y, z);
			fertilizer.stackSize--;
			world.playAuxSFX(2005, x, y, z, 0);
			return true;
		} else if (fertilizer.getItem() instanceof IFertiliser) {
			((TileEntityCrop) world.getTileEntity(x, y, z)).applyFertiliser((IFertiliser) fertilizer.getItem(), world.rand);
			fertilizer.stackSize--;
			world.playAuxSFX(2005, x, y, z, 0);
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
	public IAgriCraftPlant createNewCrop(Object... args) {
		try {
			return new BlockModPlant(args);
		} catch (MissingArgumentsException e) {
			e.printStackTrace();
			return null;
		}
	}
}
