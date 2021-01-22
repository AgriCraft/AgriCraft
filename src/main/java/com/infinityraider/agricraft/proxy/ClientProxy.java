package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.handler.ItemToolTipHandler;
import com.infinityraider.agricraft.utility.CustomWoodTypeRegistry;
import com.infinityraider.agricraft.utility.ModelErrorSuppressor;
import com.infinityraider.infinitylib.proxy.base.IClientProxyBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class ClientProxy implements IClientProxyBase, IProxy {

    @Override
    public void preInitStart(FMLPreInitializationEvent event) {
        IProxy.super.preInitStart(event);
        registerEventHandler(new ModelErrorSuppressor());
    }

    @Override
    public void registerEventHandlers() {
        IProxy.super.registerEventHandlers();
        registerEventHandler(ItemToolTipHandler.getInstance());
    }

    @Override
    public void initCustomWoodTypes() {
        CustomWoodTypeRegistry.initClient();
    }

    @Override
    public String translateToLocal(String string) {
        return I18n.format(string);
    }

    @Override
    public String getLocale() {
        return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
    }

}
