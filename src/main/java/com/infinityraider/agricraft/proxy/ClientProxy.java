package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.blocks.BlockBase;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.handler.ItemToolTipHandler;
import com.infinityraider.agricraft.handler.MissingJsonHandler;
import com.infinityraider.agricraft.handler.SoundHandler;
import com.infinityraider.agricraft.handler.config.AgriCraftConfig;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.init.AgriCraftItems;
import com.infinityraider.agricraft.items.ItemBase;
import com.infinityraider.agricraft.renderers.blocks.BlockRendererRegistry;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.utility.OreDictHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.lang.reflect.Field;
import java.util.Iterator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientProxy implements IProxy {

	@Override
	public Side getPhysicalSide() {
		return Side.CLIENT;
	}

	@Override
	public Side getEffectiveSide() {
		return FMLCommonHandler.instance().getEffectiveSide();
	}

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
	@SuppressWarnings("unchecked")
	public void registerRenderers() {
		//BLOCKS
		//------
		for (Field field : AgriCraftBlocks.class.getDeclaredFields()) {
			if (field.getType().isAssignableFrom(BlockBase.class)) {
				try {
					Object obj = field.get(null);
					if (obj != null) {
						BlockBase block = (BlockBase) obj;
						StateMapperBase stateMapper = new StateMapperBase() {
							@Override
							protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
								return block.getBlockModelResourceLocation();
							}
						};
						ModelLoader.setCustomStateMapper(block, stateMapper);
						//register the renderer
						BlockRendererRegistry.getInstance().registerCustomBlockRenderer(block);
					}
				} catch (IllegalAccessException e) {
					AgriCore.getLogger("AgriCraft").trace(e);
				}
			}
		}

		//ITEMS
		//-----
		for (Field field : AgriCraftItems.class.getDeclaredFields()) {
			if (field.getType().isAssignableFrom(ItemBase.class)) {
				try {
					Object obj = field.get(null);
					// This is a safer check
					if (obj instanceof ItemBase) {
						((ItemBase) obj).registerItemRenderer();
					}
				} catch (IllegalAccessException e) {
					AgriCore.getLogger("AgriCraft").trace(e);
				}
			}
		}

		// Seeds
//		for (ItemAgriCraftSeed seed : AgriCraftCrops.seeds) {
//			try {
//				seed.registerItemRenderer();
//			} catch (Exception e) {
//				AgriCore.getLogger("AgriCraft").trace(e);
//			}
//		}

		// Clippings
		AgriCraftItems.clipping.registerItemRenderer();

		/*
		This method is getting too big... I'm still in favor of having a
		registerRenderers function in each class...
		 */
		// Custom Crops

		// Nuggets
		OreDictHelper.registerNuggetRenderers();

		//villager
		if (!AgriCraftConfig.disableWorldGen && AgriCraftConfig.villagerEnabled) {
			//TODO: register villager skin
			//VillagerRegistry.instance().registerVillagerSkin(78943, new ResourceLocation("textures/entity/villager/farmer.png"));  //For now, it uses the texture for the vanilla farmer
		}

		AgriCore.getLogger("AgriCraft").debug("Renderers registered");
	}

	@Override
	public void registerEventHandlers() {
		IProxy.super.registerEventHandlers();

		MissingJsonHandler missingJsonHandler = new MissingJsonHandler();
		MinecraftForge.EVENT_BUS.register(missingJsonHandler);

		ItemToolTipHandler itemToolTipHandler = new ItemToolTipHandler();
		MinecraftForge.EVENT_BUS.register(itemToolTipHandler);

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
		while (mods.hasNext()) {
			modContainer = (ModContainer) mods.next();
			if (modContainer.getModId().equalsIgnoreCase("NotEnoughItems")) {
				//API.hideItem(stack);
			}
		}
	}

	@Override
	public void registerVillagerSkin(int id, String resource) {
		//TODO
		//VillagerRegistry.instance().registerVillagerSkin(id, new ResourceLocation(Reference.MOD_ID, resource));
	}

	@Override
	public void initConfiguration(FMLPreInitializationEvent event) {
		IProxy.super.initConfiguration(event);
		ConfigurationHandler.initClientConfigs(event);
	}

	@Override
	public void queueTask(Runnable task) {
		if (getEffectiveSide() == Side.CLIENT) {
			Minecraft.getMinecraft().addScheduledTask(task);
		} else {
			FMLClientHandler.instance().getServer().addScheduledTask(task);
		}
	}
}
