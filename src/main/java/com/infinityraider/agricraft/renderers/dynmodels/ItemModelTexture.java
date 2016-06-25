/*
 * 
 */
package com.infinityraider.agricraft.renderers.dynmodels;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author RlonRyan
 */
@SideOnly(Side.CLIENT)
public final class ItemModelTexture {
	
	public static final String SEPARATOR = "~";
	public final ResourceLocation texture;
	public final float x1;
	public final float y1;
	public final float x2;
	public final float y2;
	public final float u1;
	public final float v1;
	public final float u2;
	public final float v2;

	public ItemModelTexture(String texture) {
		String[] str = texture.split(SEPARATOR);
		String tex = str.length > 0 ? str[0] : "agricraft:items/debugger";
		tex = tex.contains(":") ? tex : tex.replaceFirst("/", ":");
		this.texture = new ResourceLocation(tex);
		float[] dim = new float[]{0, 0, 16, 16, 0, 0, 16, 16};
		for (int i = 1; i < str.length && i < 9; i++) {
			try {
				dim[i - 1] = Float.parseFloat(str[i]);
			} catch (NumberFormatException e) {
				// Oh well...
			}
		}
		this.x1 = dim[0];
		this.y1 = dim[1];
		this.x2 = dim[2];
		this.y2 = dim[3];
		this.u1 = dim[4];
		this.v1 = dim[5];
		this.u2 = dim[6];
		this.v2 = dim[7];
	}

	public ItemModelTexture(ResourceLocation texture, float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2) {
		this.texture = texture;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.u1 = u1;
		this.v1 = v1;
		this.u2 = u2;
		this.v2 = v2;
	}

	@Override
	public String toString() {
		return texture.toString() + SEPARATOR + x1 + SEPARATOR + y1 + SEPARATOR + x2 + SEPARATOR + y2 + SEPARATOR + u1 + SEPARATOR + v1 + SEPARATOR + u2 + SEPARATOR + v2;
	}
	
}
