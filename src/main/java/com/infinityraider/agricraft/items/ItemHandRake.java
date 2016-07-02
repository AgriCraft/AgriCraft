package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.utility.WeightedRandom;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import com.agricraft.agricore.core.AgriCore;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;
import com.infinityraider.agricraft.api.v1.misc.IWeedable;
import net.minecraft.tileentity.TileEntity;
import com.infinityraider.agricraft.api.v1.items.IAgriRakeItem;

/**
 * Tool to uproot weeds. Comes in a wooden and iron variant.
 */
public class ItemHandRake extends ItemBase implements IAgriRakeItem {

	private static final int WOOD_VARIANT_META = 0;
	private static final int IRON_VARIANT_META = 1;
	private static final int[] dropChance = new int[]{10, 25};

	public ItemHandRake() {
		super("hand_rake", true, "", "iron");
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IWeedable) {
			IWeedable tile = (IWeedable) te;
			if (tile.hasWeed()) {
				tile.clearWeed();
				if (AgriCraftConfig.rakingDrops && world.rand.nextInt(100) < dropChance[stack.getItemDamage() % dropChance.length]) {
					ItemStack drop = ItemDropRegistry.instance().getDrop(world.rand);
					if (drop != null && drop.getItem() != null) {
						float f = 0.7F;
						double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
						double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
						double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
						EntityItem entityitem = new EntityItem(world, (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, drop);
						entityitem.setPickupDelay(10);
						world.spawnEntityInWorld(entityitem);
					}
				}
				return EnumActionResult.SUCCESS;
			}
			return EnumActionResult.FAIL;
		}
		return EnumActionResult.PASS;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
		list.add(new ItemStack(item, 1, WOOD_VARIANT_META));
		list.add(new ItemStack(item, 1, IRON_VARIANT_META));
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		String base = super.getUnlocalizedName(itemStack);
		switch (itemStack.getItemDamage()) {
			case WOOD_VARIANT_META:
				return base + ".wood";
			case IRON_VARIANT_META:
				return base + ".iron";
			default:
				throw new IllegalArgumentException("Unsupported meta value of " + itemStack.getItemDamage() + " for ItemHandRake.");
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
		list.add(AgriCore.getTranslator().translate("agricraft_tooltip.handRake"));
	}

	public static final class ItemDropRegistry {

		private static ItemDropRegistry INSTANCE;

		private final WeightedRandom<ItemStack> registry;

		static {
			instance().registerDrop(new ItemStack(Blocks.TALLGRASS, 1, 1), 20);
			instance().registerDrop(new ItemStack(Blocks.TALLGRASS, 1, 2), 10);
			instance().registerDrop(new ItemStack(Blocks.DOUBLE_PLANT, 1, 2), 10);
		}

		private ItemDropRegistry() {
			registry = new WeightedRandom<>();
		}

		public static ItemDropRegistry instance() {
			if (INSTANCE == null) {
				INSTANCE = new ItemDropRegistry();
			}
			return INSTANCE;
		}

		public void registerDrop(ItemStack stack, int weight) {
			registry.addEntry(stack, weight);
		}

		public void removeDrop(ItemStack stack) {
			registry.removeEntry(stack);
		}

		public ItemStack getDrop(Random rand) {
			return registry.getRandomEntry(rand).copy();
		}

		public int getWeight(ItemStack stack) {
			return registry.getWeight(stack);
		}
	}

}
