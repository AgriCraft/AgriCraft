package com.InfinityRaider.AgriCraft.utility.icon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class IconRegistrar implements IIconRegistrar {
    private static final IconRegistrar INSTANCE = new IconRegistrar();

    private IconRegistrar() {}

    public static IconRegistrar getInstance() {
        return INSTANCE;
    }

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite registerIcon(String texturePath) {
        try {
            return Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(new ResourceLocation(texturePath));
        } catch(Exception e) {
            return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        }
    }
}
