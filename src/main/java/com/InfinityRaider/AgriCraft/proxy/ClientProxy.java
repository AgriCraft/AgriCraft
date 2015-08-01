package com.InfinityRaider.AgriCraft.proxy;

import codechicken.nei.api.API;
import com.InfinityRaider.AgriCraft.blocks.BlockAgriCraft;
import com.InfinityRaider.AgriCraft.compatibility.NEI.NEIConfig;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.handler.ItemToolTipHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.renderers.blocks.*;
import com.InfinityRaider.AgriCraft.renderers.player.renderhooks.RenderPlayerHooks;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Field;
import java.util.Iterator;

public class ClientProxy extends CommonProxy {

    @Override
    public int getRenderId(Block block) {
        return RenderBlockBase.getRenderId(block);
    }

    @Override
    public void registerVillagerSkin(int id, String resource) {
        VillagerRegistry.instance().registerVillagerSkin(id, new ResourceLocation(Reference.MOD_ID.toLowerCase(), resource));
    }

    //register custom renderers
    @Override
    public void registerRenderers() {
        //BLOCKS
        //------
        for(Field field:Blocks.class.getDeclaredFields()) {
            if(field.getType().isAssignableFrom(BlockAgriCraft.class)) {
                try {
                    ((BlockAgriCraft) field.get(null)).getRenderer();
                } catch (IllegalAccessException e) {
                    LogHelper.printStackTrace(e);
                }
            }
        }

        //villager
        if (!ConfigurationHandler.disableWorldGen && ConfigurationHandler.villagerEnabled) {
            VillagerRegistry.instance().registerVillagerSkin(78943, new ResourceLocation("textures/entity/villager/farmer.png"));  //For now, it uses the texture for the vanilla farmer
        }

        LogHelper.debug("Renderers registered");
    }

    //register forge event handlers
    @Override
    public void registerEventHandlers() {
        super.registerEventHandlers();

        ItemToolTipHandler itemToolTipHandler = new ItemToolTipHandler();
        MinecraftForge.EVENT_BUS.register(itemToolTipHandler);

        RenderPlayerHooks renderPlayerHooks = new RenderPlayerHooks();
        MinecraftForge.EVENT_BUS.register(renderPlayerHooks);
    }

    @Override
    public void initConfiguration(FMLPreInitializationEvent event) {
        super.initConfiguration(event);
        ConfigurationHandler.initClientConfigs(event);
    }

    //initialize NEI
    @Override
    public void initNEI() {
        NEIConfig configNEI = new NEIConfig();
        configNEI.loadConfig();
    }

    //hide items in NEI
    @Override
    public void hideItemInNEI(ItemStack stack) {
        Iterator mods = Loader.instance().getActiveModList().iterator();
        ModContainer modContainer;
        while(mods.hasNext()) {
            modContainer = (ModContainer) mods.next();
            if(modContainer.getModId().equalsIgnoreCase("NotEnoughItems")) {
                API.hideItem(stack);
            }
        }
    }
}
