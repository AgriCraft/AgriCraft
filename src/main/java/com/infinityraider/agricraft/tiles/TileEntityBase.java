package com.infinityraider.agricraft.tiles;

import com.infinityraider.agricraft.blocks.BlockBase;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import com.infinityraider.agricraft.multiblock.IMultiBlockComponent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import com.infinityraider.agricraft.api.v1.misc.IAgriDisplayable;
import com.infinityraider.agricraft.reference.AgriNBT;

/**
 * The root class for all AgriCraft TileEntities.
 */
public abstract class TileEntityBase extends TileEntity implements IAgriDisplayable {

	/**
	 * The orientation of the block. Defaults to AgriForgeDirection.UNKNOWN;
	 */
	protected AgriForgeDirection orientation = AgriForgeDirection.UNKNOWN;

	public final int xCoord() {
		return this.getPos().getX();
	}

	public final int yCoord() {
		return this.getPos().getY();
	}

	public final int zCoord() {
		return this.getPos().getZ();
	}

	@Override
	public BlockBase getBlockType() {
		return (BlockBase) super.getBlockType();
	}

	/**
	 * Saves the tile entity to an NBTTag.
	 */
	@Override
	public final NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (this.orientation != null) {
			tag.setByte(AgriNBT.DIRECTION, (byte) this.orientation.ordinal());
		}
		if (this.isMultiBlock()) {
			NBTTagCompound multiBlockTag = new NBTTagCompound();
			((IMultiBlockComponent) this).getMultiBlockData().writeToNBT(multiBlockTag);
			tag.setTag(AgriNBT.MULTI_BLOCK, multiBlockTag);
		}
		this.writeTileNBT(tag);
		return tag;
	}

	protected abstract void writeTileNBT(NBTTagCompound tag);

	/**
	 * Reads the tile entity from an NBTTag.
	 */
	@Override
	public final void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey(AgriNBT.DIRECTION)) {
			this.setOrientation(tag.getByte(AgriNBT.DIRECTION));
		}
		if (this.isMultiBlock()) {
			if (tag.hasKey(AgriNBT.MULTI_BLOCK)) {
				NBTTagCompound multiBlockTag = tag.getCompoundTag(AgriNBT.MULTI_BLOCK);
				((IMultiBlockComponent) this).getMultiBlockData().readFromNBT(multiBlockTag);
			}
		}
		this.readTileNBT(tag);
	}

	protected abstract void readTileNBT(NBTTagCompound tag);

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	/**
	 * Determines if the block may be rotated.
	 *
	 * @return if the block rotates.
	 */
	public abstract boolean isRotatable();

	/**
	 * Retrieves the block's orientation as a AgriForgeDirection.
	 *
	 * @return the block's orientation.
	 */
	public final AgriForgeDirection getOrientation() {
		return orientation;
	}

	/**
	 * Sets the block's orientation.
	 *
	 * @param orientation the new orientation of the block.
	 */
	public final void setOrientation(AgriForgeDirection orientation) {
		if (this.isRotatable() && orientation != AgriForgeDirection.UNKNOWN) {
			this.orientation = orientation;
			if (this.worldObj != null) {
				this.markForUpdate();
			}
		}
	}

	/**
	 * Sets the block's orientation from an integer. This is not the recommended
	 * method, and is only included for serialization purposes.
	 *
	 * @param orientation the orientation index
	 */
	private void setOrientation(int orientation) {
		this.setOrientation(AgriForgeDirection.getOrientation(orientation));
	}

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), this.getUpdateTag());
    }

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return tag;
	}

	//read data from packet
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	/**
	 * Marks the tile entity for an update.
	 */
	public final void markForUpdate() {
		this.markDirty();
	}

	private boolean isMultiBlock() {
		return this instanceof IMultiBlockComponent;
	}

	public Class getTileClass() {
		return this.getClass();
	}

	public TextureAtlasSprite getTexture(IBlockState state, @Nullable EnumFacing side) {
		//TODO
		return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
	}
}
