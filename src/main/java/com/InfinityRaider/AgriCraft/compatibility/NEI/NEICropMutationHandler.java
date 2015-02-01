package com.InfinityRaider.AgriCraft.compatibility.NEI;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.InfinityRaider.AgriCraft.farming.mutation.Mutation;
import com.InfinityRaider.AgriCraft.farming.mutation.MutationHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NEICropMutationHandler extends TemplateRecipeHandler {
    //this is a class which handles the recipe for crop mutation (has to contain a CachedRecipe for the mutation)
    private static String name = "Crop Mutation";
    private static String id = "cropMutation";

    //the class defining a mutation recipe (must be contained in a TemplateRecipeHandler class)
    public class CachedCropMutationRecipe extends TemplateRecipeHandler.CachedRecipe {
        //the stacks needed for a recipe (parent1 + parent2 = mutation)
        PositionedStack parent1;
        PositionedStack parent2;
        PositionedStack result;
        PositionedStack requiredBlock;

        //constructor
        public CachedCropMutationRecipe(Mutation mutation) {
            this.parent1 = new PositionedStack(mutation.parent1.copy(), Constants.nei_X1, Constants.nei_Y1);
            this.parent2 = new PositionedStack(mutation.parent2.copy(), Constants.nei_X2, Constants.nei_Y1);
            this.result = new PositionedStack(mutation.result.copy(), Constants.nei_X3, Constants.nei_Y1);
            switch(mutation.id) {
                case 0:requiredBlock = null;break;
                case 1:requiredBlock = new PositionedStack(new ItemStack(mutation.requirement, 1, mutation.requirementMeta), Constants.nei_X3, Constants.nei_Y2);break;
                case 2:requiredBlock = new PositionedStack(new ItemStack(mutation.requirement, 1, mutation.requirementMeta), Constants.nei_X4, Constants.nei_Y2);break;
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
            ArrayList<PositionedStack> list = new ArrayList<PositionedStack>();
            list.add(parent1);
            list.add(parent2);
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
                if (mutation.result.getItem() != null && mutation.parent1.getItem() != null && mutation.parent2.getItem() != null) {
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
        if(result.getItem() instanceof ItemSeeds) {
            Mutation[] mutations = MutationHandler.getParentMutations(result);
            for(Mutation mutation:mutations) {
                if (mutation.parent1.getItem()!=null && mutation.parent2.getItem()!=null) {
                    arecipes.add(new CachedCropMutationRecipe(mutation));
                }
            }
        }
    }

    //loads the mutation recipes for a given parent
    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if(ingredient.getItem() instanceof ItemSeeds) {
            Mutation[] mutations = MutationHandler.getMutations(ingredient);
            for (Mutation mutation:mutations) {
                if (mutation.result.getItem() != null && mutation.parent1.getItem() != null && mutation.parent2.getItem() != null) {
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

    //defines rectangles on the recipe gui which can be clicked to show all crop mutation recipes
    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(65, 13, 4, 39), id));
        transferRects.add(new RecipeTransferRect(new Rectangle(97, 13, 4, 39), id));
        transferRects.add(new RecipeTransferRect(new Rectangle(59, 19, 48, 4), id));
    }

}
