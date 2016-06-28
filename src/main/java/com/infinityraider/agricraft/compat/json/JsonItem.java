/*
 */
package com.infinityraider.agricraft.compat.json;

import com.agricraft.agricore.base.AgriItem;
import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriPlant;
import com.infinityraider.agricraft.items.ItemBase;
import com.infinityraider.agricraft.items.ItemBase;
import com.infinityraider.agricraft.items.ItemNugget;
import com.infinityraider.agricraft.utility.RegisterHelper;
import com.infinityraider.agricraft.utility.StackHelper;
import java.util.List;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author RlonRyan
 */
public class JsonItem extends ItemBase {

	public static final String NBT_ITEM_ID = "agri_item_id";
	
	@SideOnly(Side.CLIENT)
	public static final ModelResourceLocation DEFAULT_MODEL =  RegisterHelper.getItemModel("agricraft:items/debugger");
	
	public JsonItem() {
		super("agri_item", true);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String id = StackHelper.getTag(stack).getString(NBT_ITEM_ID);
		AgriItem item = AgriCore.getItems().getItem(id);
		return item == null ? "Generic Item" : item.getName().toString();
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
		AgriCore.getItems().getAll().forEach((e) -> {
			AgriCore.getLogger("AgriCraft").debug("Creating JSON Item: {0}!", e.getName());
			ItemStack stack = new ItemStack(item);
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString(NBT_ITEM_ID, e.getId());
			stack.setTagCompound(tag);
			subItems.add(stack);
		});
	}
	
	@Override
	public boolean getHasSubtypes() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerItemRenderer() {
		ModelLoader.setCustomMeshDefinition(this, (ItemStack stack) -> {
			String id = StackHelper.getTag(stack).getString(NBT_ITEM_ID);
			AgriItem item = AgriCore.getItems().getItem(id);
			if (item != null) {
				return RegisterHelper.getItemModel(item.getTexture());
			} else {
				return DEFAULT_MODEL;
			}
		});
	}

}
