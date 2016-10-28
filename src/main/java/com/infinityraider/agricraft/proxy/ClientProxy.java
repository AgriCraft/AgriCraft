package com.infinityraider.agricraft.proxy;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.handler.ItemToolTipHandler;
import com.infinityraider.agricraft.handler.SoundHandler;
import com.infinityraider.agricraft.utility.CustomWoodType;
import com.infinityraider.agricraft.utility.ModelErrorSuppressor;
import com.infinityraider.infinitylib.proxy.base.IClientProxyBase;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class ClientProxy implements IClientProxyBase, IProxy {
    @Override
    public void registerEventHandlers() {
        IProxy.super.registerEventHandlers();
        registerEventHandler(new ItemToolTipHandler());
        registerEventHandler(new SoundHandler());
    }

    @Override
    public void initConfiguration(FMLPreInitializationEvent event) {
        registerEventHandler(new ModelErrorSuppressor());
    }

    @Override
    public void initCustomWoodTypes() {
        CustomWoodType.initClient();
    }
}
