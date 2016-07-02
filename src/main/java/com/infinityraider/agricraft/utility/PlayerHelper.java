package com.infinityraider.agricraft.utility;

import com.agricraft.agricore.util.TypeHelper;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

public abstract class PlayerHelper {

	public static float getPlayerYaw(EntityPlayer player) {
		float yaw = player.rotationYaw + player.cameraYaw;
		while (yaw < 0) {
			yaw = yaw + 360;
		}
		return yaw;
	}

	public static AgriForgeDirection getPlayerFacing(EntityPlayer player) {
		float yaw = getPlayerYaw(player);
		if (yaw > 45 && yaw <= 135) {
			return AgriForgeDirection.WEST;
		}
		if (yaw > 135 && yaw <= 225) {
			return AgriForgeDirection.NORTH;
		}
		if (yaw > 225 && yaw <= 315) {
			return AgriForgeDirection.EAST;
		}
		return AgriForgeDirection.SOUTH;
	}
	
	public static boolean sendMessage(EntityPlayer player, String... lines) {
		return sendMessage(player, TypeHelper.asList(lines));
	}
	
	public static boolean sendMessage(EntityPlayer player, List<String> lines) {
		lines.forEach((msg) -> player.addChatComponentMessage(new TextComponentString(msg)));
		return true;
	}

}
