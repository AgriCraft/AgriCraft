package com.infinityraider.agricraft.api.v1;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * An object implementing this interface will be passed to any IconRegisterable during TextureStitchEvent
 */
@SideOnly(Side.CLIENT)
public interface IIconRegistrar {
    /**
     * Method used to register a TextureAtlasSprite
     * @param texturePath texture path pointing to the texture (e.g. modid:blocks/tileExample or modid:items/itemExample)
     * @return the registered TextureAtlasSprite
     */
    TextureAtlasSprite registerIcon(String texturePath);
}
