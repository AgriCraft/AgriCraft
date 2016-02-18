package com.infinityraider.agricraft.utility.icon;

import com.infinityraider.agricraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * A utility class for finding and loading icons.
 *
 * Much shorter than the whole Minecraft.get...
 *
 * These methods hopefully should not return null.
 *
 * @author RlonRyan
 */
public final class IconUtil {

	private IconUtil() {
		// NOP
	}

	public static TextureAtlasSprite getDefaultIcon() {
		return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
	}

	public static TextureAtlasSprite getIcon(final String resourceLocation) {
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(resourceLocation);
	}

	public static TextureAtlasSprite getIcon(final Block block) {
		return (block == null) ? getDefaultIcon() : getIcon(block.getRegistryName().replaceFirst(":", ":blocks/"));
	}

	public static TextureAtlasSprite getIcon(final Item item) {
		return (item == null) ? getDefaultIcon() : getIcon(item.getRegistryName().replaceFirst(":", ":items/"));
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
        } catch(Exception e) {
			LogHelper.debug(e.getLocalizedMessage());
            return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        }
    }

}
