package com.InfinityRaider.AgriCraft.gui;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Reference;
import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

import java.util.ArrayList;
import java.util.List;


public class AgriCraftGuiConfig extends GuiConfig {

    public AgriCraftGuiConfig(GuiScreen guiScreen) {
        super(guiScreen, getConfigElements(), Reference.MOD_ID, false, false,
                GuiConfig.getAbridgedConfigPath(ConfigurationHandler.config.toString()));
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> configElements = new ArrayList<IConfigElement>();
        configElements.add(new DummyConfigElement.DummyCategoryElement("AgriCraft General Settings",
                "agricraft.configgui.ctgy.agricraft", AgriCraftEntry.class));
        configElements.add(new DummyConfigElement.DummyCategoryElement("Irrigation Settings",
                "agricraft.configgui.ctgy.irrigation", IrrigationEntry.class));
        configElements.add(new DummyConfigElement.DummyCategoryElement("Integration Settings",
                "agricraft.configgui.ctgy.integration", IntegrationEntry.class));
        return configElements;
    }

    public static class AgriCraftEntry extends GuiConfigEntries.CategoryEntry {

        public AgriCraftEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return new GuiConfig(this.owningScreen,
                    new ConfigElement(ConfigurationHandler.config.getCategory(ConfigurationHandler.CATEGORY_AGRICRAFT)).getChildElements(),
                    this.owningScreen.modID, ConfigurationHandler.CATEGORY_AGRICRAFT, false, false,
                    GuiConfig.getAbridgedConfigPath(ConfigurationHandler.config.toString()));
        }
    }

    public static class IrrigationEntry extends GuiConfigEntries.CategoryEntry {

        public IrrigationEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return new GuiConfig(this.owningScreen,
                    new ConfigElement(ConfigurationHandler.config.getCategory(ConfigurationHandler.CATEGORY_IRRIGATION)).getChildElements(),
                    this.owningScreen.modID, ConfigurationHandler.CATEGORY_IRRIGATION, false, false,
                    GuiConfig.getAbridgedConfigPath(ConfigurationHandler.config.toString()));
        }
    }

    public static class IntegrationEntry extends GuiConfigEntries.CategoryEntry {

        public IntegrationEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return new GuiConfig(this.owningScreen,
                    new ConfigElement(ConfigurationHandler.config.getCategory(ConfigurationHandler.CATEGORY_INTEGRATION)).getChildElements(),
                    this.owningScreen.modID, ConfigurationHandler.CATEGORY_INTEGRATION, false, false,
                    GuiConfig.getAbridgedConfigPath(ConfigurationHandler.config.toString()));
        }
    }
}
