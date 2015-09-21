
package com.InfinityRaider.AgriCraft.tileentity;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import com.InfinityRaider.AgriCraft.api.v1.IDebuggable;
import com.InfinityRaider.AgriCraft.reference.Names;

public abstract class TileEntityMultiBlock extends TileEntityCustomWood implements IDebuggable {

	private int xPosition = 0;
	private int yPosition = 0;
	private int zPosition = 0;
	private int xSize = 1;
	private int ySize = 1;
	private int zSize = 1;

	// boolean to convert pre-1.4 multiblocks
	// Lets remove this... it has been quite some time...
	private boolean oldVersion = false;

	public TileEntityMultiBlock() {
		super();
	}

	// OVERRIDES
	// ---------
	// this saves the data on the tile entity
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("xPosition", xPosition);
		tag.setInteger("yPosition", yPosition);
		tag.setInteger("zPosition", zPosition);
		tag.setInteger("xSize", xSize);
		tag.setInteger("ySize", ySize);
		tag.setInteger("zSize", zSize);
	}

	// this loads the saved data for the tile entity
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey(Names.NBT.connected)) {
			oldVersion = true;
		} else {
			xPosition = tag.getInteger("xPosition");
			yPosition = tag.getInteger("yPosition");
			zPosition = tag.getInteger("zPosition");
			xSize = tag.getInteger("xSize");
			ySize = tag.getInteger("ySize");
			zSize = tag.getInteger("zSize");
		}
	}

	// updates the tile entity every tick
	@Override
	public void updateEntity() {
		if (!this.worldObj.isRemote) {
			if (oldVersion) {
				this.checkForMultiBlock();
			}
		}
	}

	// MULTIBLOCK METHODS
	// ------------------
	public int getXPosition() {
		return xPosition;
	}

	public int getYPosition() {
		return yPosition;
	}

	public int getZPosition() {
		return zPosition;
	}

	public int getXSize() {
		return xSize;
	}

	public int getYSize() {
		return ySize;
	}

	public int getZSize() {
		return zSize;
	}

	// checks if this is made of wood
	public boolean isWood() {
		return this.getBlockMetadata() == 0;
	}

	// checks if this is part of a multiblock
	public boolean isMultiBlock() {
		return this.getConnectedBlocks() > 1;
	}

	// returns the number of blocks in the multiblock
	public int getConnectedBlocks() {
		return xSize * ySize * zSize;
	}

	/**
	 * Determines if a tile entity may join to the multiblock.
	 * 
	 * @param tileEntity
	 * @return
	 */
	public abstract boolean canJoinMultiBlock(TileEntity tileEntity);

	public boolean hasNeighbour(char axis, int direction) {
		if (this.worldObj == null) {
			return false;
		}
		boolean x = axis == 'x';
		boolean y = axis == 'y';
		boolean z = axis == 'z';
		return this.isMultiBlockPartner(this.getWorldObj().getTileEntity(this.xCoord + (x ? direction : 0), this.yCoord + (y ? direction : 0), this.zCoord + (z ? direction : 0)));
	}

	// check if a tile entity is part of this multiblock
	public boolean isMultiBlockPartner(TileEntity tileEntity) {
		return this.getConnectedBlocks() > 1 && (this.canJoinMultiBlock(tileEntity)) && (this.getConnectedBlocks() == ((TileEntityMultiBlock) tileEntity).getConnectedBlocks());
	}

	// updates the multiblock, returns true if something has changed
	public boolean updateMultiBlock() {
		return this.checkForMultiBlock();
	}

	// multiblockify
	public boolean checkForMultiBlock() {
		if (!this.worldObj.isRemote) {
			// find dimensions
			int xPos = this.findArrayXPosition();
			int yPos = this.findArrayYPosition();
			int zPos = this.findArrayZPosition();
			int xSizeNew = this.findArrayXSize();
			int ySizeNew = this.findArrayYSize();
			int zSizeNew = this.findArrayZSize();
			if (xSizeNew == 1 && ySizeNew == 1 && zSizeNew == 1) {
				return false;
			}
			// iterate through the x-, y- and z-directions if all blocks are tanks
			for (int x = this.xCoord - xPos; x < this.xCoord - xPos + xSizeNew; x++) {
				for (int y = this.yCoord - yPos; y < this.yCoord - yPos + ySizeNew; y++) {
					for (int z = this.zCoord - zPos; z < this.zCoord - zPos + zSizeNew; z++) {
						if (this.canJoinMultiBlock(this.worldObj.getTileEntity(x, y, z))) {
							TileEntityMultiBlock tank = (TileEntityMultiBlock) this.worldObj.getTileEntity(x, y, z);
							int[] tankSize = tank.findArrayDimensions();
							if (!(xSizeNew == tankSize[0] && ySizeNew == tankSize[1] && zSizeNew == tankSize[2])) {
								return false;
							}
						} else {
							return false;
						}
					}
				}
			}
			// turn all the blocks into one multiblock
			this.setDimensions(xSizeNew, ySizeNew, zSizeNew);
			for (int x = this.xCoord - xPos; x < this.xCoord - xPos + xSizeNew; x++) {
				for (int y = this.yCoord - yPos; y < this.yCoord - yPos + ySizeNew; y++) {
					for (int z = this.zCoord - zPos; z < this.zCoord - zPos + zSizeNew; z++) {
						TileEntityMultiBlock tank = ((TileEntityMultiBlock) this.worldObj.getTileEntity(x, y, z));
						tank.setDimensions(xSizeNew, ySizeNew, zSizeNew);
						tank.markForUpdate();
					}
				}
			}
			// this.setFluidLevel(lvl);
			return true;
		}
		return false;
	}

	private void setDimensions(int xSize, int ySize, int zSize) {
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
		this.findPositionInMultiBlock();
	}

	private void findPositionInMultiBlock() {
		this.xPosition = findArrayXPosition();
		this.yPosition = findArrayYPosition();
		this.zPosition = findArrayZPosition();
	}

	private void resetMultiBlock() {
		this.xSize = 1;
		this.ySize = 1;
		this.zSize = 1;
		this.xPosition = 0;
		this.yPosition = 0;
		this.zPosition = 0;
	}

	// breaks up the multiblock and divides the fluid among the tanks
	public void breakMultiBlock(int lvl) {
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				for (int z = 0; z < zSize; z++) {
					if (!(this.worldObj.getTileEntity(this.xCoord - xPosition + x, this.yCoord - yPosition + y, this.zCoord - zPosition + z) instanceof TileEntityMultiBlock)) {
						continue;
					}
					TileEntityMultiBlock block = (TileEntityMultiBlock) this.worldObj.getTileEntity(this.xCoord - xPosition + x, this.yCoord - yPosition + y, this.zCoord - zPosition + z);
					if (block == null) {
						continue;
					}
					oldVersion = false;
					block.markForUpdate();
					block.resetMultiBlock();
				}
			}
		}
	}

	// returns the xPosition of this block in along a row of these blocks along the X-axis
	protected int findArrayXPosition() {
		if (this.canJoinMultiBlock(this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord))) {
			return (((TileEntityMultiBlock) this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord)).findArrayXPosition() + 1);
		} else {
			return 0;
		}
	}

	// returns the yPosition of this block in along a row of these blocks along the Y-axis
	protected int findArrayYPosition() {
		if (this.canJoinMultiBlock(this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord))) {
			return (((TileEntityMultiBlock) this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord)).findArrayYPosition() + 1);
		} else {
			return 0;
		}
	}

	// returns the zPosition of this block in along a row of these blocks along the Z-axis
	protected int findArrayZPosition() {
		if (this.canJoinMultiBlock(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1))) {
			return (((TileEntityMultiBlock) this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1)).findArrayZPosition() + 1);
		} else {
			return 0;
		}
	}

	// returns the x size of an array of these blocks this block is in along the X-axis
	protected int findArrayXSize() {
		if (this.canJoinMultiBlock(this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord))) {
			return ((TileEntityMultiBlock) this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord)).findArrayXSize();
		} else {
			return this.findArrayXPosition() + 1;
		}
	}

	// returns the y size of an array of these blocks this block is in along the Y-axis
	protected int findArrayYSize() {
		if (this.canJoinMultiBlock(this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord))) {
			return ((TileEntityMultiBlock) this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord)).findArrayYSize();
		} else {
			return this.findArrayYPosition() + 1;
		}
	}

	// returns the z size of an array of these blocks this block is in along the Z-axis
	protected int findArrayZSize() {
		if (this.canJoinMultiBlock(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1))) {
			return ((TileEntityMultiBlock) this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1)).findArrayZSize();
		} else {
			return this.findArrayZPosition() + 1;
		}
	}

	// returns the x, y and z sizes of 3 arrays of these blocks along the cardinal directions
	protected int[] findArrayDimensions() {
		int[] size = new int[3];
		size[0] = this.findArrayXSize();
		size[1] = this.findArrayYSize();
		size[2] = this.findArrayZSize();
		return size;
	}

	// returns the xPosition of this block in the multiblock
	private int calculateXPosition() {
		if (this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord))) {
			return (((TileEntityMultiBlock) this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord)).calculateXPosition() + 1);
		} else {
			return 0;
		}
	}

	// returns the yPosition of this block in the multiblock
	private int calculateYPosition() {
		if (this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord))) {
			return (((TileEntityMultiBlock) this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord)).calculateYPosition() + 1);
		} else {
			return 0;
		}
	}

	// returns the zPosition of this block in the multiblock
	private int calculateZPosition() {
		if (this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1))) {
			return (((TileEntityMultiBlock) this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1)).calculateZPosition() + 1);
		} else {
			return 0;
		}
	}

	// returns the x size of the multiblock
	private int calculateXSize() {
		if (this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord))) {
			return ((TileEntityMultiBlock) this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord)).calculateXSize();
		} else {
			return this.calculateXPosition() + 1;
		}
	}

	// returns the y size of an array of these blocks this block is in along the Y-axis
	private int calculateYSize() {
		if (this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord))) {
			return ((TileEntityMultiBlock) this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord)).calculateYSize();
		} else {
			return this.calculateYPosition() + 1;
		}
	}

	// returns the z size of an array of these blocks this block is in along the Z-axis
	private int calculateZSize() {
		if (this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1))) {
			return ((TileEntityMultiBlock) this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1)).calculateZSize();
		} else {
			return this.calculateZPosition() + 1;
		}
	}

	// returns the x, y and z sizes of the multiblock
	public int[] calculateDimensions() {
		int[] size = new int[3];
		size[0] = this.calculateXSize();
		size[1] = this.calculateYSize();
		size[2] = this.calculateZSize();
		return size;
	}

	// debug info
	@Override
	public void addDebugInfo(List<String> list) {
		list.add("MULTIBLOCK:");
		list.add("Multiblock: " + (this.isWood() ? "wood" : "iron"));
		super.addDebugInfo(list);
		list.add("  - MultiBlock: " + this.isMultiBlock());
		list.add("  - Connected: " + this.getConnectedBlocks());
		int[] size = this.calculateDimensions();
		list.add("  - MultiBlock Size: " + size[0] + "x" + size[1] + "x" + size[2]);
		boolean left = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord));
		boolean right = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord));
		boolean back = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1));
		boolean front = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1));
		boolean top = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord));
		boolean below = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord));
		list.add("  - Found multiblock partners on: " + (left ? "left, " : "") + (right ? "right, " : "") + (back ? "back, " : "") + (front ? "front, " : "") + (top ? "top, " : "") + (below ? "below" : ""));
		list.add("this clicked is on  layer " + this.getYPosition() + ".");
	}

	/*
	 * @Override public void addWailaInformation(List information) { super.addWailaInformation(information); }
	 */
}
