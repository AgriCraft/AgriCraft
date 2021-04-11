package com.infinityraider.agricraft.util;

import com.agricraft.agricore.core.AgriCore;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.tags.*;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public final class TagUtil {
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
            return isValidTag(registry, "minecraft", parts[0]);
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

        // Check that the tag registry contains the given object.
        if (!registry.getRegisteredTags().contains(new ResourceLocation(prefix, suffix))) {
            AgriCore.getLogger("agricraft").error("Unable to resolve Item Tag Entry: \"{0}:{1}\".", prefix, suffix);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Finds the first ItemStack in the Tag Registry with matching ore name.
     *
     * @param id the id of the tag.
     * @return an optional containing a matching ItemStack, or the empty
     * optional.
     */
    @Nonnull
    public static Optional<ItemStack> getFirstStack(@Nonnull ITagCollection<Item> registry, @Nonnull ResourceLocation id) {
        return Optional.ofNullable(registry.get(id))
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
     * Fetches a Collection of all ItemStacks respecting the given attributes, if possible.
     *
     * @param element the registry name of the Item.
     * @param amount the stack size for the ItemStacks
     * @param useTags to check if the element corresponds to a registry name in the tag registry instead
     * @param data a json string representation of the tags to be associated with the item.
     * @param ignoredData a List of NBT tags to ignore
     * @return a Collection containing all matching ItemStacks, or empty
     */
    @Nonnull
    public static final Collection<ItemStack> fetchItemStacks(@Nullable String element, int amount, boolean useTags, String data, List<String> ignoredData) {
        // If null or empty return nothing.
        if (element == null || element.isEmpty()) {
            return Collections.emptyList();
        }

        // Split the element.
        final String[] parts = element.split(":");

        // If only 1 part, then assume is suffix.
        if (parts.length == 1) {
            return fetchItemStacks("minecraft", parts[0], amount, useTags, data, ignoredData);
        } else if (parts.length == 2) {
            return fetchItemStacks(parts[0], parts[1], amount, useTags, data, ignoredData);
        } else if (parts.length > 2) {
            AgriCore.getLogger("agricraft").warn("Invalid stack identifier detected!\n\tGiven: \"{0}\"\n\tAssuming: \"{1}:{2}\"", element, parts[0], parts[1]);
            return fetchItemStacks(parts[0], parts[1], amount, useTags, data, ignoredData);
        } else {
            throw new AssertionError("String.split() method worked incorrectly. This should be an impossible error.");
        }
    }

    /**
     * Creates an ItemStack with the given attributes, if possible.
     *
     * @param prefix the registry domain of the item.
     * @param suffix the registry path of the item.
     * @param amount the stack size for the ItemStacks
     * @param useTags to check if the element corresponds to a registry name in the tag registry instead
     * @param data a json string representation of the tags to be associated with the item.
     * @param ignoredData a List of NBT tags to ignore
     * @return a Collection containing all matching ItemStacks, or empty
     */
    @Nonnull
    public static final Collection<ItemStack> fetchItemStacks(@Nonnull String prefix, @Nonnull String suffix, int amount, boolean useTags, String data, List<String> ignoredData) {
        // Validate
        Preconditions.checkNotNull(prefix, "A stack identifier must have a non-null prefix!");
        Preconditions.checkNotNull(suffix, "A stack identifier must have a non-null suffix!");

        // Test if the prefix is the special oredict prefix.
        if (useTags) {
            return makeItemStackFromTag(prefix, suffix, amount, data, ignoredData);
        } else {
            return makeItemStackNormal(prefix, suffix, amount, data, ignoredData);
        }
    }

    @Nonnull
    private static Collection<ItemStack> makeItemStackNormal(@Nonnull String prefix, @Nonnull String suffix, int amount, String data, List<String> ignoredData) {
        // Step 0. Fetch the item.
        final Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(prefix, suffix));

        // Step 1. Check that item is not null.
        if (item == null) {
            AgriCore.getLogger("agricraft").error("Unable to resolve item: {0}:{1}.", prefix, suffix);
            return Collections.emptyList();
        }

        // Step 2. Create the ItemStack.
        final ItemStack stack = new ItemStack(item, amount);

        // Step 3. Add NBT data.
        return addNbtData(stack, data).map(ImmutableList::of).orElse(ImmutableList.of());
    }

    @Nonnull
    private static Collection<ItemStack> makeItemStackFromTag(@Nonnull String prefix, @Nonnull String suffix, int amount, String data, List<String> ignoredData) {
        return Optional.ofNullable(TagCollectionManager.getManager().getItemTags().get(new ResourceLocation(prefix, suffix)))
                .map(tag -> tag.getAllElements()
                        .stream()
                        .map(item -> addNbtData(new ItemStack(item, amount), data))//TODO: ignored NBT values
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())
                ).orElse(Collections.emptyList());
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
     * Fetches a Collection of all BlockStates respecting the given attributes, if possible.
     *
     * @param element the registry name of the Block.
     * @param useTags to check if the element corresponds to a registry name in the tag registry instead
     * @param data a json string representation of the required BlockState Properties and their value
     * @param ignoredData a List of BlockState Properties to ignore
     * @return a Collection containing all matching BlockStates, or empty
     * optional.
     */
    @Nonnull
    public static final Collection<BlockState> fetchBlockStates(@Nullable String element, boolean useTags, String data, List<String> ignoredData) {
        // If null or empty return nothing.
        if (element == null || element.isEmpty()) {
            return Collections.emptyList();
        }

        // Split the element.
        final String[] parts = element.split(":");

        // If only 1 part, then assume is suffix.
        if (parts.length == 1) {
            return fetchBlockStates("minecraft", parts[0], useTags, data, ignoredData);
        } else if (parts.length == 2) {
            return fetchBlockStates(parts[0], parts[1], useTags, data, ignoredData);
        } else if (parts.length > 2) {
            AgriCore.getLogger("agricraft").warn("Invalid stack identifier detected!\n\tGiven: \"{0}\"\n\tAssuming: \"{1}:{2}\"", element, parts[0], parts[1]);
            return fetchBlockStates(parts[0], parts[1], useTags, data, ignoredData);
        } else {
            throw new AssertionError("String.split() method worked incorrectly. This should be an impossible error.");
        }
    }

    @Nonnull
    public static final Collection<BlockState> fetchBlockStates(@Nonnull String prefix, @Nonnull String suffix,
                                                                boolean useTags, @Nullable String data, List<String> ignoredData) {
        // Validate
        Preconditions.checkNotNull(prefix, "A Block identifier must have a non-null prefix!");
        Preconditions.checkNotNull(suffix, "A Block identifier must have a non-null suffix!");

        // Test if the prefix is the special ore dict prefix.
        if (useTags) {
            return fetchBlockStatesFromTag(prefix, suffix, data, ignoredData);
        } else {
            return fetchBlockStatesNormal(prefix, suffix, data, ignoredData);
        }
    }

    @Nonnull
    private static Collection<BlockState> fetchBlockStatesNormal(@Nonnull String prefix, @Nonnull String suffix,  @Nullable String data, List<String> ignoredData) {
        // Step 0. Fetch the item.
        final Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(prefix, suffix));

        // Step 1. Check that item is not null.
        if (block == null) {
            AgriCore.getLogger("agricraft").error("Unable to resolve block: {0}:{1}.", prefix, suffix);
            return Collections.emptyList();
        }

        // Step 2. Return the block
        return block.getStateContainer().getValidStates();  // TODO: filter based on data and ignoredData
    }

    @Nonnull
    private static Collection<BlockState> fetchBlockStatesFromTag(@Nonnull String prefix, @Nonnull String suffix, @Nullable String data, List<String> ignoredData) {
        // Do the thing.
        return Optional.ofNullable(TagCollectionManager.getManager().getBlockTags().get(new ResourceLocation(prefix, suffix)))
                .map(tag -> tag.getAllElements()
                        .stream()
                        .flatMap(block -> block.getStateContainer().getValidStates()
                                .stream())
                        .filter(state -> true)  // TODO: filter based on data and ignoredData
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    /**
     * Fetches a Collection of all FluidStates respecting the given attributes, if possible.
     *
     * @param element the registry name of the Fluid.
     * @param useTags to check if the element corresponds to a registry name in the tag registry instead
     * @param data a json string representation of the required BlockState Properties and their value
     * @param ignoredData a List of BlockState Properties to ignore
     * @return a Collection containing all matching BlockStates, or empty
     * optional.
     */
    @Nonnull
    public static final Collection<FluidState> fetchFluidStates(@Nullable String element, boolean useTags, String data, List<String> ignoredData) {
        // If null or empty return nothing.
        if (element == null || element.isEmpty()) {
            return Collections.emptyList();
        }

        // Split the element.
        final String[] parts = element.split(":");

        // If only 1 part, then assume is suffix.
        if (parts.length == 1) {
            return fetchFluidStates("minecraft", parts[0], useTags, data, ignoredData);
        } else if (parts.length == 2) {
            return fetchFluidStates(parts[0], parts[1], useTags, data, ignoredData);
        } else if (parts.length > 2) {
            AgriCore.getLogger("agricraft").warn("Invalid stack identifier detected!\n\tGiven: \"{0}\"\n\tAssuming: \"{1}:{2}\"", element, parts[0], parts[1]);
            return fetchFluidStates(parts[0], parts[1], useTags, data, ignoredData);
        } else {
            throw new AssertionError("String.split() method worked incorrectly. This should be an impossible error.");
        }
    }

    @Nonnull
    public static final Collection<FluidState> fetchFluidStates(@Nonnull String prefix, @Nonnull String suffix,
                                                                boolean useTags, @Nullable String data, List<String> ignoredData) {
        // Validate
        Preconditions.checkNotNull(prefix, "A Fluid identifier must have a non-null prefix!");
        Preconditions.checkNotNull(suffix, "A Fluid identifier must have a non-null suffix!");

        // Test if the prefix is the special ore dict prefix.
        if (useTags) {
            return fetchFluidStatesFromTag(prefix, suffix, data, ignoredData);
        } else {
            return fetchFluidStatesNormal(prefix, suffix, data, ignoredData);
        }
    }

    @Nonnull
    private static Collection<FluidState> fetchFluidStatesNormal(@Nonnull String prefix, @Nonnull String suffix,  @Nullable String data, List<String> ignoredData) {
        // Step 0. Fetch the item.
        final Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(prefix, suffix));

        // Step 1. Check that item is not null.
        if (fluid == null) {
            AgriCore.getLogger("agricraft").error("Unable to resolve block: {0}:{1}.", prefix, suffix);
            return Collections.emptyList();
        }

        // Step 2. Return the Fluid States
        return fluid.getStateContainer().getValidStates();  // TODO: filter based on data and ignoredData
    }

    @Nonnull
    private static Collection<FluidState> fetchFluidStatesFromTag(@Nonnull String prefix, @Nonnull String suffix, @Nullable String data, List<String> ignoredData) {
        // Do the thing.
        return Optional.ofNullable(TagCollectionManager.getManager().getFluidTags().get(new ResourceLocation(prefix, suffix)))
                .map(tag -> tag.getAllElements()
                        .stream()
                        .flatMap(fluid -> fluid.getStateContainer().getValidStates()
                                .stream())
                        .filter(state -> true)  // TODO: filter based on data and ignoredData
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    /**
     * Dummy constructor to prevent instantiation of utility class.
     */
    private TagUtil() {
        // Nothing to see here @TehNut!
    }
}
