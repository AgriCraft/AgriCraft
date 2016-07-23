package com.infinityraider.agricraft.gui.journal;

import com.infinityraider.agricraft.gui.component.Component;
import com.infinityraider.agricraft.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import com.agricraft.agricore.core.AgriCore;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.mutation.IAgriMutation;
import com.infinityraider.agricraft.apiimpl.MutationRegistry;
import com.infinityraider.agricraft.gui.component.ComponentItem;
import com.infinityraider.agricraft.gui.component.ComponentText;
import com.infinityraider.agricraft.gui.component.ComponentTexture;
import java.util.stream.Collectors;

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

	private List<ComponentItem> fruits = new ArrayList<>();
	private List<ComponentItem> seeds = new ArrayList<>();

	public JournalPageSeed(List<IAgriPlant> discoveredSeeds, int page) {
		this.discoveredSeeds = discoveredSeeds;
		this.page = page;
		this.plant = discoveredSeeds.get(page);
		addFruits(fruits);
		addSeeds(seeds);
	}

	@Override
	public ResourceLocation getForeground() {
		return new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalSeedPage.png");
	}

	@Override
	public void addTooltip(int x, int y, List<String> toolTip) {
		for (ComponentItem component : fruits) {
			if (component.isOverComponent(x, y)) {
				toolTip.add(component.getComponent().getDisplayName());
				return;
			}
		}
		for (ComponentItem component : seeds) {
			if (component.isOverComponent(x, y)) {
				toolTip.add(component.getComponent().getDisplayName());
				return;
			}
		}
	}

	@Override
	public int getPagesToBrowseOnMouseClick(int x, int y) {
		for (ComponentItem component : seeds) {
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
	public void addTextComponents(List<ComponentText> textComponents) {
		textComponents.add(getTitle());
		textComponents.add(getDescriptionHead());
		textComponents.add(getSeedInformation());
		textComponents.add(getTier());
		textComponents.add(getBrightnessTitle());
		textComponents.add(getFruitTitle());
		textComponents.add(getGrowthTitle());
		textComponents.addAll(getMutationTitles());
	}

	private ComponentText getTitle() {
		String text = plant.getSeedName();
		int x = 82;
		int y = 17;
		float scale = 0.8F;
		while (Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) * scale > 74) {
			scale = scale - 0.1F;
		}
		return new ComponentText(text, x, y, scale, true);
	}

	private ComponentText getDescriptionHead() {
		String text = AgriCore.getTranslator().translate("agricraft_journal.information") + ": ";
		int x = 29;
		int y = 31;
		float scale = 0.5F;
		return new ComponentText(text, x, y, scale, false);
	}

	private ComponentText getSeedInformation() {
		String text = AgriCore.getTranslator().translate(plant.getInformation());
		int x = 29;
		int y = 38;
		float scale = 0.5F;
		return new ComponentText(text, x, y, scale, false);
	}

	private ComponentText getTier() {
		String text = AgriCore.getTranslator().translate("agricraft_journal.tier") + ": " + plant.getTier();
		int x = 29;
		int y = 66;
		float scale = 0.5F;
		return new ComponentText(text, x, y, scale, false);
	}

	private ComponentText getBrightnessTitle() {
		String text = AgriCore.getTranslator().translate("agricraft_journal.brightness") + ": ";
		int x = 29;
		int y = 76;
		float scale = 0.5F;
		return new ComponentText(text, x, y, scale, false);
	}

	private ComponentText getFruitTitle() {
		String text = AgriCore.getTranslator().translate("agricraft_journal.fruits") + ": ";
		int x = 29;
		int y = 95;
		float scale = 0.5F;
		return new ComponentText(text, x, y, scale, false);
	}

	private ComponentText getGrowthTitle() {
		String text = AgriCore.getTranslator().translate("agricraft_journal.growthStages") + ": ";
		int x = 29;
		int y = 122;
		float scale = 0.5F;
		return new ComponentText(text, x, y, scale, false);
	}

	private List<ComponentText> getMutationTitles() {
		String text = AgriCore.getTranslator().translate("agricraft_journal.mutations") + ": ";
		int x = 132;
		int y = 13;
		float scale = 0.5F;
		List<ComponentText> list = new ArrayList<>();
		list.add(new ComponentText(text, x, y, scale, false));
		return list;
	}

	// **************************** //
	// ITEMS TO RENDER ON THIS PAGE //
	// **************************** //
	@Override
	public void addItemComponents(List<ComponentItem> components) {
		addSeed(components);
		addFruits(components);
		addSeeds(components);
	}

	private void addSeed(List<ComponentItem> components) {
		ItemStack stack = plant.getSeed();
		int x = 26;
		int y = 11;
		components.add(new ComponentItem(stack, x, y));
	}

	private void addFruits(List<ComponentItem> components) {
		if (this.plant == null) {
			this.plant = discoveredSeeds.get(page);
		}
		List<ItemStack> allFruits = plant.getAllFruits();
		if (allFruits != null) {
			for (int i = 0; i < allFruits.size(); i++) {
				ItemStack stack = allFruits.get(i);
				if (stack != null && stack.getItem() != null) {
					int x = 30 + 24 * i;
					int y = 102;
					components.add(new ComponentItem(stack, x, y));
				}
			}
		}
	}

	private void addSeeds(List<ComponentItem> components) {
		List<IAgriMutation> completedMutations = getCompletedMutations();
		List<IAgriMutation> uncompletedMutations = getUncompleteMutations();
		int y = 1;
		int x = 132;
		for (IAgriMutation mutation : completedMutations) {
			y = y + 20;
			ItemStack resultStack = mutation.getChild().getSeed();
			ItemStack parent1Stack = mutation.getParents().get(0).getSeed();
			ItemStack parent2Stack = mutation.getParents().get(1).getSeed();
			components.add(new ComponentItem(parent1Stack, x, y));
			components.add(new ComponentItem(parent2Stack, x + 35, y));
			components.add(new ComponentItem(resultStack, x + 69, y));
		}
		for (IAgriMutation mutation : uncompletedMutations) {
			y = y + 20;
			ItemStack parent1Stack = mutation.getParents().get(0).getSeed();
			ItemStack parent2Stack = mutation.getParents().get(1).getSeed();
			components.add(new ComponentItem(parent1Stack, x, y));
			components.add(new ComponentItem(parent2Stack, x + 35, y));
		}
	}

	private List<IAgriMutation> getCompletedMutations() {
		List<IAgriMutation> mutations = getDiscoveredParentMutations();
		mutations.addAll(getDiscoveredChildMutations());
		return mutations;
	}

	private List<IAgriMutation> getDiscoveredParentMutations() {
		return MutationRegistry.getInstance().getMutationsForParent(discoveredSeeds.get(page)).stream()
				.filter(this::isMutationDiscovered)
				.collect(Collectors.toList());
	}

	private List<IAgriMutation> getDiscoveredChildMutations() {
		return MutationRegistry.getInstance().getMutationsForChild(discoveredSeeds.get(page)).stream()
				.filter(this::isMutationDiscovered)
				.collect(Collectors.toList());
	}

	private List<IAgriMutation> getUncompleteMutations() {
		return MutationRegistry.getInstance().getMutationsForParent(discoveredSeeds.get(page)).stream()
				.filter(this::isMutationHalfDiscovered)
				.collect(Collectors.toList());
	}

	private boolean isMutationDiscovered(IAgriMutation mutation) {
		return areParentsDiscovered(mutation) && isSeedDiscovered(mutation.getChild());
	}

	private boolean isMutationHalfDiscovered(IAgriMutation mutation) {
		return areParentsDiscovered(mutation) && !isSeedDiscovered(mutation.getChild());

	}

	private boolean areParentsDiscovered(IAgriMutation mutation) {
		return this.discoveredSeeds.containsAll(mutation.getParents());
	}

	private boolean isSeedDiscovered(IAgriPlant seed) {
		return this.discoveredSeeds.contains(seed);
	}

	// ******************************* //
	// TEXTURES TO RENDER ON THIS PAGE //
	// ******************************* //
	@Override
	public void addTextureComponents(List<ComponentTexture> textureComponents) {
		addBrightnessTextures(textureComponents);
		addFruitIconFrames(textureComponents);
		addMutationTemplates(textureComponents);
		addGrowthStageIcons(textureComponents);
	}

	private void addBrightnessTextures(List<ComponentTexture> textures) {
		int x = 29;
		int y = 81;
		int u = 4;
		int v = 8;
		int[] brightnessRange = plant.getGrowthRequirement().getBrightnessRange();
		textures.add(new ComponentTexture(BRIGHTNESS_BAR, x, y, 2 + 16 * u, v));
		textures.add(new ComponentTexture(BRIGHTNESS_FRAME, x + u * brightnessRange[0], y, 1, v));
		textures.add(new ComponentTexture(BRIGHTNESS_FRAME, x + u * brightnessRange[1] + 1, y, 1, v));
		textures.add(new ComponentTexture(BRIGHTNESS_FRAME, x + u * brightnessRange[0] + 1, y, u * (brightnessRange[1] - brightnessRange[0]), 1));
		textures.add(new ComponentTexture(BRIGHTNESS_FRAME, x + u * brightnessRange[0] + 1, y + v - 1, u * (brightnessRange[1] - brightnessRange[0]), 1));
	}

	private void addFruitIconFrames(List<ComponentTexture> components) {
		for (int i = 0; i < fruits.size(); i++) {
			components.add(new ComponentTexture(ICON_FRAME, 29 + 24 * i, 101, 18, 18));
		}
	}

	private void addMutationTemplates(List<ComponentTexture> components) {
		int n = getCompletedMutations().size();
		int l = getUncompleteMutations().size();
		int y = 0;
		for (int i = 0; i < n; i++) {
			y = y + 20;
			components.add(new ComponentTexture(MUTATION_TEMPLATE, 132, y, 86, 18));
		}
		for (int i = 0; i < l; i++) {
			y = y + 20;
			components.add(new ComponentTexture(MUTATION_TEMPLATE, 132, y, 86, 18));
			components.add(new ComponentTexture(QUESTION_MARK, 201, y + 1, 16, 16));
		}
	}

	private void addGrowthStageIcons(List<ComponentTexture> components) {
		for (int i = 0; i < 8; i++) {
			int x = 30 + 24 * (i % 4);
			int y = 129 + 24 * (i / 4);
			// Hackity-Hack, don't talk back. This is pure magic.
			final ResourceLocation loc = new ResourceLocation(plant.getPrimaryPlantTexture(i).toString().replace(":blocks/", ":textures/blocks/").concat(".png"));
			components.add(new ComponentTexture(loc, x, y, 16, 16));
		}
	}
}
