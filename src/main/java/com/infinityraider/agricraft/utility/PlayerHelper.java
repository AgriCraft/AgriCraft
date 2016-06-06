package com.infinityraider.agricraft.utility;

import com.agricraft.agricore.core.AgriCore;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

import static com.infinityraider.agricraft.config.AgriCraftConfig.STAT_FORMAT;
import static com.infinityraider.agricraft.config.AgriCraftConfig.cropStatCap;
import java.text.MessageFormat;

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

	public static void addStats(List<String> lines, int growth, int gain, int strength) {
		try {
			lines.add(MessageFormat.format(STAT_FORMAT, AgriCore.getTranslator().translate("agricraft_tooltip.growth"), growth, cropStatCap));
			lines.add(MessageFormat.format(STAT_FORMAT, AgriCore.getTranslator().translate("agricraft_tooltip.gain"), gain, cropStatCap));
			lines.add(MessageFormat.format(STAT_FORMAT, AgriCore.getTranslator().translate("agricraft_tooltip.strength"), strength, cropStatCap));
		} catch (IllegalArgumentException e) {
			lines.add("Invalid Stat Format!");
		}
	}

}
