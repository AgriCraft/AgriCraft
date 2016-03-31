package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.tileentity.TileEntityBase;
import com.sun.istack.internal.NotNull;
import net.minecraft.block.Block;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockBase<T extends TileEntityBase> implements IBlockRenderingHandler<T> {
    private final Block block;
    private final T dummy;
    private final boolean inv;
    private final boolean statRender;
    private final boolean dynRender;

    protected RenderBlockBase(Block block, T te, boolean inv, boolean statRender, boolean dynRender) {
        this.block = block;
        this.dummy = te;
        this.inv = inv;
        this.statRender = statRender;
        this.dynRender = dynRender;
    }

    @Override
    @NotNull public T getTileEntity() {
        return dummy;
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public boolean doInventoryRendering() {
        return inv;
    }

    @Override
    public boolean hasDynamicRendering() {
        return dynRender;
    }

    @Override
    public boolean hasStaticRendering() {
        return statRender;
    }
}
