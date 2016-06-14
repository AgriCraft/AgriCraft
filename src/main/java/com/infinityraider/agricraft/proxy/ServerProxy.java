package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.handler.PlayerConnectToServerHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

@SideOnly(Side.SERVER)
public class ServerProxy implements IProxy {

	@Override
	public Side getPhysicalSide() {
		return Side.SERVER;
	}

	@Override
	public Side getEffectiveSide() {
		return getPhysicalSide();
	}

	@Override
	public EntityPlayer getClientPlayer() {
		return null;
	}

	@Override
	public World getClientWorld() {
		return null;
	}

	@Override
	public World getWorldByDimensionId(int dimension) {
		return FMLServerHandler.instance().getServer().worldServerForDimension(dimension);
	}

	@Override
	public void registerRenderers() {
	}

	@Override
	public void initNEI() {
	}

	@Override
	public void hideItemInNEI(ItemStack stack) {
	}

	@Override
	public void registerEventHandlers() {
		IProxy.super.registerEventHandlers();
		
        PlayerConnectToServerHandler playerConnectToServerHandler = new PlayerConnectToServerHandler();
        FMLCommonHandler.instance().bus().register(playerConnectToServerHandler);
        MinecraftForge.EVENT_BUS.register(playerConnectToServerHandler);
	}

	@Override
	public void registerVillagerSkin(int id, String resource) {
	}

	@Override
	public void queueTask(Runnable task) {
		FMLServerHandler.instance().getServer().addScheduledTask(task);
	}
}
