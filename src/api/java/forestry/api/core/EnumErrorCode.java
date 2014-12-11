/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum EnumErrorCode {

	UNKNOWN("unknown"),

	OK("ok"),

	INVALIDBIOME("invalidBiome"),

	ISRAINING("isRaining"),

	NOTRAINING("notRaining"),

	NOFUEL("noFuel"), // Biogas & Peat-fired

	NOHEAT("noHeat"), // Biogas engine

	NODISPOSAL("noDisposal"),

	NORESOURCE("noResource"),

	NOTGLOOMY("notGloomy"),

	NOTLUCID("notLucid"),

	NOTDAY("notDay"),

	NOTNIGHT("notNight"),

	NOFLOWER("noFlower"),

	NOQUEEN("noQueen"),

	NODRONE("noDrone"),

	NOSKY("noSky"),

	NOSPACE("noSpace"),

	NORECIPE("noRecipe"),

	NOENERGYNET("noEnergyNet"),

	NOTHINGANALYZE("noSpecimen"),

	FORCEDCOOLDOWN("forcedCooldown"),

	NOHONEY("noHoney"),

	NOTPOSTPAID("notPostpaid", "noStamps"),

	NORECIPIENT("noRecipient"),

	NOTALPHANUMERIC("notAlphaNumeric"),

	NOTUNIQUE("notUnique"),

	NOSTAMPS("noStamps"),

	NOCIRCUITBOARD("noCircuitBoard"),

	WRONGSTACKSIZE("wrongStacksize"),

	NOFERTILIZER("noFertilizer"),

	NOFARMLAND("noFarmland"),

	CIRCUITMISMATCH("circuitMismatch"),

	NOLIQUID("noLiquid"),

	NOPAPER("noPaper"),

	NOSTAMPSNOPAPER("noStampsNoPaper", "noStamps"),

	NOSUPPLIES("noSupplies", "noResource"),

	NOTRADE("noTrade", "noResource"),

	NOPOWER("noPower");

	private String name;
	private String iconName;
	@SideOnly(Side.CLIENT)
	private IIcon icon;

	private EnumErrorCode(String name) {
		this(name, name);
	}

	private EnumErrorCode(String name, String iconName) {
		this.name = name;
		this.iconName = iconName;
	}

	public String getDescription() {
		return "errors." + name + ".desc";
	}

	public String getHelp() {
		return "errors." + name + ".help";
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		icon = register.registerIcon("forestry:errors/" + iconName);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return icon;
	}

	@SideOnly(Side.CLIENT)
	public static void initIcons(IIconRegister register) {
		for (EnumErrorCode code : values())
			code.registerIcons(register);
	}
}
