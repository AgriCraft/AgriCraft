package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.AgriCraft;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

public class ChickenBreedItemInjector {
    public static void inject() {
        try {
            // Fetch field
            Class<ChickenEntity> clazz = ChickenEntity.class;
            Field field = ObfuscationReflectionHelper.findField(clazz, "field_184761_bD");
            // Set accessible
            field.setAccessible(true);
            // Remove private modifier
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            // Create new ingredient based on the old one and add the agricraft seed
            Ingredient ingredient = Ingredient.fromStacks(Stream.concat(
                    Arrays.stream(((Ingredient) field.get(null)).getMatchingStacks()),
                    Stream.of(new ItemStack(AgriCraft.instance.getModItemRegistry().seed))
            ));
            // Set the ingredient
            field.set(null, ingredient);
        } catch(Exception e) {
            AgriCraft.instance.getLogger().error("Failed to inject AgriCraft seed as chicken feed");
            AgriCraft.instance.getLogger().printStackTrace(e);
        }

    }
}
