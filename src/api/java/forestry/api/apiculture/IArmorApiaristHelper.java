package forestry.api.apiculture;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Helper class for checking whether a player is wearing Apiarist Armor
 *
 * @author Vexatos
 */
public interface IArmorApiaristHelper {

	/**
	 * Called when the apiarist's armor acts as protection against an attack.
	 * @param stack ItemStack to check
	 * @param player Player being attacked
	 * @param cause Optional cause of attack, such as a bee effect identifier
	 * @param doProtect Whether or not to actually do the side effects of protection
	 * @return Whether or not the item is valid Apiarist Armor and should protect the player from that attack
	 */
	boolean isArmorApiarist(ItemStack stack, EntityPlayer player, String cause, boolean doProtect);

	/**
	 * Called when the apiarist's armor acts as protection against an attack.
	 * @param player Player being attacked
	 * @param cause Optional cause of attack, such as a bee effect identifier
	 * @param doProtect Whether or not to actually do the side effects of protection
	 * @return The number of valid Apiarist Armor pieces the player is wearing that are actually protecting
	 */
	int wearsItems(EntityPlayer player, String cause, boolean doProtect);
}
