package com.InfinityRaider.AgriCraft.compatibility.NEI;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.InfinityRaider.AgriCraft.api.v2.IRake;
import com.InfinityRaider.AgriCraft.items.ItemHandRake;
import com.InfinityRaider.AgriCraft.reference.Reference;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NEIWeedRakingHandler  extends TemplateRecipeHandler {
    private static String name = StatCollector.translateToLocal("agricraft_nei.weedRaking.title");
    private static String id = "weedRaking";

    private static final int COLOR_BLACK = 1644054;

    public class CachedWeedRakingRecipe extends TemplateRecipeHandler.CachedRecipe {
        ArrayList<PositionedStack> results;
        PositionedStack rake;

        //constructor
        public CachedWeedRakingRecipe(ItemStack rake) {

        }

        @Override
        public PositionedStack getIngredient() {
            return rake;
        }

        //return ingredients
        @Override
        public List<PositionedStack> getIngredients() {
            List<PositionedStack> list = new ArrayList<PositionedStack>();
            list.add(rake);
            return list;
        }

        //return result
        @Override
        public PositionedStack getResult() {
            return results.get(0);
        }
    }

    //loads the mutation recipes for a given mutation
    @Override
    public void loadCraftingRecipes(String id, Object... results) {
        if(id.equalsIgnoreCase(NEIWeedRakingHandler.id)) {

        }
        else if(id.equalsIgnoreCase("item")) {
            for (Object object : results) {
                if (object instanceof ItemStack && getClass()==NEIWeedRakingHandler.class) {
                    loadCraftingRecipes(new ItemStack(((ItemStack) object).getItem(), 1, ((ItemStack) object).getItemDamage()));
                }
            }
        }
    }

    //loads the mutation recipes for a given mutation
    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if(ItemHandRake.ItemDropRegistry.instance().getWeight(result)>0) {

        }
    }

    //loads the mutation recipes for a given parent
    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if(ingredient == null || ingredient.getItem() == null) {
            return;
        }
        if(ingredient.getItem() instanceof IRake) {
           // List<ItemStack> drops = ItemHandRake.ItemDropRegistry.instance().getAllDrops();
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
        return new ResourceLocation(Reference.MOD_ID.toLowerCase(),"textures/gui/nei/weedRaking.png").toString();
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
        CachedWeedRakingRecipe rakingRecipe = (CachedWeedRakingRecipe) arecipes.get(recipe);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 5, 11, 166, 85);
    }
}