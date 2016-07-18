package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.tiles.TileEntityCrop;
import com.infinityraider.agricraft.compat.computer.tiles.TileEntityPeripheral;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import com.agricraft.agricore.core.AgriCore;
import net.minecraft.world.World;

import java.util.List;

import static com.infinityraider.agricraft.compat.computer.methods.MethodUtilities.*;

public abstract class MethodBase implements IMethod {

	// Not entirely sure why this is private if it is final...
	public final String name;
	public final String signature;
	public final boolean requiresJournal;
	public final boolean appliesToPeripheral;
	public final boolean appliesToCrop;

	public MethodBase(String name) {
		this(name, false, true, false);
	}

	public MethodBase(String name, boolean j, boolean p, boolean c) {
		this.name = name;
		this.requiresJournal = j;
		this.appliesToPeripheral = p;
		this.appliesToCrop = c;
		this.signature = genSignature(name, getParameters());
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final String getSignature() {
		return signature;
	}

	@Override
	public final String getDescription() {
		return AgriCore.getTranslator().translate("agricraft_description.method." + this.getName());
	}

	@Override
	public final Object[] call(TileEntityPeripheral peripheral, World world, BlockPos pos, ItemStack journal, Object... args) throws MethodException {
		
		if (appliesToPeripheral) {
			if (args != null && args.length != 0) {
				throw new MethodException(this, "Too many arguments!");
			} else {
				return callMethodForPeripheral(peripheral, journal);
			}
		} else if (appliesToCrop) {

			AgriForgeDirection dir = AgriForgeDirection.valueOf(args[0].toString());

			if (dir == AgriForgeDirection.UNKNOWN) {
				throw new MethodException(this, "Invalid Direction!");
			}

			TileEntity tile = world.getTileEntity(pos.add(dir.offsetX, dir.offsetY, dir.offsetZ));

			if (tile == null) {
				throw new MethodException(this, "Missing Crop!");
			}
			
			if (!(tile instanceof TileEntityCrop)) {
				throw new MethodException(this, "Not a crop!");
			}

			return callMethodForCrop((TileEntityCrop) tile, journal);

		} else {
			throw new MethodException(this, "Command does not apply to anything!");
		}

	}

	// The object array thing here is quite odd.
	private Object[] callMethodForPeripheral(TileEntityPeripheral peripheral, ItemStack journal) throws MethodException {
		if (requiresJournal) {
			if (journal == null || journal.getItem() == null) {
				throw new MethodException(this, "Journal is missing");
			}
			ItemStack specimen = peripheral.getSpecimen();
			ItemStack seed = specimen.copy();
			if (!isSeedDiscovered(journal, seed)) {
				throw new MethodException(this, "No information about this seed in the journal");
			}
		}
		return onMethodCalled(peripheral);
	}

	private Object[] callMethodForCrop(TileEntityCrop crop, ItemStack journal) throws MethodException {
		boolean hasJournal = journal != null;
		if (requiresJournal) {
			if (!hasJournal) {
				throw new MethodException(this, "Journal is missing");
			}
			if (!isSeedDiscovered(journal, crop.getSeed().toStack())) {
				throw new MethodException(this, "No information about this seed in the journal");
			}
		}
		return onMethodCalled(crop);
	}

	protected abstract List<MethodParameter> getParameters();

	protected abstract Object[] onMethodCalled(TileEntityCrop crop) throws MethodException;

	protected abstract Object[] onMethodCalled(TileEntityPeripheral peripheral) throws MethodException;

}
