package com.infinityraider.agricraft.blocks.tiles.irrigation;

import com.infinityraider.agricraft.api.irrigation.IConnectable;
import com.infinityraider.agricraft.api.irrigation.IIrrigationComponent;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.network.MessageSyncFluidLevel;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.blocks.tiles.TileEntityCustomWood;

import com.infinityraider.infinitylib.block.multiblock.IMultiBlockComponent;
import com.infinityraider.infinitylib.block.multiblock.IMultiBlockPartData;
import com.infinityraider.infinitylib.block.multiblock.MultiBlockManager;
import com.infinityraider.infinitylib.block.multiblock.MultiBlockPartData;
import com.infinityraider.infinitylib.network.NetworkWrapper;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import com.agricraft.agricore.core.AgriCore;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import net.minecraft.util.ITickable;
import com.infinityraider.agricraft.reference.AgriNBT;

public class TileEntityTank extends TileEntityCustomWood implements ITickable, IFluidHandler, IIrrigationComponent, IMultiBlockComponent<MultiBlockManager, MultiBlockPartData>, IDebuggable {

	public static final int SYNC_DELTA = Constants.HALF_BUCKET_mB;

	public static final int DISCRETE_MAX = Constants.WHOLE;

	public static final int SINGLE_CAPACITY = 8 * Constants.BUCKET_mB;

	/**
	 * Don't call this directly, use getFluidLevel() and setFluidLevel(int
	 * amount) because only the tank at position (0, 0, 0) in the multiblock
	 * holds the liquid.
	 * <p>
	 * Represents the amount of fluid the tank is holding.
	 * </p>
	 */
	private int fluidLevel = 0;
	private int lastFluidLevel = 0;
	private int lastDiscreteFluidLevel = 0;
	private MultiBlockPartData multiBlockData;
	/**
	 * Main component cache is only used in the server thread because it's
	 * accessed there very often
	 */
	private TileEntityTank mainComponent;

	@Override
	protected void writeNBT(NBTTagCompound tag) {
		if (this.fluidLevel > 0) {
			tag.setInteger(AgriNBT.LEVEL, this.fluidLevel);
		}
	}

	@Override
	protected void readNBT(NBTTagCompound tag) {
		this.fluidLevel = tag.hasKey(AgriNBT.LEVEL) ? tag.getInteger(AgriNBT.LEVEL) : 0;
	}

	//updates the tile entity every tick
	@Override
	public void update() {
		if (!this.worldObj.isRemote) {
			if (this.worldObj.canBlockSeeSky(getPos()) && this.worldObj.isRaining()) {
				if (!this.hasNeighbour(EnumFacing.UP)) {
					Biome biome = this.worldObj.getBiomeGenForCoords(getPos());
					if (biome.getRainfall() > 0) {
						this.setFluidLevel(this.getFluidAmount(0) + 1);
					}
				}
			}
			Block block = this.worldObj.getBlockState(pos.add(0, 1, 0)).getBlock();
			if (AgriCraftConfig.fillFromFlowingWater && (block == Blocks.WATER || block == Blocks.FLOWING_WATER)) {
				this.setFluidLevel(this.getFluidAmount(0) + 5);
			}
		}
	}

	@Override
	public void syncFluidLevel() {
		if (needsSync()) {
			NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(this.worldObj.provider.getDimension(), this.xCoord(), this.yCoord(), this.zCoord(), 64);
			NetworkWrapper.getInstance().sendToAllAround(new MessageSyncFluidLevel(this.fluidLevel, this.getPos()), point);
		}
	}

	private boolean needsSync() {
		int newDiscreteLvl = getDiscreteFluidLevel();
		//sync when the discrete fluid LEVEL has changed
		if (newDiscreteLvl != lastDiscreteFluidLevel) {
			lastDiscreteFluidLevel = newDiscreteLvl;
			lastFluidLevel = fluidLevel;
			return true;
		}
		//sync when the fluid LEVEL ahs changed too much
		if (SYNC_DELTA <= Math.abs(lastFluidLevel - fluidLevel)) {
			lastDiscreteFluidLevel = newDiscreteLvl;
			lastFluidLevel = fluidLevel;
			return true;
		}
		return false;
	}

	public boolean isConnectedToChannel(EnumFacing direction) {
		if ((this.worldObj != null) && (direction !=null) && (direction.getFrontOffsetY() == 0)) {
			TileEntity tile = this.getWorld().getTileEntity(pos.offset(direction));
			if (tile instanceof TileEntityChannel) {
				return ((TileEntityChannel) tile).isSameMaterial(this);
			}
		}
		return false;
	}

	//TANK METHODS
	//------------
	public FluidStack getContents() {
		return new FluidStack(FluidRegistry.WATER, this.getFluidAmount(0));
	}

	@Override
	public int getFluidAmount(int y) {
		if (this.getMainComponent() == this) {
			return this.fluidLevel;
		}
		TileEntityTank mainComponent = this.getMainComponent();
		return mainComponent != null ? mainComponent.getFluidAmount(0) : 0;
	}

	/**
	 * TEMPORARY: Fix to correct build.
	 *
	 * @param lvl
	 * @return
	 */
	@Override
	public float getFluidHeight(int lvl) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public int getYPosition() {
		return getMultiBlockData().posY();
	}

	/**
	 * Maps the current fluid LEVEL into the interval [0,
	 * {@value #DISCRETE_MAX}]
	 */
	public int getDiscreteFluidLevel() {
		IMultiBlockPartData data = getMultiBlockData();
		float discreteFactor = DISCRETE_MAX / ((float) SINGLE_CAPACITY * data.sizeX() * data.sizeZ());
		int discreteFluidLevel = Math.round(discreteFactor * getFluidAmount(0));
		// This is so the fluid shows up over the bottom...
		if (discreteFluidLevel < 2 && getFluidAmount(0) > 0) {
			discreteFluidLevel = 2;
		}
		return discreteFluidLevel;
	}

	@Override
	public int getFluidHeight() {
		return this.getDiscreteFluidLevel();
	}

	@Override
	public boolean canAcceptFluid(int y, int amount, boolean partial) {
		return (partial && this.getFluidAmount(0) < this.getCapacity()) || (this.getFluidAmount(0) + amount <= this.getCapacity());
	}

	@Override
	public int acceptFluid(int y, int amount, boolean partial) {
		if (!worldObj.isRemote && this.canAcceptFluid(y, amount, partial) && amount >= 0) {
			int room = this.getCapacity() - this.getFluidAmount(0);
			if (room >= amount) {
				this.setFluidLevel(this.getFluidAmount(0) + amount);
				amount = 0;
			} else if (room > 0) {
				this.setFluidLevel(this.getCapacity());
				amount = amount - room;
			}
		}
		return amount;
	}

	@Override
	public void setFluidLevel(int lvl) {
		if (lvl != this.getFluidAmount(0)) {
			TileEntityTank tank = this.getMainComponent();
			lvl = lvl > tank.getCapacity() ? tank.getCapacity() : lvl;
			tank.fluidLevel = lvl;
			if (!tank.worldObj.isRemote) {
				tank.syncFluidLevel();
			}
		}
	}

	@Override
	public boolean canConnectTo(EnumFacing side, IConnectable component) {
		return false;
	}

	@Override
	public int getCapacity() {
		return SINGLE_CAPACITY * getMultiBlockData().size();
	}

	public boolean isEmpty() {
		return this.getFluidAmount(0) == 0;
	}

	/**
	 * IFluidHandler methods
	 */
	/**
	 * ---------------------
	 */
	//try to fill the tank
	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if (resource == null || !this.canFill(from, resource.getFluid())) {
			return 0;
		}
		int filled = Math.min(resource.amount, this.getCapacity() - this.getFluidAmount(0));
		if (doFill && !worldObj.isRemote) {
			this.setFluidLevel(this.getFluidAmount(0) + filled);
		}
		return filled;
	}

	//try to drain from the tank
	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		if (resource == null || !this.canDrain(from, resource.getFluid())) {
			return null;
		}
		int drained = Math.min(resource.amount, this.getFluidAmount(0));
		if (doDrain && !worldObj.isRemote) {
			this.setFluidLevel(this.getFluidAmount(0) - drained);
		}
		return new FluidStack(FluidRegistry.WATER, drained);
	}

	//try to drain from the tank
	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		return this.drain(from, new FluidStack(FluidRegistry.WATER, maxDrain), doDrain);
	}

	//check if the tank can be filled
	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return fluid == FluidRegistry.WATER && this.getFluidAmount(0) != this.getCapacity();
	}

	//check if the tank can be drained
	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return fluid == FluidRegistry.WATER && this.getFluidAmount(0) != 0;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		FluidTankInfo[] info = new FluidTankInfo[1];
		info[0] = new FluidTankInfo(this.getContents(), this.getCapacity());
		return info;
	}

	/**
	 * MultiBlock methods
	 */
	/**
	 * ------------------
	 */
	@Override
	public TileEntityTank getMainComponent() {
		if (worldObj.isRemote) {
			IMultiBlockPartData data = this.getMultiBlockData();
			return (TileEntityTank) worldObj.getTileEntity(getPos().add(-data.posX(), -data.posY(), -data.posZ()));
		}
		if (this.mainComponent == null) {
			IMultiBlockPartData data = this.getMultiBlockData();
			this.mainComponent = (TileEntityTank) worldObj.getTileEntity(getPos().add(-data.posX(), -data.posY(), -data.posZ()));
		}
		return mainComponent;
	}

	// This is kinda an odd choice.
	@Override
	public MultiBlockManager getMultiBlockManager() {
		return MultiBlockManager.INSTANCE;
	}

	@Override
	public void setMultiBlockPartData(MultiBlockPartData data) {
		this.multiBlockData = data;
		this.mainComponent = null;
		this.markForUpdate();
	}

	@Override
	public MultiBlockPartData getMultiBlockData() {
		if (this.multiBlockData == null) {
			this.multiBlockData = new MultiBlockPartData(0, 0, 0, 1, 1, 1);
		}
		return this.multiBlockData;
	}

	@Override
	public boolean hasNeighbour(EnumFacing dir) {
		IMultiBlockPartData data = this.getMultiBlockData();
		int x = data.posX() + dir.getFrontOffsetX();
		int y = data.posY() + dir.getFrontOffsetY();
		int z = data.posZ() + dir.getFrontOffsetZ();
		return (x >= 0 && x < data.sizeX()) && (y >= 0 && y < data.sizeY()) && (z >= 0 && z < data.sizeZ());
	}

	@Override
	public boolean isValidComponent(IMultiBlockComponent component) {
		return component instanceof TileEntityTank && this.isSameMaterial((TileEntityTank) component);
	}

	@Override
	public void preMultiBlockCreation(int sizeX, int sizeY, int sizeZ) {
		int lvl = 0;
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				for (int z = 0; z < sizeZ; z++) {
					TileEntityTank tank = (TileEntityTank) worldObj.getTileEntity(getPos().add(xCoord(), yCoord(), zCoord()));
					if (tank == null) {
						continue;
					}
					lvl = lvl + tank.fluidLevel;
					tank.fluidLevel = 0;
				}
			}
		}
		this.fluidLevel = lvl;
	}

	@Override
	public void postMultiBlockCreation() {
		this.mainComponent = null;
	}

	@Override
	public void preMultiBlockBreak() {
		MultiBlockPartData data = this.getMultiBlockData();
		int[] fluidLevelByLayer = new int[data.sizeY()];
		int area = data.sizeX() * data.sizeZ();
		int fluidContentByLayer = area * SINGLE_CAPACITY;
		int layer = 0;
		while (fluidLevel > 0 && layer < fluidLevelByLayer.length) {
			fluidLevelByLayer[layer] = (fluidLevel >= fluidContentByLayer) ? (fluidContentByLayer / area) : (fluidLevel / area);
			fluidLevel = (fluidLevel >= fluidContentByLayer) ? (fluidLevel - fluidContentByLayer) : 0;
			layer++;
		}
		for (int x = 0; x < data.sizeX(); x++) {
			for (int y = 0; y < fluidLevelByLayer.length; y++) {
				for (int z = 0; z < data.sizeZ(); z++) {
					TileEntityTank tank = (TileEntityTank) worldObj.getTileEntity(getPos().add(xCoord(), yCoord(), zCoord()));
					if (tank != null) {
						tank.fluidLevel = fluidLevelByLayer[y];
					}
				}
			}
		}
	}

	@Override
	public void postMultiBlockBreak() {
		this.mainComponent = null;
		this.syncFluidLevel();
	}

	/**
	 * IDebuggable methods
	 */
	/**
	 * -------------------
	 */
	//debug info
	@Override
	public void addDebugInfo(List<String> list) {
		super.addDebugInfo(list);
		IMultiBlockPartData data = this.getMultiBlockData();
		TileEntityTank root = getMainComponent();
		list.add("TANK:");
		list.add("coordinates: (" + xCoord() + ", " + yCoord() + ", " + zCoord() + ")");
		list.add("root coords: (" + root.xCoord() + ", " + root.yCoord() + ", " + root.zCoord() + ")");
		list.add("Tank: (single capacity: " + SINGLE_CAPACITY + ")");
		list.add("  - FluidLevel: " + this.getFluidAmount(0) + "/" + this.getCapacity());
		list.add("  - Water level is on layer " + (int) Math.floor((this.getFluidAmount(0) - 0.1F) / (this.getCapacity() * data.sizeX() * data.sizeZ())) + ".");
		list.add("  - Water height is " + this.getFluidHeight());
		StringBuilder neighbours = new StringBuilder();
		for (EnumFacing dir : EnumFacing.values()) {
			if (dir == null) {
				continue;
			}
			if (this.hasNeighbour(dir)) {
				neighbours.append(dir.name()).append(", ");
			}
		}
		list.add("  - Neighbours: " + neighbours.toString());
		list.add("  - MultiBlock data: " + data.toString());
		list.add("  - MultiBlock Size: " + data.sizeX() + "x" + data.sizeY() + "x" + data.sizeZ());
	}

	/**
	 * Waila methods
	 */
	/**
	 * -------------
	 */
	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	public void addDisplayInfo(List information) {
		super.addDisplayInfo(information);
		information.add(AgriCore.getTranslator().translate("agricraft_tooltip.waterLevel") + ": " + this.getFluidAmount(0) + "/" + this.getCapacity());
	}

	public int getCode(EnumFacing dir) {
		if(this.isConnectedToChannel(dir)) {
			return 1;
		} else if(this.hasNeighbour(dir)) {
			return 2;
		} else {
			return 0;
		}
	}

}
