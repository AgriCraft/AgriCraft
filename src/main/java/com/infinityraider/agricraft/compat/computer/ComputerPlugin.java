/*
 */
package com.infinityraider.agricraft.compat.computer;

import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.infinityraider.agricraft.api.AgriPlugin;
import com.infinityraider.agricraft.api.IAgriPlugin;
import com.infinityraider.agricraft.compat.computer.blocks.BlockPeripheral;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.infinitylib.render.block.BlockRendererRegistry;
import com.infinityraider.infinitylib.utility.RegisterHelper;

import com.agricraft.agricore.util.TypeHelper;

/**
 *
 *
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
        RegisterHelper.registerBlock(PERHIPHERAL, Reference.MOD_ID.toLowerCase(), PERHIPHERAL.getInternalName());
        TileEntity te = PERHIPHERAL.createNewTileEntity(null, 0);
        GameRegistry.registerTileEntity(te.getClass(), PERHIPHERAL.getInternalName());
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            BlockRendererRegistry.getInstance().registerCustomBlockRenderer(PERHIPHERAL);
        }
    }

}
