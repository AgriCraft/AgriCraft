package com.agricraft.agricraft.compat.jei;


import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.common.item.crafting.MagnifyingHelmetRecipe;
import com.agricraft.agricraft.common.registry.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class AgriCraftJeiPlugin  implements IModPlugin {

	public static final ResourceLocation ID = new ResourceLocation(AgriApi.MOD_ID, "compat_jei");

	@Override
	@NotNull
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		// Register all The Seeds.
		registration.registerSubtypeInterpreter(ModItems.SEED.get(), (stack, context) -> {
			AgriGenome genome = AgriGenome.fromNBT(stack.getTag());
			if (genome != null) {
				return genome.getSpeciesGene().getTrait();
			}
			return "agricraft:unknown";
		});
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		registration.getCraftingCategory().addCategoryExtension(MagnifyingHelmetRecipe.class, MagnifyingHelmetExtension::new);
	}

}