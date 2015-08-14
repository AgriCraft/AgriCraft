package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockGrate;
import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityGrate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockGrate extends BlockCustomWood {
    @Override
    protected String getTileEntityName() {
        return Names.Objects.grate;
    }

    @Override
    public RenderBlockBase getRenderer() {
        return new RenderBlockGrate();
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.grate;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityGrate();
    }
}
