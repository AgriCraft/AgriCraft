package com.infinityraider.agricraft.api.v1.content.items;

import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * Representation of crop sticks items
 */
public interface IAgriCropStickItem {
    /**
     * @return the crop sticks variant for this item
     */
    Variant getVariant();

    /**
     * @return this, but cast to item
     */
    default Item asItem() {
        return (Item) this;
    }

    /**
     * enum representation of a crop sticks variant
     */
    interface Variant extends StringRepresentable {
        /**
         * @return the ID of the variant
         */
        String getId();

        /**
         * @return sound associated with the variant
         */
        SoundType getSound();

        /**
         * @return the material of the variant
         */
        Material getMaterial();

        /**
         * @return the item representation of this variant
         */
        IAgriCropStickItem getItem();

        /**
         * Checks if this variant can survive in a given fluid
         * @param fluid the fluid
         * @return if this variant can survive in the fluid
         */
        boolean canExistInFluid(Fluid fluid);

        /**
         * Plays the placement sound of the variant
         * @param world world object
         * @param pos position
         */
        void playCropStickSound(Level world, BlockPos pos);

        /**
         * Checks if this variant is the same as the item in a stack
         * @param stack the stack
         * @return true if the item in the stack is of the same type
         */
        default boolean isSameType(ItemStack stack) {
            return this.isSameType(stack.getItem());
        }

        /**
         * Checks if this variant is the same as an item
         * @param item the item
         * @return true if the item is of the same type
         */
        default boolean isSameType(Item item) {
            if(item instanceof IAgriCropStickItem) {
                return this.isSameType((IAgriCropStickItem) item);
            }
            return false;
        }

        /**
         * Checks if this variant is the same as an item
         * @param item the item
         * @return true if the item is of the same type
         */
        default boolean isSameType(IAgriCropStickItem item) {
            return this == item.getVariant();
        }

        /**
         * Creates an registers a new crop stick variant.
         *
         * This variant is baked into a BlockState definition, and must therefore be called before blocks are being registered
         *
         * A unique item for this variant will also be registered, this can be retrieved via the getItem() method on the crop sticks variant.
         *
         * @param name the name of the variant
         * @param material the material
         * @param sound sound for the material
         * @param fluidPredicate predicate determining if this crop stick type can survive in certain fluids
         * @return a new crop stick variant, or null
         */
        @Nullable
        static Variant create(String name, Material material, SoundType sound, Predicate<Fluid> fluidPredicate) {
            return AgriApi.createCropStickVariant(name, material, sound, fluidPredicate);
        }
    }
}
