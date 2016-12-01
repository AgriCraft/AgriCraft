package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.container.ContainerPeripheral;
import com.infinityraider.agricraft.container.ContainerSeedAnalyzer;
import com.infinityraider.agricraft.container.ContainerSeedStorage;
import com.infinityraider.agricraft.container.ContainerSeedStorageController;
import com.infinityraider.agricraft.gui.GuiPeripheral;
import com.infinityraider.agricraft.gui.GuiSeedAnalyzer;
import com.infinityraider.agricraft.gui.storage.GuiSeedStorage;
import com.infinityraider.agricraft.gui.storage.GuiSeedStorageController;
import com.infinityraider.agricraft.gui.journal.GuiJournal;
import com.infinityraider.agricraft.compat.computer.tiles.TileEntityPeripheral;
import com.infinityraider.agricraft.items.ItemJournal;
import com.infinityraider.agricraft.blocks.tiles.analyzer.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.blocks.tiles.storage.TileEntitySeedStorage;
import com.infinityraider.agricraft.blocks.tiles.storage.TileEntitySeedStorageController;
import com.infinityraider.agricraft.gui.AgriGuiWrapper;
import com.infinityraider.agricraft.utility.StackHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	public static final int ANALYZER_GUI_ID = 1;
	public static final int JOURNAL_GUI_ID = 2;
	public static final int SEED_STORAGE_GUI_ID = 3;
	public static final int SEED_CONTROLLER_GUI_ID = 4;
	public static final int PERIPHERAL_GUI_ID = 5;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
		switch (ID) {
			case (ANALYZER_GUI_ID):
				if (te instanceof TileEntitySeedAnalyzer) {
					return new ContainerSeedAnalyzer(player.inventory, (TileEntitySeedAnalyzer) te);
				}
			case (SEED_STORAGE_GUI_ID):
				if (te instanceof TileEntitySeedStorage) {
					return new ContainerSeedStorage(player.inventory, (TileEntitySeedStorage) te);
				}
			case (SEED_CONTROLLER_GUI_ID):
				if (te instanceof TileEntitySeedStorageController) {
					return new ContainerSeedStorageController(player.inventory, (TileEntitySeedStorageController) te);
				}
			case (PERIPHERAL_GUI_ID):
				if (te instanceof TileEntityPeripheral) {
					return new ContainerPeripheral(player.inventory, (TileEntityPeripheral) te);
				}
		}
		return null;
	}

	@Override
	public Gui getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
		switch (ID) {
			case (ANALYZER_GUI_ID):
				if (te instanceof TileEntitySeedAnalyzer) {
					return new GuiSeedAnalyzer(player.inventory, (TileEntitySeedAnalyzer) te);
				}
			case (JOURNAL_GUI_ID):
				if (StackHelper.isValid(player.getHeldItemMainhand(), ItemJournal.class)) {
                    return new AgriGuiWrapper(new GuiJournal(player.getHeldItemMainhand()));
				}
			case (SEED_STORAGE_GUI_ID):
				if (te instanceof TileEntitySeedStorage) {
					return new AgriGuiWrapper(new GuiSeedStorage(player.inventory, (TileEntitySeedStorage) te));
				}
			case (SEED_CONTROLLER_GUI_ID):
				if (te instanceof TileEntitySeedStorageController) {
					return new AgriGuiWrapper(new GuiSeedStorageController(player.inventory, (TileEntitySeedStorageController) te));
				}
			case (PERIPHERAL_GUI_ID):
				if (te instanceof TileEntityPeripheral) {
					return new GuiPeripheral(player.inventory, (TileEntityPeripheral) te);
				}
			default:
				return null;
		}
	}
}
