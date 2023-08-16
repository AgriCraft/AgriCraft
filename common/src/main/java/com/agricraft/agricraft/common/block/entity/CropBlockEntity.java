package com.agricraft.agricraft.common.block.entity;

import com.agricraft.agricraft.common.config.StatsConfig;
import com.agricraft.agricraft.common.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropBlockEntity extends BlockEntity {

	private String seed = "agricraft:unknown";

	public CropBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(ModBlockEntityTypes.CROP.get(), blockPos, blockState);
	}

	public boolean setSeed(String seed) {
		if (seed.isEmpty()) {
			return false;
		}
		this.seed = seed;
		return true;
	}

	public String getSeed() {
		return this.seed;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		String s = tag.getString("seed");
		if (s.isEmpty()) {
			this.seed = "agricraft:unknown";
		} else {
			this.seed = s;
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putString("seed", this.seed);
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

	public void use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		player.sendSystemMessage(Component.literal("rightclick is " + this.seed));
		player.sendSystemMessage(Component.literal("config max strength " + StatsConfig.strengthMax));
	}

}
