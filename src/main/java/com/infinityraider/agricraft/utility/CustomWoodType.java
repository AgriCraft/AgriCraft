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

    /**
     * The default MATERIAL to use. Currently is WOOD planks.
     */
    @Nonnull
    public static final Block DEFAULT_MATERIAL = Blocks.PLANKS;

    /**
     * The default metadata to use. Currently is set to Oak(0) for Planks.
     */
    public static final int DEFAULT_META = 0;

    private static Map<IBlockState, CustomWoodType> woodTypes = new IdentityHashMap<>();

    public static List<CustomWoodType> getAllTypes() {
        return ImmutableList.copyOf(woodTypes.values());
    }

    public static CustomWoodType getFromBlockAndMeta(Block block, int meta) {
        return woodTypes.containsKey(block.getStateFromMeta(meta)) ? woodTypes.get(block.getStateFromMeta(meta)) : getDefault();
    }

    public static CustomWoodType getDefault() {
        return woodTypes.get(DEFAULT_MATERIAL.getStateFromMeta(DEFAULT_META));
    }

    private final Block block;
    private final int meta;
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite texture;

    private CustomWoodType(Block block, int meta) {
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

    @SideOnly(Side.CLIENT)
    @Nonnull
    public TextureAtlasSprite getIcon() {
        if (texture == null) {
            try {
                IBlockState state = block.getStateFromMeta(meta);
                texture = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
            } catch (Exception e) {
                AgriCore.getLogger("AgriCraft").debug("Unable to load texture for custom wood block {0}!", block.getLocalizedName());
                AgriCore.getLogger("AgriCraft").trace(e);
                texture = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
            }
        }
        return texture;
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

    public static CustomWoodType readFromNBT(NBTTagCompound tag) {
        // The old code was much too annoying, in terms of log spam.
        if (!NBTHelper.hasKey(tag, AgriNBT.MATERIAL_META, AgriNBT.MATERIAL)) {
            return getDefault();
        }
        return getFromNameAndMeta(tag.getString(AgriNBT.MATERIAL), tag.getInteger(AgriNBT.MATERIAL_META));
    }

    public static CustomWoodType getFromNameAndMeta(String name, int meta) {
        Block block = Block.getBlockFromName(name);
        if (block == Blocks.AIR) {
            AgriCore.getLogger("AgriCraft").debug("TECW: Material Defaulted!");
            return getDefault();
        } else {
            return getFromBlockAndMeta(block, meta);
        }
    }

    public static void init() {
        //on the server register every meta as a recipe. The client won't know of this, so it's perfectly ok (don't tell anyone)
        init(block -> IntStream.range(0, 16));
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        init(block -> {
            List<ItemStack> subItems = new ArrayList<>();
            block.getSubItems(block, block.getCreativeTab(), subItems);
            return subItems.stream().mapToInt(ItemStack::getItemDamage);
        });
    }

    private static void init(Function<ItemBlock, IntStream> getItemDamages) {
        if (!woodTypes.isEmpty()) {
            return;
        }
        OreDictionary.getOres("plankWood").stream()
                .filter(plank -> plank.getItem() instanceof ItemBlock)
                .flatMap(plank -> {
                    ItemBlock block = ((ItemBlock) plank.getItem());
                    if (plank.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                        return getItemDamages.apply(block).mapToObj(meta -> new CustomWoodType(block.block, meta));
                    } else {
                        return Stream.of(new CustomWoodType(block.block, plank.getItemDamage()));
                    }
                })
                .forEach(type -> woodTypes.put(type.getState(), type));
    }
}
