
package com.InfinityRaider.AgriCraft.tileentity;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.InfinityRaider.AgriCraft.api.v1.IDebuggable;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;

public abstract class TileEntityMultiBlock extends TileEntityCustomWood implements IDebuggable {

	private boolean reform = false;
	private int xPosition = 0;
	private int yPosition = 0;
	private int zPosition = 0;
	private int xSize = 1;
	private int ySize = 1;
	private int zSize = 1;
	private int size = 1;

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
			reform = true;
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
			if (reform) {
				this.formMultiBlock();
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

	public int getControllerX() {
		return this.xCoord - this.xPosition;
	}

	public int getControllerY() {
		return this.yCoord - this.yPosition;
	}

	public int getControllerZ() {
		return this.zCoord - this.zPosition;
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

	// returns the number of blocks in the multiblock
	public int getSize() {
		return size;
	}
	
	// checks if this is part of a multiblock
	public boolean isMultiBlock() {
		return size > 1;
	}
	
	public boolean isController() {
		return (xPosition + yPosition + zPosition == 0);
	}

	public boolean hasNeighbour(ForgeDirection direction) {
		if (this.worldObj == null || direction == null || direction == ForgeDirection.UNKNOWN) {
			return false;
		}
		return ((xPosition + direction.offsetX > -1) && (xPosition + direction.offsetX < xSize)) && ((yPosition + direction.offsetY > -1) && (yPosition + direction.offsetY < ySize)) && ((zPosition + direction.offsetZ > -1) && (zPosition + direction.offsetZ < zSize));
	}

	/**
	 * Check if a tile entity is part of this multiblock. TODO: fix bad method.
	 * 
	 * @param tileEntity
	 * @return
	 */
	public boolean isMultiBlockPartner(TileEntity tileEntity) {
		if (this.getSize() > 1 && tileEntity instanceof TileEntityMultiBlock) {
			TileEntityMultiBlock block = (TileEntityMultiBlock) tileEntity;
			return (this.getControllerX() == block.getControllerX()) && (this.getControllerY() == block.getControllerY()) && (this.getControllerZ() == block.getControllerZ());
		}
		return false;
	}

	/**
	 * Checks if a multiblock may be formed, and forms it if possible.
	 * 
	 * @return If a multiblock was formed.
	 */
	public final boolean formMultiBlock() {
		if (this.worldObj.isRemote) {
			return false;
		}
		// find dimensions
		int xPosNew = findEnd(ForgeDirection.WEST); // -x
		int yPosNew = findEnd(ForgeDirection.DOWN); // -y
		int zPosNew = findEnd(ForgeDirection.NORTH); // -z
		int xSizeNew = xPosNew + findEnd(ForgeDirection.EAST) + 1; // +x
		int ySizeNew = yPosNew + findEnd(ForgeDirection.UP) + 1; // +y
		int zSizeNew = zPosNew + findEnd(ForgeDirection.SOUTH) + 1; // +z
		if (xSizeNew == 1 && ySizeNew == 1 && zSizeNew == 1) {
			LogHelper.debug("No multiblock structure here...");
			LogHelper.debug("	xpos:" + xPosNew + " ypos:" + yPosNew + " zpos:" + zPosNew);
			LogHelper.debug("	xsize:" + xSizeNew + " ysize:" + ySizeNew + " zsize:" + zSizeNew);
			return false;
		}
		LogHelper.debug("Checking if all objects are multiblock parts.");
		// Check if all the blocks in the defined area are connectable multiblocks.
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
		// Turn all the blocks into one multiblock
		for (int x = 0; x < xSizeNew; x++) {
			for (int y = 0; y < ySizeNew; y++) {
				for (int z = 0; z < zSizeNew; z++) {
					TileEntity te = this.worldObj.getTileEntity(this.xCoord - xPosNew + x, this.yCoord - yPosNew + y, this.zCoord - zPosNew + z);
					if (te instanceof TileEntityMultiBlock) {
						TileEntityMultiBlock block = ((TileEntityMultiBlock) te);
						block.breakupMultiBlock();
						block.joinMultiblock(x, y, z, xSizeNew, ySizeNew, zSizeNew);
						block.addBlock();
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
	 * @param xPos
	 * @param yPos
	 * @param zPos
	 * @param xSize
	 * @param ySize
	 * @param zSize
	 */
	private void joinMultiblock(int xPos, int yPos, int zPos, int xSize, int ySize, int zSize) {
		this.xPosition = xPos;
		this.yPosition = yPos;
		this.zPosition = zPos;
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
		this.size = xSize * ySize * zSize;
		this.reform = false;
	}

	public void resetPart() {
		this.xPosition = 0;
		this.yPosition = 0;
		this.zPosition = 0;
		this.xSize = 1;
		this.ySize = 1;
		this.zSize = 1;
		this.size = 1;
		this.reform = false;
	}
	
	/**
	 * Hackity-Hack reform makes it come back!
	 */
	public final void breakupMultiBlock() {
		this.breakupMultiBlock(false);
	}

	// breaks up the multiblock.
	public final void breakupMultiBlock(boolean shouldReform) {
		if (this.worldObj.isRemote || !this.isMultiBlock()) {
			//LogHelper.debug("I hope this is a single tank...");
			return;
		}
		//int visited = 0;
		// Store these, so they won't get reset and break things.
		// Plus no need to keep calling them.
		final int anchorX = this.getControllerX();
		final int anchorY = this.getControllerY();
		final int anchorZ = this.getControllerZ();
		final int sizeX = this.getXSize();
		final int sizeY = this.getYSize();
		final int sizeZ = this.getZSize();
		//final int shouldVisit = size;
		final TileEntityMultiBlock controller;
		if (!(this.worldObj.getTileEntity(anchorX, anchorY, anchorZ) instanceof TileEntityMultiBlock)) {
			LogHelper.error("This is bad! The multiblock controller at (" + anchorX + "," + anchorY + "," + anchorZ + ") is missing! Aborting!");
			return;
		}
		controller = (TileEntityMultiBlock) this.worldObj.getTileEntity(anchorX, anchorY, anchorZ);
		//LogHelper.debug("Entering multiblock breaking loop.");
		// Break the multiblock one by one, with the controller being the last block broken.
		for (int x = sizeX - 1; x > -1; x--) {
			for (int y = sizeY - 1; y > -1; y--) {
				for (int z = sizeZ - 1; z > -1; z--) {
					if (this.worldObj.getTileEntity(anchorX + x, anchorY + y, anchorZ + z) instanceof TileEntityMultiBlock) {
						TileEntityMultiBlock block = (TileEntityMultiBlock) this.worldObj.getTileEntity(anchorX + x, anchorY + y, anchorZ + z);
						block.breakMultiPart(controller);
						block.resetPart();
						block.reform = shouldReform;
						block.markForUpdate();
						//visited++;
					} else {
						LogHelper.debug("Ooops! Went out of the multiblock when breaking it.");
					}
				}
			}
		}
		//LogHelper.debug("Visited " + visited + " of " + shouldVisit + " blocks while breaking multiblock.");
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
		formMultiBlock();
		list.add("MULTIBLOCK:");
		list.add("Multiblock:");
		super.addDebugInfo(list);
		list.add("  - MultiBlock: " + this.isMultiBlock());
		list.add("  - Connected: " + this.getSize());
		list.add("  - MultiBlock Size: " + xSize + "x" + ySize + "x" + zSize);
		boolean left = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord));
		boolean right = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord));
		boolean back = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1));
		boolean front = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1));
		boolean top = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord));
		boolean below = this.isMultiBlockPartner(this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord));
		list.add("  - Found multiblock partners on: " + (left ? "left, " : "") + (right ? "right, " : "") + (back ? "back, " : "") + (front ? "front, " : "") + (top ? "top, " : "") + (below ? "below" : ""));
		list.add("this clicked is on  layer " + this.getYPosition() + ".");
		list.add("Is controller? " + this.isController());
	}

	/*
	 * @Override public void addWailaInformation(List information) { super.addWailaInformation(information); }
	 */

	/**
	 * Determines if a tile entity may join to the multiblock.
	 * 
	 * @param tileEntity
	 * @return
	 */
	public abstract boolean canJoinMultiBlock(TileEntity tileEntity);
	
	/**
	 * Implement this method to patch into the multiblock formation loop.
	 * 
	 * @param te the block currently being added to the structure.
	 */
	public abstract void addBlock();

	/**
	 * Called when the multiblock structure is being broken. Used to patch into the break loop.
	 * 
	 * @param controller The multiblock controller. Keep in mind this could be a self-pointer.
	 */
	public abstract void breakMultiPart(TileEntityMultiBlock controller);
}
