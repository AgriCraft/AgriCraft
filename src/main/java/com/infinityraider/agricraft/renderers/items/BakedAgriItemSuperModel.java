/*
 */
package com.infinityraider.agricraft.renderers.items;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.renderers.AgriTransform;
import com.infinityraider.agricraft.renderers.items.IItemRenderingHandler;
import java.util.List;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author RlonRyan
 */
public class BakedAgriItemSuperModel implements IBakedModel, IItemOverriden, IPerspectiveAwareModel {

	private final VertexFormat format;
	private final IItemRenderingHandler renderer;
	private final Function<ResourceLocation, TextureAtlasSprite> textures;

	public BakedAgriItemSuperModel(VertexFormat format, IItemRenderingHandler renderer, Function<ResourceLocation, TextureAtlasSprite> textures) {
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
	
	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType transform) {
		return Pair.of(this, AgriTransform.getItemMatrix(transform));
	}
	
	@Override
	public final BakedAgriItemModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
		return new BakedAgriItemModel(format, textures, world, stack, entity, renderer);
	}

	@Override
	public final ItemOverrideList getOverrides() {
		return new IItemOverriden.Wrapper(this);
	}

}
