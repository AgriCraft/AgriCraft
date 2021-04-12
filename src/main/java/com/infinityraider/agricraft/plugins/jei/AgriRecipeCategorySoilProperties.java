package com.infinityraider.agricraft.plugins.jei;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class AgriRecipeCategorySoilProperties implements IRecipeCategory<IAgriSoil> {

    public static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "jei/soil");

    public final IDrawable icon;
    public final IAgriDrawable background;

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(AgriApi.getSoilRegistry().all(), ID);
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_wood), AgriRecipeCategorySoilProperties.ID);
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_iron), AgriRecipeCategorySoilProperties.ID);
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_obsidian), AgriRecipeCategorySoilProperties.ID);
    }

    public AgriRecipeCategorySoilProperties(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(new ItemStack(Blocks.FARMLAND));
        this.background = JeiPlugin.createAgriDrawable(new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/soil_properties.png"), 0, 0, 128, 128, 128, 128);
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Nonnull
    @Override
    public Class<IAgriSoil> getRecipeClass() {
        return IAgriSoil.class;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.format("agricraft.gui.soil");
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(IAgriSoil soil, IIngredients ingredients) {
        ingredients.setInputLists(
                VanillaTypes.ITEM,
                ImmutableList.of(
                        soil.getVariants().stream()
                                .map(AbstractBlock.AbstractBlockState::getBlock)
                                .distinct()
                                .map(ItemStack::new)
                                .collect(Collectors.toList()))
        );
    }

    @Override
    public void setRecipe(IRecipeLayout layout, @Nonnull IAgriSoil soil, @Nonnull IIngredients ingredients) {
        // Clear the focus as this sometimes causes display bugs
        layout.getIngredientsGroup(VanillaTypes.ITEM).setOverrideDisplayFocus(null);

        // Denote that this is a shapeless recipe.
        layout.setShapeless();

        // Setup Soil ItemStack
        layout.getIngredientsGroup(VanillaTypes.ITEM).init(0, true, 55, 62);

        // Register Recipe Elements
        layout.getItemStacks().set(ingredients);
    }

    @Override
    public void draw(IAgriSoil soil, @Nonnull MatrixStack transforms, double mouseX, double mouseY) {
        // Draw Property icons
        SoilPropertyIconRenderer.getInstance().drawIcon(soil.getHumidity(), transforms, 37, 83, mouseX, mouseY);
        SoilPropertyIconRenderer.getInstance().drawIcon(soil.getAcidity(), transforms, 37, 96, mouseX, mouseY);
        SoilPropertyIconRenderer.getInstance().drawIcon(soil.getNutrients(), transforms, 37, 109, mouseX, mouseY);
    }
}
