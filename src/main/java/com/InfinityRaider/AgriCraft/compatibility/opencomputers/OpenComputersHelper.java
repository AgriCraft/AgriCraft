package com.InfinityRaider.AgriCraft.compatibility.opencomputers;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.block.Block;

import java.lang.reflect.Method;

public class OpenComputersHelper extends ModHelper {
    @Override
    protected String modId() {
        return Names.Mods.openComputers;
    }

    @SuppressWarnings("unchecked")
    protected void onPostInit() {
        try {
            Class driverClass = Class.forName("li.cil.oc.api.Driver");
            Class blockClass = Class.forName("li.cil.oc.api.driver.Block");
            Class envClass = Class.forName("com.InfinityRaider.AgriCraft.compatibility.opencomputers.AgriCraftEnvironment");
            Method method = driverClass.getDeclaredMethod("add", blockClass);
            Object environment = envClass.getDeclaredConstructor().newInstance();
            method.invoke(null, environment);
            LogHelper.debug("AgriCraft Environment registered with OpenComputers");
        } catch(Exception e) {
            LogHelper.printStackTrace(e);
        }
    }

    public static Block getComputerBlock() {
        return (Block) Block.blockRegistry.getObject("OpenComputers:case1");
    }
}