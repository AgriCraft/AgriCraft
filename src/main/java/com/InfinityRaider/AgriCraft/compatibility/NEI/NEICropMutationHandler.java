package com.InfinityRaider.AgriCraft.compatibility.NEI;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v1.RequirementType;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.farming.mutation.Mutation;
import com.InfinityRaider.AgriCraft.farming.mutation.MutationHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NEICropMutationHandler extends TemplateRecipeHandler {
    //this is a class which handles the recipe for crop mutation (has to contain a CachedRecipe for the mutation)
    private static String name = StatCollector.translateToLocal("agricraft_nei.mutation.title");
    private static String id = "cropMutation";

    private static final int COLOR_BLACK = 1644054;

    //the class defining a mutation recipe (must be contained in a TemplateRecipeHandler class)
    public class CachedCropMutationRecipe extends TemplateRecipeHandler.CachedRecipe {
        //the stacks needed for a recipe (parent1 + parent2 = mutation)
        PositionedStack parent1;
        PositionedStack parent2;
        PositionedStack result;
        List<PositionedStack> soils = new ArrayList<PositionedStack>();
        PositionedStack requiredBlock;
        RequirementType requiredType;

        //constructor
        public CachedCropMutationRecipe(Mutation mutation) {
            ItemStack resultStack = mutation.getResult();
            ItemStack parent1Stack = mutation.getParents()[0];
            ItemStack parent2Stack = mutation.getParents()[1];
            this.parent1 = new PositionedStack(parent1Stack, Constants.nei_X_parent1, Constants.nei_Y_seeds);
            this.parent2 = new PositionedStack(parent2Stack, Constants.nei_X_parent2, Constants.nei_Y_seeds);
            this.result = new PositionedStack(resultStack, Constants.nei_X_result, Constants.nei_Y_seeds);

            IGrowthRequirement growthReq = GrowthRequirementHandler.getGrowthRequirement(result.item.getItem(), result.item.getItemDamage());
            if (growthReq.getSoil() != null) {
                soils.add(new PositionedStack(growthReq.getSoil().toStack(), Constants.nei_X_result, Constants.nei_Y_soil));
            } else {
                for (BlockWithMeta blockWithMeta : GrowthRequirementHandler.defaultSoils) {
                    soils.add(new PositionedStack(blockWithMeta.toStack(), Constants.nei_X_result, Constants.nei_Y_soil));
                }
            }

            this.requiredType = growthReq.getRequiredType();
            if (requiredType != RequirementType.NONE) {
                requiredBlock = new PositionedStack(growthReq.requiredBlockAsItemStack(), Constants.nei_X_result, Constants.nei_Y_base);
            }
        }

        //Override and return null because a mutation recipe needs two ingredients
        @Override
        public PositionedStack getIngredient() {
            return null;
        }

        //return ingredients
        @Override
        public List<PositionedStack> getIngredients() {
            List<PositionedStack> list = new ArrayList<PositionedStack>();
            list.add(parent1);
            list.add(parent2);
            list.add(soils.get(NEICropMutationHandler.this.cycleticks / 20 % soils.size()));
            if(requiredBlock!=null) {
                list.add(requiredBlock);
            }
            return list;
        }

        //return result
        @Override
        public PositionedStack getResult() {
            return result;
        }
    }

    //loads the mutation recipes for a given mutation
    @Override
    public void loadCraftingRecipes(String id, Object... results) {
        if(id.equalsIgnoreCase(NEICropMutationHandler.id)) {
            Mutation[] mutations = MutationHandler.getMutations();
            for (Mutation mutation:mutations) {
                ItemStack resultStack = mutation.getResult();
                ItemStack parent1Stack = mutation.getParents()[0];
                ItemStack parent2Stack = mutation.getParents()[1];
                if (resultStack.getItem() != null &&parent1Stack.getItem() != null && parent2Stack.getItem() != null) {
                    arecipes.add(new CachedCropMutationRecipe(mutation));
                }
            }
        }
        else if(id.equalsIgnoreCase("item")) {
            for (Object object : results) {
                if (object instanceof ItemStack && getClass()==NEICropMutationHandler.class) {
                    loadCraftingRecipes(new ItemStack(((ItemStack) object).getItem(), 1, ((ItemStack) object).getItemDamage()));
                }
            }
        }
    }

    //loads the mutation recipes for a given mutation
    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if(CropPlantHandler.isValidSeed(result)) {
            Mutation[] mutations = MutationHandler.getMutationsFromChild(result);
            for(Mutation mutation:mutations) {
                ItemStack parent1Stack = mutation.getParents()[0];
                ItemStack parent2Stack = mutation.getParents()[1];
                if (parent1Stack.getItem()!=null && parent2Stack.getItem()!=null) {
                    arecipes.add(new CachedCropMutationRecipe(mutation));
                }
            }
        }
    }

    //loads the mutation recipes for a given parent
    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if(ingredient == null || ingredient.getItem() == null) {
            return;
        }
        if(CropPlantHandler.isValidSeed(ingredient)) {
            Mutation[] mutations = MutationHandler.getMutationsFromParent(ingredient);
            for (Mutation mutation:mutations) {
                ItemStack resultStack = mutation.getResult();
                ItemStack parent1Stack = mutation.getParents()[0];
                ItemStack parent2Stack = mutation.getParents()[1];
                if (resultStack.getItem() != null && parent1Stack.getItem() != null && parent2Stack!= null && parent2Stack.getItem() != null) {
                    arecipes.add(new CachedCropMutationRecipe(mutation));
                }
            }
        }
        else if(ingredient.getItem() instanceof ItemBlock) {
            BlockWithMeta block = new BlockWithMeta(((ItemBlock) ingredient.getItem()).field_150939_a, ingredient.getItemDamage());
            Mutation[] mutations = MutationHandler.getMutations();
            for(Mutation mutation:mutations) {
                IGrowthRequirement req = GrowthRequirementHandler.getGrowthRequirement(mutation.getResult().getItem(), mutation.getResult().getItemDamage());
                if(req.isValidSoil(block)) {
                    arecipes.add(new CachedCropMutationRecipe(mutation));
                    continue;
                }
                if(block.equals(req.getRequiredBlock())) {
                    arecipes.add(new CachedCropMutationRecipe(mutation));
                }
            }
        }
    }

    //returns the name for this recipe
    @Override
    public String getRecipeName() {
        return name;
    }

    //returns the id for this recipe
    @Override
    public String getOverlayIdentifier() {
        return id;
    }

    //gets the texture to display the recipe in
    @Override
    public String getGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID.toLowerCase(),"textures/gui/nei/cropMutation.png").toString();
    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }

    //defines rectangles on the recipe gui which can be clicked to show all crop mutation recipes
    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(65, 2, 4, 39), id));
        transferRects.add(new RecipeTransferRect(new Rectangle(97, 2, 4, 39), id));
        transferRects.add(new RecipeTransferRect(new Rectangle(59, 8, 48, 4), id));
    }

    @Override
    public void drawBackground(int recipe) {
        CachedCropMutationRecipe mutationRecipe = (CachedCropMutationRecipe) arecipes.get(recipe);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 5, 11, 166, 85);

        String soil = StatCollector.translateToLocal("agricraft_nei.soil");
        GuiDraw.drawStringR(soil + ":", Constants.nei_X_result - 7, Constants.nei_Y_soil + 4, COLOR_BLACK, false);

        if (mutationRecipe.requiredType != RequirementType.NONE) {
            String needs = StatCollector.translateToLocal("agricraft_nei.needs");
            String modifier = mutationRecipe.requiredType == RequirementType.BELOW
                    ? StatCollector.translateToLocal("agricraft_nei.below")
                    : StatCollector.translateToLocal("agricraft_nei.nearby");

            GuiDraw.drawStringR(needs + ":", Constants.nei_X_result - 7, Constants.nei_Y_base + 4, COLOR_BLACK, false);
            GuiDraw.drawString(modifier, Constants.nei_X_parent2 - 8, Constants.nei_Y_base + 4, COLOR_BLACK, false);
        }
    }
}
