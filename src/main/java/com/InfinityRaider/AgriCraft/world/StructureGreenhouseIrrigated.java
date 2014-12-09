package com.InfinityRaider.AgriCraft.world;

import java.util.List;
import java.util.Random;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

public class StructureGreenhouseIrrigated extends StructureVillagePieces.Village {
    //structure dimensions
    private static final int xSize = 17;
    private static final int ySize = 10;
    private static final int zSize = 16;
    //helper fields
    private int averageGroundLevel = -1;
    private boolean chestSpawned;

    public StructureGreenhouseIrrigated(StructureBoundingBox structureBoundingBox, int coordBaseMode) {
        this.coordBaseMode = coordBaseMode;
        this.boundingBox = structureBoundingBox;
    }

    //public method to build the component
    public static StructureGreenhouseIrrigated buildComponent(StructureVillagePieces.Start villagePiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5) {
        StructureBoundingBox boundingBox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, xSize, ySize, zSize, p4);
        return (canVillageGoDeeper(boundingBox)) && (StructureComponent.findIntersecting(pieces, boundingBox) == null)?new StructureGreenhouseIrrigated(boundingBox, p4) : null;
    }

    //get random loot
    private WeightedRandomChestContent[] getLoot() {
        int size = (int) Math.ceil(Math.random()*10);
        WeightedRandomChestContent[] loot = new WeightedRandomChestContent[size];
        for(int i=0;i<size;i++) {
            ItemStack seed = SeedHelper.getRandomSeed();
            loot[i] = new WeightedRandomChestContent(seed.getItem(), seed.getItemDamage(), 1, 3, 85);
        }
        return loot;
    }

    //write to NBT
    @Override
    protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
        super.func_143012_a(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean(Names.chestSpawned, this.chestSpawned);
    }

    //read from NBT
    @Override
    protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
        super.func_143011_b(par1NBTTagCompound);
        this.chestSpawned = par1NBTTagCompound.getBoolean(Names.chestSpawned);
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
        this.fillWithBlocks(world, boundingBox, 0, 0, 0, xSize-1, 0, zSize-1, Blocks.cobblestone, Blocks.cobblestone, false);   //args: (world, boundingBox, minX, minY, MinZ, maxX, maxY, maxZ, placeBlock, replaceBlock, doReplace)
        //ring of gravel
        this.fillWithBlocks(world, boundingBox, 0, 1, 0, xSize-1, 1, 0, Blocks.gravel, Blocks.gravel, false);
        this.fillWithBlocks(world, boundingBox, 0, 1, 0, 0, 1, zSize-1, Blocks.gravel, Blocks.gravel, false);
        this.fillWithBlocks(world, boundingBox, 0, 1, zSize-1, xSize-1, 1, zSize-1, Blocks.gravel, Blocks.gravel, false);
        this.fillWithBlocks(world, boundingBox, xSize-1, 1, 0, xSize-1, 1, zSize-1, Blocks.gravel, Blocks.gravel, false);
        //grass patch
        this.fillWithBlocks(world, boundingBox, 1, 1, 1, 9, 1, 5, Blocks.grass, Blocks.grass, false);
        this.fillWithBlocks(world, boundingBox, 10, 1, 1, 13, 1, 2, Blocks.grass, Blocks.grass, false);
        this.fillWithBlocks(world, boundingBox, 14, 1, 1, 15, 1, 5, Blocks.grass, Blocks.grass, false);
        //cobble foundations
        this.fillWithBlocks(world, boundingBox, 1, 1, 6, 1, 1, 9, Blocks.cobblestone, Blocks.cobblestone, false);
        this.fillWithBlocks(world, boundingBox, 1, 1, 11, 1, 1, 14, Blocks.cobblestone, Blocks.cobblestone, false);
        this.fillWithBlocks(world, boundingBox, 15, 1, 6, 15, 1, 9, Blocks.cobblestone, Blocks.cobblestone, false);
        this.fillWithBlocks(world, boundingBox, 15, 1, 11, 15, 1, 14, Blocks.cobblestone, Blocks.cobblestone, false);
        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 2, 1, 6, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 8, 1, 6, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 14, 1, 6, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 2, 1, 15, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 8, 1, 15, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 14, 1, 15, boundingBox);
        this.fillWithBlocks(world, boundingBox, 10, 1, 3, 10, 1, 5, Blocks.cobblestone, Blocks.cobblestone, false);
        this.fillWithBlocks(world, boundingBox, 13, 1, 3, 13, 1, 5, Blocks.cobblestone, Blocks.cobblestone, false);
        this.fillWithBlocks(world, boundingBox, 11, 1, 3, 12, 1, 3, Blocks.cobblestone, Blocks.cobblestone, false);
        //place slabs
        this.placeBlockAtCurrentPosition(world, Blocks.double_stone_slab, 0, 1, 1, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.double_stone_slab, 0, 15, 1, 10, boundingBox);
        this.fillWithBlocks(world, boundingBox, 2, 1, 7, 2, 1, 13, Blocks.double_stone_slab, Blocks.double_stone_slab, false);
        this.fillWithBlocks(world, boundingBox, 8, 1, 7, 8, 1, 13, Blocks.double_stone_slab, Blocks.double_stone_slab, false);
        this.fillWithBlocks(world, boundingBox, 14, 1, 7, 14, 1, 13, Blocks.double_stone_slab, Blocks.double_stone_slab, false);
        this.fillWithBlocks(world, boundingBox, 2, 1, 7, 14, 1, 7, Blocks.double_stone_slab, Blocks.double_stone_slab, false);
        this.fillWithBlocks(world, boundingBox, 2, 1, 13, 14, 1, 13, Blocks.double_stone_slab, Blocks.double_stone_slab, false);
        this.fillWithBlocks(world, boundingBox, 11, 1, 4, 12, 1, 6, Blocks.double_stone_slab, Blocks.double_stone_slab, false);
        //place water
        this.fillWithBlocks(world, boundingBox, 3, 1, 6, 7, 1, 6, Blocks.water, Blocks.water, false);
        this.fillWithBlocks(world, boundingBox, 9, 1, 6, 10, 1, 6, Blocks.water, Blocks.water, false);
        this.fillWithBlocks(world, boundingBox, 3, 1, 14, 7, 1, 14, Blocks.water, Blocks.water, false);
        this.fillWithBlocks(world, boundingBox, 9, 1, 14, 13, 1, 14, Blocks.water, Blocks.water, false);
        this.placeBlockAtCurrentPosition(world, Blocks.water, 0, 13, 1, 6, boundingBox);
        //place farmland
        this.fillWithMetadataBlocks(world, boundingBox, 3, 1, 8, 7, 1, 12, Blocks.farmland, 7, Blocks.farmland, 7, false);
        this.fillWithMetadataBlocks(world, boundingBox, 9, 1, 8, 13, 1, 12, Blocks.farmland, 7, Blocks.farmland, 7, false);
        //place standing logs
        this.fillWithMetadataBlocks(world, boundingBox, 10, 2, 3, 10, 5, 3, Blocks.log, 0, Blocks.log, 0, false);
        this.fillWithMetadataBlocks(world, boundingBox, 13, 2, 3, 13, 5, 3, Blocks.log, 0, Blocks.log, 0, false);
        this.fillWithMetadataBlocks(world, boundingBox, 1, 2, 6, 1, 6, 6, Blocks.log, 0, Blocks.log, 0, false);
        this.fillWithMetadataBlocks(world, boundingBox, 3, 2, 6, 3, 5, 6, Blocks.log, 0, Blocks.log, 0, false);
        this.fillWithMetadataBlocks(world, boundingBox, 6, 2, 6, 6, 5, 6, Blocks.log, 0, Blocks.log, 0, false);
        this.fillWithMetadataBlocks(world, boundingBox, 8, 2, 6, 8, 6, 6, Blocks.log, 0, Blocks.log, 0, false);
        this.fillWithMetadataBlocks(world, boundingBox, 10, 2, 6, 10, 5, 6, Blocks.log, 0, Blocks.log, 0, false);
        this.fillWithMetadataBlocks(world, boundingBox, 13, 2, 6, 13, 5, 6, Blocks.log, 0, Blocks.log, 0, false);
        this.fillWithMetadataBlocks(world, boundingBox, 15, 2, 6, 15, 6, 6, Blocks.log, 0, Blocks.log, 0, false);
        this.fillWithMetadataBlocks(world, boundingBox, 1, 2, 9, 1, 5, 9, Blocks.log, 0, Blocks.log, 0, false);
        this.fillWithMetadataBlocks(world, boundingBox, 15, 2, 9, 15, 5, 9, Blocks.log, 0, Blocks.log, 0, false);
        this.fillWithMetadataBlocks(world, boundingBox, 1, 2, 11, 1, 5, 11, Blocks.log, 0, Blocks.log, 0, false);
        this.fillWithMetadataBlocks(world, boundingBox, 15, 2, 11, 15, 5, 11, Blocks.log, 0, Blocks.log, 0, false);
        this.fillWithMetadataBlocks(world, boundingBox, 1, 2, 14, 1, 6, 14, Blocks.log, 0, Blocks.log, 0, false);
        this.fillWithMetadataBlocks(world, boundingBox, 8, 2, 14, 8, 6, 14, Blocks.log, 0, Blocks.log, 0, false);
        this.fillWithMetadataBlocks(world, boundingBox, 15, 2, 14, 15, 6, 14, Blocks.log, 0, Blocks.log, 0, false);
        //logs along x-axis
        this.fillWithMetadataBlocks(world, boundingBox, 11, 5, 3, 12, 5, 3, Blocks.log, 4, Blocks.log, 4, false);
        this.fillWithMetadataBlocks(world, boundingBox, 2, 6, 6, 7, 6, 6, Blocks.log, 4, Blocks.log, 4, false);
        this.fillWithMetadataBlocks(world, boundingBox, 9, 6, 6, 14, 6, 6, Blocks.log, 4, Blocks.log, 4, false);
        this.fillWithMetadataBlocks(world, boundingBox, 4, 4, 6, 5, 4, 6, Blocks.log, 4, Blocks.log, 4, false);
        this.fillWithMetadataBlocks(world, boundingBox, 11, 4, 6, 12, 4, 6, Blocks.log, 4, Blocks.log, 4, false);
        this.fillWithMetadataBlocks(world, boundingBox, 2, 6, 14, 7, 6, 14, Blocks.log, 4, Blocks.log, 4, false);
        this.fillWithMetadataBlocks(world, boundingBox, 9, 6, 14, 14, 6, 14, Blocks.log, 4, Blocks.log, 4, false);
        //logs along z-axis
        this.fillWithMetadataBlocks(world, boundingBox, 1, 6, 7, 1, 6, 13, Blocks.log, 8, Blocks.log, 8, false);
        this.fillWithMetadataBlocks(world, boundingBox, 8, 6, 7, 8, 6, 13, Blocks.log, 8, Blocks.log, 8, false);
        this.fillWithMetadataBlocks(world, boundingBox, 15, 6, 7, 15, 6, 13, Blocks.log, 8, Blocks.log, 8, false);
        this.fillWithMetadataBlocks(world, boundingBox, 10, 5, 4, 10, 5, 5, Blocks.log, 8, Blocks.log, 8, false);
        this.fillWithMetadataBlocks(world, boundingBox, 13, 5, 4, 13, 5, 5, Blocks.log, 8, Blocks.log, 8, false);
        this.placeBlockAtCurrentPosition(world, Blocks.log, 8, 1, 4, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.log, 8, 15, 4, 10, boundingBox);
        //cobble walls
        this.fillWithBlocks(world, boundingBox, 4, 2, 6, 5, 2, 6, Blocks.cobblestone, Blocks.cobblestone, false);
        this.fillWithBlocks(world, boundingBox, 10, 2, 4, 10, 2, 5, Blocks.cobblestone, Blocks.cobblestone, false);
        this.fillWithBlocks(world, boundingBox, 11, 2, 3, 12, 2, 3, Blocks.cobblestone, Blocks.cobblestone, false);
        this.fillWithBlocks(world, boundingBox, 13, 2, 4, 13, 2, 5, Blocks.cobblestone, Blocks.cobblestone, false);
        this.fillWithBlocks(world, boundingBox, 1, 2, 7, 1, 2, 8, Blocks.cobblestone, Blocks.cobblestone, false);
        this.fillWithBlocks(world, boundingBox, 1, 2, 12, 1, 2, 13, Blocks.cobblestone, Blocks.cobblestone, false);
        this.fillWithBlocks(world, boundingBox, 15, 2, 7, 15, 2, 8, Blocks.cobblestone, Blocks.cobblestone, false);
        this.fillWithBlocks(world, boundingBox, 15, 2, 12, 15, 2, 13, Blocks.cobblestone, Blocks.cobblestone, false);
        this.fillWithBlocks(world, boundingBox, 2, 2, 14, 7, 2, 14, Blocks.cobblestone, Blocks.cobblestone, false);
        this.fillWithBlocks(world, boundingBox, 9, 2, 14, 14, 2, 14, Blocks.cobblestone, Blocks.cobblestone, false);
        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 2, 2, 6, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 7, 2, 6, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 9, 2, 6, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 14, 2, 6, boundingBox);
        //place glass
        this.fillWithBlocks(world, boundingBox, 1, 3, 7, 1, 5, 8, Blocks.glass, Blocks.glass, false);
        this.fillWithBlocks(world, boundingBox, 1, 3, 12, 1, 5, 13, Blocks.glass, Blocks.glass, false);
        this.fillWithBlocks(world, boundingBox, 2, 3, 6, 2, 5, 6, Blocks.glass, Blocks.glass, false);
        this.fillWithBlocks(world, boundingBox, 4, 3, 6, 5, 3, 6, Blocks.glass, Blocks.glass, false);
        this.fillWithBlocks(world, boundingBox, 7, 3, 6, 7, 5, 6, Blocks.glass, Blocks.glass, false);
        this.fillWithBlocks(world, boundingBox, 9, 3, 6, 9, 5, 6, Blocks.glass, Blocks.glass, false);
        this.fillWithBlocks(world, boundingBox, 14, 3, 6, 14, 5, 6, Blocks.glass, Blocks.glass, false);
        this.fillWithBlocks(world, boundingBox, 11, 5, 4, 12, 5, 6, Blocks.glass, Blocks.glass, false);
        this.fillWithBlocks(world, boundingBox, 15, 3, 7, 15, 5, 8, Blocks.glass, Blocks.glass, false);
        this.fillWithBlocks(world, boundingBox, 15, 3, 12, 15, 5, 13, Blocks.glass, Blocks.glass, false);
        this.fillWithBlocks(world, boundingBox, 2, 3, 14, 7, 5, 14, Blocks.glass, Blocks.glass, false);
        this.fillWithBlocks(world, boundingBox, 9, 3, 14, 14, 5, 14, Blocks.glass, Blocks.glass, false);
        this.fillWithBlocks(world, boundingBox, 2, 6, 7, 7, 6, 13, Blocks.glass, Blocks.glass, false);
        this.fillWithBlocks(world, boundingBox, 9, 6, 7, 14, 6, 13, Blocks.glass, Blocks.glass, false);
        this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 4, 5, 6, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 1, 5, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 15, 5, 10, boundingBox);
        //place fences
        this.fillWithBlocks(world, boundingBox, 1, 2, 1, 2, 2, 1, Blocks.fence, Blocks.fence, false);
        this.fillWithBlocks(world, boundingBox, 4, 2, 1, 5, 2, 1, Blocks.fence, Blocks.fence, false);
        this.fillWithBlocks(world, boundingBox, 7, 2, 1, 15, 2, 1, Blocks.fence, Blocks.fence, false);
        this.fillWithBlocks(world, boundingBox, 1, 2, 2, 1, 2, 5, Blocks.fence, Blocks.fence, false);
        this.fillWithBlocks(world, boundingBox, 15, 2, 2, 15, 2, 5, Blocks.fence, Blocks.fence, false);
        this.fillWithBlocks(world, boundingBox, 8, 4, 9, 8, 4, 11, Blocks.fence, Blocks.fence, false);
        this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 8, 5, 9, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 8, 5, 11, boundingBox);
        //wooden pillars
        this.fillWithBlocks(world, boundingBox, 3, 2, 1, 3, 4, 1, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(world, boundingBox, 3, 2, 4, 3, 4, 4, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(world, boundingBox, 6, 2, 1, 6, 4, 1, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(world, boundingBox, 6, 2, 4, 6, 4, 4, Blocks.planks, Blocks.planks, false);
        //oak stairs
        this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, 6, 3, 4, 3, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, 6, 6, 4, 3, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, 6, 11, 4, 3, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, 6, 12, 4, 3, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, 4, 5, 4, 1, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, 4, 5, 4, 4, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, 4, 10, 4, 4, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, 4, 10, 4, 5, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, 5, 4, 4, 1, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, 5, 4, 4, 4, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, 5, 13, 4, 4, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, 5, 13, 4, 5, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, 7, 3, 4, 2, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, 7, 6, 4, 2, boundingBox);
        //place doors
        this.placeBlockAtCurrentPosition(world, Blocks.wooden_door, 0, 1, 2, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.wooden_door, 8, 1, 3, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.wooden_door, 2, 15, 2, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.wooden_door, 8, 15, 3, 10, boundingBox);
        //place torches
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 2, 0, 4, 6, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 2, 0, 4, 9, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 2, 0, 4, 11, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 2, 0, 4, 14, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 2, 9, 4, 3, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 2, 14, 4, 9, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 2, 14, 4, 11, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 5, 1, 3, 1, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 5, 15, 3, 1, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 4, 1, 4, 5, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 4, 8, 4, 5, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 4, 10, 4, 2, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 4, 13, 4, 2, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 4, 15, 4, 5, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 4, 8, 4, 13, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 1, 14, 4, 3, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 1, 16, 4, 6, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 1, 16, 4, 9, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 1, 16, 4, 14, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 1, 16, 4, 14, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 1, 2, 4, 9, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 1, 2, 4, 11, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 3, 1, 4, 15, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 3, 8, 4, 15, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 3, 15, 4, 15, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 3, 3, 4, 7, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 3, 6, 4, 7, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 3, 8, 4, 7, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 3, 10, 4, 7, boundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 3, 13, 4, 7, boundingBox);
        //place crops
        this.fillWithBlocks(world, boundingBox, 3, 2, 8, 7, 2, 12, com.InfinityRaider.AgriCraft.init.Blocks.blockCrop, com.InfinityRaider.AgriCraft.init.Blocks.blockCrop, false);
        this.fillWithBlocks(world, boundingBox, 9, 2, 8, 13, 2, 12, com.InfinityRaider.AgriCraft.init.Blocks.blockCrop, com.InfinityRaider.AgriCraft.init.Blocks.blockCrop, false);
        //place water tank
        this.fillWithBlocks(world, boundingBox, 3, 5, 1, 6, 8, 4, com.InfinityRaider.AgriCraft.init.Blocks.blockWaterTank, com.InfinityRaider.AgriCraft.init.Blocks.blockWaterTank, false);
        //place irrigation channels
        this.fillWithBlocks(world, boundingBox, 5, 5, 5, 5, 5, 10, com.InfinityRaider.AgriCraft.init.Blocks.blockWaterChannel, com.InfinityRaider.AgriCraft.init.Blocks.blockWaterChannel, false);
        this.fillWithBlocks(world, boundingBox, 6, 5, 10, 11, 5, 10, com.InfinityRaider.AgriCraft.init.Blocks.blockWaterChannel, com.InfinityRaider.AgriCraft.init.Blocks.blockWaterChannel, false);
        //place sprinklers
        this.placeBlockAtCurrentPosition(world, com.InfinityRaider.AgriCraft.init.Blocks.blockSprinkler, 0, 5, 4, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, com.InfinityRaider.AgriCraft.init.Blocks.blockSprinkler, 0, 11, 4, 10, boundingBox);
        //place seed analyzer
        this.placeBlockAtCurrentPosition(world, com.InfinityRaider.AgriCraft.init.Blocks.seedAnalyzer, 0, 11, 2, 4, boundingBox);
        //place chest
        this.placeBlockAtCurrentPosition(world, Blocks.chest, 3, 12, 2, 4, boundingBox);
        return true;
    }
}