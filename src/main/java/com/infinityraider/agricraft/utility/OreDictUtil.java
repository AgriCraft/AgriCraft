package com.infinityraider.agricraft.utility;

import com.agricraft.agricore.core.AgriCore;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public final class OreDictUtil {

    public static final String PREFIX_OREDICT = "oredict";

    /**
     * Determines if the given string represents a valid oredict resource entry.
     *
     * @param element the string representation of the oredict entry, should start with {@link PREFIX_OREDICT}.
     * @return {@literal true} if and only if the given string represents a valid oredict entry, {@literal false} otherwise.
     */
    public static final boolean isValidOre(@Nullable String element) {
        // If null or empty return nothing.
        if (element == null || element.isEmpty()) {
            return false;
        }

        // Split the element.
        final String[] parts = element.split(":");

        // If only 1 part, then assume is suffix.
        if (parts.length == 1) {
            return false;
        } else if (parts.length == 2) {
            return isValidOre(parts[0], parts[1]);
        } else if (parts.length > 2) {
            AgriCore.getLogger("agricraft").warn("Invalid stack identifier detected!\n\tGiven: \"{0}\"\n\tAssuming: \"{1}:{2}\"", element, parts[0], parts[1]);
            return isValidOre(parts[0], parts[1]);
        } else {
            throw new AssertionError("String.split() method worked incorrectly. This should be an impossible error.");
        }
    }

    /**
     * Determines if the given string represents a valid oredict resource entry.
     *
     * @param prefix
     * @param suffix
     * @return {@literal true} if and only if the given string represents a valid oredict entry, {@literal false} otherwise.
     */
    public static final boolean isValidOre(@Nonnull String prefix, @Nonnull String suffix) {
        // Validate
        Preconditions.checkNotNull(prefix, "A stack identifier must have a non-null prefix!");
        Preconditions.checkNotNull(suffix, "A stack identifier must have a non-null suffix!");

        // Check that prefix is correct.
        if (!PREFIX_OREDICT.equals(prefix)) {
            return false;
        }

        // Check that the oredictionary contains the given object.
        if (!OreDictionary.doesOreNameExist(suffix)) {
            AgriCore.getLogger("agricraft").error("Unable to resolve Ore Dictionary Entry: \"{0}:{1}\".", prefix, suffix);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Finds the first ItemStack in the OreDictionary with matching ore name.
     *
     * @param oreName the name of the ore type.
     * @return an optional containing a matching ItemStack, or the empty
     * optional.
     */
    @Nonnull
    public static Optional<ItemStack> getFirstOre(@Nonnull String oreName) {
        return OreDictionary.getOres(oreName).stream()
                .findFirst();
    }

    /**
     * Creates an ItemStack with the given attributes, if possible.
     *
     * Unlike {@link OreDictionary#makeItemStack()} this method will not throw a
     * runtime exception if the JSON NBT string is invalid.
     *
     * This method allows for the optional support of PREFIX_OREDICT as a modid
     * prefix, in which case the first matching ore is returned as the item
     * stack.
     *
     * @param element the registry name of the item.
     * @param meta the metadata value associated with the item.
     * @param amount the amount of the item to be in the stack.
     * @param tags a json string representation of the tags to be associated
     * with the item.
     * @return an optional containing a matching ItemStack, or the empty
     * optional.
     */
    @Nonnull
    public static final Optional<ItemStack> makeItemStack(@Nullable String element, int meta, int amount, @Nullable String tags) {
        // If null or empty return nothing.
        if (element == null || element.isEmpty()) {
            return Optional.empty();
        }

        // Split the element.
        final String[] parts = element.split(":");

        // If only 1 part, then assume is suffix.
        if (parts.length == 1) {
            return makeItemStack("minecraft", parts[0], meta, amount, tags);
        } else if (parts.length == 2) {
            return makeItemStack(parts[0], parts[1], meta, amount, tags);
        } else if (parts.length > 2) {
            AgriCore.getLogger("agricraft").warn("Invalid stack identifier detected!\n\tGiven: \"{0}\"\n\tAssuming: \"{1}:{2}\"", element, parts[0], parts[1]);
            return makeItemStack(parts[0], parts[1], meta, amount, tags);
        } else {
            throw new AssertionError("String.split() method worked incorrectly. This should be an impossible error.");
        }
    }

    /**
     * Creates an ItemStack with the given attributes, if possible.
     *
     * Unlike {@link OreDictionary#makeItemStack()} this method will not throw a
     * runtime exception if the JSON NBT string is invalid.
     *
     * This method allows for the optional support of PREFIX_OREDICT as a modid
     * prefix, in which case the first matching ore is returned as the item
     * stack.
     *
     * @param prefix the registry domain of the item.
     * @param suffix the registry path of the item.
     * @param meta the metadata value associated with the item.
     * @param amount the amount of the item to be in the stack.
     * @param tags a json string representation of the tags to be associated
     * with the item.
     * @return an optional containing a matching ItemStack, or the empty
     * optional.
     */
    @Nonnull
    public static final Optional<ItemStack> makeItemStack(@Nonnull String prefix, @Nonnull String suffix, int meta, int amount, @Nullable String tags) {
        // Validate
        Preconditions.checkNotNull(prefix, "A stack identifier must have a non-null prefix!");
        Preconditions.checkNotNull(suffix, "A stack identifier must have a non-null suffix!");

        // Test if the prefix is the special oredict prefix.
        if (PREFIX_OREDICT.equals(prefix)) {
            return makeItemStackOreDict(prefix, suffix, meta, amount, tags);
        } else {
            return makeItemStackNormal(prefix, suffix, meta, amount, tags);
        }
    }

    @Nonnull
    private static Optional<ItemStack> makeItemStackNormal(@Nonnull String prefix, @Nonnull String suffix, int meta, int amount, String tags) {
        // Step 0. Fetch the item.
        final Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(prefix, suffix));

        // Step 1. Check that item is not null.
        if (item == null) {
            AgriCore.getLogger("agricraft").error("Unable to resolve item: {0}:{1}.", prefix, suffix);
            return Optional.empty();
        }

        // Step 2. Create the itemstack.
        final ItemStack stack = new ItemStack(item, amount, meta);

        // Step 3. Add NBT data.
        return addNbtData(stack, tags);
    }

    @Nonnull
    private static Optional<ItemStack> makeItemStackOreDict(@Nonnull String prefix, @Nonnull String suffix, int meta, int amount, String tags) {
        // Do the thing.
        return getFirstOre(suffix)
                .map((s) -> {
                    s = s.copy();
                    s.setCount(amount);
                    return s;
                })
                .flatMap(s -> addNbtData(s, tags));
    }

    @Nonnull
    private static Optional<ItemStack> addNbtData(@Nonnull ItemStack stack, @Nullable String tags) {
        // Step 0. Validate.
        Preconditions.checkNotNull(stack, "The itemstack to add NBT data to may not be null");

        // Step 1. Abort if tags are null.
        if (Strings.isNullOrEmpty(tags)) {
            return Optional.of(stack);
        }

        // Step 2. Get the tag instance.
        final NBTTagCompound tag = StackHelper.getTag(stack);

        // Step 3. Parse the tags.
        try {
            final NBTTagCompound added = JsonToNBT.getTagFromJson(tags);
            tag.merge(added);
            stack.setTagCompound(tag);
            return Optional.of(stack);
        } catch (NBTException e) {
            AgriCore.getLogger("agricraft").error("Unable to parse NBT Data: \"{0}\".\nCause: {1}", tags, e);
            return Optional.empty();
        }
    }

    /**
     * Dummy constructor to prevent instantiation of utility class.
     */
    private OreDictUtil() {
        // Nothing to see here @TehNut!
    }

}
