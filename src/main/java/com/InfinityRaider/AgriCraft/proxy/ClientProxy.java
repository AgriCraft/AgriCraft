package com.InfinityRaider.AgriCraft.proxy;

import codechicken.nei.api.API;
import com.InfinityRaider.AgriCraft.blocks.BlockAgriCraft;
import com.InfinityRaider.AgriCraft.compatibility.NEI.NEIConfig;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.handler.ItemToolTipHandler;
import com.InfinityRaider.AgriCraft.handler.SoundHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.init.Items;
import com.InfinityRaider.AgriCraft.items.ItemAgricraft;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.player.renderhooks.RenderPlayerHooks;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

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
        //BLOCKS
        //------
        for(Field field:Blocks.class.getDeclaredFields()) {
            if(field.getType().isAssignableFrom(BlockAgriCraft.class)) {
                try {
                    Object obj = field.get(null);
                    if(obj!=null) {
                        ((BlockAgriCraft) obj).getRenderer();
                    }
                } catch (IllegalAccessException e) {
                    LogHelper.printStackTrace(e);
                }
            }
        }

        //ITEMS
        //-----
        for(Field field: Items.class.getDeclaredFields()) {
            if(field.getType().isAssignableFrom(ItemAgricraft.class)) {
                try {
                    Object obj = field.get(null);
                    if(obj!=null) {
                        ((ItemAgricraft) obj).getItemRenderer();
                    }
                }catch (IllegalAccessException e) {
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

    @Override
    public void registerEventHandlers() {
        super.registerEventHandlers();

        ItemToolTipHandler itemToolTipHandler = new ItemToolTipHandler();
        MinecraftForge.EVENT_BUS.register(itemToolTipHandler);

        RenderPlayerHooks renderPlayerHooks = new RenderPlayerHooks();
        MinecraftForge.EVENT_BUS.register(renderPlayerHooks);

        SoundHandler soundHandler = new SoundHandler();
        MinecraftForge.EVENT_BUS.register(soundHandler);
    }

    @Override
    public void initNEI() {
        NEIConfig configNEI = new NEIConfig();
        configNEI.loadConfig();
    }

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

    @Override
    public int getRenderId(Block block) {
        return RenderBlockBase.getRenderId(block);
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
