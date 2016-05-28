package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.api.v1.IAgriCraftSeed;
import com.infinityraider.agricraft.api.v1.IMutation;
import com.infinityraider.agricraft.creativetab.AgriCraftTab;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.handler.config.MutationConfig;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.ICropPlant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;

public class ItemModSeed extends Item implements IAgriCraftSeed {
	
	private final ICropPlant plant;

	//@SideOnly(Side.CLIENT)
	private String information;

	//@SideOnly(Side.CLIENT)
	private final String seedName;

	/**
	 * This constructor shouldn't be called from anywhere except from the
	 * BlockModPlant public constructor, if you create a new BlockModPlant, its
	 * constructor will create the seed for you
	 */
	public ItemModSeed(ICropPlant plant, String information) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			this.information = information;
		}
		this.setCreativeTab(AgriCraftTab.agriCraftTab);

		this.seedName = plant.getSeedName();
		this.information = information;
		this.plant = plant;
	}

	@SideOnly(Side.CLIENT)
	public void registerItemRenderer() {
	}

	@Override
	public List<IMutation> getMutations() {
		List<IMutation> list = new ArrayList<>();
		list.add(new Mutation(new ItemStack(this), new ItemStack(Items.pumpkin_seeds), new ItemStack(Items.wheat_seeds)));
		IMutation mutation = MutationConfig.getInstance().getDefaultMutation(new ItemStack(this));
		if (mutation != null) {
			mutation.setChance(((double) tier()) / 100.0D);
			list.add(mutation);
		}
		return list;
	}

	@Override
	public ICropPlant getPlant() {
		return this.plant;
	}

	@Override
	public int tier() {
		return (this.getPlant()).getTier();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getInformation() {
		return this.information;
	}

	@SideOnly(Side.CLIENT)
	public void setInformation(String information) {
		this.information = information;
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
