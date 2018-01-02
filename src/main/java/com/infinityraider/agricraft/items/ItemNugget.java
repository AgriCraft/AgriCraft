package com.infinityraider.agricraft.items;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.reference.AgriNuggetType;
import com.infinityraider.agricraft.utility.OreDictHelper;
import com.infinityraider.infinitylib.item.IAutoRenderedItem;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.utility.IRecipeRegister;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class ItemNugget extends ItemBase implements IAutoRenderedItem, IRecipeRegister {

    public ItemNugget() {
        super("agri_nugget");
        this.setCreativeTab(CreativeTabs.MATERIALS);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> varients) {
        for (AgriNuggetType type : AgriNuggetType.values()) {
            ItemStack stack = new ItemStack(this, 1, type.ordinal());
            varients.add(stack);
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
    public void registerRecipes() {
        for (AgriNuggetType type : AgriNuggetType.values()) {
            // 1) Ore Dictionary registration.
            AgriCore.getLogger("agricraft").info("Registering in Ore Dictionary: {0}", type.nugget);
            ItemStack oneNugget = new ItemStack(this, 1, type.ordinal());
            OreDictionary.registerOre(type.nugget, oneNugget);

            // 2) Conditional recipes. Only if the ingot exists, because AgriCraft doesn't add its own.
            ItemStack ingot = OreDictHelper.getIngot(type.ingot);
            if (ingot != null) {
                AgriCore.getLogger("agricraft").info("Adding a recipe to convert nine {0} into one {1}", type.nugget, type.ingot);
                //GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', type.nugget));

                // TODO: Research the necessity of 'uncrafting' ingots back into nuggets. It causes compatibility issues.
                //ItemStack nineNuggets = new ItemStack(this, 9, type.ordinal());
                //GameRegistry.addRecipe(new ShapelessOreRecipe(nineNuggets, type.ingot));
            }
        }
    }

}
