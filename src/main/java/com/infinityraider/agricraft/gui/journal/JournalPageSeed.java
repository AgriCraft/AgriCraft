package com.infinityraider.agricraft.gui.journal;

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
import com.infinityraider.agricraft.gui.component.ComponentStack;
import com.infinityraider.agricraft.gui.component.ComponentText;
import com.infinityraider.agricraft.gui.component.ComponentIcon;
import com.infinityraider.agricraft.gui.component.IComponent;
import java.util.Arrays;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class JournalPageSeed extends JournalPage {

	private static final ResourceLocation MUTATION_TEMPLATE = new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalMutationTemplate.png");
	private static final ResourceLocation QUESTION_MARK = new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalQuestionMark.png");
	private static final ResourceLocation BRIGHTNESS_BAR = new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalBrightnessBar.png");
	private static final ResourceLocation BRIGHTNESS_FRAME = new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalBrightnessFrame.png");
	
	private final int page;
	private final IAgriPlant plant;
	private final List<IAgriPlant> discoveredSeeds;
	
	private final List<IComponent> fruits = new ArrayList<>();
	private final List<IComponent> seeds = new ArrayList<>();

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
		// Pre-Handled
	}

	@Override
	public int getPagesToBrowseOnMouseClick(int x, int y) {
		for (IComponent component : seeds) {
			if (component.isOverComponent(x, y)) {
				ItemStack selected = (ItemStack) component.getComponent();
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
	public void addComponents(List<IComponent> components) {
		components.add(getTitle());
		components.add(getDescriptionHead());
		components.add(getSeedInformation());
		components.add(getTier());
		components.add(getBrightnessTitle());
		components.add(getFruitTitle());
		components.add(getGrowthTitle());
		components.addAll(getMutationTitles());
		
		// Items
		addSeed(components);
		addFruits(components);
		addSeeds(components);
		
		// Textures
		addBrightnessTextures(components);
		addMutationTemplates(components);
		addGrowthStageIcons(components);
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
	private void addSeed(List<IComponent> components) {
		ItemStack stack = plant.getSeed();
		components.add(new ComponentStack(stack, 26, 11, false));
	}

	private void addFruits(List<IComponent> components) {
		int x = 30;
		for (ItemStack stack : discoveredSeeds.get(page).getAllFruits()) {
			components.add(new ComponentStack(stack, x, 102, true));
			x += 24;
		}
	}

	private void addSeeds(List<IComponent> components) {
		List<IAgriMutation> completedMutations = getCompletedMutations();
		List<IAgriMutation> uncompletedMutations = getUncompleteMutations();
		int y = 1;
		int x = 132;
		for (IAgriMutation mutation : completedMutations) {
			y = y + 20;
			ItemStack resultStack = mutation.getChild().getSeed();
			ItemStack parent1Stack = mutation.getParents().get(0).getSeed();
			ItemStack parent2Stack = mutation.getParents().get(1).getSeed();
			components.add(new ComponentStack(parent1Stack, x, y, false));
			components.add(new ComponentStack(parent2Stack, x + 35, y, false));
			components.add(new ComponentStack(resultStack, x + 69, y, false));
		}
		for (IAgriMutation mutation : uncompletedMutations) {
			y = y + 20;
			ItemStack parent1Stack = mutation.getParents().get(0).getSeed();
			ItemStack parent2Stack = mutation.getParents().get(1).getSeed();
			components.add(new ComponentStack(parent1Stack, x, y, false));
			components.add(new ComponentStack(parent2Stack, x + 35, y, false));
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
	private void addBrightnessTextures(List<IComponent> textures) {
		int x = 29;
		int y = 81;
		int u = 4;
		int v = 8;
		int[] brightnessRange = plant.getGrowthRequirement().getBrightnessRange();
		textures.add(new ComponentIcon(BRIGHTNESS_BAR, x, y, 2 + 16 * u, v, Arrays.toString(brightnessRange)));
		textures.add(new ComponentIcon(BRIGHTNESS_FRAME, x + u * brightnessRange[0], y, 1, v));
		textures.add(new ComponentIcon(BRIGHTNESS_FRAME, x + u * brightnessRange[1] + 1, y, 1, v));
		textures.add(new ComponentIcon(BRIGHTNESS_FRAME, x + u * brightnessRange[0] + 1, y, u * (brightnessRange[1] - brightnessRange[0]), 1));
		textures.add(new ComponentIcon(BRIGHTNESS_FRAME, x + u * brightnessRange[0] + 1, y + v - 1, u * (brightnessRange[1] - brightnessRange[0]), 1));
	}

	private void addMutationTemplates(List<IComponent> components) {
		int n = getCompletedMutations().size();
		int l = getUncompleteMutations().size();
		int y = 0;
		for (int i = 0; i < n; i++) {
			y = y + 20;
			components.add(new ComponentIcon(MUTATION_TEMPLATE, 132, y, 86, 18));
		}
		for (int i = 0; i < l; i++) {
			y = y + 20;
			components.add(new ComponentIcon(MUTATION_TEMPLATE, 132, y, 86, 18));
			components.add(new ComponentIcon(QUESTION_MARK, 201, y + 1, 16, 16));
		}
	}

	private void addGrowthStageIcons(List<IComponent> components) {
		for (int i = 0; i < 8; i++) {
			int x = 30 + 24 * (i % 4);
			int y = 129 + 24 * (i / 4);
			// Hackity-Hack, don't talk back. This is pure magic.
			final ResourceLocation loc = new ResourceLocation(plant.getPrimaryPlantTexture(i).toString().replace(":blocks/", ":textures/blocks/").concat(".png"));
			components.add(new ComponentIcon(loc, x, y, 16, 16));
		}
	}
}
