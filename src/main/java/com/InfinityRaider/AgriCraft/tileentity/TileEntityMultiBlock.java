
package com.InfinityRaider.AgriCraft.tileentity;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.InfinityRaider.AgriCraft.api.v1.IDebuggable;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;

public abstract class TileEntityMultiBlock extends TileEntityCustomWood implements IDebuggable {

	/**
	 * Represents if the multiblock is pre-1.4
	 */
	private boolean oldVersion = false;

	private int xPosition = 0;
	private int yPosition = 0;
	private int zPosition = 0;
	private int xSize = 1;
	private int ySize = 1;
	private int zSize = 1;
	private int size = 1;

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
			size = xSize * ySize * zSize;
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

	// checks if this is part of a multiblock
	public boolean isMultiBlock() {
		return this.getConnectedBlocks() > 1;
	}

	// returns the number of blocks in the multiblock
	public int getConnectedBlocks() {
		return size;
	}

	/**
	 * Determines if a tile entity may join to the multiblock.
	 * 
	 * @param tileEntity
	 * @return
	 */
	public abstract boolean canJoinMultiBlock(TileEntity tileEntity);

	public boolean hasNeighbour(ForgeDirection direction) {
		if (this.worldObj == null || direction == null || direction == ForgeDirection.UNKNOWN) {
			return false;
		}
		return ((xPosition + direction.offsetX > -1) && (xPosition + direction.offsetX < xSize)) &&
				((yPosition + direction.offsetY > -1) && (yPosition + direction.offsetY < ySize)) &&
				((zPosition + direction.offsetZ > -1) && (zPosition + direction.offsetZ < zSize));
	}

	/**
	 * Check if a tile entity is part of this multiblock.
	 * TODO: fix bad method.
	 * 
	 * @param tileEntity
	 * @return
	 */
	public boolean isMultiBlockPartner(TileEntity tileEntity) {
		return this.getConnectedBlocks() > 1 && (this.canJoinMultiBlock(tileEntity)) && (this.getConnectedBlocks() == ((TileEntityMultiBlock) tileEntity).getConnectedBlocks());
	}

	// multiblockify
	public final boolean checkForMultiBlock() {		
		if (this.worldObj.isRemote) {
			return false;
		}
		// find dimensions
		int xPosNew = findEnd(ForgeDirection.WEST); //-x
		int yPosNew = findEnd(ForgeDirection.DOWN); //-y
		int zPosNew = findEnd(ForgeDirection.NORTH); //-z
		int xSizeNew = xPosNew + findEnd(ForgeDirection.EAST) + 1; //+x
		int ySizeNew = yPosNew + findEnd(ForgeDirection.UP) + 1; //+y
		int zSizeNew = zPosNew + findEnd(ForgeDirection.SOUTH) + 1; //+z
		if (xSizeNew == 1 && ySizeNew == 1 && zSizeNew == 1) {
			LogHelper.debug("No multiblock structure here...");
			LogHelper.debug("xpos:" + xPosNew + " ypos:" + yPosNew + " zpos:" + zPosNew);
			LogHelper.debug("xsize:" + xSizeNew + " ysize:" + ySizeNew + " zsize:" + zSizeNew);
			return false;
		}
		LogHelper.debug("Checking if all objects are multiblock parts.");
		//Check if all the blocks in the defined area are connectable multiblocks.
		for (int x = this.xCoord - xPosNew; x < this.xCoord - xPosNew + xSizeNew; x++) {
			for (int y = this.yCoord - yPosNew; y < this.yCoord - yPosNew + ySizeNew; y++) {
				for (int z = this.zCoord - zPosNew; z < this.zCoord - zPosNew + zSizeNew; z++) {
					if (!this.canJoinMultiBlock(this.worldObj.getTileEntity(x, y, z))) {
						return false;
					}
				}
			}
		}
		LogHelper.debug("Forming Multiblock.");
		LogHelper.debug("xpos:" + xPosNew + " ypos:" + yPosNew + " zpos:" + zPosNew);
		LogHelper.debug("xsize:" + xSizeNew + " ysize:" + ySizeNew + " zsize:" + zSizeNew);
		//Turn all the blocks into one multiblock
		for (int x = 0; x < xSizeNew; x++) {
			for (int y = 0; y < ySizeNew; y++) {
				for (int z = 0; z < zSizeNew; z++) {
					TileEntity te = this.worldObj.getTileEntity(this.xCoord - xPosNew + x, this.yCoord - yPosNew + y, this.zCoord - zPosNew + z);
					if (te instanceof TileEntityMultiBlock) {
						TileEntityMultiBlock block = ((TileEntityMultiBlock) te);
						block.breakMultiBlock();
						block.setMultiblock(x, y, z, xSizeNew, ySizeNew, zSizeNew);
						addBlock(block);
						block.markForUpdate();
					} else {
						LogHelper.debug("This is odd... a tile entity in the structure isn't the right type...");
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * Implement this method to patch into the multiblock formation loop.
	 * 
	 * @param te the block currently being added to the structure.
	 */
	public abstract void addBlock(TileEntityMultiBlock te);

	private void setMultiblock(int xPos, int yPos, int zPos, int xSize, int ySize, int zSize) {
		this.xPosition = xPos;
		this.yPosition = yPos;
		this.zPosition = zPos;
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
		this.size = xSize * ySize * zSize;
		this.oldVersion = false;
	}

	public void resetMultiBlock() {
		this.xPosition = 0;
		this.yPosition = 0;
		this.zPosition = 0;
		this.xSize = 1;
		this.ySize = 1;
		this.zSize = 1;
		this.size = 1;
		this.oldVersion = false;
	}

	// breaks up the multiblock.
	public void breakMultiBlock() {
		if (!this.isMultiBlock()) {
			return;
		}
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				for (int z = 0; z < zSize; z++) {
					if (this.worldObj.getTileEntity(this.xCoord - xPosition + x, this.yCoord - yPosition + y, this.zCoord - zPosition + z) instanceof TileEntityMultiBlock) {
						TileEntityMultiBlock block = (TileEntityMultiBlock) this.worldObj.getTileEntity(this.xCoord - xPosition + x, this.yCoord - yPosition + y, this.zCoord - zPosition + z);
						block.resetMultiBlock();
						block.markForUpdate();
					}
				}
			}
		}
	}
	
	/**
	 * Find the distance to the end of the multiblock in a certain direction.
	 * 
	 * @param dir
	 * @return
	 */
	private int findEnd(ForgeDirection dir) {
		if (this.canJoinMultiBlock(this.worldObj.getTileEntity(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ))) {
			return ((TileEntityMultiBlock) this.worldObj.getTileEntity(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ)).findEnd(dir) + 1;
		} else {
			return 0;
		}
	}

	// debug info
	@Override
	public void addDebugInfo(List<String> list) {
		checkForMultiBlock();
		list.add("MULTIBLOCK:");
		list.add("Multiblock:");
		super.addDebugInfo(list);
		list.add("  - MultiBlock: " + this.isMultiBlock());
		list.add("  - Connected: " + this.getConnectedBlocks());
		list.add("  - MultiBlock Size: " + xSize + "x" + ySize + "x" + zSize);
		boolean left = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord));
		boolean right = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord));
		boolean back = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1));
		boolean front = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1));
		boolean top = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord));
		boolean below = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord));
		list.add("  - Found multiblock partners on: " + (left ? "left, " : "") + (right ? "right, " : "") + (back ? "back, " : "") + (front ? "front, " : "") + (top ? "top, " : "") + (below ? "below" : ""));
		list.add("this clicked is on  layer " + this.getYPosition() + ".");
		list.add("Is controller? " + (xPosition + yPosition + zPosition == 0));
	}

	/*
	 * @Override public void addWailaInformation(List information) { super.addWailaInformation(information); }
	 */
}
