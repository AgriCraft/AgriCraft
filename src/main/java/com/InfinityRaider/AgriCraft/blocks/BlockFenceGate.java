package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockFenceGate;
import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityFenceGate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockFenceGate extends BlockCustomWood {
    @Override
    protected String getTileEntityName() {
        return Names.Objects.fenceGate;
    }

    @Override
    public RenderBlockBase getRenderer() {
        return new RenderBlockFenceGate();
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.fenceGate;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityFenceGate();
    }
}
