package com.infinityraider.agricraft.world;

import com.infinityraider.agricraft.entity.EntityVillagerFarmer;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.init.WorldGen;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import com.infinityraider.agricraft.tiles.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import com.agricraft.agricore.core.AgriCore;
import net.minecraft.block.BlockFarmland;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import java.util.List;
import java.util.Random;
import com.infinityraider.agricraft.reference.AgriCraftProperties;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;
import com.infinityraider.agricraft.api.v1.IAgriCraftStats;
import com.infinityraider.agricraft.farming.PlantStats;

public class StructureGreenhouse extends StructureVillagePieces.House1 {
	//structure dimensions

	private static final int xSize = 17;
	private static final int ySize = 10;
	private static final int zSize = 11;
	//helper fields
	private int averageGroundLevel = -1;

	public StructureGreenhouse() {
	}

	@SuppressWarnings("unused")
	public StructureGreenhouse(StructureVillagePieces.Start villagePiece, int nr, Random rand, StructureBoundingBox structureBoundingBox, EnumFacing coordBaseMode) {
		super();
		this.func_186164_a(coordBaseMode);
		this.boundingBox = structureBoundingBox;
	}
	//public method to build the component

	public static StructureGreenhouse buildComponent(StructureVillagePieces.Start villagePiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
		StructureBoundingBox boundingBox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, xSize, ySize, zSize, facing);
		return (canVillageGoDeeper(boundingBox)) && (StructureComponent.findIntersecting(pieces, boundingBox) == null) ? new StructureGreenhouse(villagePiece, p5, random, boundingBox, facing) : null;
	}
	//structure generation code

	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox boundingBox) {
		//level off ground
		if (this.averageGroundLevel < 0) {
			this.averageGroundLevel = getAverageGroundLevel(world, boundingBox);
			if (this.averageGroundLevel < 0) {
				return true;
			}
			this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 7, 0);
		}
		//cobblestone base
		this.fillWithBlocks(world, boundingBox, 0, 0, 0, xSize - 1, 0, zSize - 1, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);   //args: (world, boundingBox, minX, minY, MinZ, maxX, maxY, maxZ, placeBlock, replaceBlock, doReplace)
		//ring of gravel
		this.fillWithBlocks(world, boundingBox, 0, 1, 0, xSize - 1, 1, 0, Blocks.gravel.getDefaultState(), Blocks.gravel.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 0, 1, 0, 0, 1, zSize - 1, Blocks.gravel.getDefaultState(), Blocks.gravel.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 0, 1, zSize - 1, xSize - 1, 1, zSize - 1, Blocks.gravel.getDefaultState(), Blocks.gravel.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, xSize - 1, 1, 0, xSize - 1, 1, zSize - 1, Blocks.gravel.getDefaultState(), Blocks.gravel.getDefaultState(), false);
		//cobble foundations
		this.fillWithBlocks(world, boundingBox, 1, 1, 1, 1, 1, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 1, 1, 6, 1, 1, 9, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 15, 1, 1, 15, 1, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 15, 1, 6, 15, 1, 9, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
		this.setBlockState(world, Blocks.cobblestone.getDefaultState(), 2, 1, 1, boundingBox);
		this.setBlockState(world, Blocks.cobblestone.getDefaultState(), 8, 1, 1, boundingBox);
		this.setBlockState(world, Blocks.cobblestone.getDefaultState(), 14, 1, 5, boundingBox);
		this.setBlockState(world, Blocks.cobblestone.getDefaultState(), 2, 1, 9, boundingBox);
		this.setBlockState(world, Blocks.cobblestone.getDefaultState(), 8, 1, 9, boundingBox);
		this.setBlockState(world, Blocks.cobblestone.getDefaultState(), 14, 1, 9, boundingBox);
		//place slabs
		this.setBlockState(world, Blocks.double_stone_slab.getDefaultState(), 1, 1, 5, boundingBox);
		this.setBlockState(world, Blocks.double_stone_slab.getDefaultState(), 15, 1, 5, boundingBox);
		this.fillWithBlocks(world, boundingBox, 2, 1, 2, 2, 1, 8, Blocks.double_stone_slab.getDefaultState(), Blocks.double_stone_slab.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 8, 1, 2, 8, 1, 8, Blocks.double_stone_slab.getDefaultState(), Blocks.double_stone_slab.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 14, 1, 2, 14, 1, 8, Blocks.double_stone_slab.getDefaultState(), Blocks.double_stone_slab.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 2, 1, 2, 14, 1, 2, Blocks.double_stone_slab.getDefaultState(), Blocks.double_stone_slab.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 2, 1, 8, 14, 1, 8, Blocks.double_stone_slab.getDefaultState(), Blocks.double_stone_slab.getDefaultState(), false);
		//place water
		this.fillWithBlocks(world, boundingBox, 3, 1, 1, 7, 1, 1, Blocks.water.getDefaultState(), Blocks.water.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 9, 1, 1, 10, 1, 1, Blocks.water.getDefaultState(), Blocks.water.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 3, 1, 9, 7, 1, 9, Blocks.water.getDefaultState(), Blocks.water.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 9, 1, 9, 13, 1, 9, Blocks.water.getDefaultState(), Blocks.water.getDefaultState(), false);
		this.setBlockState(world, Blocks.water.getDefaultState(), 13, 1, 6, boundingBox);
		//place farmland
		this.fillWithBlocks(world, boundingBox, 3, 1, 3, 7, 1, 7, Blocks.farmland.getDefaultState().withProperty(BlockFarmland.MOISTURE, 7), Blocks.farmland.getDefaultState().withProperty(BlockFarmland.MOISTURE, 7), false);
		this.fillWithBlocks(world, boundingBox, 9, 1, 3, 13, 1, 7, Blocks.farmland.getDefaultState().withProperty(BlockFarmland.MOISTURE, 7), Blocks.farmland.getDefaultState().withProperty(BlockFarmland.MOISTURE, 7), false);
		//place standing logs
		this.fillWithBlocks(world, boundingBox, 1, 2, 1, 1, 6, 1, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 8, 2, 1, 8, 6, 1, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 15, 2, 1, 15, 6, 1, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 1, 2, 4, 1, 5, 4, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 15, 2, 4, 15, 5, 4, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 1, 2, 6, 1, 5, 6, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 15, 2, 6, 15, 5, 6, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 1, 2, 9, 1, 6, 9, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 8, 2, 9, 8, 6, 9, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 15, 2, 9, 15, 6, 9, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
		//logs along x-axis
		this.fillWithBlocks(world, boundingBox, 2, 6, 1, 7, 6, 1, Blocks.log.getStateFromMeta(4), Blocks.log.getStateFromMeta(4), false);
		this.fillWithBlocks(world, boundingBox, 9, 6, 1, 14, 6, 1, Blocks.log.getStateFromMeta(4), Blocks.log.getStateFromMeta(4), false);
		this.fillWithBlocks(world, boundingBox, 2, 6, 9, 7, 6, 9, Blocks.log.getStateFromMeta(4), Blocks.log.getStateFromMeta(4), false);
		this.fillWithBlocks(world, boundingBox, 9, 6, 9, 14, 6, 9, Blocks.log.getStateFromMeta(4), Blocks.log.getStateFromMeta(4), false);
		//logs along z-axis
		this.fillWithBlocks(world, boundingBox, 1, 6, 2, 1, 6, 8, Blocks.log.getStateFromMeta(8), Blocks.log.getStateFromMeta(8), false);
		this.fillWithBlocks(world, boundingBox, 8, 6, 2, 8, 6, 8, Blocks.log.getStateFromMeta(8), Blocks.log.getStateFromMeta(8), false);
		this.fillWithBlocks(world, boundingBox, 15, 6, 2, 15, 6, 8, Blocks.log.getStateFromMeta(8), Blocks.log.getStateFromMeta(8), false);
		this.setBlockState(world, Blocks.log.getStateFromMeta(8), 1, 4, 5, boundingBox);
		this.setBlockState(world, Blocks.log.getStateFromMeta(8), 15, 4, 5, boundingBox);
		//cobble walls
		this.fillWithBlocks(world, boundingBox, 2, 2, 1, 7, 2, 1, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 9, 2, 1, 14, 2, 1, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 1, 2, 2, 1, 2, 3, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 1, 2, 7, 1, 2, 8, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 15, 2, 2, 15, 2, 3, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 15, 2, 7, 15, 2, 8, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 2, 2, 9, 7, 2, 9, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 9, 2, 9, 14, 2, 9, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
		//place glass
		this.fillWithBlocks(world, boundingBox, 1, 3, 2, 1, 5, 3, Blocks.glass.getDefaultState(), Blocks.glass.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 1, 3, 7, 1, 5, 8, Blocks.glass.getDefaultState(), Blocks.glass.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 15, 3, 2, 15, 5, 3, Blocks.glass.getDefaultState(), Blocks.glass.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 15, 3, 7, 15, 5, 8, Blocks.glass.getDefaultState(), Blocks.glass.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 2, 3, 1, 7, 5, 1, Blocks.glass.getDefaultState(), Blocks.glass.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 9, 3, 1, 14, 5, 1, Blocks.glass.getDefaultState(), Blocks.glass.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 2, 3, 9, 7, 5, 9, Blocks.glass.getDefaultState(), Blocks.glass.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 9, 3, 9, 14, 5, 9, Blocks.glass.getDefaultState(), Blocks.glass.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 2, 6, 2, 7, 6, 8, Blocks.glass.getDefaultState(), Blocks.glass.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 9, 6, 2, 14, 6, 8, Blocks.glass.getDefaultState(), Blocks.glass.getDefaultState(), false);
		this.setBlockState(world, Blocks.glass.getDefaultState(), 1, 5, 5, boundingBox);
		this.setBlockState(world, Blocks.glass.getDefaultState(), 15, 5, 5, boundingBox);
		//place doors
		this.setBlockState(world, Blocks.oak_door.getStateFromMeta(0), 1, 2, 5, boundingBox);
		this.setBlockState(world, Blocks.oak_door.getStateFromMeta(8), 1, 3, 5, boundingBox);
		this.setBlockState(world, Blocks.oak_door.getStateFromMeta(2), 15, 2, 5, boundingBox);
		this.setBlockState(world, Blocks.oak_door.getStateFromMeta(8), 15, 3, 5, boundingBox);
		//place air blocks
		this.fillWithBlocks(world, boundingBox, 0, 2, 0, 0, 9, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 16, 2, 0, 16, 9, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 0, 2, 0, 16, 9, 0, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 0, 2, 10, 16, 9, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 2, 2, 2, 14, 5, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
		this.fillWithBlocks(world, boundingBox, 1, 7, 1, 14, 9, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
		//place torches
		this.setBlockState(world, Blocks.torch.getDefaultState(), 0, 4, 1, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 0, 4, 4, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 0, 4, 6, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 0, 4, 9, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 14, 4, 4, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 14, 4, 6, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 1, 4, 0, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 8, 4, 0, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 15, 4, 0, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 8, 4, 8, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 16, 4, 1, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 16, 4, 4, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 16, 4, 6, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 16, 4, 9, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 2, 4, 4, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 2, 4, 6, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 1, 4, 10, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 8, 4, 10, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 15, 4, 10, boundingBox);
		this.setBlockState(world, Blocks.torch.getDefaultState(), 8, 4, 2, boundingBox);
		//place crops
		List<IAgriCraftPlant> plants = CropPlantHandler.getPlantsUpToTier(AgriCraftConfig.greenHouseMaxTier);
		for (int x = 3; x <= 7; x++) {
			for (int z = 3; z <= 7; z++) {
				this.generateStructureCrop(world, boundingBox, x, 2, z, (z % 2 == 0 && x % 2 == 0) || (x == 5 && z == 5), plants);
			}
		}
		for (int x = 9; x <= 13; x++) {
			for (int z = 3; z <= 7; z++) {
				this.generateStructureCrop(world, boundingBox, x, 2, z, (z % 2 == 0 && x % 2 == 0) || (x == 11 && z == 5), plants);
			}
		}
		this.spawnVillagers(world, boundingBox, 3, 1, 3, 1);
		return true;
	}

	//place a crop
	protected boolean generateStructureCrop(World world, StructureBoundingBox boundingBox, int x, int y, int z, boolean crosscrop, List<IAgriCraftPlant> plants) {
		int xCoord = this.getXWithOffset(x, z);
		int yCoord = this.getYWithOffset(y);
		int zCoord = this.getZWithOffset(x, z);
		AgriCore.getLogger("AgriCraft").debug("Placing crop at (" + xCoord + "," + yCoord + "," + zCoord + ")");
		if (boundingBox.isVecInside(new Vec3i(xCoord, yCoord, zCoord))) {
			BlockPos pos = new BlockPos(xCoord, yCoord, zCoord);
			world.setBlockState(pos, com.infinityraider.agricraft.init.AgriCraftBlocks.blockCrop.getDefaultState().withProperty(AgriCraftProperties.GROWTHSTAGE, 0), 2);
			TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(pos);
			if (crop != null) {
				if (crosscrop) {
					crop.setCrossCrop(true);
				} else {
					ItemStack seed = CropPlantHandler.getRandomSeed(world.rand, false, plants);
					crop.setPlant(
							new PlantStats(
									(int) Math.ceil(Math.random() * 7),
									(int) Math.ceil(Math.random() * 7),
									(int) Math.ceil(Math.random() * 7),
									false),
							seed.getItem(),
							seed.getItemDamage()
					);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	//place a seed analyzer
	protected boolean generateStructureSeedAnalyzer(World world, StructureBoundingBox boundingBox, int x, int y, int z, AgriForgeDirection direction) {
		int xCoord = this.getXWithOffset(x, z);
		int yCoord = this.getYWithOffset(y);
		int zCoord = this.getZWithOffset(x, z);
		if (boundingBox.isVecInside(new Vec3i(xCoord, yCoord, zCoord))) {
			BlockPos pos = new BlockPos(xCoord, yCoord, zCoord);
			world.setBlockState(pos, com.infinityraider.agricraft.init.AgriCraftBlocks.blockSeedAnalyzer.getDefaultState(), 2);
			TileEntitySeedAnalyzer analyzer = (TileEntitySeedAnalyzer) world.getTileEntity(pos);
			if (analyzer != null) {
				if (direction != null) {
					analyzer.setOrientation(direction);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected int func_180779_c(int spawned, int defaultId) {
		return AgriCraftConfig.villagerEnabled ? WorldGen.getVillagerId() : 0;
	}

	@Override
	protected void spawnVillagers(World world, StructureBoundingBox boundingBox, int x, int y, int z, int limit) {
		if (AgriCraftConfig.villagerEnabled) {
			int nrVillagersSpawned = getNumberOfSpawnedVillagers(world);
			if (nrVillagersSpawned < limit) {
				for (int i1 = nrVillagersSpawned; i1 < limit; ++i1) {
					int j1 = this.getXWithOffset(x + i1, z);
					int k1 = this.getYWithOffset(y);
					int l1 = this.getZWithOffset(x + i1, z);

					if (!boundingBox.isVecInside(new Vec3i(j1, k1, l1))) {
						break;
					}
					++nrVillagersSpawned;
					EntityVillager entityvillager = new EntityVillagerFarmer(world, this.func_180779_c(nrVillagersSpawned, i1));
					entityvillager.setLocationAndAngles((double) j1 + 0.5D, (double) k1 + 1, (double) l1 + 0.5D, 0.0F, 0.0F);
					world.spawnEntityInWorld(entityvillager);
				}
			}
			setNumberOfSpawnedVillagers(world, nrVillagersSpawned);
		}
	}

	//hacky method to find out how many villagers have been spawned
	private int getNumberOfSpawnedVillagers(World world) {
		NBTTagCompound villageTag = new NBTTagCompound();
		this.readStructureBaseNBT(world, villageTag);
		return villageTag.getInteger("VCount");
	}

	//hacky method to update the number of villagers that have been spawned
	private void setNumberOfSpawnedVillagers(World world, int nr) {
		NBTTagCompound villageTag = new NBTTagCompound();
		this.readStructureBaseNBT(world, villageTag);
		villageTag.setInteger("VCount", nr);
		this.writeStructureToNBT(villageTag);
	}
}
