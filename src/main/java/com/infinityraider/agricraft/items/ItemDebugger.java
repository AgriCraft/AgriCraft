package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.items.modes.DebugModeClearGrass;
import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.infinityraider.agricraft.entity.EntityVillagerFarmer;
import com.infinityraider.agricraft.init.WorldGen;
import com.infinityraider.agricraft.items.modes.DebugModeDirtPlane;
import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.infinitylib.handler.ConfigurationHandler;
import com.infinityraider.infinitylib.item.ItemDebuggerBase;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemDebugger extends ItemDebuggerBase {

	public ItemDebugger() {
		super(true);
		this.setMaxStackSize(1);
		if (ConfigurationHandler.getInstance().debug) {
			this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
		}
	}

	@Override
	protected List<DebugMode> getDebugModes() {
		List<DebugMode> list = new ArrayList<>();
		list.add(new DebugModeClearGrass());
		list.add(new DebugModeDirtPlane());
		return list;
	}

	@Override
	public List<String> getOreTags() {
		return Collections.emptyList();
	}

}
