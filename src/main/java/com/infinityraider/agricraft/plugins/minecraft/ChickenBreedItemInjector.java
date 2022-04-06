package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.infinityraider.infinitylib.utility.UnsafeUtil;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

public class ChickenBreedItemInjector {
    public static void inject() {
        try {
            // Fetch field
            Class<Chicken> clazz = Chicken.class;
            Field field = ObfuscationReflectionHelper.findField(clazz, "field_6742");
            // Set accessible
            field.setAccessible(true);
            // Create new ingredient based on the old one and add the agricraft seed
            Ingredient ingredient = Ingredient.of(Stream.concat(
                    Arrays.stream(((Ingredient) field.get(null)).getItems()),
                    Stream.of(new ItemStack(AgriItemRegistry.SEED))
            ));
            // Set the ingredient
            if(!UnsafeUtil.getInstance().replaceStaticField(field, ingredient)) {
                AgriCraft.instance.getLogger().error("Failed to inject AgriCraft seed as chicken feed");
            }
        } catch(Exception e) {
            AgriCraft.instance.getLogger().error("Failed to inject AgriCraft seed as chicken feed");
            AgriCraft.instance.getLogger().printStackTrace(e);
        }

    }
}
