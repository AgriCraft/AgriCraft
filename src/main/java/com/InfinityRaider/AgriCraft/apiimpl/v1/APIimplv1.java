package com.InfinityRaider.AgriCraft.apiimpl.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import com.InfinityRaider.AgriCraft.api.API;
import com.InfinityRaider.AgriCraft.api.APIBase;
import com.InfinityRaider.AgriCraft.api.APIStatus;
import com.InfinityRaider.AgriCraft.api.v1.APIv1;
import com.InfinityRaider.AgriCraft.api.v1.IBlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.ISeedStats;
import com.InfinityRaider.AgriCraft.api.v1.SeedRequirementStatus;
import com.InfinityRaider.AgriCraft.api.v1.ISeedRequirements;
import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirement;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirements;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.init.Items;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.BlockWithMeta;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import com.google.common.collect.Lists;

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
		return Lists.newArrayList(new ItemStack(Items.handRake));
	}

	@Override
	public List<Block> getCropsBlocks() {
		return Lists.newArrayList((Block)Blocks.blockCrop);
	}

	@Override
	public boolean isSeed(ItemStack seed) {
		if (seed != null && seed.getItem() != null && seed.getItem() instanceof IPlantable) {
			if (seed.getItem() instanceof ItemSeeds) {
				return true;
			} else if (seed.hasTagCompound()) {
				NBTTagCompound tag = (NBTTagCompound) seed.getTagCompound().copy();
				if (tag.hasKey(Names.NBT.growth) && tag.hasKey(Names.NBT.gain) && tag.hasKey(Names.NBT.strength)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isNativePlantingDisabled(ItemStack seed) {
		return ConfigurationHandler.disableVanillaFarming;
	}

	@Override
	public boolean isHandledByAgricraft(ItemStack seed) {
		return SeedHelper.isValidSeedStack(seed);
	}

	@Override
	public ISeedStats getSeedStats(ItemStack seed) {
		if (!isHandledByAgricraft(seed)) {
			return null;
		}
		if (seed.stackTagCompound != null && seed.stackTagCompound.hasKey(Names.NBT.growth)
				&& seed.stackTagCompound.getBoolean(Names.NBT.analyzed)) {
			return new SeedStats(seed.stackTagCompound.getInteger(Names.NBT.growth), seed.stackTagCompound.getInteger(Names.NBT.gain),
					seed.stackTagCompound.getInteger(Names.NBT.strength));
		} else {
			return new SeedStats(-1, -1, -1);
		}
	}

	@Override
	public ISeedRequirements getSeedRequirements(ItemStack seed) {
		if (SeedHelper.isValidSeedStack(seed)) {
			SeedRequirements result = new SeedRequirements();
			GrowthRequirement growthRequirement = GrowthRequirements.getGrowthRequirement((ItemSeeds) seed.getItem(), seed.getItemDamage());
			result.setNeedsCrops(ConfigurationHandler.disableVanillaFarming);
			if (growthRequirement.requiresSpecificSoil()) {
				if (growthRequirement.getSoil().getBlock() == net.minecraft.init.Blocks.farmland) {
					result.setSoilBlock(Lists.newArrayList((IBlockWithMeta)new BlockWithMeta(net.minecraft.init.Blocks.dirt)));
					result.setNeedsTilling(true);
				} else {
					result.setSoilBlock(Lists.newArrayList((IBlockWithMeta)growthRequirement.getSoil()));
					result.setNeedsTilling(false);
				}
			} else {
				result.setSoilBlock(new ArrayList<IBlockWithMeta>());
				result.setNeedsTilling(false);
				for (BlockWithMeta elem : GrowthRequirements.defaultSoils) {
					if (elem.getBlock() == net.minecraft.init.Blocks.farmland) {
						result.getSoilBlock().add(new BlockWithMeta(net.minecraft.init.Blocks.dirt));
						result.setNeedsTilling(true);
					} else {
						result.getSoilBlock().add(elem);
					}
				}
			}
			switch (growthRequirement.getRequiredType()) {
			case BELOW:
				result.setBelowBlock(Lists.newArrayList((IBlockWithMeta)growthRequirement.getRequiredBlock()));
				result.setNearBlocks(null);
				break;
			case NEARBY:
				result.setBelowBlock(null);
				result.setNearBlocks(Lists.newArrayList((IBlockWithMeta)growthRequirement.getRequiredBlock()));
				break;
			default:
				result.setBelowBlock(null);
				result.setNearBlocks(null);
				break;
			}
			return result;
		}
		return null;
	}

	@Override
	public boolean canPlaceCrops(World world, int x, int y, int z, ItemStack crops) {
		if (crops == null || crops.getItem() == null || crops.getItem() != Items.crops) {
			return false;
		} else if (GrowthRequirements.isSoilValid(world, x, y - 1, z) && world.isAirBlock(x, y, z)) {
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
			return crop.weed;
		}
		return false;
	}

	@Override
	public boolean isEmpty(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityCrop) {
			TileEntityCrop crop = (TileEntityCrop) te;
			return crop.hasPlant();
		}
		return false;
	}

	@Override
	public boolean isCrossCrops(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityCrop) {
			TileEntityCrop crop = (TileEntityCrop) te;
			return crop.crossCrop;
		}
		return false;
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
			if (!crop.weed) {
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
		if (!ConfigurationHandler.enableWeeds) {
			return false;
		}
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityCrop) {
			TileEntityCrop crop = (TileEntityCrop) te;
			if (!crop.weed) {
				return false;
			}
      int weedGrowthStage = world.getBlockMetadata(x, y, z);
      int toolMeta = rake.getItemDamage();
      while (!world.isRemote && weedGrowthStage > 0) {
	      weedGrowthStage = (toolMeta == 1) ? 0 : Math.max(random.nextInt(weedGrowthStage/2+1)-1, 0)+weedGrowthStage/2;
	      crop.updateWeed(weedGrowthStage);
      }
      return true;
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
			if(!crop.weed && !crop.crossCrop && !crop.hasPlant()) {
				crop.crossCrop = true;
				crops.stackSize--;
				crop.markForUpdate();
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
			if(crop.crossCrop) {
				crop.crossCrop = false;
				crop.markForUpdate();
				return new ItemStack(Items.crops, 1);
			}
		}
		return null;
	}

	@Override
	public SeedRequirementStatus canApplySeeds(World world, int x, int y, int z, ItemStack seed) {
		if (SeedHelper.isValidSeedStack(seed)) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TileEntityCrop) {
				TileEntityCrop crop = (TileEntityCrop) te;
				if (crop.crossCrop || crop.hasPlant()) {
					return SeedRequirementStatus.BAD_LOCATION;
				}
				GrowthRequirement growthRequirement = GrowthRequirements.getGrowthRequirement((ItemSeeds) seed.getItem(), seed.getItemDamage());
				if(!growthRequirement.isValidSoil(world, x, y-1, z)) {
					return SeedRequirementStatus.WRONG_SOIL;
				}
				if (!growthRequirement.isBaseBlockPresent(world, x, y, z)) {
					return SeedRequirementStatus.MISSING_REQUIREMENTS;
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
			if (SeedHelper.isValidSeedStack(seed)) {
				TileEntity te = world.getTileEntity(x, y, z);
				if (te instanceof TileEntityCrop) {
					TileEntityCrop crop = (TileEntityCrop) te;
					if (crop.crossCrop || crop.hasPlant() || !GrowthRequirements.getGrowthRequirement((ItemSeeds) seed.getItem(), seed.getItemDamage()).canGrow(world, x, y, z)) {
						return false;
					}
					if (seed.stackTagCompound != null && seed.stackTagCompound.hasKey(Names.NBT.growth)) {
						crop.setPlant(seed.stackTagCompound.getInteger(Names.NBT.growth), seed.stackTagCompound.getInteger(Names.NBT.gain), seed.stackTagCompound.getInteger(Names.NBT.strength), seed.stackTagCompound.getBoolean(Names.NBT.analyzed), (ItemSeeds) seed.getItem(), seed.getItemDamage());
					} else {
						crop.setPlant(Constants.defaultGrowth, Constants.defaultGain, Constants.defaultStrength, false, (ItemSeeds) seed.getItem(), seed.getItemDamage());
					}
					crop.markForUpdate();
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
			if(crop.isMature()) {
				crop.getWorldObj().setBlockMetadataWithNotify(crop.xCoord, crop.yCoord, crop.zCoord, 2, 2);
				crop.markForUpdate();
				return SeedHelper.getPlantFruits(crop.seed, world, x, y, z, crop.gain, crop.seedMeta);
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
		if (ModIntegration.LoadedMods.magicalCrops && ConfigurationHandler.integration_allowMagicFertiliser && fertilizer.getItem() == Item.itemRegistry.getObject("magicalcrops:magicalcrops_MagicalCropFertilizer")) {
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
				return (crop.crossCrop && ConfigurationHandler.bonemealMutation) ||
						(crop.hasPlant() && !crop.isMature() && crop.isFertile() && SeedHelper.getSeedTier(crop.seed, crop.seedMeta) < 4);
			} else if (ModIntegration.LoadedMods.magicalCrops && ConfigurationHandler.integration_allowMagicFertiliser && fertilizer.getItem() == Item.itemRegistry.getObject("magicalcrops:magicalcrops_MagicalCropFertilizer")) {
				return crop.hasPlant() && !crop.isMature() && crop.isFertile();
			}
		}
		return false;
	}

	@Override
	public boolean applyFertilizer(World world, int x, int y, int z, ItemStack fertilizer) {
		if (world.isRemote || !isValidFertilizer(world, x, y, z, fertilizer)) {
			return false;
		}
		TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
		if (fertilizer.getItem() == net.minecraft.init.Items.dye && fertilizer.getItemDamage() == 15) {
			Blocks.blockCrop.func_149853_b(world, random, x, y, z);
			fertilizer.stackSize--;
			world.playAuxSFX(2005, x, y, z, 0);
			return true;
		} else if (ModIntegration.LoadedMods.magicalCrops && ConfigurationHandler.integration_allowMagicFertiliser && fertilizer.getItem() == Item.itemRegistry.getObject("magicalcrops:magicalcrops_MagicalCropFertilizer")) {
			if (ConfigurationHandler.integration_instantMagicFertiliser)  {
				world.setBlockMetadataWithNotify(x, y, z, 7, 2);
			} else {
				Blocks.blockCrop.func_149853_b(world, random, x, y, z);
			}
			fertilizer.stackSize--;
			world.playAuxSFX(2005, x, y, z, 0);
			return true;
		}
		return false;
	}

}
