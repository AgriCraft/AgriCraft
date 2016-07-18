/*
 */
package com.infinityraider.agricraft.compat.computer;

import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.api.AgriPlugin;
import com.infinityraider.agricraft.api.IAgriPlugin;
import com.infinityraider.agricraft.compat.computer.blocks.BlockPeripheral;
import com.infinityraider.agricraft.renderers.blocks.BlockRendererRegistry;
import com.infinityraider.agricraft.utility.RegisterHelper;
import java.util.Set;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

/**
 *
 * @author RlonRyan
 */
@AgriPlugin
public class ComputerPlugin implements IAgriPlugin {

	public static final BlockPeripheral PERHIPHERAL = new BlockPeripheral();
	public static final Set<String> COMPUTER_MODS = TypeHelper.asSet(
			"computercraft",
			"opencomputers"
	);
	public static final boolean ENABLED = COMPUTER_MODS.stream().anyMatch(Loader::isModLoaded);

	@Override
	public boolean isEnabled() {
		return ENABLED;
	}

	@Override
	public void initPlugin() {
		RegisterHelper.registerBlock(PERHIPHERAL, PERHIPHERAL.getInternalName());
		TileEntity te = PERHIPHERAL.createNewTileEntity(null, 0);
		GameRegistry.registerTileEntity(te.getClass(), PERHIPHERAL.getTileName());
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			BlockRendererRegistry.getInstance().registerCustomBlockRenderer(PERHIPHERAL);
		}
	}

}
