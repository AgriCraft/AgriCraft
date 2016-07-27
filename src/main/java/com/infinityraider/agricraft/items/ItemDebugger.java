package com.infinityraider.agricraft.items;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.infinityraider.agricraft.entity.EntityVillagerFarmer;
import com.infinityraider.agricraft.init.WorldGen;
import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.infinitylib.handler.ConfigurationHandler;
import com.infinityraider.infinitylib.item.ItemDebuggerBase;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemDebugger extends ItemDebuggerBase {

	@AgriConfigurable(
			category = AgriConfigCategory.DEBUG,
			key = "Grass Breaker Radius",
			min = "1", max = "50",
			comment = "The radius of the grass breaking tool."
	)
	private static int radius = 10;

	@AgriConfigurable(
			category = AgriConfigCategory.DEBUG,
			key = "Enable Debugger Grass Breaker",
			comment = "Enable debugger use as grass clearing tool."
	)
	private static boolean enableGrassBreaker = true;

	public ItemDebugger() {
		super(true);
		this.setMaxStackSize(1);
		if(ConfigurationHandler.getInstance().debug) {
			this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
		}
	}

	@Override
	protected List<DebugMode> getDebugModes() {
		List<DebugMode> list = new ArrayList<>();
		if(enableGrassBreaker) {
			list.add(new DebugModeClearGrass());
		}
		return list;
	}

	public static class DebugModeClearGrass extends DebugMode {
		@Override
		public String debugName() {
			return "clear grass";
		}

		@Override
		public void debugAction(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
			if (!world.isRemote) {
				pos = pos.toImmutable();
				for (int x = -radius; x < radius; x++) {
					for (int z = -radius; z < radius; z++) {
						BlockPos loc = pos.add(x, 0, z);
						Block block = world.getBlockState(loc).getBlock();
						if (block instanceof BlockBush) {
							world.destroyBlock(loc, false);
						}
					}
				}
			}
		}
	}

	public static class DebugModeSpawnFarmer extends DebugMode {
		@Override
		public String debugName() {
			return "spawn farmer";
		}

		@Override
		public void debugAction(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
			if (!world.isRemote) {
				EntityVillagerFarmer entityvillager = new EntityVillagerFarmer(world, WorldGen.getVillagerId());
				entityvillager.setLocationAndAngles((double) pos.getX() + 0.5D, (double) pos.getY() + 1, (double) pos.getZ() + 0.5D, 0.0F, 0.0F);
				world.spawnEntityInWorld(entityvillager);
			}
		}
	}

	@Override
	public List<String> getOreTags() {
		return Collections.emptyList();
	}

	@Override
	public List<Tuple<Integer, ModelResourceLocation>> getModelDefinitions() {
		return Collections.emptyList();
	}
}
