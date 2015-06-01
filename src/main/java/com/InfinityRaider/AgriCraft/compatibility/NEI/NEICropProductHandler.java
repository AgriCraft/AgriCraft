package com.InfinityRaider.AgriCraft.compatibility.NEI;

import codechicken.nei.recipe.TemplateRecipeHandler;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Reference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import java.awt.*;

public class NEICropProductHandler extends TemplateRecipeHandler {
    //this is a class which handles the recipe for crop products (has to contain a CachedRecipe for the products)
    private static String name = StatCollector.translateToLocal("agricraft_nei.mutation.title");
    private static String id = "cropMutation";

    //loads the crop product recipes for a given product
    @Override
    public void loadCraftingRecipes(String id, Object... results) {

    }

    //loads the crop product recipes for a given product
    @Override
    public void loadCraftingRecipes(ItemStack result) {

    }

    //loads the crop product recipes for a given seed
    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if(CropPlantHandler.isValidSeed(ingredient)) {
            Item seed = ingredient.getItem();
            int meta = ingredient.getItemDamage();
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
        return new ResourceLocation(Reference.MOD_ID.toLowerCase(),"textures/gui/nei/cropProduct.png").toString();
    }

    @Override
    public int recipiesPerPage() {
        return 2;
    }

    //defines rectangles on the recipe gui which can be clicked to show all crop mutation recipes
    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(65, 2, 4, 39), id));
        transferRects.add(new RecipeTransferRect(new Rectangle(97, 2, 4, 39), id));
    }

    @Override
    public void drawBackground(int recipe) {

    }
}
