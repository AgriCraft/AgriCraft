package com.agricraft.agricraft.common.item.journal;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriMutation;
import com.agricraft.agricraft.api.codecs.AgriRequirement;
import com.agricraft.agricraft.api.codecs.AgriSoilCondition;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.requirement.AgriGrowthConditionRegistry;
import com.agricraft.agricraft.api.requirement.AgriSeason;
import com.agricraft.agricraft.api.tools.journal.JournalPage;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlantPage implements JournalPage {

	public static final ResourceLocation ID = new ResourceLocation(AgriApi.MOD_ID, "plant_page");

	private final AgriPlant plant;
	private final List<ResourceLocation> researched;
	private final ResourceLocation plantId;
	private final List<ItemStack> products;
	private final List<List<ResourceLocation>> mutationsOnPage;
	private final List<List<ResourceLocation>> mutationsOffPage;
	private final boolean[] brightnessMask;
	private final boolean[] humidityMask;
	private final boolean[] acidityMask;
	private final boolean[] nutrientsMask;
	private final boolean[] seasonMask;

	public PlantPage(ResourceLocation plantId, List<ResourceLocation> researched) {
		this.plantId = plantId;
		this.plant = AgriApi.getPlant(plantId).orElse(AgriPlant.NO_PLANT);
		this.researched = researched;
		this.brightnessMask = new boolean[16];
		AgriRequirement req = this.plant.getGrowthRequirements();
		for (int light = 0; light < this.brightnessMask.length; light++) {
			this.brightnessMask[light] = AgriGrowthConditionRegistry.getLight().apply(this.plant, 1, light).isFertile();
		}
		this.humidityMask = new boolean[AgriSoilCondition.Humidity.values().length - 1];
		for (int humidity = 0; humidity < this.humidityMask.length; humidity++) {
			this.humidityMask[humidity] = AgriGrowthConditionRegistry.getHumidity().apply(this.plant, 1, AgriSoilCondition.Humidity.values()[humidity]).isFertile();
		}
		this.acidityMask = new boolean[AgriSoilCondition.Acidity.values().length - 1];
		for (int acidity = 0; acidity < this.acidityMask.length; acidity++) {
			this.acidityMask[acidity] = AgriGrowthConditionRegistry.getAcidity().apply(this.plant, 1, AgriSoilCondition.Acidity.values()[acidity]).isFertile();
		}
		this.nutrientsMask = new boolean[AgriSoilCondition.Nutrients.values().length - 1];
		for (int nutrients = 0; nutrients < this.nutrientsMask.length; nutrients++) {
			this.nutrientsMask[nutrients] = AgriGrowthConditionRegistry.getNutrients().apply(this.plant, 1, AgriSoilCondition.Nutrients.values()[nutrients]).isFertile();
		}
		this.seasonMask = new boolean[AgriSeason.values().length - 1];
		for (int season = 0; season < this.seasonMask.length; season++) {
			this.seasonMask[season] = AgriGrowthConditionRegistry.getSeason().apply(this.plant, 1, AgriSeason.values()[season]).isFertile();
		}
		this.products = new ArrayList<>();
		this.plant.getAllPossibleProducts(products::add);
		List<List<ResourceLocation>> mutations = Stream.concat(
				this.gatherMutationSprites(mutation -> mutation.parent1().equals(this.plantId) || mutation.parent2().equals(this.plantId)),
				this.gatherMutationSprites(mutation -> mutation.child().equals(this.plantId))
		).collect(Collectors.toList());
		int count = mutations.size();
		if (count <= 9) {
			this.mutationsOnPage = mutations.subList(0, count);
			this.mutationsOffPage = ImmutableList.of();
		} else {
			this.mutationsOnPage = mutations.subList(0, 6);
			this.mutationsOffPage = mutations.subList(6, count);
		}
	}

	@Override
	public ResourceLocation getDrawerId() {
		return ID;
	}

	public AgriPlant getPlant() {
		return this.plant;
	}

	public ResourceLocation getPlantId() {
		return plantId;
	}

	public List<ItemStack> getProducts() {
		return products;
	}

	public List<List<ResourceLocation>> getMutationsOnPage() {
		return mutationsOnPage;
	}

	public List<List<ResourceLocation>> getMutationsOffPage() {
		return mutationsOffPage;
	}

	public boolean[] brightnessMask() {
		return this.brightnessMask;
	}

	public boolean[] humidityMask() {
		return this.humidityMask;
	}

	public boolean[] acidityMask() {
		return this.acidityMask;
	}

	public boolean[] nutrientsMask() {
		return this.nutrientsMask;
	}

	public boolean[] seasonMask() {
		return this.seasonMask;
	}

	protected Stream<List<ResourceLocation>> gatherMutationSprites(Predicate<AgriMutation> filter) {
		Optional<Registry<AgriMutation>> optional = AgriApi.getMutationRegistry();
		if (optional.isEmpty()) {
			return Stream.empty();
		}
		return optional.get().stream().filter(filter)
				.map(mutation -> Stream.of(mutation.parent1(), mutation.parent2(), mutation.child())
						.map(plant -> isPlantKnown(plant) ? plant : AgriPlant.UNKNOWN)
						.toList()
				);
	}

	protected boolean isPlantKnown(ResourceLocation plant) {
//		if(AgriCraft.instance.getConfig().progressiveJEI()) {
		return this.researched.contains(plant);
//		}
//		return true;
	}

}
