package com.InfinityRaider.AgriCraft.compatibility.NEI;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.CrossCropHelper;
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
        PositionedStack mutation;

        //constructor
        public CachedCropMutationRecipe(ItemStack[] parents, ItemStack mutation) {
            this(parents[0], parents[1], mutation);
        }

        //constructor
        public CachedCropMutationRecipe(ItemStack parent1, ItemStack parent2, ItemStack mutation) {
            this.parent1 = new PositionedStack(parent1.copy(), Constants.nei_X1, Constants.nei_Y);
            this.parent2 = new PositionedStack(parent2.copy(), Constants.nei_X2, Constants.nei_Y);
            this.mutation = new PositionedStack(mutation.copy(), Constants.nei_X3, Constants.nei_Y);
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
            return list;
        }

        //return result
        @Override
        public PositionedStack getResult() {
            return mutation;
        }
    }

    //loads the mutation recipes for a given mutation
    @Override
    public void loadCraftingRecipes(String id, Object... results) {
        if(id.equalsIgnoreCase(NEICropMutationHandler.id)) {
            ItemStack[] mutations = CrossCropHelper.getMutations();
            ItemStack[][] parents = CrossCropHelper.getParents();
            for(int i=0;i<mutations.length;i++) {
                if(mutations[i].getItem()!=null && parents[i][0].getItem()!=null && parents[i][1].getItem()!=null) {
                    arecipes.add(new CachedCropMutationRecipe(parents[i][0], parents[i][1], mutations[i]));
                }
            }
        }
        else if(id.equalsIgnoreCase("item")) {
            for (Object object : results) {
                if (object instanceof ItemStack && id.equalsIgnoreCase("item") && getClass() == NEICropMutationHandler.class) {
                    loadCraftingRecipes(new ItemStack(((ItemStack) object).getItem(), 1, ((ItemStack) object).getItemDamage()));
                }
            }
        }
    }

    //loads the mutation recipes for a given mutation
    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if(result.getItem() instanceof ItemSeeds) {
            ItemStack[][] parents = CrossCropHelper.getParents(result);
            if((parents!=null) && (parents.length>0)) {
                for(ItemStack[]parent:parents) {
                    if (parent[0].getItem() != null && parent[1] != null) {
                        arecipes.add(new CachedCropMutationRecipe(parent, result));
                    }
                }
            }
        }
    }

    //loads the mutation recipes for a given parent
    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if(ingredient.getItem() instanceof ItemSeeds) {
            ItemStack[] mutations = CrossCropHelper.getMutations(ingredient);
            ItemStack[] coParents = CrossCropHelper.getCoParents(ingredient);
            for(int i=0;i<mutations.length;i++) {
                if(mutations[i].getItem()!=null && coParents[i].getItem()!=null) {
                    arecipes.add(new CachedCropMutationRecipe(new ItemStack(ingredient.getItem(), 1, ingredient.getItemDamage()), coParents[i], mutations[i]));
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

    //defines rectangles on the recipe gui wich can be clicked to show all crop mutation recipes
    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(65, 13, 4, 39), id));
        transferRects.add(new RecipeTransferRect(new Rectangle(97, 13, 4, 39), id));
        transferRects.add(new RecipeTransferRect(new Rectangle(59, 19, 48, 4), id));
    }

}
