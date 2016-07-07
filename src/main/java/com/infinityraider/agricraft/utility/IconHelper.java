package com.infinityraider.agricraft.utility;

import com.agricraft.agricore.core.AgriCore;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
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
public final class IconHelper {
	
	public static final String EXPANSION_POINT = ":";
	public static final String EXPANSION_BLOCK = ":blocks/";
	public static final String EXPANSION_ITEM = ":items/";
	
	// So that we don't have to keep hacking...
	private static final Map<String, Deque<String>> findCache = new HashMap<>();
	
	private static final AtomicInteger failCounter = new AtomicInteger();

	private IconHelper() {
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
			this.framesTextureData.add(new int[][] {TextureUtil.MISSING_TEXTURE_DATA});
		}

	}
	
	public static TextureAtlasSprite getDefaultIcon() {
		return DefaultIcon.instance;
	}

	public static TextureAtlasSprite getIcon(final String resourceLocation) {
		TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(resourceLocation);
		if (!sprite.getIconName().equals("missingno")) {
			return sprite;
		} else {
			final int fail = failCounter.addAndGet(1);
			//AgriCore.getLogger("AgriCraft").debug("Failed to load Icon: " + resourceLocation);
			//AgriCore.getLogger("AgriCraft").debug("Icon load failure #" + fail);
			return getDefaultIcon();
		}
	}
	
	private static TextureAtlasSprite getIcon(final String path, final String expansion) {
		return getIcon(path.replaceFirst(EXPANSION_POINT, expansion));
	}
	
	private static TextureAtlasSprite getIcon(final String path, final String expansion, final String postfix) {
		return getIcon(path.replaceFirst(EXPANSION_POINT, expansion).concat(postfix));
	}

	public static TextureAtlasSprite getIcon(final Block block) {
		return (block == null) ? getDefaultIcon() : getIcon(block.getRegistryName().toString(), EXPANSION_BLOCK);
	}

	public static TextureAtlasSprite getIcon(final Item item) {
		return (item == null) ? getDefaultIcon() : getIcon(item.getRegistryName().toString(), EXPANSION_ITEM);
	}

	public static TextureAtlasSprite getParticleIcon(final ItemStack stack) {
		return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack).getParticleTexture();
	}

	public static TextureAtlasSprite getParticleIcon(final Item item, final int meta) {
		return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(item, meta);
	}

	public static TextureAtlasSprite registerIcon(String texturePath) {
		try {
			return Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(new ResourceLocation(texturePath));
		} catch (Exception e) {
			AgriCore.getLogger("AgriCraft").debug(e.getLocalizedMessage());
			return getDefaultIcon();
		}
	}
	
	/**
	 * Pure hack to find icons...
	 * 
	 * @param name
	 * @return 
	 */
	public static Deque<String> findMatches(String name) {
		name = name.toLowerCase();
		if (findCache.containsKey(name)) {
			return findCache.get(name);
		}
		Deque<String> matches = new ArrayDeque<>();
		try {
			Field f = TextureMap.class.getDeclaredField("mapRegisteredSprites");
			f.setAccessible(true);
			Map<String, TextureAtlasSprite> textureMap = (Map<String, TextureAtlasSprite>)f.get(Minecraft.getMinecraft().getTextureMapBlocks());
			for (String e : textureMap.keySet()) {
				if (e.contains(name)) {
					matches.add(e);
				}
			}
			if (!findCache.isEmpty()) {
				findCache.put(name, matches);
			} else {
				matches.add("missingno");
			}
		} catch (NoSuchFieldException | IllegalAccessException e) {
			// Shoot
		} catch (SecurityException e) {
			AgriCore.getLogger("AgriCraft").debug("Locked out of TextureMap...");
		}
		return matches;
	}

}
