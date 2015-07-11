package com.InfinityRaider.AgriCraft.gui.journal;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.farming.mutation.Mutation;
import com.InfinityRaider.AgriCraft.farming.mutation.MutationHandler;
import com.InfinityRaider.AgriCraft.gui.Component;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import java.util.ArrayList;
import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class JournalPageSeed extends JournalPage {
    private static final ResourceLocation ICON_FRAME = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/journal/GuiJournalSeedFrame.png");
    private static final ResourceLocation MUTATION_TEMPLATE = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/journal/GuiJournalMutationTemplate.png");

    private ArrayList<ItemStack> discoveredSeeds;
    private int page;

    private CropPlant plant;

    private ArrayList<Component<ItemStack>> fruits;
    private ArrayList<Component<ItemStack>> seeds;

    public JournalPageSeed(ArrayList<ItemStack> discoveredSeeds, int page) {
        this.discoveredSeeds = discoveredSeeds;
        this.page = page;
        this.plant = CropPlantHandler.getPlantFromStack(discoveredSeeds.get(page));
        this.fruits = getFruits();
        this.seeds = getSeeds();
    }

    @Override
    public ResourceLocation getForeground() {
        return new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/journal/GuiJournalSeedPage.png");
    }

    @Override
    public ArrayList<String> getTooltip(int x, int y) {
        for(Component<ItemStack> component:fruits) {
            if(component.isOverComponent(x, y)) {
                ArrayList<String> toolTip = new ArrayList<String>();
                toolTip.add(component.getComponent().getDisplayName());
                return toolTip;
            }
        }
        for(Component<ItemStack> component:seeds) {
            if(component.isOverComponent(x, y)) {
                ArrayList<String> toolTip = new ArrayList<String>();
                toolTip.add(component.getComponent().getDisplayName());
                return toolTip;
            }
        }
        return null;
    }

    @Override
    public int getPagesToBrowseOnMouseClick(int x, int y) {
        for(Component<ItemStack> component:seeds) {
            if(component.isOverComponent(x, y)) {
                ItemStack selected = component.getComponent();
                for(int i=0;i<discoveredSeeds.size();i++) {
                    ItemStack current = discoveredSeeds.get(i);
                    if(selected.getItem()==current.getItem() && selected.getItemDamage()==current.getItemDamage()) {
                        return i-page;
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
        ArrayList<Component<String>> textComponents = new ArrayList<Component<String>>();
        textComponents.add(getTitle());
        textComponents.add(getDescriptionHead());
        textComponents.add(getSeedInformation());
        textComponents.add(getTier());
        textComponents.add(getFruitTitle());
        textComponents.add(getGrowthTitle());
        textComponents.add(getMutationTitle());
        return textComponents;
    }

    private Component<String> getTitle() {
        String text = plant.getSeed().getDisplayName();
        int x = 82;
        int y = 17;
        float scale = 0.8F;
        while (Minecraft.getMinecraft().fontRenderer.getStringWidth(text) * scale > 74) {
            scale = scale - 0.1F;
        }
        return new Component<String>(text, x, y, scale, true);
    }

    private Component<String> getDescriptionHead() {
        String text = StatCollector.translateToLocal("agricraft_journal.information") + ": ";
        int x = 29;
        int y = 31;
        float scale = 0.5F;
        return new Component<String>(text, x, y, scale);
    }

    private Component<String> getSeedInformation() {
        String text = StatCollector.translateToLocal(plant.getInformation());
        int x = 29;
        int y = 38;
        float scale = 0.5F;
        return new Component<String>(text, x, y, scale);
    }

    private Component<String> getTier() {
        String text = StatCollector.translateToLocal("agricraft_journal.tier") + ": " + plant.getTier();
        int x = 29;
        int y = 70;
        float scale = 0.5F;
        return new Component<String>(text, x , y, scale);
    }

    private Component<String> getFruitTitle() {
        String text = StatCollector.translateToLocal("agricraft_journal.fruits") + ": ";
        int x = 29;
        int y = 84;
        float scale = 0.5F;
        return new Component<String>(text, x, y, scale);
    }

    private Component<String> getGrowthTitle() {
        String text = StatCollector.translateToLocal("agricraft_journal.growthStages") + ": ";
        int x = 29;
        int y = 117;
        float scale = 0.5F;
        return new Component<String>(text, x, y, scale);
    }

    private Component<String> getMutationTitle() {
        String text = StatCollector.translateToLocal("agricraft_journal.mutations") + ": ";
        int x = 132;
        int y = 13;
        float scale = 0.5F;
        return new Component<String>(text, x, y, scale);
    }


    // **************************** //
    // ITEMS TO RENDER ON THIS PAGE //
    // **************************** //

    @Override
    public ArrayList<Component<ItemStack>> getItemComponents() {
        ArrayList<Component<ItemStack>> itemComponents = new ArrayList<Component<ItemStack>>();
        itemComponents.add(getSeed());
        itemComponents.addAll(fruits);
        itemComponents.addAll(seeds);
        return itemComponents;
    }

    private Component<ItemStack> getSeed() {
        ItemStack stack = plant.getSeed();
        int x = 26;
        int y = 11;
        return new Component<ItemStack>(stack, x, y);
    }

    private ArrayList<Component<ItemStack>> getFruits() {
        if(this.plant==null) {
            this.plant = CropPlantHandler.getPlantFromStack(discoveredSeeds.get(page));
        }
        ArrayList<Component<ItemStack>> fruits = new ArrayList<Component<ItemStack>>();
        ArrayList<ItemStack> allFruits = plant.getAllFruits();
        if(allFruits != null ) {
            for (int i = 0; i < allFruits.size(); i++) {
                ItemStack stack = allFruits.get(i);
                if (stack != null && stack.getItem() != null) {
                    int x = 30 + 24 * i;
                    int y = 91;
                    fruits.add(new Component<ItemStack>(stack, x, y, 16, 16));
                }
            }
        }
        return fruits;
    }

    private ArrayList<Component<ItemStack>> getSeeds() {
        ArrayList<Mutation> mutations = getDiscoveredMutations();
        ArrayList<Component<ItemStack>> seeds = new ArrayList<Component<ItemStack>>();
        for(int i = 0;i<mutations.size();i++) {
            Mutation mutation = mutations.get(i);
            int y = 21 + i*20;
            int x = 132;
            ItemStack resultStack = mutation.getResult();
            ItemStack parent1Stack = mutation.getParents()[0];
            ItemStack parent2Stack = mutation.getParents()[1];
            seeds.add(new Component<ItemStack>(parent1Stack, x, y, 16, 16));
            seeds.add(new Component<ItemStack>(parent2Stack, x + 35, y, 16, 16));
            seeds.add(new Component<ItemStack>(resultStack, x + 69, y, 16, 16));
        }
        return seeds;
    }

    private ArrayList<Mutation> getDiscoveredMutations() {
        ArrayList<Mutation> allMutations = new ArrayList<Mutation>();
        ArrayList<Mutation> mutations = new ArrayList<Mutation>();
        allMutations.addAll(Arrays.asList(MutationHandler.getMutationsFromParent(discoveredSeeds.get(page))));
        allMutations.addAll(Arrays.asList(MutationHandler.getMutationsFromChild(discoveredSeeds.get(page))));
        for(Mutation mutation:allMutations) {
            if(isMutationDiscovered(mutation)) {
                mutations.add(mutation);
            }
        }
        return mutations;
    }

    private boolean isMutationDiscovered(Mutation mutation) {
        ItemStack resultStack = mutation.getResult();
        ItemStack parent1Stack = mutation.getParents()[0];
        ItemStack parent2Stack = mutation.getParents()[1];
        return isSeedDiscovered(parent1Stack) && isSeedDiscovered(parent2Stack) && isSeedDiscovered(resultStack);
    }

    private boolean isSeedDiscovered(ItemStack seed) {
        for (ItemStack current:discoveredSeeds) {
            if(current.getItem()==seed.getItem() && current.getItemDamage()==seed.getItemDamage()) {
                return true;
            }
        }
        return false;
    }


    // ******************************* //
    // TEXTURES TO RENDER ON THIS PAGE //
    // ******************************* //

    @Override
    public ArrayList<Component<ResourceLocation>> getTextureComponents() {
        ArrayList<Component<ResourceLocation>> textureComponents = new ArrayList<Component<ResourceLocation>>();
        textureComponents.add(getSoil());
        textureComponents.addAll(getGrowthStages());
        textureComponents.addAll(getFruitIconFrames());
        textureComponents.addAll(getMutationTemplates());
        return textureComponents;
    }

    private Component<ResourceLocation> getSoil() {
        ItemStack seed = plant.getSeed();
        BlockWithMeta soil = GrowthRequirementHandler.getGrowthRequirement(seed.getItem(), seed.getItemDamage()).getSoil();
        ResourceLocation texture;
        if (soil != null) {
            texture = RenderHelper.getBlockResource(soil.getBlock().getIcon(1, soil.getMeta()));
        } else {
            texture = RenderHelper.getBlockResource(Blocks.farmland.getIcon(1, 7));
        }
        int x = 26;
        int y = 11;
        return new Component<ResourceLocation>(texture, x, y, 16, 16);
    }

    private ArrayList<Component<ResourceLocation>> getGrowthStages() {
        ArrayList<Component<ResourceLocation>> growthStages = new ArrayList<Component<ResourceLocation>>();
        for(int i=0;i<8;i++) {
            ResourceLocation texture = RenderHelper.getBlockResource(plant.getPlantIcon(i));
            int x = 30 + 24 * (i % 4);
            int y = 124 + 24 * (i / 4);
            growthStages.add(new Component<ResourceLocation>(texture, x, y, 16, 16));
        }
        return growthStages;
    }

    private ArrayList<Component<ResourceLocation>> getFruitIconFrames() {
        if(this.fruits==null) {
            this.fruits = getFruits();
        }
        ArrayList<Component<ResourceLocation>> components = new ArrayList<Component<ResourceLocation>>();
        for(int i=0;i<fruits.size();i++) {
            components.add(new Component<ResourceLocation>(ICON_FRAME, 29+24*i, 90, 18, 18));
        }
        return components;
    }

    private ArrayList<Component<ResourceLocation>> getMutationTemplates() {
        if(this.seeds == null) {
            this.seeds = getSeeds();
        }
        ArrayList<Component<ResourceLocation>> components = new ArrayList<Component<ResourceLocation>>();
        for(int i=0;i<seeds.size()/3;i++) {
            components.add(new Component<ResourceLocation>(MUTATION_TEMPLATE, 132, 20+20*i, 86, 18));
        }
        return components;
    }

}
