/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.mail;

import com.mojang.authlib.GameProfile;
import forestry.api.core.INBTTagable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IMailAddress extends INBTTagable {

	EnumAddressee getType();
	String getName();

	boolean isValid();

	boolean isPlayer();
	boolean isTrader();

	GameProfile getPlayerProfile();
}
