package com.infinityraider.agricraft.items;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.apiimpl.v1.PlantRegistry;
import com.infinityraider.agricraft.apiimpl.v1.SeedRegistry;
import com.infinityraider.agricraft.utility.StackHelper;
import net.minecraft.nbt.NBTTagCompound;
import com.infinityraider.agricraft.api.v1.seed.IAgriSeedHandler;
import com.infinityraider.agricraft.apiimpl.v1.StatRegistry;

/**
 * Class representing clipping items.
 *
 * @todo Convert to conform with new API.
 * @author The AgriCraft Team
 */
public class ItemClipping extends ItemBase implements IAgriSeedHandler {
	
	public static final String NBT_CLIPPING_ID = "agri_clipping_id";

	public ItemClipping() {
		super("clipping", true);
		this.setCreativeTab(null);
	}

	//this is called when you right click with this item in hand
	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		TileEntity te = world.getTileEntity(pos);
		if (world.isRemote || !StackHelper.hasTag(stack) || !(te instanceof IAgriCrop)) {
			return EnumActionResult.PASS;
		}
		IAgriCrop crop = (IAgriCrop) te;
		AgriSeed seed = SeedRegistry.getInstance().getSeed(stack);
		if (!crop.acceptsSeed(stack) || seed == null) {
			return EnumActionResult.FAIL;
		}
		stack.stackSize = stack.stackSize - 1;
		if (world.rand.nextInt(10) <= seed.getStat().getStrength()) {
			crop.setSeed(seed);
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String text = AgriCore.getTranslator().translate("item.agricraft:clipping.name");
		AgriSeed seed = SeedRegistry.getInstance().getSeed(stack);
		return (seed == null ? "Generic" : seed.getPlant().getPlantName()) + " " + text;
	}
	
	@Override
	public boolean isValid(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ItemClipping;
	}

	@Override
	public AgriSeed getSeed(ItemStack stack) {
		if (stack != null && stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			IAgriPlant plant = PlantRegistry.getInstance().getPlant(tag.getString(NBT_CLIPPING_ID));
			IAgriStat stat = StatRegistry.getInstance().getStat(tag);
			if (plant != null && stat != null) {
				return new AgriSeed(plant, stat);
			}
		}
		return null;
	}
	
	public ItemStack getClipping(AgriSeed seed, int amount) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(NBT_CLIPPING_ID, seed.getPlant().getId());
		seed.getStat().writeToNBT(tag);
		ItemStack stack = new ItemStack(this);
		stack.setTagCompound(tag);
		return stack;
	}

}
