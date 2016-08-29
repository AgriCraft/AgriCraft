package com.infinityraider.agricraft.items;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.apiimpl.SeedRegistry;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.utility.StackHelper;
import net.minecraft.nbt.NBTTagCompound;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.infinitylib.render.item.IAutoRenderedItem;
import com.infinityraider.infinitylib.render.item.ItemModelTexture;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Class representing clipping items.
 *
 * @todo Convert to conform with new API.
 * @author The AgriCraft Team
 */
public class ItemClipping extends ItemBase implements IAutoRenderedItem {

	public ItemClipping() {
		super("clipping", false);
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
		AgriSeed seed = SeedRegistry.getInstance().valueOf(stack).orElse(null);
		if (!crop.acceptsSeed(seed) || seed == null) {
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
		Optional<AgriSeed> seed = SeedRegistry.getInstance().valueOf(stack);
        return seed.map(s -> s.getPlant().getPlantName()) + " " + text;
	}

	public static ItemStack getClipping(AgriSeed seed, int amount) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(AgriNBT.SEED, seed.getPlant().getId());
		seed.getStat().writeToNBT(tag);
		ItemStack stack = new ItemStack(AgriItems.getInstance().AGRI_CLIPPING);
		stack.setTagCompound(tag);
		return stack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getModelId(ItemStack stack) {
		Optional<AgriSeed> seed = SeedRegistry.getInstance().valueOf(stack);
        return seed.map(s -> s.getPlant().getId()).orElse("");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getBaseTexture(ItemStack stack) {
		return "agricraft:items/clipping";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<ItemModelTexture> getOverlayTextures(ItemStack stack) {
		AgriSeed seed = SeedRegistry.getInstance().valueOf(stack).orElse(null);
		ResourceLocation tex = (seed == null) ? new ResourceLocation("agricraft:items/debugger") : seed.getPlant().getPrimaryPlantTexture(Constants.MATURE);
		return TypeHelper.asList(
				new ItemModelTexture(tex, 4, 4, 12, 12, 0, 0, 16, 16)
		);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<ResourceLocation> getAllTextures() {
		return TypeHelper.asList(
				new ResourceLocation("agricraft:items/clipping")
		);
	}

	@Override
	public List<String> getOreTags() {
		return Collections.emptyList();
	}

}
