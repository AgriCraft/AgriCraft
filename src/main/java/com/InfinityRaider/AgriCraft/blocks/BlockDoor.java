package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockDoor;
import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityDoor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDoor extends BlockCustomWood {
    @Override
    protected String getTileEntityName() {
        return Names.Objects.door;
    }

    @Override
    public RenderBlockBase getRenderer() {
        return new RenderBlockDoor();
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.door;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityDoor();
    }
}
