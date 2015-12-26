package com.InfinityRaider.AgriCraft.utility.icon;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IIconRegistrar {
    @SideOnly(Side.CLIENT)
    TextureAtlasSprite registerIcon(String texturePath);
}
