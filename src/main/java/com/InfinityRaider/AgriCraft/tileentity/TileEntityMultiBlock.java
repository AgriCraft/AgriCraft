
package com.InfinityRaider.AgriCraft.tileentity;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.InfinityRaider.AgriCraft.api.v1.IDebuggable;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;

/**
 * The base TileEntity for all AgriCraft multiblock TileEntities.
 */
public abstract class TileEntityMultiBlock extends TileEntityCustomWood implements IDebuggable {
	
	/**
	 * The component storing the multiblock data. Should never be null.
	 */
	private MultiBlockComponent component;
	
	/**
	 * If the {@link TileEntityMultiBlock} should attempt to reform a multiblock on the next tick.
	 */
	private boolean reform = false;
	
	
	public TileEntityMultiBlock() {
		this.component = new MultiBlockComponent(xCoord, yCoord, zCoord, 0, 0, 0, 1, 1, 1);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.component.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey(Names.NBT.connected) || (tag.hasKey("xPosition"))) {
			reform = true;
		} else {
			this.component = MultiBlockComponent.readFromNBT(tag);
		}
		if (this.component == null) {
			this.resetComponent();
		}
	}

	/**
	 *  Updates the tile entity every tick.
	 *  Attempts to reform if the entity is marked for reformation.
	 */
	@Override
	public void updateEntity() {
		if (this.component == null) {
			LogHelper.error("The multiblock component at (" + this.xCoord + this.yCoord + this.zCoord + ") is impossibly null.");
		}
		if (!this.worldObj.isRemote) {
			if (this.reform) {
				LogHelper.debug("Reforming multiblock on request.");
				this.formMultiBlock();
				this.reform = false;
			}
		}
	}
	
	/**
	 * Retrieves the {@link MultiBlockComponent} tied to this tile entity.
	 * <p>
	 * This has to be done through a getter to prevent the pointer from being modified in this class.
	 * </p>
	 * @return the {@link MultiBlockComponent} associated with the entity.
	 */
	public final MultiBlockComponent getComponent() {
		return this.component;
	}

	/**
	 * Checks if a multiblock may be formed, and forms it if possible.
	 * <p>
	 * TODO: Decrease number of loops.
	 * </p>
	 * @return If a multiblock was formed.
	 */
	public final boolean formMultiBlock() {
		if (this.worldObj.isRemote) {
			return false;
		}
		// find dimensions
		final int xPosNew = findEnd(ForgeDirection.WEST); // -x
		final int yPosNew = findEnd(ForgeDirection.DOWN); // -y
		final int zPosNew = findEnd(ForgeDirection.NORTH); // -z
		final int xSizeNew = xPosNew + findEnd(ForgeDirection.EAST) + 1; // +x
		final int ySizeNew = yPosNew + findEnd(ForgeDirection.UP) + 1; // +y
		final int zSizeNew = zPosNew + findEnd(ForgeDirection.SOUTH) + 1; // +z
		final int anchorXNew = this.xCoord - xPosNew;
		final int anchorYNew = this.yCoord - yPosNew;
		final int anchorZNew = this.zCoord - zPosNew;
		
		if (xSizeNew == 1 && ySizeNew == 1 && zSizeNew == 1) {
			//LogHelper.debug("No multiblock structure here...");
			//LogHelper.debug("	xpos:" + xPosNew + " ypos:" + yPosNew + " zpos:" + zPosNew);
			//LogHelper.debug("	xsize:" + xSizeNew + " ysize:" + ySizeNew + " zsize:" + zSizeNew);
			return false;
		}
		//LogHelper.debug("Checking if all objects are multiblock parts.");
		// Check if all the blocks in the defined area are connectable multiblocks.
		for (int x = 0; x < xSizeNew; x++) {
			for (int y = 0; y < ySizeNew; y++) {
				for (int z = 0; z < zSizeNew; z++) {
					if (!this.canJoinMultiBlock(this.worldObj.getTileEntity(anchorXNew + x, anchorYNew + y, anchorZNew + z))) {
						return false;
					}
				}
			}
		}
		//LogHelper.debug("Forming Multiblock.");
		//LogHelper.debug("xpos:" + xPosNew + " ypos:" + yPosNew + " zpos:" + zPosNew);
		//LogHelper.debug("xsize:" + xSizeNew + " ysize:" + ySizeNew + " zsize:" + zSizeNew);
		// Turn all the blocks into one multiblock
		for (int x = 0; x < xSizeNew; x++) {
			for (int y = 0; y < ySizeNew; y++) {
				for (int z = 0; z < zSizeNew; z++) {
					TileEntity te = this.worldObj.getTileEntity(anchorXNew + x, anchorYNew + y, anchorZNew + z);
					if (te instanceof TileEntityMultiBlock) {
						TileEntityMultiBlock block = ((TileEntityMultiBlock) te);
						block.breakupMultiBlock();
						block.component = new MultiBlockComponent(anchorXNew, anchorYNew, anchorZNew, x, y, z, xSizeNew, ySizeNew, zSizeNew);
						block.addBlock();
						block.markForUpdate();
					} else {
						LogHelper.debug("This is odd... a tile entity in the structure isn't the right type...");
					}
				}
			}
		}
		//LogHelper.debug("Formation complete.");
		return true;
	}
	
	/**
	 * Breaks up a multiblock structure, and does not attempt to reform.
	 */
	public final void breakupMultiBlock() {
		this.breakupMultiBlock(false);
	}

	/**
	 * Breaks up a multiblock structure.
	 * 
	 * @param shouldReform if the broken off parts should try to reform one tick later.
	 */
	public final void breakupMultiBlock(boolean shouldReform) {
		if (this.worldObj.isRemote || !this.component.isPartOfMultiBlock) {
			//LogHelper.debug("I hope this is a single tank...");
			return;
		}
		//int visited = 0;
		// Store these, so they won't get reset and break things.
		// Plus no need to keep calling them.
		final int anchorX = this.component.anchorX;
		final int anchorY = this.component.anchorY;
		final int anchorZ = this.component.anchorZ;
		final int sizeX = this.component.sizeX;
		final int sizeY = this.component.sizeY;
		final int sizeZ = this.component.sizeZ;
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
						block.resetComponent();
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
	
	private final void resetComponent() {
		//LogHelper.debug("Resetting component.");
		this.component = new MultiBlockComponent(xCoord, yCoord, zCoord, 0, 0, 0, 1, 1, 1);
	}

	/**
	 * Find the distance to the end of the multiblock in a certain direction.
	 * 
	 * @param dir the direction to search.
	 * @return the number of connectable blocks in the given direction.
	 */
	private int findEnd(ForgeDirection dir) {
		if (this.canJoinMultiBlock(this.worldObj.getTileEntity(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ))) {
			return ((TileEntityMultiBlock) this.worldObj.getTileEntity(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ)).findEnd(dir) + 1;
		} else {
			return 0;
		}
	}

	@Override
	public void addDebugInfo(List<String> list) {
		formMultiBlock();
		list.add("MULTIBLOCK:");
		list.add("Multiblock:");
		super.addDebugInfo(list);
		list.add("  - MultiBlock: " + this.component.isPartOfMultiBlock);
		if (this.component.isPartOfMultiBlock) {
			list.add("  - Connected: " + this.component.size);
			list.add("  - MultiBlock Size: " + this.component.sizeX + "x" + this.component.sizeY + "x" + this.component.sizeZ);
			list.add("  - Is controller? " + this.component.isController);
			list.add("  - Clicked on layer: " + this.component.posY);
			list.add("  - Component Information: ");
			list.add("     - Position: (" + component.posX + "," + component.posY + "," + component.posZ + ").");
			list.add("     - Anchor: (" + component.anchorX + "," + component.anchorY + "," + component.anchorZ + ").");
		}
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
