package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.handler.PlayerConnectToServerHandler;
import com.infinityraider.infinitylib.proxy.IServerProxyBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy implements IServerProxyBase {

	@Override
	public void registerEventHandlers() {
		super.registerEventHandlers();
        PlayerConnectToServerHandler playerConnectToServerHandler = new PlayerConnectToServerHandler();
        FMLCommonHandler.instance().bus().register(playerConnectToServerHandler);
        MinecraftForge.EVENT_BUS.register(playerConnectToServerHandler);
	}

	@Override
	public void registerVillagerSkin(int id, String resource) {
	}
}
