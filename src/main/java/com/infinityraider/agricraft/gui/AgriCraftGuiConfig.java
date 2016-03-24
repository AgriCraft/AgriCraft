package com.infinityraider.agricraft.gui;

import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.handler.config.ConfigCategory;
import com.infinityraider.agricraft.reference.Reference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class AgriCraftGuiConfig extends GuiConfig {

	public AgriCraftGuiConfig(GuiScreen guiScreen) {
		super(guiScreen, getConfigElements(), Reference.MOD_ID, false, false,
				GuiConfig.getAbridgedConfigPath(ConfigurationHandler.config.toString()));
	}

	private static List<IConfigElement> getConfigElements() {
		List<IConfigElement> configElements = new ArrayList<>();
		for (ConfigCategory e : ConfigCategory.values()) {
			String descr = "AgriCraft " + e.name + " Settings";
			String name = "agricraft.configgui.ctgy." + e.name;
			configElements.add(new DummyConfigElement.DummyCategoryElement(descr, name, new ConfigElement(ConfigurationHandler.config.getCategory(e.name)).getChildElements()));
		}
		return configElements;
	}
}
