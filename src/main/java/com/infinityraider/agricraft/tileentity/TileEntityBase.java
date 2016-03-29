package com.infinityraider.agricraft.tileentity;

import com.infinityraider.agricraft.blocks.BlockBase;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import com.infinityraider.agricraft.utility.multiblock.IMultiBlockComponent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * The root class for all AgriCraft TileEntities.
 */
public abstract class TileEntityBase extends TileEntity {
    /**
     * The orientation of the block.
     * Defaults to AgriForgeDirection.UNKNOWN;
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
    public final void writeToNBT (NBTTagCompound tag){
        super.writeToNBT(tag);
        if (this.orientation != null) {
            tag.setByte(AgriCraftNBT.DIRECTION, (byte) this.orientation.ordinal());
        }
        if(this.isMultiBlock()) {
            NBTTagCompound multiBlockTag = new NBTTagCompound();
            ((IMultiBlockComponent) this).getMultiBlockData().writeToNBT(multiBlockTag);
            tag.setTag(AgriCraftNBT.MULTI_BLOCK, multiBlockTag);
        }
		this.writeTileNBT(tag);
    }
	
	protected abstract void writeTileNBT(NBTTagCompound tag);

    /**
     * Reads the tile entity from an NBTTag.
     */
    @Override
    public final void readFromNBT (NBTTagCompound tag){
        super.readFromNBT(tag);
        if (tag.hasKey(AgriCraftNBT.DIRECTION)) {
            this.setOrientation(tag.getByte(AgriCraftNBT.DIRECTION));
        }
        if(this.isMultiBlock()) {
            if(tag.hasKey(AgriCraftNBT.MULTI_BLOCK)) {
                NBTTagCompound multiBlockTag = tag.getCompoundTag(AgriCraftNBT.MULTI_BLOCK);
                ((IMultiBlockComponent) this).getMultiBlockData().readFromNBT(multiBlockTag);
            }
        }
		this.readTileNBT(tag);
    }
	
	protected abstract void readTileNBT(NBTTagCompound tag);

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
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
     * Sets the block's orientation from an integer.
     * This is not the recommended method, and is only included for serialization purposes.
     *
     * @param orientation the orientation index
     */
    private void setOrientation(int orientation) {
        this.setOrientation(AgriForgeDirection.getOrientation(orientation));
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), nbtTag);
    }

    //read data from packet
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        if(worldObj.isRemote) {
            markForUpdate();
        }
    }

    /**
     * Marks the tile entity for an update.
     */
    public final void markForUpdate() {

    }

    /**
     * Add the waila information to a list.
     * I reccomend a call to the super method where applicable.
     *
     * @param information the list to add to.
     */
    @SideOnly(Side.CLIENT)
    public abstract void addWailaInformation(List information);

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
