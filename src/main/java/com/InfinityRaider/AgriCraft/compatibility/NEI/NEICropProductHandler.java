package com.InfinityRaider.AgriCraft.compatibility.NEI;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v1.RequirementType;
import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
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

public class NEICropProductHandler extends TemplateRecipeHandler {
    //this is a class which handles the recipe for crop products (has to contain a CachedRecipe for the products)
    private static String name = StatCollector.translateToLocal("agricraft_nei.cropProduct.title");
    private static String id = "cropProduct";

    private static final int COLOR_BLACK = 1644054;

    //the class defining a  recipe (must be contained in a TemplateRecipeHandler class)
    public class CachedCropProductRecipe extends TemplateRecipeHandler.CachedRecipe {
        PositionedStack seed;
        List<PositionedStack> products;
        List<PositionedStack> soils = new ArrayList<PositionedStack>();
        PositionedStack requiredBlock;
        RequirementType requiredType;

        //constructor
        public CachedCropProductRecipe(ItemStack stack) {
            this.seed = new PositionedStack(stack, Constants.nei_X_parent1, Constants.nei_Y_seeds);
            this.products = new ArrayList<PositionedStack>();
            List<ItemStack> drops = CropPlantHandler.getPlantFromStack(stack).getAllFruits();
            if(drops != null) {
                for (int i = 0; i < drops.size(); i++) {
                    products.add(new PositionedStack(drops.get(i), Constants.nei_X_parent2 + 16 * (i % 3), Constants.nei_Y_seeds + 16 * ((i / 3) - 1)));
                }
            }
            IGrowthRequirement growthReq = GrowthRequirementHandler.getGrowthRequirement(stack.getItem(), stack.getItemDamage());
            if (growthReq.getSoil() != null) {
                soils.add(new PositionedStack(growthReq.getSoil().toStack(), Constants.nei_X_parent1, Constants.nei_Y_soil));
            } else {
                for (BlockWithMeta blockWithMeta : GrowthRequirementHandler.defaultSoils) {
                    soils.add(new PositionedStack(blockWithMeta.toStack(), Constants.nei_X_parent1, Constants.nei_Y_soil));
                }
            }
            this.requiredType = growthReq.getRequiredType();
            if (requiredType != RequirementType.NONE) {
                requiredBlock = new PositionedStack(growthReq.requiredBlockAsItemStack(), Constants.nei_X_parent1, Constants.nei_Y_base);
            }
        }

        @Override
        public PositionedStack getIngredient() {
            return seed;
        }

        //return ingredients
        @Override
        public List<PositionedStack> getIngredients() {
            List<PositionedStack> list = new ArrayList<PositionedStack>();
            list.add(seed);
            list.add(soils.get(NEICropProductHandler.this.cycleticks / 20 % soils.size()));
            if(requiredBlock!=null) {
                list.add(requiredBlock);
            }
            list.addAll(products);
            return list;
        }

        //return result
        @Override
        public PositionedStack getResult() {
            return null;
        }
    }

    //loads the crop product recipes for a given product
    @Override
    public void loadCraftingRecipes(String id, Object... results) {
        if(id.equalsIgnoreCase(NEICropProductHandler.id)) {
            ArrayList<CropPlant> plants = CropPlantHandler.getPlants();
            for (CropPlant plant:plants) {
                if (plant.getSeed()!=null && plant.getSeed().getItem()!=null) {
                    arecipes.add(new CachedCropProductRecipe(plant.getSeed()));
                }
            }
        }
        else if(id.equalsIgnoreCase("item")) {
            for (Object object : results) {
                if (object instanceof ItemStack && getClass()==NEICropProductHandler.class) {
                    loadCraftingRecipes(new ItemStack(((ItemStack) object).getItem(), 1, ((ItemStack) object).getItemDamage()));
                }
            }
        }
    }

    //loads the crop product recipes for a given product
    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for(CropPlant plant:CropPlantHandler.getPlants()) {
            ArrayList<ItemStack> drops = plant.getAllFruits();
            if(drops==null) {
                continue;
            }
            for(ItemStack drop:drops) {
                if(drop == null || drop.getItem() == null) {
                    continue;
                }
                if(drop.getItem() == result.getItem() && drop.getItemDamage() == result.getItemDamage()) {
                    arecipes.add(new CachedCropProductRecipe(plant.getSeed()));
                    break;
                }
            }
        }
    }

    //loads the crop product recipes for a given seed
    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if(CropPlantHandler.isValidSeed(ingredient)) {
            arecipes.add(new CachedCropProductRecipe(ingredient));
        }
        else if(ingredient.getItem() instanceof ItemBlock) {
            BlockWithMeta block = new BlockWithMeta(((ItemBlock) ingredient.getItem()).field_150939_a, ingredient.getItemDamage());
            ArrayList<CropPlant> plants = CropPlantHandler.getPlants();
            for(CropPlant plant:plants) {
                if(plant.getSeed()==null || plant.getSeed().getItem()==null) {
                    continue;
                }
                IGrowthRequirement req = GrowthRequirementHandler.getGrowthRequirement(plant.getSeed().getItem(), plant.getSeed().getItemDamage());
                if(req.isValidSoil(block)) {
                    arecipes.add(new CachedCropProductRecipe(plant.getSeed()));
                    continue;
                }
                if(block.equals(req.getRequiredBlock())) {
                    arecipes.add(new CachedCropProductRecipe(plant.getSeed()));
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
        return new ResourceLocation(Reference.MOD_ID.toLowerCase(),"textures/gui/nei/cropProduce.png").toString();
    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }

    //defines rectangles on the recipe gui which can be clicked to show all crop mutation recipes
    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(72, 27, 18, 6), id));
    }

    @Override
    public void drawBackground(int recipe) {
        CachedCropProductRecipe cropProductRecipe = (CachedCropProductRecipe) arecipes.get(recipe);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 5, 11, 166, 85);

        String soil = StatCollector.translateToLocal("agricraft_nei.soil");
        GuiDraw.drawStringR(soil + ":", Constants.nei_X_parent1 - 7, Constants.nei_Y_soil + 4, COLOR_BLACK, false);

        if (cropProductRecipe.requiredType != RequirementType.NONE) {
            String needs = StatCollector.translateToLocal("agricraft_nei.needs");
            String modifier = cropProductRecipe.requiredType == RequirementType.BELOW
                    ? StatCollector.translateToLocal("agricraft_nei.below")
                    : StatCollector.translateToLocal("agricraft_nei.nearby");

            GuiDraw.drawStringR(needs + ":", Constants.nei_X_parent1 - 7, Constants.nei_Y_base + 4, COLOR_BLACK, false);
            GuiDraw.drawString(modifier, Constants.nei_X_result - 8, Constants.nei_Y_base + 4, COLOR_BLACK, false);
        }
    }
}
