package com.infinityraider.agricraft.content.plants;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.AgriNuggetType;
import com.infinityraider.agricraft.util.OreDictUtil;
import com.infinityraider.infinitylib.item.IAutoRenderedItem;
import com.infinityraider.infinitylib.item.ItemBase;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import com.infinityraider.infinitylib.utility.IRecipeRegisterer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemNugget extends ItemBase implements IAutoRenderedItem, IRecipeRegisterer {

    public ItemNugget() {
        super("agri_nugget");
        this.setCreativeTab(CreativeTabs.MATERIALS);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> varients) {
        if (tab == this.getCreativeTab() || tab == CreativeTabs.SEARCH) {
            for (AgriNuggetType type : AgriNuggetType.values()) {
                ItemStack stack = new ItemStack(this, 1, type.ordinal());
                varients.add(stack);
            }
        }
    }

    @Override
    public boolean getHasSubtypes() {
        return true;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return AgriNuggetType.getNugget(stack.getMetadata()).getUnlocalizedName();
    }

    @Override
    public String getModelId(ItemStack stack) {
        return AgriNuggetType.getNugget(stack.getMetadata()).nugget;
    }

    @Override
    public String getBaseTexture(ItemStack stack) {
        return AgriNuggetType.getNugget(stack.getMetadata()).texture;
    }

    @Override
    public List<ResourceLocation> getAllTextures() {
        List<ResourceLocation> textures = new ArrayList<>(AgriNuggetType.values().length);
        for (AgriNuggetType type : AgriNuggetType.values()) {
            textures.add(new ResourceLocation(type.texture));
        }
        return textures;
    }

    @Override
    public void registerRecipes(IForgeRegistry<IRecipe> registry) {
        for (AgriNuggetType type : AgriNuggetType.values()) {
            // 1) Ore Dictionary registration.
            AgriCore.getLogger("agricraft").info("Registering in Ore Dictionary: {0}", type.nugget);
            ItemStack oneNugget = new ItemStack(this, 1, type.ordinal());
            OreDictionary.registerOre(type.nugget, oneNugget);

            // 2) Conditional recipes. Only if the ingot exists, because AgriCraft doesn't add its own.
            ItemStack ingot = OreDictUtil.getFirstOre(type.ingot).orElse(ItemStack.EMPTY);
            if (!ingot.isEmpty()) {
                AgriCore.getLogger("agricraft").info("Adding a recipe to convert nine {0} into one {1}", type.nugget, type.ingot);
                final ResourceLocation group = new ResourceLocation(AgriCraft.instance.getModId(), "combine_nugget");
                final ResourceLocation name = new ResourceLocation(AgriCraft.instance.getModId(), "combine_nugget_" + type.name().toLowerCase());
                final ShapedOreRecipe recipe = new ShapedOreRecipe(
                        group,
                        ingot,
                        "nnn",
                        "nnn",
                        "nnn",
                        'n', type.nugget
                );
                recipe.setRegistryName(name);
                AgriCore.getLogger("agricraft").info("Registering nugget recipe: {0}!", recipe.getRegistryName());
                registry.register(recipe);
            }
        }
    }

}
