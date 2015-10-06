
package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

/**
 * The root Item class for all AgriCraft Items (excluding blockItems).
 */
public abstract class ItemAgricraft extends Item {

	public ItemAgricraft(String name) {
		super();
		this.setCreativeTab(AgriCraftTab.agriCraftTab);
		this.setMaxStackSize(64);
		RegisterHelper.registerItem(this, name);
	}

	public ItemAgricraft() {
		super();
		this.setCreativeTab(AgriCraftTab.agriCraftTab);
		this.setMaxStackSize(64);
		RegisterHelper.registerItem(this, getInternalName());
	}

	protected abstract String getInternalName();

	/**
	 * <p>
	 * Registers the item's icon.
	 * </p>
	 * <p>
	 * Normally, there is no need to override...?
	 * </p>
	 * 
	 * @see net.minecraft.item.Item#registerIcons(net.minecraft.client.renderer.texture.IIconRegister)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		// LogHelper.debug("Registering icon for: " + this.getUnlocalizedName());
		itemIcon = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.') + 1));
	}
}
