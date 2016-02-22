package com.infinityraider.agricraft.api.v1;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Blocks or Items with implementing this interface will have the registerIcons method called during TextureStitchEvent
 */
public interface IAgriCraftRenderable {
    /**
     * Called during TextureStitchEvent, only on the client.
     */
    @SideOnly(Side.CLIENT)
    void registerIcons();
	
	@SideOnly(Side.CLIENT)
    public TextureAtlasSprite getIcon();
	
}
