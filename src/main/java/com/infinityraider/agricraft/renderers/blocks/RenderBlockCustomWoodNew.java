package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.renderers.TessellatorV2;
import com.infinityraider.agricraft.tileentity.TileEntityCustomWood;
import com.infinityraider.agricraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockCustomWoodNew<T extends TileEntityCustomWood> extends RenderBlockAgriCraft {
	
    protected final T teDummy;

    protected RenderBlockCustomWoodNew(Block block, T te, boolean inventory, boolean tesr, boolean isbrh) {
		super(block, te, inventory, tesr, isbrh);
        this.teDummy = te;
    }

	@Override
	protected final void doInventoryRender(TessellatorV2 tess, ItemStack item) {
		this.teDummy.setMaterial(item);
		this.doInventoryRender(tess, item, this.teDummy.getIcon());
	}
	
	protected abstract void doInventoryRender(TessellatorV2 tess, ItemStack item, TextureAtlasSprite matIcon);

	@Override
	protected final void doRenderBlock(TessellatorV2 tess, IBlockAccess world, Block block, IBlockState state, BlockPos pos) {
		final TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityCustomWood) {
			final TileEntityCustomWood cw = (TileEntityCustomWood)te;
			final TextureAtlasSprite matIcon = cw.getIcon();
			doRenderBlock(tess, world, block, state, pos, matIcon, block.colorMultiplier(world, pos));
		} else {
			LogHelper.debug("Bad blocks being passed to CustomWood Renderer!");
		}
	}
	
	protected abstract void doRenderBlock(TessellatorV2 tess, IBlockAccess world, Block block, IBlockState state, BlockPos pos, TextureAtlasSprite matIcon, int cm);
	
}
