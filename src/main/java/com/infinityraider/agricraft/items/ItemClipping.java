package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.api.v1.ICropPlant;
import com.infinityraider.agricraft.api.v1.ISeedStats;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.tileentity.TileEntityCrop;
import com.infinityraider.agricraft.utility.LogHelper;
import com.infinityraider.agricraft.utility.RegisterHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemClipping extends ItemBase {
	
	private static final List<ItemClipping> INSTANCES = Collections.synchronizedList(new ArrayList<>());

	public final ICropPlant plant;
	public final String texture;

	public ItemClipping(ICropPlant plant, String plantName, String texture) {
		super("clipping_" + plantName, false);
		this.plant = plant;
		this.setCreativeTab(null);
		texture = texture == null || texture.isEmpty() ? "agricraft:items/debugger" : texture;
		this.texture = texture.replaceFirst("_stem[0-9]", "_stem");
		INSTANCES.add(this);
		LogHelper.debug("Created Clipping: clipping_" + plantName + " With Texture: " + this.texture);
	}
	
	@SideOnly(Side.CLIENT)
	public static final void registerClippingRenderers() {
		for (ItemClipping e : INSTANCES) {
			e.registerItemRenderer();
		}
	}

	@Override
	public void registerItemRenderer() {
		RegisterHelper.registerItemRendererTex(this, "clipping", texture);
	}

	@Override
	public boolean canItemEditBlocks() {
		return true;
	}

	//this is called when you right click with this item in hand
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return false;
		}
		if (stack == null || stack.getItem() == null || !stack.hasTagCompound()) {
			return false;
		}
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (!(block instanceof BlockCrop)) {
			return false;
		}
		TileEntity te = world.getTileEntity(pos);
		if (te == null || !(te instanceof TileEntityCrop)) {
			return true;
		}
		TileEntityCrop crop = (TileEntityCrop) te;
		BlockCrop blockCrop = (BlockCrop) block;
		if (crop.isCrossCrop()) {
			blockCrop.harvest(world, pos, state, player, crop);
		}
		if (!crop.canPlant()) {
			return true;
		}
		ItemStack seed = ItemStack.loadItemStackFromNBT(stack.getTagCompound());
		ISeedStats stats = PlantStats.getStatsFromStack(seed);
		if (stats == null) {
			return false;
		}
		if (world.rand.nextInt(10) <= stats.getStrength()) {
			blockCrop.plantSeed(seed, world, pos);
		}
		stack.stackSize = stack.stackSize - 1;
		return true;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		StringBuilder sb = new StringBuilder();
		if (plant == null) {
			return StatCollector.translateToLocal("item.agricraft:clipping.name");
		}
		List<ItemStack> fruits = plant.getAllFruits();
		if (!fruits.isEmpty()) {
			sb.append(fruits.get(0).getDisplayName());
		}
		sb.append(" ");
		sb.append(StatCollector.translateToLocal("item.agricraft:clipping.name"));
		return sb.toString();
	}

}
