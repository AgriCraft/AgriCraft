package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityDoor;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class RenderBlockDoor extends RenderBlockCustomWood {
    public RenderBlockDoor() {
        super(Blocks.blockDoor, new TileEntityDoor(), true);
    }

    @Override
    protected void renderInInventory(ItemRenderType type, ItemStack item, Object... data) {

    }

    @Override
    protected boolean doWorldRender(Tessellator tessellator, IBlockAccess world, double x, double y, double z, TileEntity tile, Block block, float f, int modelId, RenderBlocks renderer, boolean callFromTESR) {
        return false;
    }

    @Override
    public boolean shouldBehaveAsTESR() {
        return false;
    }

    @Override
    public boolean shouldBehaveAsISBRH() {
        return false;
    }
}
