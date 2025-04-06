package com.agricraft.agricraft;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.SideUtils;
import com.agricraft.agricraft.api.adapter.AgriAdapter;
import com.agricraft.agricraft.api.codecs.AgriMutation;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.fertilizer.AgriFertilizer;
import com.agricraft.agricraft.api.genetic.AgriGene;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.api.genetic.AgriMutationHandler;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.plant.AgriPlantModifierFactory;
import com.agricraft.agricraft.api.plant.AgriWeed;
import com.agricraft.agricraft.api.registries.AgriCraftStats;
import com.agricraft.agricraft.api.requirement.AgriGrowthCondition;
import com.agricraft.agricraft.api.requirement.SeasonLogic;
import com.agricraft.agricraft.api.stat.AgriStat;
import com.agricraft.agricraft.api.tools.journal.JournalData;
import com.agricraft.agricraft.api.tools.seedbag.BagSorter;
import com.agricraft.agricraft.common.adapter.FertilizerAdapter;
import com.agricraft.agricraft.common.adapter.GenomeAdapter;
import com.agricraft.agricraft.common.genetic.AgriMutationHandlerImpl;
import com.agricraft.agricraft.common.item.SeedBagItem;
import com.agricraft.agricraft.common.registry.AgriDataComponents;
import com.agricraft.agricraft.common.registry.AgriRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AgriApiImpl implements AgriApi {

	public static final String MOD_ID = "agricraft";

	private static final List<AgriAdapter<AgriGenome>> GENOME_ADAPTERS = new ArrayList<>();
	private static final List<AgriAdapter<AgriFertilizer>> FERTILIZER_ADAPTERS = new ArrayList<>();

	private AgriMutationHandler mutationHandler;

	public AgriApiImpl() {
		mutationHandler = new AgriMutationHandlerImpl();
		this.registerFertilizerAdapter(new FertilizerAdapter());
		this.registerGenomeAdapter(new GenomeAdapter());
		this.registerSeedBagSorter(SeedBagItem.DEFAULT_SORTER);
		this.registerSeedBagSorter(new SeedBagItem.StatSorter(AgriCraftStats.GAIN, AgriCraftStats.GAIN.getId()));
		this.registerSeedBagSorter(new SeedBagItem.StatSorter(AgriCraftStats.GROWTH, AgriCraftStats.GROWTH.getId()));
		this.registerSeedBagSorter(new SeedBagItem.StatSorter(AgriCraftStats.STRENGTH, AgriCraftStats.STRENGTH.getId()));
		this.registerSeedBagSorter(new SeedBagItem.StatSorter(AgriCraftStats.RESISTANCE, AgriCraftStats.RESISTANCE.getId()));
		this.registerSeedBagSorter(new SeedBagItem.StatSorter(AgriCraftStats.FERTILITY, AgriCraftStats.FERTILITY.getId()));
		this.registerSeedBagSorter(new SeedBagItem.StatSorter(AgriCraftStats.MUTATIVITY, AgriCraftStats.MUTATIVITY.getId()));
	}

	public Registry<AgriGene<?>> getGeneRegistry() {
		return AgriRegistries.GENE_REGISTRY;
	}

	public Registry<AgriStat> getStatRegistry() {
		return AgriRegistries.STAT_REGISTRY;
	}

	@Override
	public Registry<AgriGrowthCondition<?>> getGrowthConditionRegistry() {
		return AgriRegistries.GROWTH_CONDITION_REGISTRY;
	}

	@Override
	public Registry<AgriPlantModifierFactory> getPlantModifierFactoryRegistry() {
		return AgriRegistries.PLANT_MODIFIER_FACTORY_REGISTRY;
	}

	@Override
	public Optional<Registry<AgriPlant>> getPlantRegistry() {
		return SideUtils.getRegistryAccess().flatMap(reg -> reg.registry(AgriPlant.REGISTRY_KEY));
	}

	@Override
	public Optional<Registry<AgriPlant>> getPlantRegistry(RegistryAccess registryAccess) {
		return registryAccess.registry(AgriPlant.REGISTRY_KEY);
	}

	@Override
	public Optional<Registry<AgriWeed>> getWeedRegistry() {
		return SideUtils.getRegistryAccess().flatMap(reg -> reg.registry(AgriWeed.REGISTRY_KEY));
	}

	@Override
	public Optional<Registry<AgriWeed>> getWeedRegistry(RegistryAccess registryAccess) {
		return registryAccess.registry(AgriWeed.REGISTRY_KEY);
	}

	@Override
	public Optional<Registry<AgriSoil>> getSoilRegistry() {
		return SideUtils.getRegistryAccess().flatMap(reg -> reg.registry(AgriSoil.REGISTRY_KEY));
	}

	@Override
	public Optional<Registry<AgriSoil>> getSoilRegistry(RegistryAccess registryAccess) {
		return registryAccess.registry(AgriSoil.REGISTRY_KEY);
	}

	@Override
	public Optional<Registry<AgriMutation>> getMutationRegistry() {
		return SideUtils.getRegistryAccess().flatMap(reg -> reg.registry(AgriMutation.REGISTRY_KEY));
	}

	@Override
	public Optional<Registry<AgriMutation>> getMutationRegistry(RegistryAccess registryAccess) {
		return registryAccess.registry(AgriMutation.REGISTRY_KEY);
	}

	@Override
	public Optional<Registry<AgriFertilizer>> getFertilizerRegistry() {
		return SideUtils.getRegistryAccess().flatMap(reg -> reg.registry(AgriFertilizer.REGISTRY_KEY));
	}

	@Override
	public Optional<Registry<AgriFertilizer>> getFertilizerRegistry(RegistryAccess registryAccess) {
		return registryAccess.registry(AgriFertilizer.REGISTRY_KEY);
	}

	@Override
	public Optional<JournalData> getJournalData(ItemStack itemStack) {
		return Optional.ofNullable(itemStack.get(AgriDataComponents.JOURNAL_DATA));
	}

	@Override
	public Optional<AgriGenome> getGenome(ItemStack itemStack) {
		return Optional.ofNullable(itemStack.get(AgriDataComponents.GENOME));
	}

	@Override
	public void registerGenomeAdapter(AgriAdapter<AgriGenome> adapter) {
		GENOME_ADAPTERS.add(adapter);
	}

	@Override
	public Optional<AgriAdapter<AgriGenome>> getGenomeAdapter(Object obj) {
		return GENOME_ADAPTERS.stream().filter(adapter -> adapter.accepts(obj)).findFirst();
	}

	@Override
	public void registerFertilizerAdapter(AgriAdapter<AgriFertilizer> adapter) {
		FERTILIZER_ADAPTERS.add(adapter);
	}

	@Override
	public Optional<AgriAdapter<AgriFertilizer>> getFertilizerAdapter(Object obj) {
		return FERTILIZER_ADAPTERS.stream().filter(adapter -> adapter.accepts(obj)).findFirst();
	}

	@Override
	public SeasonLogic getSeasonLogic() {
		return SeasonLogic.INSTANCE;
	}

	@Override
	public AgriMutationHandler getMutationHandler() {
		return this.mutationHandler;
	}

	@Override
	public void setMutationHandler(AgriMutationHandler handler) {
		this.mutationHandler = handler;
	}

	@Override
	public void registerSeedBagSorter(BagSorter sorter) {
		SeedBagItem.SORTERS.add(sorter);
	}

}
