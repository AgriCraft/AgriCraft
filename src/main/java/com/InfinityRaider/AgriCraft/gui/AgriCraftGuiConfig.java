package com.InfinityRaider.AgriCraft.gui;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
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
        for(Field field:ConfigurationHandler.Categories.class.getDeclaredFields()) {
            if(field.getType() == String.class) {
                try {
                    String category = (String) field.get(null);
                    String descr = "AgriCraft " + category + " Settings";
                    String name = "agricraft.configgui.ctgy." + category;
                    configElements.add(new DummyConfigElement.DummyCategoryElement(descr, name, new ConfigElement(ConfigurationHandler.config.getCategory(category)).getChildElements()));
                } catch(Exception e) {
                    LogHelper.printStackTrace(e);
                }
            }
        }
        return configElements;
    }
}
