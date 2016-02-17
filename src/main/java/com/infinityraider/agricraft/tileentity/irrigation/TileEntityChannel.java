package com.infinityraider.agricraft.tileentity.irrigation;

import com.infinityraider.agricraft.api.v1.IDebuggable;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.network.MessageSyncFluidLevel;
import com.infinityraider.agricraft.network.NetworkWrapperAgriCraft;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.tileentity.TileEntityCustomWood;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class TileEntityChannel extends TileEntityCustomWood implements ITickable, IIrrigationComponent, IDebuggable {
	public static final AgriForgeDirection[] VALID_DIRECTIONS = new AgriForgeDirection[]{
		AgriForgeDirection.NORTH,
		AgriForgeDirection.SOUTH,
		AgriForgeDirection.WEST,
		AgriForgeDirection.EAST
	};

	private final IIrrigationComponent[] neighbours = new IIrrigationComponent[4];
	protected int ticksSinceNeighbourCheck = 0;
	protected static final int NEIGHBOUR_CHECK_DELAY = 1024;

	// Might want to move this to a static import class...
	protected static final int MIN = 5;
	protected static final int MAX = 12;
	protected static final int HEIGHT = MAX - MIN;
	protected static final int DISCRETE_MAX = 16;
	protected static final int ABSOLUTE_MAX = ConfigurationHandler.channelCapacity;
	protected static final float DISCRETE_FACTOR = (float) DISCRETE_MAX / (float) ABSOLUTE_MAX;

	private int lvl;
	private int lastDiscreteLvl = 0;

	public TileEntityChannel() {
		super();
	}

	//this saves the data on the tile entity
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (this.lvl > 0) {
			tag.setInteger(AgriCraftNBT.LEVEL, this.lvl);
		}
	}

	//this loads the saved data for the tile entity
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		if (tag.hasKey(AgriCraftNBT.LEVEL)) {
			this.lvl = tag.getInteger(AgriCraftNBT.LEVEL);
		} else {
			this.lvl = 0;
		}
		super.readFromNBT(tag);
	}

	@Override
	public int getFluidLevel() {
		return this.lvl;
	}

	@Override
	public int getCapacity() {
		return ABSOLUTE_MAX;
	}

	@Override
	public void setFluidLevel(int lvl) {
		if (lvl >= 0 && lvl <= ABSOLUTE_MAX && lvl != this.lvl) {
			this.lvl = lvl;
			syncFluidLevel();
		}
	}

	@Override
	public int pushFluid(int amount) {
		if (!worldObj.isRemote && amount >= 0 && this.canAccept()) {
			int room = this.getCapacity() - this.getFluidLevel();
			if (room >= amount) {
				this.setFluidLevel(this.getFluidLevel() + amount);
				amount = 0;
			} else if (room > 0) {
				this.setFluidLevel(this.getCapacity());
				amount = amount - room;
			}
		}
		return amount;
	}

	@Override
	public int pullFluid(int amount) {
		if (!worldObj.isRemote && amount >= 0 && this.canProvide()) {
			if (amount <= this.getFluidLevel()) {
				this.setFluidLevel(this.getFluidLevel() - amount);
			} else {
				amount = this.getFluidLevel();
				this.setFluidLevel(0);
			}
		}
		return amount;
	}

	@Override
	public boolean canConnectTo(IIrrigationComponent component) {
		return (component instanceof TileEntityTank || component instanceof TileEntityChannel) && this.isSameMaterial((TileEntityCustomWood) component);
	}

	@Override
	public boolean canAccept() {
		return this.lvl < ABSOLUTE_MAX;
	}

	@Override
	public boolean canProvide() {
		return this.lvl > 0;
	}


	public final void updateNeighbours() {
		if (ticksSinceNeighbourCheck == 0) {
			findNeighbours();
		}
		ticksSinceNeighbourCheck = (ticksSinceNeighbourCheck + 1) % NEIGHBOUR_CHECK_DELAY;
	}

	@Override
	public float getFluidHeight() {
        return getFluidHeight(getFluidLevel());
    }

    @Override
    public float getFluidHeight(int lvl) {
        return MIN+HEIGHT*((float) lvl)/(ABSOLUTE_MAX);
    }


	public final void findNeighbours() {
		for (int i = 0; i < VALID_DIRECTIONS.length; i++) {
			AgriForgeDirection dir = VALID_DIRECTIONS[i];
			TileEntity te = worldObj.getTileEntity(dir.offset(getPos()));
			if (!(te instanceof IIrrigationComponent)) {
				neighbours[i] = null;
			} else {
				IIrrigationComponent neighbour = (IIrrigationComponent) te;
				neighbours[i] = (neighbour.canConnectTo(this) || this.canConnectTo(neighbour)) ? neighbour : null;
			}
		}
		ticksSinceNeighbourCheck = 0;
	}

	/**
	 * Only used for rendering
	 */
	@SideOnly(Side.CLIENT)
	public boolean hasNeighbourCheck(AgriForgeDirection direction) {
		if (this.worldObj == null) {
			return false;
		}
		TileEntity tileEntityAt = this.worldObj.getTileEntity(direction.offset(getPos()));
		return (tileEntityAt != null) && (tileEntityAt instanceof IIrrigationComponent) && (this.isSameMaterial((TileEntityCustomWood) tileEntityAt));
	}

	public IIrrigationComponent getNeighbor(AgriForgeDirection direction) {
		if (this.worldObj == null) {
			return null;
		} else if (direction.offsetY == 0 && direction.offsetX + direction.offsetZ != 0) {
			TileEntity tileEntityAt = this.worldObj.getTileEntity(this.getPos().add(direction.offsetX, 0, direction.offsetZ));
			return tileEntityAt instanceof IIrrigationComponent ? (IIrrigationComponent) tileEntityAt : null;
		}
		return null;
	}

	//updates the tile entity every tick
	@Override
	public void tick() {
		if (!this.worldObj.isRemote) {
			updateNeighbours();
			//calculate total fluid lvl and capacity
			int totalLvl = 0;
			int nr = 1;
			int updatedLevel = this.getFluidLevel();
			for (IIrrigationComponent component : neighbours) {
				if (component == null) {
					continue;
				}
				//neighbour is a channel: add its volume to the total and increase the COUNT
				if (component instanceof TileEntityChannel) {
					if (!(component instanceof TileEntityValve && ((TileEntityValve) component).isPowered())) {
						totalLvl = totalLvl + ((TileEntityChannel) component).lvl;
						nr++;
					}
				} //neighbour is a tank: calculate the fluid levels of the tank and the channel
				else {
					TileEntityTank tank = (TileEntityTank) component;
					int Y = tank.getYPosition();
					float y_c = Constants.WHOLE * Y + this.getFluidHeight(updatedLevel);  //initial channel water Y1
					float y_t = tank.getFluidHeight();           //initial tank water Y1
					float y1 = (float) MIN + Constants.WHOLE * Y;   //minimum Y1 of the channel
					float y2 = (float) MAX + Constants.WHOLE * Y;  //maximum Y1 of the channel
					int V_tot = tank.getFluidLevel() + updatedLevel;
					if (y_c != y_t) {
						//total volume is below the channel connection
						if (y_t <= y1) {
							updatedLevel = 0;
							tank.setFluidLevel(V_tot);
						} //total volume is above the channel connection
						else if (y_t >= y2) {
							updatedLevel = ABSOLUTE_MAX;
							tank.setFluidLevel(V_tot - ABSOLUTE_MAX);
						} //total volume is between channel connection top and bottom
						else {
							//some parameters
							int tankYSize = tank.getMultiBlockData().sizeY();
							int C = tank.getCapacity();
							//calculate the Y1 corresponding to the total volume: Y1 = f(V_tot), V_tank = f(Y1), V_channel = f(Y1)
							float enumerator = (V_tot) + ((ABSOLUTE_MAX * y1) / (y2 - y1) + ((float) 2 * C) / (Constants.WHOLE * tankYSize - 2));
							float denominator = ((ABSOLUTE_MAX) / (y2 - y1) + ((float) C) / ((float) (Constants.WHOLE * tankYSize - 2)));
							float y = enumerator / denominator;
							//convert the Y1 to volumes
							int channelVolume = (int) Math.floor(ABSOLUTE_MAX * (y - y1) / (y2 - y1));
							int tankVolume = (int) Math.ceil(C * (y - 2) / (Constants.WHOLE * tankYSize - 2));
							updatedLevel = channelVolume;
							tank.setFluidLevel(tankVolume);
						}
					}
				}
			}
			//equalize water LEVEL over all neighbouring channels
			totalLvl = totalLvl + updatedLevel;
			int rest = totalLvl % nr;
			int newLvl = totalLvl / nr;
			if (nr > 1) {
				//set fluid levels
				for (IIrrigationComponent component : neighbours) {
					//TODO: cleanup
					if (component instanceof TileEntityChannel) {
						if (!(component instanceof TileEntityValve && ((TileEntityValve) component).isPowered())) {
							int lvl = rest == 0 ? newLvl : newLvl + 1;
							rest = rest == 0 ? 0 : rest - 1;
							component.setFluidLevel(lvl);
						}
					}
				}
			}
			this.setFluidLevel(newLvl + rest);
		}
	}

	@Override
	public void syncFluidLevel() {
		if (!this.worldObj.isRemote) {
			int newDiscreteLvl = getDiscreteFluidLevel();
			if (newDiscreteLvl != lastDiscreteLvl) {
				lastDiscreteLvl = newDiscreteLvl;
				IMessage msg = new MessageSyncFluidLevel(this.lvl, this.getPos());
				NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(this.worldObj.provider.getDimensionId(), this.xCoord(), this.yCoord(), this.zCoord(), 64);
				NetworkWrapperAgriCraft.wrapper.sendToAllAround(msg, point);
			}
		}
	}

	/**
	 * Maps the current fluid LEVEL into the integer interval [0, 16]
	 */
	public int getDiscreteFluidLevel() {
		int discreteFluidLevel = Math.round(DISCRETE_FACTOR * lvl);
		if (discreteFluidLevel == 0 && lvl > 0) {
			discreteFluidLevel = 1;
		}
		return discreteFluidLevel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addDebugInfo(List<String> list) {
		list.add("CHANNEL:");
		super.addDebugInfo(list);
		list.add("  - FluidLevel: " + this.getFluidLevel() + "/" + ABSOLUTE_MAX);
		list.add("  - FluidHeight: " + this.getFluidHeight());
		list.add("  - Connections: ");
		for (AgriForgeDirection dir : VALID_DIRECTIONS) {
			if (this.hasNeighbourCheck(dir)) {
				list.add("      - " + dir.name());
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	public void addWailaInformation(List information) {
		//Required call to super.
		super.addWailaInformation(information);
		information.add(StatCollector.translateToLocal("agricraft_tooltip.waterLevel") + ": " + this.getFluidLevel() + "/" + ABSOLUTE_MAX);
	}
}
