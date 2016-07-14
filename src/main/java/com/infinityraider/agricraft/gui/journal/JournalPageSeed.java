package com.infinityraider.agricraft.gui.journal;

import com.infinityraider.agricraft.farming.mutation.MutationHandler;
import com.infinityraider.agricraft.gui.Component;
import com.infinityraider.agricraft.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import com.agricraft.agricore.core.AgriCore;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.mutation.IAgriMutation;

@SideOnly(Side.CLIENT)
public class JournalPageSeed extends JournalPage {

	private static final ResourceLocation ICON_FRAME = new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalSeedFrame.png");
	private static final ResourceLocation MUTATION_TEMPLATE = new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalMutationTemplate.png");
	private static final ResourceLocation QUESTION_MARK = new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalQuestionMark.png");
	private static final ResourceLocation BRIGHTNESS_BAR = new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalBrightnessBar.png");
	private static final ResourceLocation BRIGHTNESS_FRAME = new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalBrightnessFrame.png");

	private List<IAgriPlant> discoveredSeeds;
	private int page;

	private IAgriPlant plant;

	private List<Component<ItemStack>> fruits;
	private List<Component<ItemStack>> seeds;

	public JournalPageSeed(List<IAgriPlant> discoveredSeeds, int page) {
		this.discoveredSeeds = discoveredSeeds;
		this.page = page;
		this.plant = discoveredSeeds.get(page);
		this.fruits = getFruits();
		this.seeds = getSeeds();
	}

	@Override
	public ResourceLocation getForeground() {
		return new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalSeedPage.png");
	}

	@Override
	public ArrayList<String> getTooltip(int x, int y) {
		for (Component<ItemStack> component : fruits) {
			if (component.isOverComponent(x, y)) {
				ArrayList<String> toolTip = new ArrayList<>();
				toolTip.add(component.getComponent().getDisplayName());
				return toolTip;
			}
		}
		for (Component<ItemStack> component : seeds) {
			if (component.isOverComponent(x, y)) {
				ArrayList<String> toolTip = new ArrayList<>();
				toolTip.add(component.getComponent().getDisplayName());
				return toolTip;
			}
		}
		return null;
	}

	@Override
	public int getPagesToBrowseOnMouseClick(int x, int y) {
		for (Component<ItemStack> component : seeds) {
			if (component.isOverComponent(x, y)) {
				ItemStack selected = component.getComponent();
				for (int i = 0; i < discoveredSeeds.size(); i++) {
					ItemStack current = discoveredSeeds.get(i).getSeed();
					if (selected.getItem() == current.getItem() && selected.getItemDamage() == current.getItemDamage()) {
						return i - page;
					}
				}
				break;
			}
		}
		return 0;
	}

	// *************************** //
	// TEXT TO RENDER ON THIS PAGE //
	// *************************** //
	@Override
	public ArrayList<Component<String>> getTextComponents() {
		ArrayList<Component<String>> textComponents = new ArrayList<>();
		textComponents.add(getTitle());
		textComponents.add(getDescriptionHead());
		textComponents.add(getSeedInformation());
		textComponents.add(getTier());
		textComponents.add(getBrightnessTitle());
		textComponents.add(getFruitTitle());
		textComponents.add(getGrowthTitle());
		textComponents.addAll(getMutationTitles());
		return textComponents;
	}

	private Component<String> getTitle() {
		String text = plant.getSeedName();
		int x = 82;
		int y = 17;
		float scale = 0.8F;
		while (Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) * scale > 74) {
			scale = scale - 0.1F;
		}
		return new Component<>(text, x, y, scale, true);
	}

	private Component<String> getDescriptionHead() {
		String text = AgriCore.getTranslator().translate("agricraft_journal.information") + ": ";
		int x = 29;
		int y = 31;
		float scale = 0.5F;
		return new Component<>(text, x, y, scale);
	}

	private Component<String> getSeedInformation() {
		String text = AgriCore.getTranslator().translate(plant.getInformation());
		int x = 29;
		int y = 38;
		float scale = 0.5F;
		return new Component<>(text, x, y, scale);
	}

	private Component<String> getTier() {
		String text = AgriCore.getTranslator().translate("agricraft_journal.tier") + ": " + plant.getTier();
		int x = 29;
		int y = 66;
		float scale = 0.5F;
		return new Component<>(text, x, y, scale);
	}

	private Component<String> getBrightnessTitle() {
		String text = AgriCore.getTranslator().translate("agricraft_journal.brightness") + ": ";
		int x = 29;
		int y = 76;
		float scale = 0.5F;
		return new Component<>(text, x, y, scale);
	}

	private Component<String> getFruitTitle() {
		String text = AgriCore.getTranslator().translate("agricraft_journal.fruits") + ": ";
		int x = 29;
		int y = 95;
		float scale = 0.5F;
		return new Component<>(text, x, y, scale);
	}

	private Component<String> getGrowthTitle() {
		String text = AgriCore.getTranslator().translate("agricraft_journal.growthStages") + ": ";
		int x = 29;
		int y = 122;
		float scale = 0.5F;
		return new Component<>(text, x, y, scale);
	}

	private ArrayList<Component<String>> getMutationTitles() {
		String text = AgriCore.getTranslator().translate("agricraft_journal.mutations") + ": ";
		int x = 132;
		int y = 13;
		float scale = 0.5F;
		ArrayList<Component<String>> list = new ArrayList<>();
		list.add(new Component<>(text, x, y, scale));
		return list;
	}

	// **************************** //
	// ITEMS TO RENDER ON THIS PAGE //
	// **************************** //
	@Override
	public ArrayList<Component<ItemStack>> getItemComponents() {
		ArrayList<Component<ItemStack>> itemComponents = new ArrayList<>();
		itemComponents.add(getSeed());
		itemComponents.addAll(fruits);
		itemComponents.addAll(seeds);
		return itemComponents;
	}

	private Component<ItemStack> getSeed() {
		ItemStack stack = plant.getSeed();
		int x = 26;
		int y = 11;
		return new Component<>(stack, x, y);
	}

	private List<Component<ItemStack>> getFruits() {
		if (this.plant == null) {
			this.plant = discoveredSeeds.get(page);
		}
		List<Component<ItemStack>> fruits = new ArrayList<>();
		List<ItemStack> allFruits = plant.getAllFruits();
		if (allFruits != null) {
			for (int i = 0; i < allFruits.size(); i++) {
				ItemStack stack = allFruits.get(i);
				if (stack != null && stack.getItem() != null) {
					int x = 30 + 24 * i;
					int y = 102;
					fruits.add(new Component<>(stack, x, y, 16, 16));
				}
			}
		}
		return fruits;
	}

	private List<Component<ItemStack>> getSeeds() {
		List<IAgriMutation> completedMutations = getCompletedMutations();
		List<IAgriMutation> uncompletedMutations = getUncompleteMutations();
		List<Component<ItemStack>> seeds = new ArrayList<>();
		int y = 1;
		int x = 132;
		for (IAgriMutation mutation : completedMutations) {
			y = y + 20;
			ItemStack resultStack = mutation.getChild().getSeed();
			ItemStack parent1Stack = mutation.getParents()[0].getSeed();
			ItemStack parent2Stack = mutation.getParents()[1].getSeed();
			seeds.add(new Component<>(parent1Stack, x, y, 16, 16));
			seeds.add(new Component<>(parent2Stack, x + 35, y, 16, 16));
			seeds.add(new Component<>(resultStack, x + 69, y, 16, 16));
		}
		for (IAgriMutation mutation : uncompletedMutations) {
			y = y + 20;
			ItemStack parent1Stack = mutation.getParents()[0].getSeed();
			ItemStack parent2Stack = mutation.getParents()[1].getSeed();
			seeds.add(new Component<>(parent1Stack, x, y, 16, 16));
			seeds.add(new Component<>(parent2Stack, x + 35, y, 16, 16));
		}
		return seeds;
	}

	private List<IAgriMutation> getCompletedMutations() {
		List<IAgriMutation> mutations = getDiscoveredParentMutations();
		mutations.addAll(getDiscoveredChildMutations());
		return mutations;
	}

	private List<IAgriMutation> getDiscoveredParentMutations() {
		ArrayList<IAgriMutation> allMutations = new ArrayList<>();
		ArrayList<IAgriMutation> mutations = new ArrayList<>();
		allMutations.addAll(Arrays.asList(MutationHandler.getMutationsFromParent(discoveredSeeds.get(page))));
		for (IAgriMutation mutation : allMutations) {
			if (isMutationDiscovered(mutation)) {
				mutations.add(mutation);
			}
		}
		return mutations;
	}

	private List<IAgriMutation> getDiscoveredChildMutations() {
		ArrayList<IAgriMutation> allMutations = new ArrayList<>();
		ArrayList<IAgriMutation> mutations = new ArrayList<>();
		allMutations.addAll(Arrays.asList(MutationHandler.getMutationsFromChild(discoveredSeeds.get(page))));
		for (IAgriMutation mutation : allMutations) {
			if (isMutationDiscovered(mutation)) {
				mutations.add(mutation);
			}
		}
		return mutations;
	}

	private List<IAgriMutation> getUncompleteMutations() {
		ArrayList<IAgriMutation> allMutations = new ArrayList<>();
		ArrayList<IAgriMutation> mutations = new ArrayList<>();
		allMutations.addAll(Arrays.asList(MutationHandler.getMutationsFromParent(discoveredSeeds.get(page))));
		for (IAgriMutation mutation : allMutations) {
			if (isMutationHalfDiscovered(mutation)) {
				mutations.add(mutation);
			}
		}
		return mutations;
	}

	private boolean isMutationDiscovered(IAgriMutation mutation) {
		return areParentsDiscovered(mutation) && isSeedDiscovered(mutation.getChild());
	}

	private boolean isMutationHalfDiscovered(IAgriMutation mutation) {
		return areParentsDiscovered(mutation) && !isSeedDiscovered(mutation.getChild());

	}

	private boolean areParentsDiscovered(IAgriMutation mutation) {
		return this.discoveredSeeds.containsAll(Arrays.asList(mutation.getParents()));
	}

	private boolean isSeedDiscovered(IAgriPlant seed) {
		return this.discoveredSeeds.contains(seed);
	}

	// ******************************* //
	// TEXTURES TO RENDER ON THIS PAGE //
	// ******************************* //
	@Override
	public ArrayList<Component<ResourceLocation>> getTextureComponents() {
		ArrayList<Component<ResourceLocation>> textureComponents = new ArrayList<>();
		textureComponents.addAll(getBrightnessTextures());
		textureComponents.addAll(getFruitIconFrames());
		textureComponents.addAll(getMutationTemplates());
		textureComponents.addAll(getGrowthStageIcons());
		return textureComponents;
	}

	private ArrayList<Component<ResourceLocation>> getBrightnessTextures() {
		ArrayList<Component<ResourceLocation>> textures = new ArrayList<>();
		int x = 29;
		int y = 81;
		int u = 4;
		int v = 8;
		int[] brightnessRange = plant.getGrowthRequirement().getBrightnessRange();
		textures.add(new Component<>(BRIGHTNESS_BAR, x, y, 2 + 16 * u, v));
		textures.add(new Component<>(BRIGHTNESS_FRAME, x + u * brightnessRange[0], y, 1, v));
		textures.add(new Component<>(BRIGHTNESS_FRAME, x + u * brightnessRange[1] + 1, y, 1, v));
		textures.add(new Component<>(BRIGHTNESS_FRAME, x + u * brightnessRange[0] + 1, y, u * (brightnessRange[1] - brightnessRange[0]), 1));
		textures.add(new Component<>(BRIGHTNESS_FRAME, x + u * brightnessRange[0] + 1, y + v - 1, u * (brightnessRange[1] - brightnessRange[0]), 1));
		return textures;
	}

	private List<Component<ResourceLocation>> getFruitIconFrames() {
		if (this.fruits == null) {
			this.fruits = getFruits();
		}
		ArrayList<Component<ResourceLocation>> components = new ArrayList<>();
		for (int i = 0; i < fruits.size(); i++) {
			components.add(new Component<>(ICON_FRAME, 29 + 24 * i, 101, 18, 18));
		}
		return components;
	}

	private ArrayList<Component<ResourceLocation>> getMutationTemplates() {
		int n = getCompletedMutations().size();
		int l = getUncompleteMutations().size();
		ArrayList<Component<ResourceLocation>> components = new ArrayList<>();
		int y = 0;
		for (int i = 0; i < n; i++) {
			y = y + 20;
			components.add(new Component<>(MUTATION_TEMPLATE, 132, y, 86, 18));
		}
		for (int i = 0; i < l; i++) {
			y = y + 20;
			components.add(new Component<>(MUTATION_TEMPLATE, 132, y, 86, 18));
			components.add(new Component<>(QUESTION_MARK, 201, y + 1, 16, 16));
		}
		return components;
	}

	private ArrayList<Component<ResourceLocation>> getGrowthStageIcons() {
		ArrayList<Component<ResourceLocation>> growthStages = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			int x = 30 + 24 * (i % 4);
			int y = 129 + 24 * (i / 4);
			// Hackity-Hack, don't talk back. This is pure magic.
			final ResourceLocation loc = new ResourceLocation(plant.getPrimaryPlantTexture(i).toString().replace(":blocks/", ":textures/blocks/").concat(".png"));
			growthStages.add(new Component<>(loc, x, y, 16, 16));
		}
		return growthStages;
	}
}
