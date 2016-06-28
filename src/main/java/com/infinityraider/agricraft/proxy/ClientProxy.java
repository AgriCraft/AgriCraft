package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.blocks.BlockBase;
import com.infinityraider.agricraft.handler.ItemToolTipHandler;
import com.infinityraider.agricraft.handler.MissingJsonHandler;
import com.infinityraider.agricraft.handler.SoundHandler;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.init.AgriBlocks;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.items.ItemBase;
import com.infinityraider.agricraft.renderers.blocks.BlockRendererRegistry;
import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.util.ReflectionHelper;
import com.infinityraider.agricraft.renderers.dynmodels.AgriCraftModelLoader;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

		// Init Model Loader
		ModelLoaderRegistry.registerLoader(AgriCraftModelLoader.INSTANCE);

		//BLOCKS
		//------
		ReflectionHelper.forEachIn(AgriBlocks.class, BlockBase.class, (block) -> {
			StateMapperBase stateMapper = new StateMapperBase() {
				@Override
				protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
					return block.getBlockModelResourceLocation();
				}
			};
			ModelLoader.setCustomStateMapper(block, stateMapper);
			//register the renderer
			BlockRendererRegistry.getInstance().registerCustomBlockRenderer(block);
		});

		//ITEMS
		//-----
		ReflectionHelper.forEachIn(AgriItems.class, ItemBase.class, (item) -> {
			item.registerItemRenderer();
		});

		// Clippings
		AgriItems.clipping.registerItemRenderer();

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
	public void registerVillagerSkin(int id, String resource) {
		//TODO
		//VillagerRegistry.instance().registerVillagerSkin(id, new ResourceLocation(Reference.MOD_ID, resource));
	}

	@Override
	public void initConfiguration(FMLPreInitializationEvent event) {
		IProxy.super.initConfiguration(event);
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
