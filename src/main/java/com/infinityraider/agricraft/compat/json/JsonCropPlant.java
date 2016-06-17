/*
 * Vanilla Crop Class.
 */
package com.infinityraider.agricraft.compat.json;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriPlant;
import com.agricraft.agricore.plant.AgriProduct;
import com.infinityraider.agricraft.api.v3.util.BlockWithMeta;
import com.infinityraider.agricraft.api.v3.IGrowthRequirement;
import com.infinityraider.agricraft.api.v3.IGrowthRequirementBuilder;
import com.infinityraider.agricraft.api.v3.RenderMethod;
import com.infinityraider.agricraft.api.v3.RequirementType;
import com.infinityraider.agricraft.farming.cropplant.CropPlant;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.utility.icon.IconUtil;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author RlonRyan
 */
public class JsonCropPlant extends CropPlant {

	public final AgriPlant plant;

	public JsonCropPlant(AgriPlant plant) {
		this.plant = plant;
		this.setGrowthRequirement(this.initGrowthRequirementJSON());
	}

	@Override
	public String getId() {
		return this.plant.getId();
	}

	@Override
	public String getPlantName() {
		return this.plant.getName();
	}

	@Override
	public String getInformation() {
		return this.plant.getDescription().toString();
	}

	@Override
	public ArrayList<ItemStack> getAllFruits() {
		ArrayList<ItemStack> fruits = new ArrayList<>();
		for (AgriProduct p : this.plant.getProducts().getAll()) {
			Object s = p.toStack();
			if (s instanceof ItemStack) {
				fruits.add((ItemStack) s);
			}
		}
		return fruits;
	}

	@Override
	public ItemStack getRandomFruit(Random rand) {
		for (AgriProduct p : this.plant.getProducts().getRandom(rand)) {
			Object s = p.toStack();
			if (s instanceof ItemStack) {
				return (ItemStack) s;
			}
		}
		return null;
	}

	@Override
	public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
		int amount = (int) (Math.ceil((gain + 0.00) / 3));
		ArrayList<ItemStack> list = new ArrayList<>();
		while (amount > 0) {
			list.add(getRandomFruit(rand));
			amount--;
		}
		return list;
	}

	@Override
	public void onAllowedGrowthTick(World world, BlockPos pos, int oldGrowthStage) {
		// Holder
	}

	@Override
	protected IGrowthRequirement initGrowthRequirement() {
		// Hack to avert annoying auto-call.
		return GrowthRequirementHandler.getNewBuilder().build();
	}
	
	protected final IGrowthRequirement initGrowthRequirementJSON() {

		IGrowthRequirementBuilder builder = GrowthRequirementHandler.getNewBuilder();

		if (this.plant == null) {
			AgriCore.getLogger("AgriCraft").warn("Null plant!");
			return builder.build();
		}

		this.plant.getRequirement().getSoils().forEach((b) -> {
			if (b instanceof ItemStack) {
				ItemStack stack = (ItemStack) b;
				if (stack.getItem() instanceof ItemBlock) {
					ItemBlock ib = (ItemBlock) stack.getItem();
					builder.soil(new BlockWithMeta(ib.block, ib.getMetadata(stack)));
				}
			}
		});

		this.plant.getRequirement().getBases().forEach((b) -> {
			if (b instanceof ItemStack) {
				ItemStack stack = (ItemStack) b;
				if (stack.getItem() instanceof ItemBlock) {
					ItemBlock ib = (ItemBlock) stack.getItem();
					builder.requiredBlock(new BlockWithMeta(ib.block, ib.getMetadata(stack)), RequirementType.BELOW, false);
				}
			}
		});

		this.plant.getRequirement().getNearby().forEach((obj, dist) -> {
			if (obj instanceof ItemStack) {
				ItemStack stack = (ItemStack) obj;
				if (stack.getItem() instanceof ItemBlock) {
					ItemBlock ib = (ItemBlock) stack.getItem();
					builder.nearbyBlock(new BlockWithMeta(ib.block, ib.getMetadata(stack)), dist, true);
				}
			}
		});

		builder.brightnessRange(plant.getRequirement().getMinLight(), plant.getRequirement().getMaxLight());

		return builder.build();

	}

	@Override
	public boolean canBonemeal() {
		return this.plant.canBonemeal();
	}

	@Override
	public Block getBlock() {
		return null;
	}

	@Override
	public int getTier() {
		return this.plant == null ? 1 : this.plant.getTier();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getHeight(int meta) {
		return Constants.UNIT * 13;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public RenderMethod getRenderMethod() {
		switch (this.plant.getTexture().getRenderType()) {
			default:
			case HASH:
				return RenderMethod.HASHTAG;
			case CROSS:
				return RenderMethod.CROSSED;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getPrimaryPlantTexture(int growthStage) {
		return new ResourceLocation(plant.getTexture().getPlantTexture(growthStage));
	}
	

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getSecondaryPlantTexture(int growthStage) {
		return null;
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons() {
		for (String tex : this.plant.getTexture().getPlantTextures()) {
			AgriCore.getLogger("AgriCraft").debug("Registering: " + tex);
			IconUtil.registerIcon(tex);
		}
	}

}
