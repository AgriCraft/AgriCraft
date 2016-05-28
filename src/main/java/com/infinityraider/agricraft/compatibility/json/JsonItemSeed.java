package com.infinityraider.agricraft.compatibility.json;

import com.agricraft.agricore.plant.AgriPlant;
import com.infinityraider.agricraft.items.*;
import com.infinityraider.agricraft.api.v1.IAgriCraftSeed;
import com.infinityraider.agricraft.api.v1.IMutation;
import com.infinityraider.agricraft.creativetab.AgriCraftTab;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.ICropPlant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class JsonItemSeed extends ItemBase implements IAgriCraftSeed {

	public final String name;
	public final AgriPlant plant;

	/**
	 * This constructor shouldn't be called from anywhere except from the
	 * BlockModPlant public constructor, if you create a new BlockModPlant, its
	 * contructor will create the seed for you
	 */
	public JsonItemSeed(String name, AgriPlant plant) {
		super(name, true, plant.getTexture().getSeedTexture());
		this.setCreativeTab(AgriCraftTab.agriCraftTab);

		this.name = name;
		this.plant = plant;

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerItemRenderer() {
		// UHHH....
		//RegisterHelper.registerItemRendererTex(this, this.plant.getTexture().getSeedTexture());
	}

	@Override
	public List<IMutation> getMutations() {
		List<IMutation> list = new ArrayList<>();
		list.add(new Mutation(new ItemStack(this), new ItemStack(Items.pumpkin_seeds), new ItemStack(Items.wheat_seeds)));
		return list;
	}

	@Override
	public ICropPlant getPlant() {
		return null;
	}

	@Override
	public int tier() {
		// TODO: FIX
		return 1;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return this.plant.getName() + " Seeds";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getInformation() {
		return this.plant.getDescription().toString();
	}

	@SideOnly(Side.CLIENT)
	public void setInformation(String information) {
		// Ignore
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.getBlockState(pos).getBlock() == AgriCraftBlocks.blockCrop) {
			AgriCore.getLogger("AgriCraft").debug("Trying to plant seed " + stack.getItem().getUnlocalizedName() + " on crops");
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

}
