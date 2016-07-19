/*
 * 
 */
package com.infinityraider.agricraft.renderers.items;

import com.google.common.base.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import javax.vecmath.Vector4f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author RlonRyan
 */
@SideOnly(Side.CLIENT)
public final class ItemQuadGenerator {

	// Min. Z
	private static final float NORTH_Z_BASE = 7.496f / 16f;
	private static final float SOUTH_Z_BASE = 8.504f / 16f;

	private ItemQuadGenerator() {
	}
	
	public static List<BakedQuad> generateItemQuads(VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, String base, ItemModelTexture... textures) {
		return generateItemQuads(format, bakedTextureGetter, base, Arrays.asList(textures));
	}

	public static List<BakedQuad> generateItemQuads(VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, String base, List<ItemModelTexture> textures) {

		List<BakedQuad> quads = new ArrayList<>();
		
		TRSRTransformation transform = TRSRTransformation.identity();
		TextureAtlasSprite base_icon = bakedTextureGetter.apply(new ResourceLocation(base));

		quads.addAll(ItemLayerModel.getQuadsForSprite(0, base_icon, format, Optional.of(transform)));

		for (ItemModelTexture tex : textures) {
			TextureAtlasSprite icon = bakedTextureGetter.apply(tex.texture);
			quads.add(genQuad(format, transform, tex.x1, tex.y1, tex.x2, tex.y2, tex.u1, tex.v1, tex.u2, tex.v2, NORTH_Z_BASE, icon, EnumFacing.NORTH, 0xffffffff));
			quads.add(genQuad(format, transform, tex.x1, tex.y1, tex.x2, tex.y2, tex.u1, tex.v1, tex.u2, tex.v2, SOUTH_Z_BASE, icon, EnumFacing.SOUTH, 0xffffffff));
		}

		return quads;

	}

	/*
	 * The following is all 'Borrowed' from Forge Code...
	 */
	public static UnpackedBakedQuad genQuad(VertexFormat format, TRSRTransformation transform, float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2, float z, TextureAtlasSprite sprite, EnumFacing facing, int color) {
		u1 = sprite.getInterpolatedU(u1);
		v1 = sprite.getInterpolatedV(v1);
		u2 = sprite.getInterpolatedU(u2);
		v2 = sprite.getInterpolatedV(v2);

		x1 /= 16f;
		y1 /= 16f;
		x2 /= 16f;
		y2 /= 16f;

		float tmp = y1;
		y1 = 1f - y2;
		y2 = 1f - tmp;

		return putQuad(format, transform, facing, color, x1, y1, x2, y2, z, u1, v1, u2, v2);
	}

	private static UnpackedBakedQuad putQuad(VertexFormat format, TRSRTransformation transform, EnumFacing side, int color,
			float x1, float y1, float x2, float y2, float z,
			float u1, float v1, float u2, float v2) {
		side = side.getOpposite();
		UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
		builder.setQuadTint(-1);
		builder.setQuadOrientation(side);

		if (side == EnumFacing.NORTH) {
			putVertex(builder, format, transform, side, x1, y1, z, u1, v2, color);
			putVertex(builder, format, transform, side, x2, y1, z, u2, v2, color);
			putVertex(builder, format, transform, side, x2, y2, z, u2, v1, color);
			putVertex(builder, format, transform, side, x1, y2, z, u1, v1, color);
		} else {
			putVertex(builder, format, transform, side, x1, y1, z, u1, v2, color);
			putVertex(builder, format, transform, side, x1, y2, z, u1, v1, color);
			putVertex(builder, format, transform, side, x2, y2, z, u2, v1, color);
			putVertex(builder, format, transform, side, x2, y1, z, u2, v2, color);
		}
		return builder.build();
	}

	private static void putVertex(UnpackedBakedQuad.Builder builder, VertexFormat format, TRSRTransformation transform, EnumFacing side,
			float x, float y, float z, float u, float v, int color) {
		Vector4f vec = new Vector4f();
		for (int e = 0; e < format.getElementCount(); e++) {
			switch (format.getElement(e).getUsage()) {
				case POSITION:
					if (transform == TRSRTransformation.identity()) {
						builder.put(e, x, y, z, 1);
					} // only apply the transform if it's not identity
					else {
						vec.x = x;
						vec.y = y;
						vec.z = z;
						vec.w = 1;
						transform.getMatrix().transform(vec);
						builder.put(e, vec.x, vec.y, vec.z, vec.w);
					}
					break;
				case COLOR:
					float r = ((color >> 16) & 0xFF) / 255f; // red
					float g = ((color >> 8) & 0xFF) / 255f; // green
					float b = ((color /*>> 0*/) & 0xFF) / 255f; // blue
					float a = ((color >> 24) & 0xFF) / 255f; // alpha
					builder.put(e, r, g, b, a);
					break;
				case UV:
					if (format.getElement(e).getIndex() == 0) {
						builder.put(e, u, v, 0f, 1f);
						break;
					}
				case NORMAL:
					builder.put(e, (float) side.getFrontOffsetX(), (float) side.getFrontOffsetY(), (float) side.getFrontOffsetZ(), 0f);
					break;
				default:
					builder.put(e);
					break;
			}
		}
	}

}
