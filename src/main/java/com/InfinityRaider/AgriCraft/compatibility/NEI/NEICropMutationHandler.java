package com.InfinityRaider.AgriCraft.compatibility.NEI;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirement;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirements;
import com.InfinityRaider.AgriCraft.farming.mutation.Mutation;
import com.InfinityRaider.AgriCraft.farming.mutation.MutationHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.BlockWithMeta;
import net.minecraft.item.ItemSeeds;
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
        GrowthRequirement.RequirementType requiredType;

        //constructor
        public CachedCropMutationRecipe(Mutation mutation) {
            this.parent1 = new PositionedStack(mutation.parent1.copy(), Constants.nei_X1, Constants.nei_Y1);
            this.parent2 = new PositionedStack(mutation.parent2.copy(), Constants.nei_X2, Constants.nei_Y1);
            this.result = new PositionedStack(mutation.result.copy(), Constants.nei_X3, Constants.nei_Y1);

            GrowthRequirement growthReq = GrowthRequirements.getGrowthRequirement((ItemSeeds) result.item.getItem(), result.item.getItemDamage());
            if (growthReq.getSoil() != null) {
                soils.add(new PositionedStack(growthReq.getSoil().toStack(), Constants.nei_X3, Constants.nei_Y2));
            } else {
                for (BlockWithMeta blockWithMeta : GrowthRequirements.defaultSoils) {
                    soils.add(new PositionedStack(blockWithMeta.toStack(), Constants.nei_X3, Constants.nei_Y2));
                }
            }

            this.requiredType = growthReq.getRequiredType();
            if (requiredType != GrowthRequirement.RequirementType.NONE) {
                requiredBlock = new PositionedStack(growthReq.requiredBlockAsItemStack(), Constants.nei_X3, Constants.nei_Y3);
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
        GuiDraw.drawStringR(soil + ":", Constants.nei_X3 - 7, Constants.nei_Y2 + 4, COLOR_BLACK, false);

        if (mutationRecipe.requiredType != GrowthRequirement.RequirementType.NONE) {
            String needs = StatCollector.translateToLocal("agricraft_nei.needs");
            String modifier = mutationRecipe.requiredType == GrowthRequirement.RequirementType.BELOW
                    ? StatCollector.translateToLocal("agricraft_nei.below")
                    : StatCollector.translateToLocal("agricraft_nei.nearby");

            GuiDraw.drawStringR(needs + ":", Constants.nei_X3 - 7, Constants.nei_Y3 + 4, COLOR_BLACK, false);
            GuiDraw.drawString(modifier, Constants.nei_X2 - 8, Constants.nei_Y3 + 4, COLOR_BLACK, false);
        }
    }
}
