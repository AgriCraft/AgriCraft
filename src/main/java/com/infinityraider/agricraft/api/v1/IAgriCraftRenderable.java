package com.infinityraider.agricraft.api.v1;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Blocks or Items with implementing this interface will have the registerIcons method called during TextureStitchEvent
 */
public interface IAgriCraftRenderable {
    /**
     * Called during TextureStitchEvent, only on the client
     * @param iconRegistrar helper object to register TextureAtlasSprites
     */
    @SideOnly(Side.CLIENT)
    void registerIcons(IIconRegistrar iconRegistrar);
	
	@SideOnly(Side.CLIENT)
    public TextureAtlasSprite getIcon();
	
}
