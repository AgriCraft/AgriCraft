package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.BlockSeedAnalyzer;
import com.infinityraider.agricraft.container.ContainerSeedAnalyzer;
import com.infinityraider.agricraft.handler.config.AgriCraftConfig;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.models.ModelSeedAnalyzer;
import com.infinityraider.agricraft.renderers.models.ModelSeedAnalyzerBook;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tileentity.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.utility.icon.IconUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

/*
 * TODO: Convert to new Renderer.
 */
@SideOnly(Side.CLIENT)
public class RenderSeedAnalyzer extends RenderBlockBase<TileEntitySeedAnalyzer> {
	private final ModelSeedAnalyzer modelSeedAnalyzer;
	private final ModelSeedAnalyzerBook modelBook;

	public RenderSeedAnalyzer(BlockSeedAnalyzer block) {
		super(block, new TileEntitySeedAnalyzer(), true, true, false);
		this.modelSeedAnalyzer = new ModelSeedAnalyzer();
		this.modelBook = new ModelSeedAnalyzerBook();
	}

	@Override
	public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, Block block,
								 @Nullable TileEntitySeedAnalyzer analyzer, boolean dynamicRender, float partialTick, int destroyStage) {
		if (analyzer != null) {
			this.renderModel(tessellator, analyzer);
			if (analyzer.hasSeed() || analyzer.hasTrowel()) {
				renderSeed(tessellator, analyzer);
			}
			tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);
		}
	}

	@Override
	public void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, Block block, @Nullable TileEntitySeedAnalyzer tile,
									 ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {
		if(tile != null) {
			GL11.glPushMatrix();
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			//TODO: fix this
			//this.renderModel(tessellator, tile);
			GL11.glPopMatrix();
		}
	}

	private void renderModel(ITessellator tessellator, TileEntitySeedAnalyzer analyzer) {
		tessellator.draw();
		//render the model
		GL11.glPushMatrix();
		GL11.glTranslatef(0.5F, 1.5F, 0.5F);
		GL11.glRotatef(180, 0F, 0F, 1F);
		switch (analyzer.getOrientation()) {
			case WEST:
				GL11.glRotatef(90, 0F, 1F, 0F);
				break;
			case SOUTH:
				GL11.glRotatef(180, 0F, 1F, 0F);
				break;
			case EAST:
				GL11.glRotatef(270, 0F, 1F, 0F);
				break;
		}
		this.modelSeedAnalyzer.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		if (analyzer.hasJournal() && AgriCraftConfig.renderBookInAnalyzer) {
			this.modelBook.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		}
		GL11.glPopMatrix();
		tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);

	}

	//renders the seed
	private void renderSeed(ITessellator tessellator, TileEntitySeedAnalyzer analyzer) {
		//grab the texture
		ItemStack stack = analyzer.getStackInSlot(ContainerSeedAnalyzer.seedSlotId);
		TextureAtlasSprite icon = IconUtil.getParticleIcon(stack); //TODO: find seed icon
		if (icon == null) {
			return;
		}
		//define rotation angle in function of system time
		float angle = (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);   //credits to Pahimar
		GL11.glPushMatrix();
		//translate to the desired position
		GL11.glTranslated(Constants.UNIT * 8, Constants.UNIT * 4, Constants.UNIT * 8);
		//resize the texture to half the size
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		//rotate the renderer
		GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-8 * Constants.UNIT, 0, 0);

		//TODO: render the seed


		GL11.glTranslatef(8 * Constants.UNIT, 0, 0);
		GL11.glRotatef(-angle, 0.0F, 1.0F, 0.0F);
		GL11.glScalef(2, 2, 2);
		GL11.glPopMatrix();
	}

	@Override
	public TextureAtlasSprite getIcon() {
		return null;
	}
}
