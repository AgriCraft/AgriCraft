package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.renderers.TessellatorV2;
import com.infinityraider.agricraft.tileentity.decoration.TileEntityFenceGate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import static com.infinityraider.agricraft.renderers.RenderUtil.*;

public class RenderBlockFenceGate extends RenderBlockCustomWood<TileEntityFenceGate> {

	public RenderBlockFenceGate() {
		super(AgriCraftBlocks.blockFenceGate, new TileEntityFenceGate(), true, false, true);
	}

	@Override
	protected void doInventoryRender(TessellatorV2 tess, ItemStack item, TextureAtlasSprite matIcon) {
		drawScaledPrism(tess, 7, 5, 0, 9, 16, 2, matIcon);
		drawScaledPrism(tess, 7, 5, 14, 9, 16, 16, matIcon);
		drawScaledPrism(tess, 7, 12, 2, 9, 15, 14, matIcon);
		drawScaledPrism(tess, 7, 6, 2, 9, 9, 14, matIcon);
		drawScaledPrism(tess, 7, 9, 6, 9, 12, 10, matIcon);
	}

	@Override
	protected void doRenderBlock(TessellatorV2 tess, IBlockAccess world, Block block, IBlockState state, BlockPos pos, TextureAtlasSprite matIcon, int cm) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFenceGate) {
			TileEntityFenceGate gate = (TileEntityFenceGate) te;
			TextureAtlasSprite icon = gate.getIcon();
			if (gate.isZAxis()) {
				renderGateZ(tess, gate, icon, cm);
			} else {
				renderGateX(tess, gate, icon, cm);
			}
		}
	}

	private static final void renderGateX(TessellatorV2 tessellator, TileEntityFenceGate gate, TextureAtlasSprite icon, int cm) {
		drawScaledPrism(tessellator, 7, 5, 0, 9, 16, 2, icon, cm);
		drawScaledPrism(tessellator, 7, 5, 14, 9, 16, 16, icon, cm);
		if (!gate.isOpen()) {
			drawScaledPrism(tessellator, 7, 12, 2, 9, 15, 14, icon, cm);
			drawScaledPrism(tessellator, 7, 6, 2, 9, 9, 14, icon, cm);
			drawScaledPrism(tessellator, 7, 9, 6, 9, 12, 10, icon, cm);
		} else if (gate.getOpenDirection() > 0) {
			drawScaledPrism(tessellator, 1, 12, 0, 7, 15, 2, icon, cm);
			drawScaledPrism(tessellator, 1, 12, 14, 7, 15, 16, icon, cm);
			drawScaledPrism(tessellator, 1, 6, 0, 7, 9, 2, icon, cm);
			drawScaledPrism(tessellator, 1, 6, 14, 7, 9, 16, icon, cm);
			drawScaledPrism(tessellator, 1, 9, 0, 3, 12, 2, icon, cm);
			drawScaledPrism(tessellator, 1, 9, 14, 3, 12, 16, icon, cm);
		} else {
			drawScaledPrism(tessellator, 9, 12, 0, 15, 15, 2, icon, cm);
			drawScaledPrism(tessellator, 9, 12, 14, 15, 15, 16, icon, cm);
			drawScaledPrism(tessellator, 9, 6, 0, 15, 9, 2, icon, cm);
			drawScaledPrism(tessellator, 9, 6, 14, 15, 9, 16, icon, cm);
			drawScaledPrism(tessellator, 13, 9, 0, 15, 12, 2, icon, cm);
			drawScaledPrism(tessellator, 13, 9, 14, 15, 12, 16, icon, cm);
		}
	}

	private static final void renderGateZ(TessellatorV2 tessellator, TileEntityFenceGate gate, TextureAtlasSprite icon, int cm) {
		drawScaledPrism(tessellator, 0, 5, 7, 2, 16, 9, icon, cm);
		drawScaledPrism(tessellator, 14, 5, 7, 16, 16, 9, icon, cm);
		if (!gate.isOpen()) {
			drawScaledPrism(tessellator, 2, 12, 7, 14, 15, 9, icon, cm);
			drawScaledPrism(tessellator, 2, 6, 7, 14, 9, 9, icon, cm);
			drawScaledPrism(tessellator, 6, 9, 7, 10, 12, 9, icon, cm);
		} else if (gate.getOpenDirection() > 0) {
			drawScaledPrism(tessellator, 0, 12, 1, 2, 15, 7, icon, cm);
			drawScaledPrism(tessellator, 14, 12, 1, 16, 15, 7, icon, cm);
			drawScaledPrism(tessellator, 0, 6, 1, 2, 9, 7, icon, cm);
			drawScaledPrism(tessellator, 14, 6, 1, 16, 9, 7, icon, cm);
			drawScaledPrism(tessellator, 0, 9, 1, 2, 12, 3, icon, cm);
			drawScaledPrism(tessellator, 14, 9, 1, 16, 12, 3, icon, cm);
		} else {
			drawScaledPrism(tessellator, 0, 12, 9, 2, 15, 15, icon, cm);
			drawScaledPrism(tessellator, 14, 12, 9, 16, 15, 15, icon, cm);
			drawScaledPrism(tessellator, 0, 6, 9, 2, 9, 15, icon, cm);
			drawScaledPrism(tessellator, 14, 6, 9, 16, 9, 15, icon, cm);
			drawScaledPrism(tessellator, 0, 9, 13, 2, 12, 15, icon, cm);
			drawScaledPrism(tessellator, 14, 9, 13, 16, 12, 15, icon, cm);
		}
	}

}
