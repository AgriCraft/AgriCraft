/*
 */
package com.infinityraider.agricraft.renderers.dynmodels;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

/**
 *
 * @author RlonRyan
 */
public class AgriCraftBakedItemModel implements IBakedModel {
	
	private final List<BakedQuad> quads;
	private final TextureAtlasSprite particle;
	private final boolean isGui3d;

	public AgriCraftBakedItemModel(List<BakedQuad> quads, TextureAtlasSprite particle, boolean isGui3d) {
		this.quads = quads;
		this.particle = particle;
		this.isGui3d = isGui3d;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState ibs, EnumFacing ef, long l) {
		return quads;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return isGui3d;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return particle;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;
	}
};
