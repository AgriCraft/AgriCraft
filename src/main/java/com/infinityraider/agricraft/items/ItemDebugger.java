package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.items.modes.DebugModeCheckSoil;
import com.infinityraider.agricraft.items.modes.DebugModeClearGrass;
import com.infinityraider.agricraft.items.modes.DebugModeDirtPlane;
import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.infinitylib.handler.ConfigurationHandler;
import com.infinityraider.infinitylib.item.ItemDebuggerBase;
import com.infinityraider.infinitylib.utility.debug.DebugMode;

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
        list.add(new DebugModeCheckSoil());
		list.add(new DebugModeClearGrass());
		list.add(new DebugModeDirtPlane());
		return list;
	}

	@Override
	public List<String> getOreTags() {
		return Collections.emptyList();
	}

}
