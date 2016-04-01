package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.creativetab.AgriCraftTab;
import com.infinityraider.agricraft.renderers.items.IItemRenderingHandler;
import com.infinityraider.agricraft.renderers.items.ItemRendererRegistry;
import com.infinityraider.agricraft.utility.RegisterHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The root Item class for all AgriCraft Items (excluding blockItems).
 */
public abstract class ItemBase<I extends ItemBase> extends Item implements ICustomRenderedItem<I> {

	public final String internalName;

	public final boolean isModelVanillia;
	
	protected final String[] varients;

	public ItemBase(String name, boolean isModelVanillia, String... varients) {
		super();
		this.setCreativeTab(AgriCraftTab.agriCraftTab);
		this.setMaxStackSize(64);
		this.internalName = name;
		this.isModelVanillia = isModelVanillia;
		if (varients.length == 0) {
			this.varients = new String[]{""};
		} else {
			this.varients = varients;
		}
		// This is a bad idea...
		RegisterHelper.registerItem(this, name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IItemRenderingHandler<I> getRenderer() {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getItemModelResourceLocation() {
		return new ModelResourceLocation(this.getRegistryName(), "inventory");
	}

	@SideOnly(Side.CLIENT)
	public void registerItemRenderer() {
		if (this.isModelVanillia) {
			RegisterHelper.registerItemRenderer(this, varients);
		} else {
			ItemRendererRegistry.getInstance().registerCustomItemRenderer(this);
		}
	}
}
