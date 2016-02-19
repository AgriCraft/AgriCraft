package com.infinityraider.agricraft.utility.icon;

import com.infinityraider.agricraft.utility.LogHelper;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A utility class for finding and loading icons.
 *
 * Much shorter than the whole Minecraft.get...
 *
 * These methods hopefully should not return null.
 *
 * @author RlonRyan
 */
@SideOnly(Side.CLIENT)
public final class IconUtil {
	
	public static final String OAK_PLANKS = "minecraft:blocks/planks_oak";

	private IconUtil() {
		// NOP
	}

	/**
	 * A class to allow for safely getting a default icon at any stage of loading.
	 */
	private static class DefaultIcon extends TextureAtlasSprite {

		public static final TextureAtlasSprite instance = new DefaultIcon("missingno");

		private DefaultIcon(String location) {
			super(location);
			this.width = 16;
			this.height = 16;
			this.framesTextureData = new ArrayList<>(1);
			this.framesTextureData.add(new int[][] {TextureUtil.missingTextureData});
		}

	}
	
	public static TextureAtlasSprite getDefaultIcon() {
		return DefaultIcon.instance;
	}

	public static TextureAtlasSprite getIcon() {
		return getDefaultIcon();
	}

	public static TextureAtlasSprite getIcon(final String resourceLocation) {
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(resourceLocation);
	}

	public static TextureAtlasSprite getIcon(final Block block) {
		return (block == null) ? getIcon() : getIcon(block.getRegistryName().replaceFirst(":", ":blocks/"));
	}

	public static TextureAtlasSprite getIcon(final Item item) {
		return (item == null) ? getIcon() : getIcon(item.getRegistryName().replaceFirst(":", ":items/"));
	}

	public static TextureAtlasSprite getItemIcon(final ItemStack stack) {
		return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack).getParticleTexture();
	}

	public static TextureAtlasSprite getItemIcon(final Item item, final int meta) {
		return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(item, 0);
	}

	public static TextureAtlasSprite registerIcon(String texturePath) {
		try {
			return Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(new ResourceLocation(texturePath));
		} catch (Exception e) {
			LogHelper.debug(e.getLocalizedMessage());
			return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		}
	}

}
