package com.infinityraider.agricraft.utility;

import com.agricraft.agricore.core.AgriCore;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Class representing possible custom wood types.
 *
 * This class is candidate for a rewrite/cleaning.
 */
public class CustomWoodType {

    private final Block block;
    private final int meta;
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite texture;

    protected CustomWoodType(Block block, int meta) {
        this.block = block;
        this.meta = meta;
    }

    public Block getBlock() {
        return block;
    }

    public int getMeta() {
        return meta;
    }

    public IBlockState getState() {
        return getBlock().getStateFromMeta(getMeta());
    }

    public ItemStack getStack() {
        return new ItemStack(getBlock(), 1, getMeta());
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setString(AgriNBT.MATERIAL, this.getBlock().getRegistryName().toString());
        tag.setInteger(AgriNBT.MATERIAL_META, this.getMeta());
        return tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof CustomWoodType) {
            CustomWoodType other = (CustomWoodType) obj;
            return other.getBlock() == this.getBlock() && other.getMeta() == this.getMeta();
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.block.getRegistryName() + ":" + this.meta;
    }
    
    @SideOnly(Side.CLIENT)
    @Nonnull
    public TextureAtlasSprite getIcon() {
        if (texture == null) {
            try {
                IBlockState state = block.getStateFromMeta(meta);
                texture = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
            } catch (Exception e) {
                AgriCore.getLogger("agricraft").debug("Unable to load texture for custom wood block {0}!", block.getLocalizedName());
                AgriCore.getLogger("agricraft").trace(e);
                texture = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
            }
        }
        return texture;
    }
    
}
