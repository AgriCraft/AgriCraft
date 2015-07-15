package com.InfinityRaider.AgriCraft.proxy;

import codechicken.nei.api.API;
import com.InfinityRaider.AgriCraft.compatibility.NEI.NEIConfig;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.handler.ItemToolTipHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.init.Items;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.renderers.*;
import com.InfinityRaider.AgriCraft.renderers.player.RenderPlayerHooks;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntitySprinkler;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityTank;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityValve;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import java.util.Iterator;

public class ClientProxy extends CommonProxy {
    public static int cropRenderId = -1;
    public static int tankRenderId = -1;
    public static int channelRenderId = -1;
    public static int channelFullRenderId = -1;
    public static int valveRenderId = -1;
    public static int waterPadRenderId = -1;

    @Override
    public int getRenderId(int nr) {
        switch(nr) {
            case Constants.cropId: return cropRenderId;
            case Constants.tankId: return tankRenderId;
            case Constants.channelId: return channelRenderId;
            case Constants.valveId: return valveRenderId;
            case Constants.channelFullId: return channelFullRenderId;
            case Constants.waterPadId: return waterPadRenderId;
        }
        return -1;
    }

    @Override
    public void registerVillagerSkin(int id, String resource) {
        VillagerRegistry.instance().registerVillagerSkin(id, new ResourceLocation(Reference.MOD_ID.toLowerCase(), resource));
    }

    //register custom renderers
    @Override
    public void registerRenderers() {
        //crops
        cropRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderCrop renderCrops = new RenderCrop();
        RenderingRegistry.registerBlockHandler(cropRenderId, renderCrops);

        //seed analyzer
        TileEntitySpecialRenderer  renderAnalyzer = new RenderSeedAnalyzer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySeedAnalyzer.class, renderAnalyzer);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.blockSeedAnalyzer), new RenderItemSeedAnalyzer(renderAnalyzer, new TileEntitySeedAnalyzer()));

        //water pad
        waterPadRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderWaterPad renderWaterPad = new RenderWaterPad();
        RenderingRegistry.registerBlockHandler(waterPadRenderId, renderWaterPad);

        //water tank
        tankRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderTank renderTank = new RenderTank();
        RenderingRegistry.registerBlockHandler(tankRenderId, renderTank);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.blockWaterTank), new RenderItemTank(new TileEntityTank()));

        //water channel
        channelRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderChannel renderChannel = new RenderChannel();
        RenderingRegistry.registerBlockHandler(channelRenderId, renderChannel);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.blockWaterChannel), new RenderItemChannel(new TileEntityChannel()));
        /*
        TileEntitySpecialRenderer renderChannelWater = new RenderChannel();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChannel.class, renderChannelWater);
        */

        //full block water channel
        channelFullRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderChannelFull renderChannelFull = new RenderChannelFull();
        RenderingRegistry.registerBlockHandler(channelFullRenderId, renderChannelFull);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.blockWaterChannelFull), new RenderItemChannelFull(new TileEntityChannel()));

        //channel valve
        valveRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderValve renderValve = new RenderValve();
        RenderingRegistry.registerBlockHandler(valveRenderId, renderValve);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.blockChannelValve), new RenderItemValve(new TileEntityValve()));
        /*
        TileEntitySpecialRenderer renderValveWater = new RenderValve();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityValve.class, renderValveWater);
        */

        //sprinkler
        TileEntitySpecialRenderer renderSprinkler = new RenderSprinkler();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySprinkler.class, renderSprinkler);
        MinecraftForgeClient.registerItemRenderer(Items.sprinkler, new RenderItemSprinkler());

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
        FMLCommonHandler.instance().bus().register(new ItemToolTipHandler());
        MinecraftForge.EVENT_BUS.register(new ItemToolTipHandler());
        MinecraftForge.EVENT_BUS.register(new RenderPlayerHooks());
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
