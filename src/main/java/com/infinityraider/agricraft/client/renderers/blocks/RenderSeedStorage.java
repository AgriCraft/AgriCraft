package com.infinityraider.agricraft.client.renderers.blocks;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.client.renderers.RenderUtil;
import com.infinityraider.agricraft.client.renderers.TessellatorV2;
import com.infinityraider.agricraft.tileentity.storage.TileEntitySeedStorage;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import static com.infinityraider.agricraft.client.renderers.RenderUtil.*;
import com.infinityraider.agricraft.utility.icon.BaseIcons;

@SideOnly(Side.CLIENT)
public class RenderSeedStorage extends RenderBlockCustomWood<TileEntitySeedStorage> {

	public RenderSeedStorage() {
		super(AgriCraftBlocks.blockSeedStorage, new TileEntitySeedStorage(), true, true, true);
		this.teDummy.setOrientation(AgriForgeDirection.EAST);
	}

	@Override
	protected void doInventoryRender(TessellatorV2 tess, ItemStack item, TextureAtlasSprite matIcon) {
		renderSides(tess, matIcon, RenderUtil.COLOR_MULTIPLIER_STANDARD);
	}

	@Override
	protected void doRenderBlock(TessellatorV2 tess, IBlockAccess world, Block block, IBlockState state, BlockPos pos, TextureAtlasSprite matIcon, int cm) {
		renderSides(tess, matIcon, cm);
	}

	@Override
	protected void doRenderTileEntity(TessellatorV2 tess, TileEntity te) {
		if (te instanceof TileEntitySeedStorage) {
			TileEntitySeedStorage storage = (TileEntitySeedStorage) te;
			if (storage.hasLockedSeed()) {
				drawSeed(tess, storage.getLockedSeed(), storage.getOrientation());
			}
		}
	}

	private void renderSides(TessellatorV2 tess, TextureAtlasSprite matIcon, int cm) {

		//casing
		drawScaledPrism(tess, 0, 0, 0, 16, 1, 16, matIcon, cm);
		drawScaledPrism(tess, 0, 15, 0, 16, 16, 16, matIcon, cm);
		drawScaledPrism(tess, 0, 1, 0, 1, 15, 16, matIcon, cm);
		drawScaledPrism(tess, 15, 1, 0, 16, 15, 16, matIcon, cm);
		drawScaledPrism(tess, 1, 1, 15, 15, 15, 16, matIcon, cm);

		//drawer
		drawScaledPrism(tess, 1.1F, 1.1F, 1, 14.9F, 14.9F, 2, matIcon, cm);
		drawScaledPrism(tess, 4, 3, 0, 5, 10, 1, matIcon, cm);
		drawScaledPrism(tess, 11, 3, 0, 12, 10, 1, matIcon, cm);
		drawScaledPrism(tess, 4, 10, 0, 12, 11, 1, matIcon, cm);
		drawScaledPrism(tess, 4, 3, 0, 12, 4, 1, matIcon, cm);

		//handle
		drawScaledPrism(tess, 7, 12, 0, 9, 13, 1, BaseIcons.IRON_BLOCK.getIcon(), cm);

		//trace
		addScaledVertexWithUV(tess, 1, 1, 0.99F, 2, 3, matIcon);
		addScaledVertexWithUV(tess, 1, 15, 0.99F, 2, 4, matIcon);
		addScaledVertexWithUV(tess, 1.5F, 15, 0.99F, 3, 4, matIcon);
		addScaledVertexWithUV(tess, 1.5F, 1, 0.99F, 3, 3, matIcon);

		addScaledVertexWithUV(tess, 15, 1, 0.99F, 2, 3, matIcon);
		addScaledVertexWithUV(tess, 14.5F, 1, 0.99F, 3, 3, matIcon);
		addScaledVertexWithUV(tess, 14.5F, 15, 0.99F, 3, 4, matIcon);
		addScaledVertexWithUV(tess, 15, 15, 0.99F, 2, 4, matIcon);

		addScaledVertexWithUV(tess, 15, 14.5F, 0.99F, 2, 3, matIcon);
		addScaledVertexWithUV(tess, 1, 14.5F, 0.99F, 3, 3, matIcon);
		addScaledVertexWithUV(tess, 1, 15, 0.99F, 3, 4, matIcon);
		addScaledVertexWithUV(tess, 15, 15, 0.99F, 2, 4, matIcon);

		addScaledVertexWithUV(tess, 15, 1, 0.99F, 2, 3, matIcon);
		addScaledVertexWithUV(tess, 1, 1, 0.99F, 3, 3, matIcon);
		addScaledVertexWithUV(tess, 1, 1.5F, 0.99F, 3, 4, matIcon);
		addScaledVertexWithUV(tess, 15, 1.5F, 0.99F, 2, 4, matIcon);

		addScaledVertexWithUV(tess, 3.5F, 2.5F, 0.99F, 2, 3, matIcon);
		addScaledVertexWithUV(tess, 3.5F, 11.5F, 0.99F, 2, 4, matIcon);
		addScaledVertexWithUV(tess, 5.5F, 11.5F, 0.99F, 3, 4, matIcon);
		addScaledVertexWithUV(tess, 5.5F, 2.5F, 0.99F, 3, 3, matIcon);

		addScaledVertexWithUV(tess, 10.5F, 2.5F, 0.99F, 2, 3, matIcon);
		addScaledVertexWithUV(tess, 10.5F, 11.5F, 0.99F, 2, 4, matIcon);
		addScaledVertexWithUV(tess, 12.5F, 11.5F, 0.99F, 3, 4, matIcon);
		addScaledVertexWithUV(tess, 12.5F, 2.5F, 0.99F, 3, 3, matIcon);

		addScaledVertexWithUV(tess, 3.5F, 2.5F, 0.99F, 2, 3, matIcon);
		addScaledVertexWithUV(tess, 3.5F, 4.5F, 0.99F, 2, 4, matIcon);
		addScaledVertexWithUV(tess, 12.5F, 4.5F, 0.99F, 3, 4, matIcon);
		addScaledVertexWithUV(tess, 12.5F, 2.5F, 0.99F, 3, 3, matIcon);

		addScaledVertexWithUV(tess, 3.5F, 9.5F, 0.99F, 2, 3, matIcon);
		addScaledVertexWithUV(tess, 3.5F, 11.5F, 0.99F, 2, 4, matIcon);
		addScaledVertexWithUV(tess, 12.5F, 11.5F, 0.99F, 3, 4, matIcon);
		addScaledVertexWithUV(tess, 12.5F, 9.5F, 0.99F, 3, 3, matIcon);
	}

	/**
	 * Render the seed as TESR
	 */
	private void drawSeed(TessellatorV2 tess, ItemStack seed, AgriForgeDirection side) {

		tess.draw();

		double a = 90;

		switch (side) {
			default:
			case NORTH:
				a *= 0;
				break;
			case EAST:
				a *= 1;
				break;
			case SOUTH:
				a *= 2;
				break;
			case WEST:
				a *= 3;
				break;
		}

		GL11.glPushMatrix();
		GL11.glTranslated(.5, .5, .5);
		GL11.glRotated(a, 0, 1, 0);
		GL11.glTranslated(-.5, -.5, -.5);
		GL11.glTranslated(0.5, 0.15, 0);

		EntityItem item = new EntityItem(AgriCraft.proxy.getClientWorld(), 0, 0, 0, seed);
		item.hoverStart = 0;
		Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(item, 0, 0, 0, 0, 0);

		GL11.glPopMatrix();

		tess.startDrawingQuads();

	}

}
