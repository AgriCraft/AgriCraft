/*
 */
package com.infinityraider.agricraft.renderers.items;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import java.util.List;
import javax.vecmath.Matrix4f;

import com.infinityraider.infinitylib.render.DefaultTransforms;
import com.infinityraider.infinitylib.render.item.IItemRenderingHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 *
 * @author RlonRyan
 */
public class BakedAgriItemSuperModel<T extends IItemRenderingHandler> implements IBakedModel, IItemOverriden {

	protected final VertexFormat format;
	protected final T renderer;
	protected final Function<ResourceLocation, TextureAtlasSprite> textures;

	public BakedAgriItemSuperModel(VertexFormat format, T renderer, Function<ResourceLocation, TextureAtlasSprite> textures) {
		this.format = format;
		this.renderer = renderer;
		this.textures = textures;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return ImmutableList.of();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}
	
	public Matrix4f handlePerspective(ItemCameraTransforms.TransformType transform) {
		return DefaultTransforms.getItemMatrix(transform);
	}
	
	@Override
	public final BakedAgriItemModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
		return new BakedAgriItemModel(this, format, textures, world, stack, entity, renderer);
	}

	@Override
	public final ItemOverrideList getOverrides() {
		return new IItemOverriden.Wrapper(this);
	}

}
