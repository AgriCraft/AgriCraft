package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.infinitylib.utility.UnsafeUtil;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

public class ChickenBreedItemInjector {
    public static void inject() {
        try {
            // Fetch field
            Field field = getFeedField();
            if(field == null) {
                AgriCraft.instance.getLogger().error("Failed to inject AgriCraft seed as chicken feed");
                return;
            }
            // Create new ingredient based on the old one and add the agricraft seed
            Ingredient ingredient = Ingredient.of(Stream.concat(
                    Arrays.stream(((Ingredient) field.get(null)).getItems()),
                    Stream.of(new ItemStack(AgriApi.getAgriContent().getItems().getSeedItem().toItem()))
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

    @Nullable
    private static Field getFeedField() {
        return Arrays.stream(Chicken.class.getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .filter(field -> field.getType() == Ingredient.class)
                .findAny()
                .orElse(null);
    }
}
