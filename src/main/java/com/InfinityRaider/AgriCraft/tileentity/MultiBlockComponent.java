
package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.utility.LogHelper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * <p>
 * Stores the immutable data associated with a multiblock.
 * </p>
 * <p>
 * Q: Why store multiblock data in here? <br>
 * A: An immutable object does not require getters and setters, and cannot be messed up after creation.
 * </p>
 */
public final class MultiBlockComponent {
	
	/**
	 * The default component to point to, as to save memory space.
	 */
	public static final MultiBlockComponent DEFAULT_COMPONENT = new MultiBlockComponent(0, 0, 0, 0, 0, 0, 1, 1, 1);

	/**
	 * The coordinates of the multiblock controller in the world. If the block is not connected to a multiblock controller, then the coordinates will be (0,0,0).
	 */
	public final int anchorX, anchorY, anchorZ;

	/**
	 * The relative coordinates of the component, in reference to the controller.
	 */
	public final int posX, posY, posZ;

	/**
	 * The dimensions of the multiblock.
	 */
	public final int sizeX, sizeY, sizeZ;

	/**
	 * The size of the multiblock (the number of blocks contained). Automatically calculated from the dimensions.
	 */
	public final int size;

	/**
	 * If the component is the multiblock controller.
	 */
	public final boolean isController;

	/**
	 * If the component is to a multiblock.
	 */
	public final boolean isPartOfMultiBlock;

	/**
	 * Construct the immutable object.
	 */
	public MultiBlockComponent(int anchorX, int anchorY, int anchorZ, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ) {
		this.anchorX = anchorX;
		this.anchorY = anchorY;
		this.anchorZ = anchorZ;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.size = sizeX * sizeY * sizeZ;
		this.isController = ((posX == 0) && (posY == 0) && (posZ == 0));
		this.isPartOfMultiBlock = this.size > 1;
		LogHelper.debug("Component formed at (" + this.posX + "," + this.posY + "," + this.posZ + ").");
		LogHelper.debug("Anchor: (" + this.anchorX + "," + this.anchorY + "," + this.anchorZ + ").");
	}

	/**
	 * Determines if another component is in the same multiblock.
	 * 
	 * @param component the component to test.
	 * @return true if member of the same multiblock as the component, false if not.
	 */
	public final boolean isPartner(MultiBlockComponent component) {
		return ((this.anchorX == component.anchorX) && (this.anchorY == component.anchorY) && (this.anchorZ == component.anchorZ));
	}

	/**
	 * Determines if the component has a neighbor in the structure to the given direction.
	 * 
	 * @param direction the direction to check.
	 * @return if a neighbor was found.
	 */
	public boolean hasNeighbour(ForgeDirection direction) {
		if ((direction != null) && (direction != ForgeDirection.UNKNOWN)) {
			return ((posX + direction.offsetX > -1) && (posX + direction.offsetX < sizeX)) && ((posY + direction.offsetY > -1) && (posY + direction.offsetY < sizeY)) && ((posZ + direction.offsetZ > -1) && (posZ + direction.offsetZ < sizeZ));
		}
		return false;
	}

	/**
	 * Generates a {@link MultiBlockComponent} from an NBTtag if the tag clears validation by {@link #validateNBT(NBTTagCompound)}.
	 * 
	 * @param tag the tag to generate the component from.
	 * @return the generated component, or null if the tag was invalid.
	 */
	public static final MultiBlockComponent readFromNBT(NBTTagCompound tag) {
		if (validateNBT(tag)) {
			LogHelper.debug("Reading MultiBlockComponent from NBT.");
			return new MultiBlockComponent(tag.getInteger("anchorX"), tag.getInteger("anchorY"), tag.getInteger("anchorZ"), tag.getInteger("posX"), tag.getInteger("posY"), tag.getInteger("posZ"), tag.getInteger("sizeX"), tag.getInteger("sizeY"), tag.getInteger("sizeZ"));
		}
		return null;
	}

	/**
	 * Transcribes the component to an NBTTag.
	 * Will not write anything if default component.
	 * 
	 * @param tag the tag to write to.
	 */
	public final void writeToNBT(NBTTagCompound tag) {
		if (isPartOfMultiBlock) {
			tag.setInteger("anchorX", anchorX);
			tag.setInteger("anchorY", anchorY);
			tag.setInteger("anchorZ", anchorZ);
			tag.setInteger("posX", posX);
			tag.setInteger("posY", posY);
			tag.setInteger("posZ", posZ);
			tag.setInteger("sizeX", sizeX);
			tag.setInteger("sizeY", sizeY);
			tag.setInteger("sizeZ", sizeZ);
		}
	}

	/**
	 * Determines if the tag holds a valid representation of a {@link MultiBlockComponent}.
	 * 
	 * @param tag the tag to check for a {@link MultiBlockComponent}.
	 * @return
	 */
	public static final boolean validateNBT(NBTTagCompound tag) {
		return tag.hasKey("anchorX") && tag.hasKey("anchorY") && tag.hasKey("anchorZ") && tag.hasKey("posX") && tag.hasKey("posY") && tag.hasKey("posZ") && tag.hasKey("sizeX") && tag.hasKey("sizeY") && tag.hasKey("sizeZ");
	}

}
