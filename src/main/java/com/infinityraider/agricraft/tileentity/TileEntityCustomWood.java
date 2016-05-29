package com.infinityraider.agricraft.tileentity;

import com.infinityraider.agricraft.api.v1.IDebuggable;
import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.renderers.RenderUtil;
import com.agricraft.agricore.core.AgriCore;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * This class represents the root tile entity for all AgriCraft custom WOOD
 * blocks. Through this class, the custom woods are remembered for the blocks. *
 */
public class TileEntityCustomWood extends TileEntityBase implements IDebuggable {

	/**
	 * The default MATERIAL to use. Currently is WOOD planks.
	 */
	@Nonnull
	public static final Block DEFAULT_MATERIAL = Blocks.planks;

	/**
	 * The default metadata to use. Currently is set to Oak(0) for Planks.
	 */
	@Nonnull
	public static final int DEFAULT_META = 0;

	/**
	 * A pointer to the the block the CustomWoodBlock is imitating.
	 *
	 * Defaults to {@link #DEFAULT_MATERIAL}.
	 */
	@Nonnull
	private Block material = DEFAULT_MATERIAL;

	/**
	 * The metadata of the block the CustomWoodBlock is imitating.
	 *
	 * Defaults to {@link #DEFAULT_META}.
	 */
	@Nonnull
	private int materialMeta = DEFAULT_META;

	public TileEntityCustomWood() {
		super();
	}

	//TODO: icon handling
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTexture() {
		return null;
	}

	@Override
	public final void writeTileNBT(NBTTagCompound tag) {
		tag.setString(AgriCraftNBT.MATERIAL, this.getMaterial().getRegistryName().toString());
		tag.setInteger(AgriCraftNBT.MATERIAL_META, this.getMaterialMeta());
		this.writeNBT(tag);
	}

	protected void writeNBT(NBTTagCompound tag) {};

	/**
	 * Loads the CustomWood entity from a NBTTag, as to load from a savefile.
	 *
	 * @param tag the TAG to load the entity data from.
	 */
	@Override
	public final void readTileNBT(NBTTagCompound tag) {
		this.setMaterial(tag);
		this.readNBT(tag);
	}

	protected void readNBT(NBTTagCompound tag) {};

	/**
	 * Tests to see if another CustomWood entity is of the same MATERIAL.
	 *
	 * @param tileEntity the CustomWood entity to test.
	 * @return if the construction materials for both entities are the same.
	 */
	public final boolean isSameMaterial(TileEntityCustomWood tileEntity) {
		return tileEntity != null && this.getBlockMetadata() == tileEntity.getBlockMetadata() && this.getMaterial() == tileEntity.getMaterial() && this.getMaterialMeta() == tileEntity.getMaterialMeta();
	}

	/**
	 * Sets the CustomWood block's MATERIAL, the MATERIAL to mimic, from an
	 * ItemStack.
	 *
	 * @param stack the ItemStack to set the block's MATERIAL from.
	 */
	public final void setMaterial(ItemStack stack) {
		if (stack == null) {
			AgriCore.getLogger("AgriCraft").debug("TECW: Passed null stack!");
		} else if (!(stack.getItem() instanceof ItemBlock)) {
			AgriCore.getLogger("AgriCraft").debug("TECW: Passsed wrong stack!");
		} else if (stack.getTagCompound() == null) {
			AgriCore.getLogger("AgriCraft").debug("TECW: Stack missing NBT Tag!");
		} else {
			this.setMaterial(stack.getTagCompound());
		}
	}

	/**
	 * Sets the CustomWood block's MATERIAL, the MATERIAL to mimic, from an
	 * NBTTag. This function is intended for use internally, for serialization.
	 *
	 * @param tag the TAG to set the block's MATERIAL from.
	 */
	private void setMaterial(NBTTagCompound tag) {
		if (tag == null) {
			AgriCore.getLogger("AgriCraft").debug("TECW: Passed Null Tag!");
		} else if (!tag.hasKey(AgriCraftNBT.MATERIAL)) {
			AgriCore.getLogger("AgriCraft").debug("TECW: Tag missing material!");
		} else if (!tag.hasKey(AgriCraftNBT.MATERIAL_META)) {
			AgriCore.getLogger("AgriCraft").debug("TECW: Tag missing meta!");
		} else {
			this.setMaterial(tag.getString(AgriCraftNBT.MATERIAL), tag.getInteger(AgriCraftNBT.MATERIAL_META));
		}
	}

	/**
	 * Sets the CustomWood block's MATERIAL, the MATERIAL to mimic, from the
	 * name of the MATERIAL (block) and its metadata value.
	 *
	 * @param name the name of the MATERIAL (block).
	 * @param meta the metadata value of the MATERIAL (block).
	 */
	public final void setMaterial(String name, int meta) {
		Block block = Block.getBlockFromName(name);
		if (block == Blocks.air) {
			AgriCore.getLogger("AgriCraft").debug("TECW: Material Defaulted!");
			this.setMaterial(DEFAULT_MATERIAL, DEFAULT_META);
		} else {
			this.setMaterial(block, meta);
		}
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
			this.material = block;
			this.materialMeta = meta;
		}
	}

	/**
	 * Retrieves the MATERIAL the CustomWood is mimicking.
	 *
	 * @return the MATERIAL, in Block form.
	 */
	public final Block getMaterial() {
		return this.material;
	}

	public final IBlockState getMaterialState() {
		return this.getMaterial().getStateFromMeta(getMaterialMeta());
	}

	/**
	 * Retrieves the metadata of the MATERIAL the CustomWood is mimicking.
	 *
	 * @return the metadata of the MATERIAL.
	 */
	public final int getMaterialMeta() {
		return this.materialMeta;
	}

	public final ItemStack getMaterialStack() {
		return new ItemStack(this.getMaterial(), 1, this.getMaterialMeta());
	}

	/**
	 * Generates an NBTTag for the MATERIAL the CustomWood is mimicking.
	 *
	 * @return an NBTTag for the CustomWood MATERIAL.
	 */
	public final NBTTagCompound getMaterialTag() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(AgriCraftNBT.MATERIAL, this.material.getRegistryName().toString());
		tag.setInteger(AgriCraftNBT.MATERIAL_META, this.materialMeta);
		return tag;
	}

	@Override
	public BlockCustomWood getBlockType() {
		return (BlockCustomWood) super.getBlockType();
	}

	@Override
	public void addDebugInfo(List<String> list) {
		list.add("this material is: " + this.material.getRegistryName() + ":" + this.getMaterialMeta());
	}

	@Override
	public boolean isRotatable() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public int colorMultiplier() {
		if (this.worldObj == null) {
			return 16777215;
		} else {
			return RenderUtil.getColorMultiplier(worldObj, this.getPos(), getBlockType());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	public void addWailaInformation(List information) {
		information.add(I18n.translateToLocal("agricraft_tooltip.material") + ": " + new ItemStack(this.material, 1, this.materialMeta).getDisplayName());
	}
}
