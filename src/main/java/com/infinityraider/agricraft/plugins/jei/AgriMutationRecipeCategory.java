package com.infinityraider.agricraft.plugins.jei;

import java.util.stream.Collectors;

import com.agricraft.agricore.core.AgriCore;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.mojang.blaze3d.matrix.MatrixStack;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;

public class AgriMutationRecipeCategory implements IRecipeCategory<IAgriMutation> {

    public final ResourceLocation id;
    private final IAgriDrawable icon;
    private final IAgriDrawable background;

    public static IAgriDrawable createAgriDrawable(ResourceLocation location, int x, int y, int u, int v, int w, int h, int textureWidth, int textureHeight) {
        return new IAgriDrawable() {
            @Override
            public int getWidth() {
                return w;
            }
    
            @Override
            public int getHeight() {
                return h;
            }
    
            @Override
            public void draw(MatrixStack transform, int x, int y) {
                this.bindTexture(location);
                Screen.blit(transform, x, y, u, v, getWidth(), getHeight(), textureWidth, textureHeight);
            }
        };
    }

    public AgriMutationRecipeCategory(IJeiHelpers jeiHelpers, ResourceLocation id) {
        this.id = id;
        this.icon = createAgriDrawable(new ResourceLocation(AgriCraft.instance.getModId(), "textures/item/debugger.png"), 0, 0, 0, 0, 16, 16, 16, 16);
        this.background = createAgriDrawable(new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/crop_mutation.png"), 0, 0, 0, 0, 128, 128, 128, 128);
    }

    @Override
    public ResourceLocation getUid() {
        return this.id;
    }

    @Override
    public Class<IAgriMutation> getRecipeClass() {
        return IAgriMutation.class;
    }

    @Override
    public String getTitle() {
        return AgriCore.getTranslator().translate("agricraft_nei.mutation.title");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(IAgriMutation mutation, IIngredients ingredients) {
        ingredients.setInputLists(AgriPlantIngredient.TYPE,
                mutation.getParents().stream().map(ImmutableList::of).collect(Collectors.toList()));
        ingredients.setOutputLists(AgriPlantIngredient.TYPE, ImmutableList.of(ImmutableList.of(mutation.getChild())));
    }

    @Override
    public void setRecipe(IRecipeLayout layout, IAgriMutation mutation, IIngredients ingredients) {
        layout.setShapeless();
        layout.getItemStacks().init(0, true, 24, 39);
        layout.getItemStacks().init(1, true, 86, 39);
        layout.getItemStacks().init(2, true, 55, 65);
        layout.getItemStacks().init(3, true, 55, 86);
        layout.getItemStacks().init(4, false, 55, 39);
        layout.getItemStacks().set(ingredients);
    }

}
