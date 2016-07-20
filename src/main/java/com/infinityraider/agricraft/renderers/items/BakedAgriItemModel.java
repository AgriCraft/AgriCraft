/*
 */
package com.infinityraider.agricraft.renderers.items;

import com.agricraft.agricore.core.AgriCore;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.renderers.AgriTransform;
import com.infinityraider.agricraft.renderers.items.IItemRenderingHandler;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.renderers.tessellation.TessellatorBakedQuad;
import java.util.List;
import javax.vecmath.Matrix4f;
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
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author RlonRyan
 */
public class BakedAgriItemModel implements IBakedModel {
	
	private final VertexFormat format;
	private final Function<ResourceLocation, TextureAtlasSprite> textures;
	private final IItemRenderingHandler renderer;
	private final ItemStack stack;
	private final World world;
	private final EntityLivingBase entity;

	public BakedAgriItemModel(VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> textures, World world, ItemStack stack, EntityLivingBase entity, IItemRenderingHandler renderer) {
		this.format = format;
		this.textures = textures;
		this.world = world;
		this.stack = stack;
		this.entity = entity;
		this.renderer = renderer;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		List<BakedQuad> list;
		if (side == null) {
			final TessellatorBakedQuad tessellator = TessellatorBakedQuad.getInstance();
			tessellator.setTextureFunction(textures);
			tessellator.startDrawingQuads(format);
			this.renderer.renderItem(tessellator, world, stack, entity);
			list = tessellator.getQuads();
			tessellator.draw();
		} else {
			list = ImmutableList.of();
		}
		return list;
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
	public ItemOverrideList getOverrides() {
		return null;
	}
	
}
