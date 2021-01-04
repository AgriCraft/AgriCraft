package com.infinityraider.agricraft.util;

import com.agricraft.agricore.core.AgriCore;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public final class TagUtil {

    public static final String PREFIX_TAG = "tag";

    /**
     * Determines if the given string represents a valid tag.
     *
     * @param element the string representation of the oredict entry, should start with ItemStackUtil.PREFIX_TAG.
     * @return {@literal true} if and only if the given string represents a valid oredict entry, {@literal false} otherwise.
     */
    public static final boolean isValidTag(@Nonnull ITagCollection<?> registry, @Nullable String element) {
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
            return isValidTag(registry, parts[0], parts[1]);
        } else if (parts.length > 2) {
            AgriCore.getLogger("agricraft").warn("Invalid stack identifier detected!\n\tGiven: \"{0}\"\n\tAssuming: \"{1}:{2}\"", element, parts[0], parts[1]);
            return isValidTag(registry, parts[0], parts[1]);
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
    public static final boolean isValidTag(@Nonnull ITagCollection<?> registry, @Nonnull String prefix, @Nonnull String suffix) {
        // Validate
        Preconditions.checkNotNull(prefix, "A stack identifier must have a non-null prefix!");
        Preconditions.checkNotNull(suffix, "A stack identifier must have a non-null suffix!");

        // Check that prefix is correct.
        if (!PREFIX_TAG.equals(prefix)) {
            return false;
        }

        // Check that the oredictionary contains the given object.
        if (!registry.getRegisteredTags().contains(new ResourceLocation(suffix))) {
            AgriCore.getLogger("agricraft").error("Unable to resolve Item Tag Entry: \"{0}:{1}\".", prefix, suffix);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Finds the first ItemStack in the OreDictionary with matching ore name.
     *
     * @param tagValue the name of the ore type.
     * @return an optional containing a matching ItemStack, or the empty
     * optional.
     */
    @Nonnull
    public static Optional<ItemStack> getFirstStack(@Nonnull ITagCollection<?> registry, @Nonnull String tagValue) {
        return Optional.ofNullable(registry.get(new ResourceLocation(tagValue)))
                .flatMap(tag -> tag.getAllElements().stream().findFirst())
                .flatMap(obj -> createStackFromObject(obj, 1));
    }

    private static Optional<ItemStack> createStackFromObject(Object object, int amount) {
        if(object instanceof IItemProvider) {
            return Optional.of(new ItemStack((IItemProvider) object, amount));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Creates an ItemStack with the given attributes, if possible.
     *
     * This method allows for the optional support of PREFIX_OREDICT as a modid
     * prefix, in which case the first matching ore is returned as the item
     * stack.
     *
     * @param element the registry name of the item.
     * @param amount the amount of the item to be in the stack.
     * @param tags a json string representation of the tags to be associated
     * with the item.
     * @return an optional containing a matching ItemStack, or the empty
     * optional.
     */
    @Nonnull
    public static final Optional<ItemStack> makeItemStack(@Nonnull ITagCollection<?> registry, @Nullable String element, int amount, @Nullable String tags) {
        // If null or empty return nothing.
        if (element == null || element.isEmpty()) {
            return Optional.empty();
        }

        // Split the element.
        final String[] parts = element.split(":");

        // If only 1 part, then assume is suffix.
        if (parts.length == 1) {
            return makeItemStack(registry,"minecraft", parts[0], amount, tags);
        } else if (parts.length == 2) {
            return makeItemStack(registry, parts[0], parts[1], amount, tags);
        } else if (parts.length > 2) {
            AgriCore.getLogger("agricraft").warn("Invalid stack identifier detected!\n\tGiven: \"{0}\"\n\tAssuming: \"{1}:{2}\"", element, parts[0], parts[1]);
            return makeItemStack(registry, parts[0], parts[1], amount, tags);
        } else {
            throw new AssertionError("String.split() method worked incorrectly. This should be an impossible error.");
        }
    }

    /**
     * Creates an ItemStack with the given attributes, if possible.
     *
     * This method allows for the optional support of PREFIX_OREDICT as a modid
     * prefix, in which case the first matching ore is returned as the item
     * stack.
     *
     * @param prefix the registry domain of the item.
     * @param suffix the registry path of the item.
     * @param amount the amount of the item to be in the stack.
     * @param tags a json string representation of the tags to be associated
     * with the item.
     * @return an optional containing a matching ItemStack, or the empty
     * optional.
     */
    @Nonnull
    public static final Optional<ItemStack> makeItemStack(@Nonnull ITagCollection<?> registry, @Nonnull String prefix, @Nonnull String suffix, int amount, @Nullable String tags) {
        // Validate
        Preconditions.checkNotNull(prefix, "A stack identifier must have a non-null prefix!");
        Preconditions.checkNotNull(suffix, "A stack identifier must have a non-null suffix!");

        // Test if the prefix is the special oredict prefix.
        if (PREFIX_TAG.equals(prefix)) {
            return makeItemStackFromTag(registry, prefix, suffix, amount, tags);
        } else {
            return makeItemStackNormal(prefix, suffix, amount, tags);
        }
    }

    @Nonnull
    private static Optional<ItemStack> makeItemStackNormal(@Nonnull String prefix, @Nonnull String suffix, int amount, String tags) {
        // Step 0. Fetch the item.
        final Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(prefix, suffix));

        // Step 1. Check that item is not null.
        if (item == null) {
            AgriCore.getLogger("agricraft").error("Unable to resolve item: {0}:{1}.", prefix, suffix);
            return Optional.empty();
        }

        // Step 2. Create the itemstack.
        final ItemStack stack = new ItemStack(item, amount);

        // Step 3. Add NBT data.
        return addNbtData(stack, tags);
    }

    @Nonnull
    private static Optional<ItemStack> makeItemStackFromTag(@Nonnull ITagCollection<?> registry, @Nonnull String prefix, @Nonnull String suffix, int amount, String tags) {
        // Do the thing.
        return getFirstStack(registry, suffix)
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
        final CompoundNBT tag = stack.hasTag() ? stack.getTag() : new CompoundNBT();

        // Step 3. Parse the tags.
        try {
            final CompoundNBT added = JsonToNBT.getTagFromJson(tags);
            tag.merge(added);
            stack.setTag(tag);
            return Optional.of(stack);
        } catch (CommandSyntaxException e) {
            AgriCore.getLogger("agricraft").error("Unable to parse NBT Data: \"{0}\".\nCause: {1}", tags, e);
            return Optional.empty();
        }
    }

    /**
     * Dummy constructor to prevent instantiation of utility class.
     */
    private TagUtil() {
        // Nothing to see here @TehNut!
    }

}
