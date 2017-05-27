/*
 */
package com.infinityraider.agricraft.utility;

import com.infinityraider.agricraft.api.util.FuzzyStack;
import com.infinityraider.agricraft.reference.AgriNBT;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

/**
 * The master registry for the materials a custom wood block can be made of.
 */
public class CustomWoodTypeRegistry {

    public static final CustomWoodType DEFAULT = new CustomWoodType(Blocks.PLANKS, 0);
    public static final FuzzyStack DEFAULT_STACK = new FuzzyStack(DEFAULT.getStack(), false, true);

    private static final Map<String, CustomWoodType> WOOD_TYPES = new HashMap<>();

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
        if (!WOOD_TYPES.isEmpty()) {
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
                .forEach(type -> WOOD_TYPES.put(type.toString(), type));
    }

    public static final Optional<CustomWoodType> getFromStack(ItemStack stack) {
        if (StackHelper.hasKey(stack, AgriNBT.MATERIAL, AgriNBT.MATERIAL_META)) {
            return getFromNbt(stack.getTagCompound());
        } else if (StackHelper.isValid(stack, ItemBlock.class)) {
            final ItemBlock itemBlock = (ItemBlock) stack.getItem();
            return getFromBlockAndMeta(itemBlock.getBlock(), itemBlock.getMetadata(stack));
        } else {
            return Optional.empty();
        }
    }

    public static final Optional<CustomWoodType> getFromNbt(NBTTagCompound tag) {
        // The old code was much too annoying, in terms of log spam.
        if (NBTHelper.hasKey(tag, AgriNBT.MATERIAL, AgriNBT.MATERIAL_META)) {
            return getFromIdAndMeta(tag.getString(AgriNBT.MATERIAL), tag.getInteger(AgriNBT.MATERIAL_META));
        } else {
            return Optional.empty();
        }
    }

    public static final Optional<CustomWoodType> getFromBlockAndMeta(Block block, int meta) {
        return getFromIdAndMeta(block.getRegistryName().toString(), meta);
    }

    public static final Optional<CustomWoodType> getFromIdAndMeta(String id, int meta) {
        return Optional.ofNullable(WOOD_TYPES.get(id + ":" + meta));
    }

    public static final Collection<CustomWoodType> getAllTypes() {
        return WOOD_TYPES.values();
    }

}
