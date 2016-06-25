package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.utility.RegisterHelper;
import java.util.List;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemNugget extends ItemBase {

	public static enum NuggetType {

		Emerald("gemEmerald", "oreEmerald"),
		Diamond("gemDiamond", "oreDiamond"),
		Quartz("quartz", "quartz"),
		Iron,
		Copper,
		Tin,
		Lead,
		Silver,
		Aluminum,
		Nickel,
		Platinum,
		Osmium;

		public final String nugget;
		public final String ingot;
		public final String ore;

		private NuggetType() {
			this.nugget = "nugget_" + this.name().toLowerCase();
			this.ingot = "ingot" + this.name();
			this.ore = "ore" + this.name();
		}

		private NuggetType(String ingot, String ore) {
			this.nugget = "nugget_" + this.name().toLowerCase();
			this.ingot = ingot;
			this.ore = ore;
		}

		private static String[] nuggets;

		public static String[] getNuggets() {
			if (nuggets == null) {
				nuggets = new String[NuggetType.values().length];
				for (int i = 0; i < NuggetType.values().length; i++) {
					nuggets[i] = NuggetType.values()[i].nugget;
				}
			}
			return nuggets;
		}

	}

	public ItemNugget() {
		super("agri_nugget", true, NuggetType.getNuggets());
		this.setCreativeTab(CreativeTabs.MATERIALS);
	}

	@Override
	public boolean getHasSubtypes() {
		return true;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> varients) {
		for (NuggetType type : NuggetType.values()) {
			ItemStack stack = new ItemStack(item, 1, type.ordinal());
			OreDictionary.registerOre(type.nugget, stack);
			varients.add(stack);
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.agricraft:" + NuggetType.values()[stack.getMetadata()].nugget;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerItemRenderer() {
		for (int i = 0; i < NuggetType.values().length; i++) {
			ModelResourceLocation model = RegisterHelper.getItemModel("agricraft:items/" + NuggetType.values()[i].nugget);
			ModelLoader.setCustomModelResourceLocation(this, i, model);
		}
	}

}
