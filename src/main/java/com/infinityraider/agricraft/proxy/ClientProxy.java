package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.handler.ItemToolTipHandler;
import com.infinityraider.agricraft.handler.MissingJsonHandler;
import com.infinityraider.agricraft.handler.SoundHandler;
import com.infinityraider.agricraft.utility.ModelErrorSuppressor;
import com.infinityraider.infinitylib.proxy.IClientProxyBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy implements IClientProxyBase {

	@Override
	public void registerEventHandlers() {
		super.registerEventHandlers();

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
		super.initConfiguration(event);
		MinecraftForge.EVENT_BUS.register(new ModelErrorSuppressor());
	}
}
