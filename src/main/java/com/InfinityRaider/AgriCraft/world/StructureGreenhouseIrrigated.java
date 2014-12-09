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

    public StructureGreenhouseIrrigated(StructureVillagePieces.Start villagePiece, int nr, Random rand, StructureBoundingBox structureBoundingBox, int coordBaseMode) {
        this.coordBaseMode = coordBaseMode;
        this.boundingBox = structureBoundingBox;
    }

    //public method to build the component
    public static StructureGreenhouseIrrigated buildComponent(StructureVillagePieces.Start villagePiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5) {
        StructureBoundingBox boundingBox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, xSize, ySize, zSize, p4);
        return (canVillageGoDeeper(boundingBox)) && (StructureComponent.findIntersecting(pieces, boundingBox) == null)?new StructureGreenhouseIrrigated(villagePiece, p5, random, boundingBox, p4) : null;
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
        LogHelper.debug("Trying to generate greenhouse");
        //level off ground
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = getAverageGroundLevel(world, boundingBox);
            if (this.averageGroundLevel < 0) {
                return true;
            }
            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 4, 0);
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
        this.fillWithBlocks(world, boundingBox, 4, 1, 4, 6, 1, 6, Blocks.double_stone_slab, Blocks.double_stone_slab, false);
        //place water
        this.fillWithBlocks(world, boundingBox, 3, 1, 6, 7, 1, 6, Blocks.water, Blocks.water, false);
        this.fillWithBlocks(world, boundingBox, 9, 1, 6, 10, 1, 6, Blocks.water, Blocks.water, false);
        this.fillWithBlocks(world, boundingBox, 3, 1, 14, 7, 1, 14, Blocks.water, Blocks.water, false);
        this.fillWithBlocks(world, boundingBox, 9, 1, 14, 13, 1, 14, Blocks.water, Blocks.water, false);
        this.placeBlockAtCurrentPosition(world, Blocks.water, 0, 13, 1, 6, boundingBox);
        //place farmland
        this.fillWithBlocks(world, boundingBox, 3, 1, 8, 7, 1, 12, Blocks.farmland, Blocks.farmland, false);
        this.fillWithBlocks(world, boundingBox, 9, 1, 8, 13, 1, 12, Blocks.farmland, Blocks.farmland, false);
        //place standing logs

        return true;
    }
}