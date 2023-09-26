package com.agricraft.agricraft.common.commands;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriProduct;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.config.CoreConfig;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.api.requirement.AgriGrowthConditionRegistry;
import com.agricraft.agricraft.api.requirement.AgriGrowthResponse;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;
import com.agricraft.agricraft.api.tools.magnifying.MagnifyingInspectable;
import com.agricraft.agricraft.common.registry.ModBlockEntityTypes;
import com.agricraft.agricraft.common.registry.ModItems;
import com.agricraft.agricraft.common.util.LangUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class FakeCrop implements AgriCrop {

	private AgriGenome genome;
	private String plantId = "";
	private AgriPlant plant;
	private int growthStage = 0;

	public FakeCrop() {
	}

	public void setGenome(AgriGenome genome) {
		CompoundTag tag = new CompoundTag();
		genome.writeToNBT(tag);
		this.genome = genome;
		this.plantId = genome.getSpeciesGene().getDominant().trait();
		this.plant = AgriApi.getPlant(this.plantId).orElse(null);
		this.growthStage = this.plant.stages().size() - 1;
	}

	public AgriGenome getGenome() {
		return genome;
	}

	public String getPlantId() {
		return this.plantId;
	}

	public AgriPlant getPlant() {
		return this.plant;
	}

	public int getGrowthStage() {
		return this.growthStage;
	}

	@Override
	public void setGrowthStage(int stage) {
		this.growthStage = stage;
	}

	public int getGrowthPercent() {
		return 100;
	}

	public boolean isMaxStage() {
		return true;
	}

	@Override
	public boolean isFertile() {
		return true;
	}

	@Override
	public AgriGrowthResponse getFertilityResponse() {
		return AgriGrowthResponse.FERTILE;
	}

	@Override
	public Optional<AgriSoil> getSoil() {
		return Optional.empty();
	}

	/**
	 * Compute the harvest products for the crop
	 *
	 * @param addToHarvest consumer to add the products to
	 */
	public void getHarvestProducts(Consumer<ItemStack> addToHarvest) {
	}

	@Override
	public void getClippingProducts(Consumer<ItemStack> addToClipping) {
	}

	@Override
	public BlockPos getBlockPos() {
		return null;
	}

	@Override
	public BlockState getBlockState() {
		return null;
	}

	@Override
	public Level getLevel() {
		return null;
	}


	@Override
	public void onApplyGrowthTick(AgriCrop crop, RandomSource random) {

	}

	@Override
	public void onPerformBonemeal(AgriCrop crop, RandomSource random) {
		this.onApplyGrowthTick(crop, random);
	}

}
