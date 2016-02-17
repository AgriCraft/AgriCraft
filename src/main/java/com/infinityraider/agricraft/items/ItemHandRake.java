package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.api.v1.ICrop;
import com.infinityraider.agricraft.api.v1.IRake;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.utility.RegisterHelper;
import com.infinityraider.agricraft.utility.WeightedRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;
import com.infinityraider.agricraft.reference.AgriCraftProperties;

/**
 * Tool to uproot weeds. Comes in a wooden and iron variant.
 */
public class ItemHandRake extends ItemBase implements IRake {

	private static final int WOOD_VARIANT_META = 0;
	private static final int IRON_VARIANT_META = 1;
	private static final int[] dropChance = new int[]{10, 25};

	private static final String[] VARIENTS = { "wood", "iron" };

	public ItemHandRake() {
		super("hand_rake");
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		return false;
	}

	/**
	 * Calculates the new weed growth age depending on the used tool variant
	 *
	 * @return 0, if iron variant is used, otherwise a random value of the
	 * interval [0, currentWeedMeta]
	 */
	private int calculateGrowthStage(int toolMeta, int currentWeedMeta, Random random) {
		if (toolMeta == IRON_VARIANT_META) {
			return 0;
		}

		return Math.max(random.nextInt(currentWeedMeta / 2 + 1) - 1, 0) + currentWeedMeta / 2;
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
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
		list.add(StatCollector.translateToLocal("agricraft_tooltip.handRake"));
	}

	@Override
	public boolean removeWeeds(World world, BlockPos pos, IBlockState state, ICrop crop, ItemStack rake) {
		if (crop.hasWeed()) {
			int weedGrowthStage = state.getValue(AgriCraftProperties.GROWTHSTAGE);
			int newWeedGrowthStage = calculateGrowthStage(rake.getItemDamage(), weedGrowthStage, world.rand);
			crop.updateWeed(newWeedGrowthStage);
			if (ConfigurationHandler.rakingDrops && !crop.hasWeed() && world.rand.nextInt(100) < dropChance[rake.getItemDamage() % dropChance.length]) {
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
			return true;
		}
		return false;
	}

	public static final class ItemDropRegistry {

		private static ItemDropRegistry INSTANCE;

		private final WeightedRandom<ItemStack> registry;

		static {
			instance().registerDrop(new ItemStack(Blocks.tallgrass, 1, 1), 20);
			instance().registerDrop(new ItemStack(Blocks.tallgrass, 1, 2), 10);
			instance().registerDrop(new ItemStack(Blocks.double_plant, 1, 2), 10);
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

	@Override
	public void registerItemRenderer() {
		RegisterHelper.registerItemRenderer(this, VARIENTS);
	}

}
