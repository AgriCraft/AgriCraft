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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ItemNugget extends ItemBase implements IAutoRenderedItem, IRecipeRegister {

	public ItemNugget() {
		super("agri_nugget");
		this.setCreativeTab(CreativeTabs.MATERIALS);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> varients) {
		for (AgriNuggetType type : AgriNuggetType.values()) {
			ItemStack stack = new ItemStack(item, 1, type.ordinal());
			OreDictionary.registerOre(type.nugget, stack);
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
            ItemStack nugget = new ItemStack(this, 9, type.ordinal());
            ItemStack ingot = OreDictHelper.getIngot(type.ingot);
            AgriCore.getLogger("agricraft").debug("Registering Nugget: {0} For: {1}", type.nugget, type.ingot);
            if (ingot != null) {
                GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', type.nugget));
                GameRegistry.addRecipe(new ShapelessOreRecipe(nugget, type.ingot));
            }
        }
    }

}
