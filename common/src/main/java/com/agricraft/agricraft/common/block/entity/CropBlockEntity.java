package com.agricraft.agricraft.common.block.entity;

import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.common.registry.ModBlockEntityTypes;
import com.agricraft.agricraft.common.util.PlatformUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CropBlockEntity extends BlockEntity {

	private AgriGenome genome;
	private String plantId = "";
	private AgriPlant plant;
	private int growthStage = 0;
	private final Map<Integer, VoxelShape> shapeByAge = new HashMap<>();

	public CropBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(ModBlockEntityTypes.CROP.get(), blockPos, blockState);
	}

	public void setGenome(AgriGenome genome) {
		this.genome = genome;
		this.plantId = genome.getSpeciesGene().getDominant().trait();
		this.plant = level.registryAccess().registry(PlatformUtils.getPlantRegistryKey()).get().get(new ResourceLocation(this.plantId));
		this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
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

	public int getPlantHeight() {
		return this.plant.stages().get(this.growthStage);
	}

	public VoxelShape getShape() {
		if (this.plant == null) {
			return Shapes.empty();
		}
		return shapeByAge.computeIfAbsent(this.growthStage, stage -> Block.box(0, 0, 0, 16, this.plant.stages().get(stage), 16));
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.genome = AgriGenome.fromNBT(tag);
		if (this.genome == null) {
			this.plantId = "agricraft:unknown";
		} else {
			this.plantId = this.genome.getSpeciesGene().getDominant().trait();
		}
		this.growthStage = tag.getInt("growth");
		if (plant == null && level != null) {
			this.plant = level.registryAccess().registry(PlatformUtils.getPlantRegistryKey()).get().get(new ResourceLocation(this.plantId));
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		genome.writeToNBT(tag);
		tag.putInt("growth", this.growthStage);
	}

	@NotNull
	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithoutMetadata();
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		if (level != null && !level.isClientSide) {
			this.plant = level.registryAccess().registry(PlatformUtils.getPlantRegistryKey()).get().get(new ResourceLocation(this.plantId));
			level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
			System.out.println(level);
		}
	}

	public void use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		player.sendSystemMessage(Component.literal("client " + level.isClientSide +" id " + this.plantId + " " + (this.plant != null) + " growth " + this.growthStage));
	}

	public void performBonemeal() {
		this.growthStage++;
		this.setChanged();
		this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
	}

}
