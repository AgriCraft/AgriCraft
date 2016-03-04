package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.blocks.BlockBase;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.handler.ItemToolTipHandler;
import com.infinityraider.agricraft.handler.MissingJsonHandler;
import com.infinityraider.agricraft.handler.TextureStitchHandler;
import com.infinityraider.agricraft.handler.SoundHandler;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.init.AgriCraftCrops;
import com.infinityraider.agricraft.init.AgriCraftItems;
import com.infinityraider.agricraft.init.CustomCrops;
import com.infinityraider.agricraft.init.ResourceCrops;
import com.infinityraider.agricraft.items.ItemBase;
import com.infinityraider.agricraft.items.ItemModSeed;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.renderers.renderinghacks.BlockRendererDispatcherWrapped;
import com.infinityraider.agricraft.renderers.player.renderhooks.RenderPlayerHooks;
import com.infinityraider.agricraft.utility.LogHelper;
import com.infinityraider.agricraft.utility.OreDictHelper;
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
		
		/*
		This method is getting too big... I'm still in favor of having a
		registerRenderers function in each class...
		*/
		
		// Custom Crops
		if (CustomCrops.customSeeds != null) {
			LogHelper.debug("Starting custom crop renderer registration...");
			for (ItemModSeed seed : CustomCrops.customSeeds) {
				try {
					seed.registerItemRenderer();
					LogHelper.debug("Registered Renderer for: " + seed.getRegistryName());
				} catch (Exception e) {
					LogHelper.printStackTrace(e);
				}
			}
			LogHelper.debug("Registered custom crop renderers!");
		}
		
		// Resource Crops
		if (ResourceCrops.vanillaSeeds != null) {
			LogHelper.debug("Starting vanillia crop renderer registration...");
			for (ItemModSeed seed : ResourceCrops.vanillaSeeds) {
				try {
					seed.registerItemRenderer();
					LogHelper.info("Registered Renderer for: " + seed.getRegistryName());
				} catch (Exception e) {
					LogHelper.printStackTrace(e);
				}
			}
			LogHelper.debug("Registered vanillia crop renderers!");
		}
		if (ResourceCrops.modSeeds != null) {
			LogHelper.debug("Starting resource crop renderer registration...");
			for (ItemModSeed seed : ResourceCrops.modSeeds) {
				try {
					seed.registerItemRenderer();
					LogHelper.info("Registered Renderer for: " + seed.getRegistryName());
				} catch (Exception e) {
					LogHelper.printStackTrace(e);
				}
			}
			LogHelper.debug("Registered resource crop renderers!");
		}
		
		// Nuggets
		OreDictHelper.registerNuggetRenderers();

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
        VillagerRegistry.instance().registerVillagerSkin(id, new ResourceLocation(Reference.MOD_ID, resource));
    }

    @Override
    public void initConfiguration(FMLPreInitializationEvent event) {
        super.initConfiguration(event);
        ConfigurationHandler.initClientConfigs(event);
    }
}
