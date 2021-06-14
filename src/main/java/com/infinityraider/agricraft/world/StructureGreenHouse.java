package com.infinityraider.agricraft.world;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.init.AgriBlocks;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import com.infinityraider.agricraft.tiles.analyzer.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.utility.WorldGenerationHelper;
import net.minecraft.block.BlockLog;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static net.minecraft.block.BlockLog.LOG_AXIS;

public class StructureGreenHouse extends StructureVillagePieces.House1 {
    //structure dimensions
    private static final int xSize = 17;
    private static final int ySize = 10;
    private static final int zSize = 11;
    private int averageGroundLevel = -1;

    public StructureGreenHouse() {}

    public StructureGreenHouse(StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox p_i45571_4_, EnumFacing facing) {
        super();
        this.setCoordBaseMode(facing);
        this.boundingBox = p_i45571_4_;
    }

    public static StructureGreenHouse buildComponent(StructureVillagePieces.Start villagePiece, List pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
        StructureBoundingBox boundingBox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, xSize, ySize, zSize, facing);
        return (canVillageGoDeeper(boundingBox)) && (StructureComponent.findIntersecting(pieces, boundingBox) == null)?new StructureGreenHouse(villagePiece, p5, random, boundingBox, facing) : null;
    }

    @Override
    public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        //level off ground
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.averageGroundLevel < 0) {
                return true;
            }
            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 7, 0);
        }

        //cobblestone base
        this.fillWithBlocks(worldIn, boundingBox, 0, 0, 0, xSize - 1, 0, zSize - 1, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);   //args: (worldIn, boundingBox, minX, minY, MinZ, maxX, maxY, maxZ, placeBlock, replaceBlock, doReplace)

        //ring of gra2vel1
        this.fillWithBlocks(worldIn, boundingBox, 0, 1, 0, xSize - 1, 1, 0, Blocks.GRAVEL.getDefaultState(), Blocks.GRAVEL.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 0, 1, 0, 0, 1, zSize - 1, Blocks.GRAVEL.getDefaultState(), Blocks.GRAVEL.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 0, 1, zSize - 1, xSize - 1, 1, zSize - 1, Blocks.GRAVEL.getDefaultState(), Blocks.GRAVEL.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, xSize - 1, 1, 0, xSize - 1, 1, zSize - 1, Blocks.GRAVEL.getDefaultState(), Blocks.GRAVEL.getDefaultState(), false);

        //cobble foundations
        this.fillWithBlocks(worldIn, boundingBox, 1, 1, 1, 1, 1, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 1, 1, 6, 1, 1, 9, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 15, 1, 1, 15, 1, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 15, 1, 6, 15, 1, 9, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(),2, 1, 1, boundingBox);
        this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(),8, 1, 1, boundingBox);
        this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(),14, 1, 5, boundingBox);
        this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(),2, 1, 9, boundingBox);
        this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(),8, 1, 9, boundingBox);
        this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(),14, 1, 9, boundingBox);

        //place slabs
        this.setBlockState(worldIn, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), 1, 1, 5, boundingBox);
        this.setBlockState(worldIn, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), 15, 1, 5, boundingBox);

        this.fillWithBlocks(worldIn, boundingBox, 2, 1, 2, 2, 1, 8, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), Blocks.DOUBLE_STONE_SLAB.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 8, 1, 2, 8, 1, 8, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), Blocks.DOUBLE_STONE_SLAB.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 14, 1, 2, 14, 1, 8, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), Blocks.DOUBLE_STONE_SLAB.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 2, 1, 2, 14, 1, 2, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), Blocks.DOUBLE_STONE_SLAB.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 2, 1, 8, 14, 1, 8, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), Blocks.DOUBLE_STONE_SLAB.getDefaultState(), false);
        //place water
        this.fillWithBlocks(worldIn, boundingBox, 3, 1, 1, 7, 1, 1, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 9, 1, 1, 10, 1, 1, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 3, 1, 9, 7, 1, 9, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 9, 1, 9, 13, 1, 9, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);
        this.setBlockState(worldIn, Blocks.WATER.getDefaultState(), 13, 1, 6, boundingBox);

        //place farmland
        this.fillWithBlocks(worldIn, boundingBox, 3, 1, 3, 7, 1, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 9, 1, 3, 13, 1, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
        //place standing logs
        this.fillWithBlocks(worldIn, boundingBox, 1, 2, 1, 1, 6, 1, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 8, 2, 1, 8, 6, 1, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 15, 2, 1, 15, 6, 1, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 1, 2, 4, 1, 5, 4, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 15, 2, 4, 15, 5, 4, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 1, 2, 6, 1, 5, 6, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 15, 2, 6, 15, 5, 6, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 1, 2, 9, 1, 6, 9, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 8, 2, 9, 8, 6, 9, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 15, 2, 9, 15, 6, 9, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);

        //logs along x-axis
        this.fillWithBlocks(worldIn, boundingBox, 2, 6, 1, 7, 6, 1, Blocks.LOG.getDefaultState().withProperty(LOG_AXIS, BlockLog.EnumAxis.X), Blocks.LOG.getDefaultState().withProperty(LOG_AXIS, BlockLog.EnumAxis.X), false);
        this.fillWithBlocks(worldIn, boundingBox, 9, 6, 1, 14, 6, 1, Blocks.LOG.getDefaultState().withProperty(LOG_AXIS, BlockLog.EnumAxis.X), Blocks.LOG.getDefaultState().withProperty(LOG_AXIS, BlockLog.EnumAxis.X), false);
        this.fillWithBlocks(worldIn, boundingBox, 2, 6, 9, 7, 6, 9, Blocks.LOG.getDefaultState().withProperty(LOG_AXIS, BlockLog.EnumAxis.X), Blocks.LOG.getDefaultState().withProperty(LOG_AXIS, BlockLog.EnumAxis.X), false);
        this.fillWithBlocks(worldIn, boundingBox, 9, 6, 9, 14, 6, 9, Blocks.LOG.getDefaultState().withProperty(LOG_AXIS, BlockLog.EnumAxis.X), Blocks.LOG.getDefaultState().withProperty(LOG_AXIS, BlockLog.EnumAxis.X), false);
        //logs along z-axis
        this.fillWithBlocks(worldIn, boundingBox, 1, 6, 2, 1, 6, 8, Blocks.LOG.getDefaultState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Z), Blocks.LOG.getDefaultState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Z), false);
        this.fillWithBlocks(worldIn, boundingBox, 8, 6, 2, 8, 6, 8, Blocks.LOG.getDefaultState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Z), Blocks.LOG.getDefaultState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Z), false);
        this.fillWithBlocks(worldIn, boundingBox, 15, 6, 2, 15, 6, 8, Blocks.LOG.getDefaultState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Z), Blocks.LOG.getDefaultState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Z), false);
        this.setBlockState(worldIn, Blocks.LOG.getDefaultState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Z), 1, 4, 5, boundingBox); // ???
        this.setBlockState(worldIn, Blocks.LOG.getDefaultState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Z), 15, 4, 5, boundingBox);
        //cobble walls
        this.fillWithBlocks(worldIn, boundingBox, 2, 2, 1, 7, 2, 1, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 9, 2, 1, 14, 2, 1, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 1, 2, 2, 1, 2, 3, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 1, 2, 7, 1, 2, 8, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 15, 2, 2, 15, 2, 3, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 15, 2, 7, 15, 2, 8, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 2, 2, 9, 7, 2, 9, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 9, 2, 9, 14, 2, 9, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        //place glass
        this.fillWithBlocks(worldIn, boundingBox, 1, 3, 2, 1, 5, 3, Blocks.GLASS.getDefaultState(), Blocks.GLASS.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 1, 3, 7, 1, 5, 8, Blocks.GLASS.getDefaultState(), Blocks.GLASS.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 15, 3, 2, 15, 5, 3, Blocks.GLASS.getDefaultState(), Blocks.GLASS.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 15, 3, 7, 15, 5, 8, Blocks.GLASS.getDefaultState(), Blocks.GLASS.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 2, 3, 1, 7, 5, 1, Blocks.GLASS.getDefaultState(), Blocks.GLASS.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 9, 3, 1, 14, 5, 1, Blocks.GLASS.getDefaultState(), Blocks.GLASS.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 2, 3, 9, 7, 5, 9, Blocks.GLASS.getDefaultState(), Blocks.GLASS.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 9, 3, 9, 14, 5, 9, Blocks.GLASS.getDefaultState(), Blocks.GLASS.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 2, 6, 2, 7, 6, 8, Blocks.GLASS.getDefaultState(), Blocks.GLASS.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 9, 6, 2, 14, 6, 8, Blocks.GLASS.getDefaultState(), Blocks.GLASS.getDefaultState(), false);
        this.setBlockState(worldIn, Blocks.GLASS.getDefaultState(), 1, 5, 5, boundingBox);
        this.setBlockState(worldIn, Blocks.GLASS.getDefaultState(), 15, 5, 5, boundingBox);
//        //place doors
        this.setBlockState(worldIn, Blocks.OAK_DOOR.getStateFromMeta(0),1, 2, 5, boundingBox);
        this.setBlockState(worldIn, Blocks.OAK_DOOR.getStateFromMeta(8),1, 3, 5, boundingBox);
        this.setBlockState(worldIn, Blocks.OAK_DOOR.getStateFromMeta(2),15, 2, 5, boundingBox);
        this.setBlockState(worldIn, Blocks.OAK_DOOR.getStateFromMeta(8),15, 3, 5, boundingBox);
        //place air blocks
        this.fillWithBlocks(worldIn, boundingBox, 0, 2, 0, 0, 9, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 16, 2, 0, 16, 9, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 0, 2, 0, 16, 9, 0, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 0, 2, 10, 16, 9, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 2, 2, 2, 14, 5, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithBlocks(worldIn, boundingBox, 1, 7, 1, 14, 9, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);

        //place torches
        this.placeTorch(worldIn, EnumFacing.WEST, 0, 4, 1, boundingBox);
        this.placeTorch(worldIn, EnumFacing.WEST, 0, 4, 4, boundingBox);
        this.placeTorch(worldIn, EnumFacing.WEST, 0, 4, 6, boundingBox);
        this.placeTorch(worldIn, EnumFacing.WEST, 0, 4, 9, boundingBox);
        this.placeTorch(worldIn, EnumFacing.WEST, 14, 4, 4, boundingBox);
        this.placeTorch(worldIn, EnumFacing.WEST, 14, 4, 6, boundingBox);

        this.placeTorch(worldIn, EnumFacing.SOUTH, 1, 4, 0, boundingBox);
        this.placeTorch(worldIn, EnumFacing.SOUTH, 8, 4, 0, boundingBox);
        this.placeTorch(worldIn, EnumFacing.SOUTH, 15, 4, 0, boundingBox);
        this.placeTorch(worldIn, EnumFacing.SOUTH, 8, 4, 8, boundingBox);

        this.placeTorch(worldIn, EnumFacing.EAST, 16, 4, 1, boundingBox);
        this.placeTorch(worldIn, EnumFacing.EAST, 16, 4, 4, boundingBox);
        this.placeTorch(worldIn, EnumFacing.EAST, 16, 4, 6, boundingBox);
        this.placeTorch(worldIn, EnumFacing.EAST, 16, 4, 9, boundingBox);
        this.placeTorch(worldIn, EnumFacing.EAST, 2, 4, 4, boundingBox);
        this.placeTorch(worldIn, EnumFacing.EAST, 2, 4, 6, boundingBox);

        this.placeTorch(worldIn, EnumFacing.NORTH, 1, 4, 10, boundingBox);
        this.placeTorch(worldIn, EnumFacing.NORTH, 8, 4, 10, boundingBox);
        this.placeTorch(worldIn, EnumFacing.NORTH, 15, 4, 10, boundingBox);
        this.placeTorch(worldIn, EnumFacing.NORTH, 8, 4, 2, boundingBox);

        Random rnd = new Random();
        List<IAgriPlant> plants = this.getPlantPool();

        for(int x=3;x<=7;x++) {
            for(int z=3;z<=7;z++) {
                this.generateStructureCrop(worldIn, boundingBox, x, 2, z, (z%2==0 && x%2==0) || (x==5 &&z==5), rnd, plants);
            }
        }
        for(int x=9;x<=13;x++) {
            for(int z=3;z<=7;z++) {
                this.generateStructureCrop(worldIn, boundingBox, x, 2, z, (z % 2 == 0 && x % 2 == 0) || (x == 11 && z == 5), rnd, plants);
            }
        }

        //TODO: SpawnVillager
        return true;
    }

    protected boolean generateStructureCrop(World world, StructureBoundingBox boundingBox, int x, int y, int z, boolean crosscrop, Random rnd, List<IAgriPlant> plants) {
        int xCoord = this.getXWithOffset(x, z);
        int yCoord = this.getYWithOffset(y);
        int zCoord = this.getZWithOffset(x, z);
        if (boundingBox.isVecInside(new Vec3i(xCoord, yCoord, zCoord))) {
            world.setBlockState(new BlockPos(xCoord, yCoord, zCoord), AgriBlocks.getInstance().CROP.getDefaultState(), 2);
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(new BlockPos(xCoord, yCoord, zCoord));
            if (crop!=null) {
                if(crosscrop) {
                    crop.setCrossCrop(true);
                }
                else {
                    if(plants.size() > 0) {
                        IAgriStat randomStat = WorldGenerationHelper.getRandomStat(rnd);
                        AgriSeed seed = WorldGenerationHelper.getRandomSeed(rnd, false, plants).withStat(randomStat);
                        crop.setSeed(seed);
                    }
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    //place a seed analyzer
    protected boolean generateStructureSeedAnalyzer(World world, StructureBoundingBox boundingBox, int x, int y, int z, EnumFacing facing) {
        int xCoord = this.getXWithOffset(x, z);
        int yCoord = this.getYWithOffset(y);
        int zCoord = this.getZWithOffset(x, z);
        if (boundingBox.isVecInside(new BlockPos(xCoord, yCoord, zCoord))) {
            world.setBlockState(new BlockPos(xCoord, yCoord, zCoord), AgriBlocks.getInstance().SEED_ANALYZER.getDefaultState());
            TileEntitySeedAnalyzer analyzer = (TileEntitySeedAnalyzer) world.getTileEntity(new BlockPos(xCoord, yCoord, zCoord));
            if (analyzer!=null) {
                if(facing!=null) {
                    analyzer.setOrientation(facing);
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    protected List<IAgriPlant> getPlantPool() {
        return AgriApi.getPlantRegistry().stream()
                .filter(plant -> plant.getTier() <= AgriCraftConfig.greenhousePlantTierLimit)
                .collect(Collectors.toList());
    }

    //TODO: spawnVillagers
}
