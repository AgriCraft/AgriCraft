package com.infinityraider.agricraft.renderers.items;

import com.google.common.base.Function;
import com.infinityraider.agricraft.renderers.items.IItemRenderingHandler;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Collection;
import java.util.Collections;

@SideOnly(Side.CLIENT)
public class ItemRenderer implements IModel {

	private final IItemRenderingHandler renderer;

	public ItemRenderer(IItemRenderingHandler renderer) {
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
	public BakedAgriItemSuperModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return new BakedAgriItemSuperModel(format, renderer, bakedTextureGetter);
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

}
