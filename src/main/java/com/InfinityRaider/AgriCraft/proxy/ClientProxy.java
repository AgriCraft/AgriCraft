package com.InfinityRaider.AgriCraft.proxy;

import com.InfinityRaider.AgriCraft.blocks.BlockBase;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.handler.ItemToolTipHandler;
import com.InfinityRaider.AgriCraft.handler.MissingJsonHandler;
import com.InfinityRaider.AgriCraft.handler.TextureStitchHandler;
import com.InfinityRaider.AgriCraft.handler.SoundHandler;
import com.InfinityRaider.AgriCraft.init.AgriCraftBlocks;
import com.InfinityRaider.AgriCraft.init.AgriCraftCrops;
import com.InfinityRaider.AgriCraft.init.AgriCraftItems;
import com.InfinityRaider.AgriCraft.items.ItemBase;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.renderers.renderinghacks.BlockRendererDispatcherWrapped;
import com.InfinityRaider.AgriCraft.renderers.player.renderhooks.RenderPlayerHooks;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.lang.reflect.Field;
import java.util.Iterator;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public World getWorldByDimensionId(int dimension) {
        return FMLClientHandler.instance().getServer().worldServerForDimension(dimension);
    }

    @Override
    public Entity getEntityById(World world, int id) {
        return world.getEntityByID(id);
    }

    @Override
    public void registerRenderers() {
        //apply the wrapped BlockRendererDispatcher
        BlockRendererDispatcherWrapped.init();

        //BLOCKS
        //------
        for(Field field:AgriCraftBlocks.class.getDeclaredFields()) {
            if(field.getType().isAssignableFrom(BlockBase.class)) {
                try {
                    Object obj = field.get(null);
                    if(obj!=null) {
                        ((BlockBase) obj).getRenderer();
                    }
                } catch (IllegalAccessException e) {
                    LogHelper.printStackTrace(e);
                }
            }
        }

        //ITEMS
        //-----
        for(Field field: AgriCraftItems.class.getDeclaredFields()) {
            if(field.getType().isAssignableFrom(ItemBase.class)) {
                try {
                    Object obj = field.get(null);
					// This is a safer check
                    if(obj instanceof ItemBase) {
                        ((ItemBase) obj).registerItemRenderer();
                    }
                }catch (IllegalAccessException e) {
                    LogHelper.printStackTrace(e);
                }
            }
        }
		
		// Seeds
		for(ItemModSeed seed : AgriCraftCrops.seeds) {
			try {
				seed.registerItemRenderer();
			} catch (Exception e) {
				LogHelper.printStackTrace(e);
			}
		}

        //villager
        if (!ConfigurationHandler.disableWorldGen && ConfigurationHandler.villagerEnabled) {
            VillagerRegistry.instance().registerVillagerSkin(78943, new ResourceLocation("textures/entity/villager/farmer.png"));  //For now, it uses the texture for the vanilla farmer
        }

        LogHelper.debug("Renderers registered");
    }

    @Override
    public void registerEventHandlers() {
        super.registerEventHandlers();

        MissingJsonHandler missingJsonHandler = new MissingJsonHandler();
        MinecraftForge.EVENT_BUS.register(missingJsonHandler);

        TextureStitchHandler textureStitchHandler = new TextureStitchHandler();
        MinecraftForge.EVENT_BUS.register(textureStitchHandler);

        ItemToolTipHandler itemToolTipHandler = new ItemToolTipHandler();
        MinecraftForge.EVENT_BUS.register(itemToolTipHandler);

        RenderPlayerHooks renderPlayerHooks = new RenderPlayerHooks();
        MinecraftForge.EVENT_BUS.register(renderPlayerHooks);

        SoundHandler soundHandler = new SoundHandler();
        MinecraftForge.EVENT_BUS.register(soundHandler);
    }

    @Override
    public void initNEI() {
        /*
        NEIConfig configNEI = new NEIConfig();
        configNEI.loadConfig();
        */
    }

    @Override
    public void hideItemInNEI(ItemStack stack) {
        Iterator mods = Loader.instance().getActiveModList().iterator();
        ModContainer modContainer;
        while(mods.hasNext()) {
            modContainer = (ModContainer) mods.next();
            if(modContainer.getModId().equalsIgnoreCase("NotEnoughItems")) {
                //API.hideItem(stack);
            }
        }
    }

    @Override
    public void registerVillagerSkin(int id, String resource) {
        VillagerRegistry.instance().registerVillagerSkin(id, new ResourceLocation(Reference.MOD_ID.toLowerCase(), resource));
    }

    @Override
    public void initConfiguration(FMLPreInitializationEvent event) {
        super.initConfiguration(event);
        ConfigurationHandler.initClientConfigs(event);
    }
}
