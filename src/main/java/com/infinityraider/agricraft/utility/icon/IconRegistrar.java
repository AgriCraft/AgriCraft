package com.InfinityRaider.AgriCraft.utility.icon;

import com.InfinityRaider.AgriCraft.api.v1.IIconRegistrar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
