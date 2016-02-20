package com.infinityraider.agricraft.utility.icon;

import com.infinityraider.agricraft.api.v1.IIconRegistrar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * I don't really care for this class...
 * 
 * At least the fact that it is passed around...
 * 
 * The issue is that the icons have to be loaded at the right time, and looking them up all the time might get costly.
 * 
 * In theory a hash map wouldn't be half bad... as the lookup times are quite good and the string key hashes can get cached...
 * 
 */
@SideOnly(Side.CLIENT)
public final class IconRegistrar implements IIconRegistrar {
	
    private static final IconRegistrar INSTANCE = new IconRegistrar();

    private IconRegistrar() {}

    public static IconRegistrar getInstance() {
        return INSTANCE;
    }

    public TextureAtlasSprite registerIcon(String texturePath) {
        try {
            return Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(new ResourceLocation(texturePath));
        } catch(Exception e) {
            return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        }
    }

}
