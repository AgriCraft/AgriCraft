package com.infinityraider.agricraft.gui.journal;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.gui.component.BasicComponents;
import com.infinityraider.agricraft.gui.component.GuiComponent;
import com.infinityraider.agricraft.reference.Reference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class JournalPageSeed implements JournalPage {

    public static final int MUTATION_ROW_HEIGHT = 20;

    private static final ResourceLocation MUTATION_TEMPLATE = new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/gui_journal_mutation_template.png");
    private static final ResourceLocation QUESTION_MARK = new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/gui_journal_question_mark.png");
    private static final ResourceLocation BRIGHTNESS_BAR = new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/gui_journal_brightness_bar.png");
    private static final ResourceLocation BRIGHTNESS_FRAME = new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/gui_journal_brightness_frame.png");
    private static final ResourceLocation SEED_PAGE_FOREGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/gui_journal_page_seed.png");

    private final GuiJournal journal;
    private final int page;
    private final IAgriPlant plant;
    private final List<IAgriPlant> discoveredSeeds;

    private final List<GuiComponent> fruits = new ArrayList<>();
    private final List<GuiComponent> seeds = new ArrayList<>();

    public JournalPageSeed(GuiJournal journal, List<IAgriPlant> discoveredSeeds, int page) {
        this.journal = journal;
        this.discoveredSeeds = discoveredSeeds;
        this.page = page;
        this.plant = discoveredSeeds.get(page);
        addFruits(fruits);
        addSeeds(seeds);
    }

    @Override
    public ResourceLocation getForeground() {
        return SEED_PAGE_FOREGROUND;
    }

    @Override
    public void addTooltip(int x, int y, List<String> toolTip) {
        // Pre-Handled
    }

    // *************************** //
    // TEXT TO RENDER ON THIS PAGE //
    // *************************** //
    @Override
    public List<GuiComponent> getComponents() {
        final List<GuiComponent> components = new ArrayList<>();

        components.add(getTitle());
        components.add(getDescriptionHead());
        components.add(getSeedInformation());
        components.add(getBrightnessTitle());
        components.add(getFruitTitle());
        components.add(getGrowthTitle());
        components.add(getMutationTitle());

        // Items
        addSeed(components);
        addFruits(components);
        addSeeds(components);

        // Textures
        addBrightnessTextures(components);
        addMutationTemplates(components);
        addGrowthStageIcons(components);

        return components;
    }

    private GuiComponent<String> getTitle() {
        final String text = plant.getPlantName();
        double scale = 0.8F;
        while (Minecraft.getMinecraft().fontRenderer.getStringWidth(text) * scale > 74) {
            scale = scale - 0.1F;
        }
        return BasicComponents.getTextComponent(text, 82, 17, scale, true);
    }

    private GuiComponent<String> getDescriptionHead() {
        final String text = AgriCore.getTranslator().translate("agricraft_journal.information") + ": ";
        return BasicComponents.getTextComponent(text, 29, 31, 0.5);
    }

    private GuiComponent<String> getSeedInformation() {
        return BasicComponents.getTextComponent(plant.getInformation(), 29, 38, 0.5);
    }

    private GuiComponent<String> getBrightnessTitle() {
        final String text = AgriCore.getTranslator().translate("agricraft_journal.brightness") + ": ";
        return BasicComponents.getTextComponent(text, 29, 76, 0.5);
    }

    private GuiComponent<String> getFruitTitle() {
        final String text = AgriCore.getTranslator().translate("agricraft_journal.fruits") + ": ";
        return BasicComponents.getTextComponent(text, 29, 95, 0.5);
    }

    private GuiComponent<String> getGrowthTitle() {
        final String text = AgriCore.getTranslator().translate("agricraft_journal.growthStages") + ": ";
        return BasicComponents.getTextComponent(text, 29, 122, 0.5);
    }

    private GuiComponent<String> getMutationTitle() {
        final String text = AgriCore.getTranslator().translate("agricraft_journal.mutations") + ": ";
        return BasicComponents.getTextComponent(text, 132, 13, 0.5);
    }

    // **************************** //
    // ITEMS TO RENDER ON THIS PAGE //
    // **************************** //
    private void addSeed(List<GuiComponent> components) {
        components.add(BasicComponents.getStackComponent(plant.getSeed(), 26, 11));
    }

    private void addFruits(List<GuiComponent> components) {
        final AtomicInteger x = new AtomicInteger(30);
        discoveredSeeds.get(page).getPossibleProducts(
                p -> components.add(BasicComponents.getStackComponentFramed(p, x.getAndAdd(24), 102))
        );
    }

    private void addSeeds(List<GuiComponent> components) {
        List<IAgriMutation> completedMutations = getCompletedMutations();
        List<IAgriMutation> uncompletedMutations = getUncompleteMutations();
        int y = 1;
        int x = 132;
        for (IAgriMutation mutation : completedMutations) {

            // Increment Row.
            y = y + MUTATION_ROW_HEIGHT;

            // Child Component
            final ItemStack resultStack = mutation.getChild().getSeed();
            final GuiComponent child = BasicComponents.getStackComponent(resultStack, x + 69, y);
            child.setMouseClickAction((c, p) -> journal.switchPage(mutation.getChild()));
            components.add(child);

            // Parent 1 Component
            final ItemStack parent1Stack = mutation.getParents().get(0).getSeed();
            final GuiComponent parent1 = BasicComponents.getStackComponent(parent1Stack, x, y);
            parent1.setMouseClickAction((c, p) -> journal.switchPage(mutation.getParents().get(0)));
            components.add(parent1);

            // Parent 2 Component
            final ItemStack parent2Stack = mutation.getParents().get(1).getSeed();
            final GuiComponent parent2 = BasicComponents.getStackComponent(parent2Stack, x + 35, y);
            parent2.setMouseClickAction((c, p) -> journal.switchPage(mutation.getParents().get(1)));
            components.add(parent2);

        }
        for (IAgriMutation mutation : uncompletedMutations) {

            // Increment Row.
            y = y + MUTATION_ROW_HEIGHT;

            // Parent 1 Component
            final ItemStack parent1Stack = mutation.getParents().get(0).getSeed();
            final GuiComponent parent1 = BasicComponents.getStackComponent(parent1Stack, x, y);
            parent1.setMouseClickAction((c, p) -> journal.switchPage(mutation.getParents().get(0)));
            components.add(parent1);

            // Parent 2 Component
            final ItemStack parent2Stack = mutation.getParents().get(1).getSeed();
            final GuiComponent parent2 = BasicComponents.getStackComponent(parent2Stack, x + 35, y);
            parent2.setMouseClickAction((c, p) -> journal.switchPage(mutation.getParents().get(1)));
            components.add(parent2);
        }
    }

    private List<IAgriMutation> getCompletedMutations() {
        List<IAgriMutation> mutations = getDiscoveredParentMutations();
        mutations.addAll(getDiscoveredChildMutations());
        return mutations;
    }

    private List<IAgriMutation> getDiscoveredParentMutations() {
        // Fetch the seed associated with this page.
        final IAgriPlant plant = discoveredSeeds.get(page);
        // Find all discovered mutations.
        return AgriApi.getMutationRegistry().stream()
                // Filter out all mutations where this plant is not a parent.
                .filter(m -> m.hasParent(plant))
                // Filter out all muations that are not discovered.
                .filter(this::isMutationDiscovered)
                // Convert into a list.
                .collect(Collectors.toList());
    }

    private List<IAgriMutation> getDiscoveredChildMutations() {
        // Fetch the seed associated with this page.
        final IAgriPlant plant = discoveredSeeds.get(page);
        // Find all discovered mutations.
        return AgriApi.getMutationRegistry().stream()
                // Filter out all mutations where this plant is not the child.
                .filter(m -> m.hasChild(plant))
                // Filter out all muations that are not discovered.
                .filter(this::isMutationDiscovered)
                // Convert into a list.
                .collect(Collectors.toList());
    }

    private List<IAgriMutation> getUncompleteMutations() {
        // Fetch the seed associated with this page.
        final IAgriPlant plant = discoveredSeeds.get(page);
        // Find all discovered mutations.
        return AgriApi.getMutationRegistry().stream()
                // Filter out all mutations where this plant is not a parent.
                .filter(m -> m.hasParent(plant))
                // Filter out all muations that are not half-discovered.
                .filter(this::isMutationHalfDiscovered)
                // Convert into a list.
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
    private void addBrightnessTextures(List<GuiComponent> textures) {
        int x = 29;
        int y = 81;
        int u = 4;
        int v = 8;
        int[] brightnessRange = new int[]{
            plant.getGrowthRequirement().getMinLight(),
            plant.getGrowthRequirement().getMaxLight()
        };
        textures.add(BasicComponents.getIconComponent(BRIGHTNESS_BAR, x, y, 2 + 16 * u, v, Arrays.toString(brightnessRange)));
        textures.add(BasicComponents.getIconComponent(BRIGHTNESS_FRAME, x + u * brightnessRange[0], y, 1, v));
        textures.add(BasicComponents.getIconComponent(BRIGHTNESS_FRAME, x + u * brightnessRange[1] + 1, y, 1, v));
        textures.add(BasicComponents.getIconComponent(BRIGHTNESS_FRAME, x + u * brightnessRange[0] + 1, y, u * (brightnessRange[1] - brightnessRange[0]), 1));
        textures.add(BasicComponents.getIconComponent(BRIGHTNESS_FRAME, x + u * brightnessRange[0] + 1, y + v - 1, u * (brightnessRange[1] - brightnessRange[0]), 1));
    }

    private void addMutationTemplates(List<GuiComponent> components) {
        int n = getCompletedMutations().size();
        int l = getUncompleteMutations().size();
        int y = 0;
        for (int i = 0; i < n; i++) {
            y = y + 20;
            components.add(BasicComponents.getIconComponent(MUTATION_TEMPLATE, 132, y, 86, 18));
        }
        for (int i = 0; i < l; i++) {
            y = y + 20;
            components.add(BasicComponents.getIconComponent(MUTATION_TEMPLATE, 132, y, 86, 18));
            components.add(BasicComponents.getIconComponent(QUESTION_MARK, 201, y + 1, 16, 16));
        }
    }

    private void addGrowthStageIcons(List<GuiComponent> components) {
        for (int i = 0; i < 8; i++) {
            int x = 30 + 24 * (i % 4);
            int y = 129 + 24 * (i / 4);
            // Hackity-Hack, don't talk back. This is pure magic.
            final ResourceLocation loc = new ResourceLocation(plant.getPrimaryPlantTexture(i).toString().replace(":blocks/", ":textures/blocks/").concat(".png"));
            components.add(BasicComponents.getIconComponent(loc, x, y, 16, 16));
        }
    }
}
