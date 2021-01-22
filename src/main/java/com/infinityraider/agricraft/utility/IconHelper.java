package com.infinityraider.agricraft.utility;

import com.agricraft.agricore.core.AgriCore;
import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A utility class for finding and loading icons.
 *
 * Much shorter than the whole Minecraft.get...
 *
 * These methods hopefully should not return null.
 *
 * On a side note, this class appears to be full of magic that was forgotten long ago.
 *
 */
@SideOnly(Side.CLIENT)
public final class IconHelper {

    public static final String EXPANSION_POINT = ":";
    public static final String EXPANSION_BLOCK = ":blocks/";
    public static final String EXPANSION_ITEM = ":items/";

    // So that we don't have to keep hacking...
    private static final Map<String, Deque<String>> FIND_CACHE = new HashMap<>();

    private static final AtomicInteger FAIL_COUNTER = new AtomicInteger();

    private IconHelper() {
        // NOP
    }

    /**
     * A class to allow for safely getting a default icon at any stage of loading.
     */
    private static class DefaultIcon extends TextureAtlasSprite {

        public static final TextureAtlasSprite INSTANCE = new DefaultIcon("missingno");

        private DefaultIcon(String location) {
            super(location);
            this.width = 16;
            this.height = 16;
            this.framesTextureData = new ArrayList<>(1);
            this.framesTextureData.add(new int[][]{TextureUtil.MISSING_TEXTURE_DATA});
        }

    }

    public static TextureAtlasSprite getDefaultIcon() {
        return DefaultIcon.INSTANCE;
    }

    public static TextureAtlasSprite getIcon(final String resourceLocation) {
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(resourceLocation);
        if (!sprite.getIconName().equals("missingno")) {
            return sprite;
        } else {
            final int fail = FAIL_COUNTER.addAndGet(1);
            //AgriCore.getLogger("agricraft").debug("Failed to load Icon: " + resourceLocation);
            //AgriCore.getLogger("agricraft").debug("Icon load failure #" + fail);
            return getDefaultIcon();
        }
    }

    private static TextureAtlasSprite getIcon(final String path, final String expansion) {
        return getIcon(path, expansion, "");
    }

    private static TextureAtlasSprite getIcon(final String path, final String expansion, final String postfix) {
        return getIcon(path.replaceFirst(EXPANSION_POINT, expansion).concat(postfix));
    }

    public static TextureAtlasSprite getIcon(@Nullable final Block block) {
        return Optional.ofNullable(block)
                .map(Block::getRegistryName)
                .map(loc -> getIcon(loc.toString(), EXPANSION_BLOCK))
                .orElseGet(IconHelper::getDefaultIcon);
    }

    public static TextureAtlasSprite getIcon(@Nullable final Item item) {
        return Optional.ofNullable(item)
                .map(Item::getRegistryName)
                .map(loc -> getIcon(loc.toString(), EXPANSION_ITEM))
                .orElseGet(IconHelper::getDefaultIcon);
    }
    
    public static TextureAtlasSprite getIcon(final FluidStack stack) {
        return (stack == null) ? getDefaultIcon() : getIcon(stack.getFluid().getStill(stack).toString());
    }
    
    public static TextureAtlasSprite getIcon(final Fluid fluid) {
        return (fluid == null) ? getDefaultIcon() : getIcon(fluid.getStill().toString());
    }

    public static TextureAtlasSprite getParticleIcon(final ItemStack stack) {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack).getParticleTexture();
    }

    public static TextureAtlasSprite getParticleIcon(final Item item, final int meta) {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(item, meta);
    }

    public static TextureAtlasSprite registerIcon(String texturePath) {
        // Validate parameters.
        Preconditions.checkNotNull(texturePath, "IconHelper cannot register an icon with a null texture path!");
        
        // Delegate to other method.
        return registerIcon(new ResourceLocation(texturePath));
    }
    
    @Nonnull
    public static TextureAtlasSprite registerIcon(@Nonnull ResourceLocation texturePath) {
        // Validate parameters.
        Preconditions.checkNotNull(texturePath, "IconHelper cannot register an icon with a null texture path!");
        
        // The icon to be returned.
        TextureAtlasSprite ret = null;
        
        // Attempt to register the sprite to the MineCraft texture map.
        try {
            ret = Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(texturePath);
        } catch (Exception e) {
            AgriCore.getLogger("agricraft").debug(e.getLocalizedMessage());
        }
        
        // Return the icon, or the default icon in the case that the icon was null.
        if (ret != null) {
            return ret;
        } else {
            return getDefaultIcon();
        }
    }

    /**
     * Pure hack to find icons...
     * 
     * I guess that this should actually be done with an access transformer.
     *
     * @param name
     * @return
     */
    public static Deque<String> findMatches(String name) {
        name = name.toLowerCase();
        if (FIND_CACHE.containsKey(name)) {
            return FIND_CACHE.get(name);
        }
        Deque<String> matches = new ArrayDeque<>();
        try {
            Field f = TextureMap.class.getDeclaredField("mapRegisteredSprites");
            f.setAccessible(true);
            Map<String, TextureAtlasSprite> textureMap = (Map<String, TextureAtlasSprite>) f.get(Minecraft.getMinecraft().getTextureMapBlocks());
            for (String e : textureMap.keySet()) {
                if (e.contains(name)) {
                    matches.add(e);
                }
            }
            if (!FIND_CACHE.isEmpty()) {
                FIND_CACHE.put(name, matches);
            } else {
                matches.add("missingno");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Shoot
            AgriCore.getLogger("agricraft").debug("Something strange is going on with the Minecraft TextureMap!");
        } catch (SecurityException e) {
            AgriCore.getLogger("agricraft").debug("Locked out of TextureMap...");
        }
        return matches;
    }

}
