package com.infinityraider.agricraft.blocks.tiles;

import com.infinityraider.agricraft.utility.CustomWoodType;
import com.infinityraider.infinitylib.block.tile.TileEntityRotatableBase;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.misc.IAgriDisplayable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * This class represents the root tile entity for all AgriCraft custom WOOD
 * blocks. Through this class, the custom woods are remembered for the blocks. *
 */
public class TileEntityCustomWood extends TileEntityRotatableBase implements IDebuggable, IAgriDisplayable {
	/**  A pointer to the the block the CustomWoodBlock is imitating. */
	@Nonnull
	private CustomWoodType woodType = CustomWoodType.getDefault();

	public TileEntityCustomWood() {
		super();
	}

	@SideOnly(Side.CLIENT)
	@Nonnull
	public TextureAtlasSprite getIcon() {
		return woodType.getIcon();
	}

    @Override
	protected final void writeRotatableTileNBT(NBTTagCompound tag) {
        woodType.writeToNBT(tag);
		this.writeNBT(tag);
	}

	protected void writeNBT(NBTTagCompound tag) {
	}

	/**
	 * Loads the CustomWood entity from a NBTTag, as to load from a savefile.
	 *
	 * @param tag the TAG to load the entity data from.
	 */
    @Override
	protected final void readRotatableTileNBT(NBTTagCompound tag) {
		this.setMaterial(CustomWoodType.readFromNBT(tag));
		this.readNBT(tag);
	}

	protected void readNBT(NBTTagCompound tag) {
	}

	/**
	 * Tests to see if another CustomWood entity is of the same MATERIAL.
	 *
	 * @param tileEntity the CustomWood entity to test.
	 * @return if the construction materials for both entities are the same.
	 */
	public final boolean isSameMaterial(TileEntityCustomWood tileEntity) {
		return tileEntity != null && this.getBlockMetadata() == tileEntity.getBlockMetadata() && this.getMaterialBlock() == tileEntity.getMaterialBlock() && this.getMaterialMeta() == tileEntity.getMaterialMeta();
	}

	/**
	 * Sets the CustomWood block's MATERIAL, the MATERIAL to mimic, from an
	 * ItemStack.
	 *
	 * @param type: the type to set the block's MATERIAL to.
	 */
	public final void setMaterial(CustomWoodType type) {
        this.woodType = type;
	}

    public final void setMaterial(ItemStack stack) {
        this.setMaterial(CustomWoodType.readFromNBT(stack.getTagCompound()));
    }

	/**
	 * Sets the CustomWood block's MATERIAL, the MATERIAL to mimic, from the
	 * name of the MATERIAL (block) and its metadata value.
	 *
	 * @param block the name of the MATERIAL (block).
	 * @param meta the metadata value of the MATERIAL (block).
	 */
	public final void setMaterial(Block block, int meta) {
		if (block != null) {
            this.setMaterial(CustomWoodType.getFromBlockAndMeta(block, meta));
		}
	}

    public final CustomWoodType getMaterial() {
        return woodType;
    }

	/**
	 * Retrieves the MATERIAL the CustomWood is mimicking.
	 *
	 * @return the MATERIAL, in Block form.
	 */
	public final Block getMaterialBlock() {
		return this.getMaterial().getBlock();
	}

	public final IBlockState getMaterialState() {
		return getMaterial().getState();
	}

	/**
	 * Retrieves the metadata of the MATERIAL the CustomWood is mimicking.
	 *
	 * @return the metadata of the MATERIAL.
	 */
	public final int getMaterialMeta() {
		return this.getMaterial().getMeta();
	}

	public final ItemStack getMaterialStack() {
		return getMaterial().getStack();
	}

	/**
	 * Generates an NBTTag for the MATERIAL the CustomWood is mimicking.
	 *
	 * @return an NBTTag for the CustomWood MATERIAL.
	 */
	public final NBTTagCompound getMaterialTag() {
		return getMaterial().writeToNBT(new NBTTagCompound());
	}

	@Override
	public void addServerDebugInfo(List<String> list) {
		list.add("this material is: " + this.getMaterialBlock().getRegistryName() + ":" + this.getMaterialMeta());
	}

    @Override
	public void addDisplayInfo(List information) {
		information.add(AgriCore.getTranslator().translate("agricraft_tooltip.material") + ": " + getMaterialStack().getDisplayName());
	}
}
