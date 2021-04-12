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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class AgriRecipeCategorySoilProperties implements IRecipeCategory<IAgriSoil> {

    public static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "jei/soil");
    private static final ResourceLocation TEXTURE = new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/soil_properties.png");

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
        this.background = JeiPlugin.createAgriDrawable(TEXTURE, 0, 0, 128, 64, 128, 64, 128, 128);
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
        layout.getIngredientsGroup(VanillaTypes.ITEM).init(0, true, 55, 1);

        // Register Recipe Elements
        layout.getItemStacks().set(ingredients);
    }

    private final int[] dxHumidity = {8, 8, 10, 10, 10, 7};
    private final int[] dxAcidity = {7, 8, 8, 8, 8, 8, 6};
    private final int[] dxNutrients = {6, 8, 9, 9, 11, 10};

    @Override
    public void draw(IAgriSoil soil, @Nonnull MatrixStack transforms, double mouseX, double mouseY) {
        // Render text
        /*
        FontRenderer renderer = Minecraft.getInstance().fontRenderer;
        AbstractGui.drawString(transforms, renderer, IAgriSoil.Tooltips.HUMIDITY, 2, 22, -1);
        AbstractGui.drawString(transforms, renderer, IAgriSoil.Tooltips.ACIDITY, 2, 35, -1);
        AbstractGui.drawString(transforms, renderer, IAgriSoil.Tooltips.NUTRIENTS, 2, 48, -1);
         */
        // Draw Property icons
        this.draw(soil.getHumidity(), transforms, 20, 64, dxHumidity, mouseX, mouseY);
        this.draw(soil.getAcidity(), transforms, 33, 77, dxAcidity, mouseX, mouseY);
        this.draw(soil.getNutrients(), transforms, 46, 90, dxNutrients, mouseX, mouseY);
    }

    protected void draw(IAgriSoil.SoilProperty property, MatrixStack transforms, int y0, int v0,
                        int[] dx, double mX, double mY) {
        if(property.isValid()) {
            // Determine coordinates
            int index = property.ordinal();
            int x0 = 37;
            int x1 = 0;
            for(int i = 0; i < index; i++) {
                x1 += dx[i];
            }
            int w = dx[index];
            // Draw the icon
            Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
            AbstractGui.blit(transforms, x0 + x1, y0, w, 13, x1, v0, w, 13, 128, 128);
            // Draw the tooltip if needed
            if(mX >= x0 + x1 && mX <= x0 + x1 + w && mY >= y0 && mY <= y0 + 13) {
                List<ITextComponent> tooltip = ImmutableList.of(property.getDescription());
                this.drawTooltip(transforms, tooltip, mX, mY + 13);
            }
        }
    }

    protected void drawTooltip(MatrixStack transforms, List<ITextComponent> tooltip, double x, double y) {
        FontRenderer renderer = Minecraft.getInstance().fontRenderer;
        int w = Minecraft.getInstance().getMainWindow().getScaledWidth();
        int h = Minecraft.getInstance().getMainWindow().getScaledHeight();
        GuiUtils.drawHoveringText(transforms, tooltip, (int) x, (int) y, w, h, -1, renderer);
    }
}
