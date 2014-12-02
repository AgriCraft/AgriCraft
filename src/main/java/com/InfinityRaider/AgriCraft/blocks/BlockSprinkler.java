package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.tileentity.TileEntitySprinkler;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSprinkler extends BlockContainer {
    public BlockSprinkler() {
        super(Material.water);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySprinkler();
    }
}
