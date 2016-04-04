package com.infinityraider.agricraft.client.renderers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public interface IIconCache extends IResourceManagerReloadListener {
    /** This registers an IBlockState to the cache, when the cache is initialized, it will find and cache all TextureAtlasSprites for this state */
    void addBlockStateToCache(IBlockState state);

    /**
     * This queries a TextureAtlasSprite from an IBlockState instance which has been cached previously
     * This will return null if there is no entry for this state in the cache
     */
    List<TextureAtlasSprite> queryIcons(IBlockState state);

    /**
     * This queries a TextureAtlasSprite from an IBlockState instance
     * If there is an entry for this state in the cache, it will do the same as the queryIcons method
     * If there is no entry for this state in the cache, it will try to retrieve it from json files, this is relatively slow.
     */
    List<TextureAtlasSprite> retrieveIcons(IBlockState state);
}
