package com.infinityraider.agricraft.plugins.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class JsonPlantCallBackTree implements IJsonPlantCallback {
	public static final String ID = AgriCraft.instance.getModId() + ":" + "tree";

	private static final String SAPLING = "sapling";

	private static final Factory FACTORY = new Factory() {
		@Override
		public String getId() {
			return ID;
		}

		@Override
		public IJsonPlantCallback makeCallBack(JsonElement json) throws JsonParseException {
			if(json instanceof JsonObject) {
				JsonObject obj = json.getAsJsonObject();
				if(obj.has(SAPLING)) {
					Block sapling = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(obj.get(SAPLING).getAsString()));
					if(sapling instanceof BonemealableBlock) {
						return new JsonPlantCallBackTree((BonemealableBlock) sapling);
					}
					throw new JsonParseException("Invalid sapling id.");
				}
				throw new JsonParseException("Json object does not contain a \"" + SAPLING + "\" field.");
			}
			throw new JsonParseException("Not a json object.");
		}
	};

	public static Factory getFactory() {
		return FACTORY;
	}

	private final BonemealableBlock growable;

	private JsonPlantCallBackTree(BonemealableBlock growable) {
		this.growable = growable;
	}

	@Override
	public Optional<InteractionResult> onRightClickPre(@Nonnull IAgriCrop crop, @Nonnull ItemStack stack, @Nullable Entity entity) {
		// Fetch world
		Level world = crop.world();
		// Perform checks
		if(stack.isEmpty()) {
			return Optional.empty();
		}
		if(stack.getItem() != Items.BONE_MEAL) {
			return Optional.empty();
		}
		if(world == null) {
			return Optional.empty();
		}
		if(!crop.isMature()) {
			return Optional.empty();
		}
		if(!growable.isValidBonemealTarget(world, crop.getPosition(), crop.getBlockState(), world.isClientSide())) {
			return Optional.empty();
		}
		if(!growable.isBonemealSuccess(world, world.getRandom(), crop.getPosition(), crop.getBlockState())) {
			return Optional.empty();
		}
		// grow
		if(world instanceof ServerLevel) {
			BlockState state = ((Block)this.growable).defaultBlockState();
			if (state.hasProperty(SaplingBlock.STAGE)) { // for trees
				state = state.setValue(SaplingBlock.STAGE, 1);
			}
			CompoundTag before = crop.asTile().serializeNBT();
			growable.performBonemeal((ServerLevel) world, world.getRandom(), crop.getPosition(), state);
			if (world.getBlockState(crop.getPosition()).getBlock().equals(this.growable)) {
				// if we couldn't grow the tree, put back the crop instead of the sapling
				world.setBlockAndUpdate(crop.getPosition(), crop.getBlockState());
				world.getBlockEntity(crop.getPosition()).deserializeNBT(before);
				return Optional.of(InteractionResult.FAIL);
			}
			world.levelEvent(2005, crop.getPosition(), 0);
		}
		return Optional.of(InteractionResult.SUCCESS);
	}

}
