package com.InfinityRaider.AgriCraft.proxy;

import codechicken.nei.api.API;

import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.compatibility.NEI.NEIConfig;
import com.InfinityRaider.AgriCraft.handler.ItemToolTipHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.init.Items;
import com.InfinityRaider.AgriCraft.renderers.*;
import com.InfinityRaider.AgriCraft.tileentity.*;
import com.InfinityRaider.AgriCraft.utility.LogHelper;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import java.util.Iterator;

public class ClientProxy extends CommonProxy {
    //register custom renderers
    public void registerRenderers() {
        //crops
        cropRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderCrop renderCrops = new RenderCrop();
        RenderingRegistry.registerBlockHandler(cropRenderID, renderCrops);
        //seed analyzer
        TileEntitySpecialRenderer  renderAnalyzer = new RenderSeedAnalyzer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySeedAnalyzer.class, renderAnalyzer);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.seedAnalyzer), new RenderItemSeedAnalyzer(renderAnalyzer, new TileEntitySeedAnalyzer()));

        //water tank
        TileEntitySpecialRenderer renderTank = new RenderTank();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, renderTank);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.blockWaterTank), new RenderItemTank(renderTank, new TileEntityTank()));

        //water channel
        TileEntitySpecialRenderer renderChannel = new RenderChannel();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChannel.class, renderChannel);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.blockWaterChannel), new RenderItemChannel(renderChannel, new TileEntityChannel()));

        //sprinkler
        TileEntitySpecialRenderer renderSprinkler = new RenderSprinkler();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySprinkler.class, renderSprinkler);
        MinecraftForgeClient.registerItemRenderer(Items.sprinkler, new RenderItemSprinkler());

        LogHelper.info("Renderers registered");
    }

    //register forge event handlers
    @Override
    public void registerEventHandlers() {
        super.registerEventHandlers();
        FMLCommonHandler.instance().bus().register(new ItemToolTipHandler());
        MinecraftForge.EVENT_BUS.register(new ItemToolTipHandler());
    }

    //initialize NEI
    public void initNEI() {
        if (ModIntegration.LoadedMods.nei) {
            NEIConfig configNEI = new NEIConfig();
            configNEI.loadConfig();
        }
    }

    //hide items in NEI
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
