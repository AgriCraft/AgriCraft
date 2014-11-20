package com.InfinityRaider.AgriCraft.proxy;

import com.InfinityRaider.AgriCraft.handler.ItemToolTipHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.renderers.*;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityTank;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import codechicken.nei.api.API;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import java.util.Iterator;

public class ClientProxy extends CommonProxy {
    @Override
    public ClientProxy getClientProxy() {return this;}

    //register custom renderers
    public void registerRenderers() {
        //crops
        TileEntitySpecialRenderer renderCrops = new RenderCrop();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrop.class, renderCrops);

        //seed analyzer
        TileEntitySpecialRenderer  renderAnalyzer = new RenderSeedAnalyzer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySeedAnalyzer.class, renderAnalyzer);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.seedAnalyzer), new RenderItemSeedAnalyzer(renderAnalyzer, new TileEntitySeedAnalyzer()));

        //water tank
        TileEntitySpecialRenderer renderTank = new RenderTank();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, renderTank);

        //water channel
        TileEntitySpecialRenderer renderChannel = new RenderChannel();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChannel.class, renderChannel);

        LogHelper.info("Renderers registered");
    }

    //register forge event handlers
    @Override
    public void registerEventHandlers() {
        super.registerEventHandlers();
        FMLCommonHandler.instance().bus().register(new ItemToolTipHandler());
        MinecraftForge.EVENT_BUS.register(new ItemToolTipHandler());
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
