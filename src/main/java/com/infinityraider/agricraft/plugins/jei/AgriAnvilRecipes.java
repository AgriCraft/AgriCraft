package com.infinityraider.agricraft.plugins.jei;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.capability.CapabilityGeneInspector;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AgriAnvilRecipes {
    public static void registerRecipes(IRecipeRegistration registration) {
        final List<ItemStack> magnifyingGlass = Collections.singletonList(new ItemStack(AgriApi.getAgriContent().getItems().getMagnifyingGlassItem()));
        registration.addRecipes(
                ForgeRegistries.ITEMS.getValues().stream()
                        .map(ItemStack::new)
                        .filter(CapabilityGeneInspector.getInstance()::shouldApplyCapability)
                        .map(stack -> {
                            ItemStack output = stack.copy();
                            CapabilityGeneInspector.getInstance().applyInspectionCapability(output);
                            return registration.getVanillaRecipeFactory().createAnvilRecipe(
                                    Collections.singletonList(stack),
                                    magnifyingGlass,
                                    Collections.singletonList(output));
                        })
                        .collect(Collectors.toList()),
                VanillaRecipeCategoryUid.ANVIL
        );
    }
}
