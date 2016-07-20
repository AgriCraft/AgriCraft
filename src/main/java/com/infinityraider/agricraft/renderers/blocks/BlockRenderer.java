package com.infinityraider.agricraft.renderers.blocks;

import com.google.common.base.Function;
import com.infinityraider.agricraft.renderers.AgriTransform;
import com.infinityraider.agricraft.renderers.items.BakedAgriItemSuperModel;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.renderers.tessellation.TessellatorVertexBuffer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Matrix4f;
import java.util.Collection;
import java.util.Collections;

@SideOnly(Side.CLIENT)
public class BlockRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> implements IModel {

	private final IBlockRenderingHandler<T> renderer;

	public BlockRenderer(IBlockRenderingHandler<T> renderer) {
		this.renderer = renderer;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return renderer.getAllTextures();
	}

	@Override
	public BakedBlockModel<T> bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return new BakedBlockModel<>(format, renderer, bakedTextureGetter);
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	@Override
	public void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage) {
		ITessellator tessellator = TessellatorVertexBuffer.getInstance();
		World world = te.getWorld();
		BlockPos pos = te.getPos();
		IBlockState state = world.getBlockState(pos);

		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		tessellator.pushMatrix();
		tessellator.translate(x, y, z);
		tessellator.setColorRGBA(255, 255, 255, 255);
		tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);

		this.renderer.renderStatic(tessellator, te, state);
		this.renderer.renderDynamic(tessellator, te, partialTicks, destroyStage);

		//tessellator.popMatrix();
		tessellator.draw();

	}

	public static class BakedBlockModel<T extends TileEntity> extends BakedAgriItemSuperModel<IBlockRenderingHandler<T>> {

		private BakedBlockModel(VertexFormat format, IBlockRenderingHandler<T> renderer, Function<ResourceLocation, TextureAtlasSprite> textures) {
			super(format, renderer, textures);
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return renderer.getIcon();
		}

		@Override
		public Matrix4f handlePerspective(ItemCameraTransforms.TransformType transform) {
			return AgriTransform.getBlockMatrix(transform);
		}

	}

}
