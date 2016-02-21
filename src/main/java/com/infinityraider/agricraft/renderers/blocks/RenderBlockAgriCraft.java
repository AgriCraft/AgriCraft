package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.renderers.*;
import com.infinityraider.agricraft.renderers.renderinghacks.BlockRendererDispatcherWrapped;
import com.infinityraider.agricraft.renderers.renderinghacks.IItemRenderer;
import com.infinityraider.agricraft.renderers.renderinghacks.ISimpleBlockRenderingHandler;
import com.infinityraider.agricraft.tileentity.TileEntityBase;
import com.infinityraider.agricraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockAgriCraft extends TileEntitySpecialRenderer<TileEntityBase> implements ISimpleBlockRenderingHandler, IItemRenderer {

	protected RenderBlockAgriCraft(Block block, TileEntityBase te, boolean inventory, boolean tesr, boolean isbrh) {
		this.registerRenderer(block, te, inventory, tesr, isbrh);
	}

	private void registerRenderer(Block block, TileEntityBase te, boolean inventory, boolean tesr, boolean isbrh) {
		if (tesr && te != null) {
			ClientRegistry.bindTileEntitySpecialRenderer(te.getTileClass(), this);
		}
		if (isbrh) {
			BlockRendererDispatcherWrapped.getInstance().registerBlockRenderingHandler(block, this);
		}
		if (inventory) {
			BlockRendererDispatcherWrapped.getInstance().registerItemRenderingHandler(Item.getItemFromBlock(block), this);
		}
	}

	/* Call from TESR */
	@Override
	public final void renderTileEntityAt(TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage) {
		TessellatorV2 tessellator = TessellatorV2.getInstance(Tessellator.getInstance());
		tessellator.setBrightness(te.getBlockType().getMixedBrightnessForBlock(te.getWorld(), te.getPos()));
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.translate(x, y, z);
		GlStateManager.disableLighting();
		doRotation(tessellator, te);
		tessellator.startDrawingQuads();
		doRenderTileEntity(tessellator, te);
		tessellator.draw();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

	/* Call from ISBRH */
	@Override
	public final boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, BlockPos pos, Block block, IBlockState state, WorldRenderer renderer) {
		final TessellatorV2 tessellator = TessellatorV2.getInstance(renderer);
		tessellator.translate(pos.getX(), pos.getY(), pos.getZ());
		tessellator.setBrightness(block.getMixedBrightnessForBlock(world, pos));
		doRotation(tessellator, world.getTileEntity(pos));
		doRenderBlock(tessellator, world, block, state, pos);
		return true;
	}

	//
	// TODO: WARNING: HACK
	//
	@Override
	@SuppressWarnings("deprecated")
	public final void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
		TessellatorV2 tessellator = TessellatorV2.getInstance(Tessellator.getInstance());
		switch (transformType) {
			case THIRD_PERSON:
				tessellator.scale(0.25, 0.25, 0.25);
				tessellator.rotate(180, 0, 1, 0);
				tessellator.translate(-.5, 0, 0);
				break;
			case FIRST_PERSON:
				tessellator.scale(0.5, 0.5, 0.5);
				tessellator.translate(0, -0.5, 0);
				break;
			default:
				tessellator.scale(0.5, 0.5, 0.5);
				tessellator.translate(-1, -.9, 0);
				break;
		}
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		GL11.glDisable(GL11.GL_LIGHTING);
		tessellator.startDrawingQuads();
		doInventoryRender(tessellator, stack);
		tessellator.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	protected void doInventoryRender(TessellatorV2 tess, ItemStack item) {
		LogHelper.debug("Bad inventory render call: " + this.getClass().getCanonicalName());
	}

	protected void doRenderTileEntity(TessellatorV2 tess, TileEntity te) {
		LogHelper.debug("Bad tile render call: " + this.getClass().getCanonicalName());
	}

	protected void doRenderBlock(TessellatorV2 tess, IBlockAccess world, Block block, IBlockState state, BlockPos pos) {
		LogHelper.debug("Bad block render call: " + this.getClass().getCanonicalName());
	}

	//HELPER METHODS
	//--------------
	@Override
	public final boolean shouldRender3D(ItemStack stack) {
		return true;
	}

	private static void doRotation(TessellatorV2 tess, TileEntity te) {
		if (te instanceof TileEntityBase) {
			final TileEntityBase tb = (TileEntityBase) te;
			if (tb.isRotatable()) {
				tess.rotateBlock(tb.getOrientation());
			}
		}
	}

}
