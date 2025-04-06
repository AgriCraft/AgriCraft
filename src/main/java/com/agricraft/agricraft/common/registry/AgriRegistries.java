package com.agricraft.agricraft.common.registry;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriMutation;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.fertilizer.AgriFertilizer;
import com.agricraft.agricraft.api.genetic.AgriGene;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.plant.AgriPlantModifierFactory;
import com.agricraft.agricraft.api.plant.AgriWeed;
import com.agricraft.agricraft.api.requirement.AgriGrowthCondition;
import com.agricraft.agricraft.api.stat.AgriStat;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

public interface AgriRegistries {

	DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AgriApi.MOD_ID);
	DeferredRegister.Items ITEMS = DeferredRegister.createItems(AgriApi.MOD_ID);
	DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AgriApi.MOD_ID);
	DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, AgriApi.MOD_ID);
	DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, AgriApi.MOD_ID);
	DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AgriApi.MOD_ID);
	DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, AgriApi.MOD_ID);
	DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, AgriApi.MOD_ID);

	DeferredRegister<AgriStat> STATS = DeferredRegister.create(AgriStat.REGISTRY_KEY, AgriApi.MOD_ID);
	DeferredRegister<AgriGene<?>> GENES = DeferredRegister.create(AgriGene.REGISTRY_KEY, AgriApi.MOD_ID);
	DeferredRegister<AgriGrowthCondition<?>> GROWTH_CONDITIONS = DeferredRegister.create(AgriGrowthCondition.REGISTRY_KEY, AgriApi.MOD_ID);
	DeferredRegister<AgriPlantModifierFactory> PLANT_MODIFIER_FACTORIES = DeferredRegister.create(AgriPlantModifierFactory.REGISTRY_KEY, AgriApi.MOD_ID);


	Registry<AgriStat> STAT_REGISTRY = STATS.makeRegistry(builder -> builder.sync(true));
	Registry<AgriGene<?>> GENE_REGISTRY = GENES.makeRegistry(builder -> builder.sync(true));
	Registry<AgriGrowthCondition<?>> GROWTH_CONDITION_REGISTRY = GROWTH_CONDITIONS.makeRegistry(builder -> builder.sync(true));
	Registry<AgriPlantModifierFactory> PLANT_MODIFIER_FACTORY_REGISTRY = PLANT_MODIFIER_FACTORIES.makeRegistry(builder -> builder.sync(true));

	/**
	 * Register the registries to the given event bus.
	 *
	 * @param bus the even bus to register to
	 */
	@ApiStatus.Internal
	static void register(IEventBus bus) {
		AgriBlocks.register();
		AgriItems.register();
		AgriBlockEntities.register();
		AgriMenuTypes.register();
		AgriDataComponents.register();
		AgriTabs.register();
		AgriRecipeSerializers.register();
		AgriGlobalLootModifiers.register();
		ModStats.register();
		ModGenes.register();
		ModGrowthConditions.register();
		ModPlantModifiers.register();


		BLOCKS.register(bus);
		ITEMS.register(bus);
		BLOCK_ENTITY_TYPES.register(bus);
		MENU_TYPES.register(bus);
		DATA_COMPONENTS.register(bus);
		CREATIVE_MODE_TAB.register(bus);
		RECIPE_SERIALIZERS.register(bus);
		GLOBAL_LOOT_MODIFIERS.register(bus);
		STATS.register(bus);
		GENES.register(bus);
		GROWTH_CONDITIONS.register(bus);
		PLANT_MODIFIER_FACTORIES.register(bus);

		bus.addListener(AgriRegistries::registerDatapackRegistry);
	}

	static void registerDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(AgriPlant.REGISTRY_KEY, AgriPlant.CODEC, AgriPlant.CODEC);
		event.dataPackRegistry(AgriWeed.REGISTRY_KEY, AgriWeed.CODEC, AgriWeed.CODEC);
		event.dataPackRegistry(AgriSoil.REGISTRY_KEY, AgriSoil.CODEC, AgriSoil.CODEC);
		event.dataPackRegistry(AgriMutation.REGISTRY_KEY, AgriMutation.CODEC, AgriMutation.CODEC);
		event.dataPackRegistry(AgriFertilizer.REGISTRY_KEY, AgriFertilizer.CODEC, AgriFertilizer.CODEC);
	}

}
