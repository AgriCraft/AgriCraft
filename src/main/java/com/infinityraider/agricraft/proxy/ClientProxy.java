package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.handler.ItemToolTipHandler;
import com.infinityraider.agricraft.handler.MissingJsonHandler;
import com.infinityraider.agricraft.handler.SoundHandler;
import com.infinityraider.agricraft.utility.CustomWoodType;
import com.infinityraider.agricraft.utility.ModelErrorSuppressor;
import com.infinityraider.infinitylib.proxy.base.IClientProxyBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class ClientProxy implements IClientProxyBase, IProxy {
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
	public void initConfiguration(FMLPreInitializationEvent event) {
		IProxy.super.initConfiguration(event);
		MinecraftForge.EVENT_BUS.register(new ModelErrorSuppressor());
	}

	@Override
	public void initCustomWoodTypes() {
		CustomWoodType.initClient();
	}
}
