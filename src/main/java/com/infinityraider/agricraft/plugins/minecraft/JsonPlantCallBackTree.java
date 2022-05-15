package com.infinityraider.agricraft.plugins.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SaplingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
					if(sapling instanceof IGrowable) {
						return new JsonPlantCallBackTree((IGrowable) sapling);
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

	private final IGrowable growable;

	private JsonPlantCallBackTree(IGrowable growable) {
		this.growable = growable;
	}

	@Override
	public Optional<ActionResultType> onRightClickPre(@Nonnull IAgriCrop crop, @Nonnull ItemStack stack, @Nullable Entity entity) {
		// Fetch world
		World world = crop.world();
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
		if(!growable.canGrow(world, crop.getPosition(), crop.getBlockState(), world.isRemote())) {
			return Optional.empty();
		}
		if(!growable.canUseBonemeal(world, world.getRandom(), crop.getPosition(), crop.getBlockState())) {
			return Optional.empty();
		}
		// grow
		if(world instanceof ServerWorld) {
			BlockState state = ((Block)this.growable).getDefaultState();
			if (state.hasProperty(SaplingBlock.STAGE)) { // for trees
				state = state.with(SaplingBlock.STAGE, 1);
			}
			CompoundNBT before = crop.asTile().serializeNBT();
			growable.grow((ServerWorld) world, world.getRandom(), crop.getPosition(), state);
			if (world.getBlockState(crop.getPosition()).getBlock().equals(this.growable)) {
				// if we couldn't grow the tree, put back the crop instead of the sapling
				world.setBlockState(crop.getPosition(), crop.getBlockState());
				world.getTileEntity(crop.getPosition()).deserializeNBT(before);
				return Optional.of(ActionResultType.FAIL);
			}
			world.playEvent(2005, crop.getPosition(), 0);
		}
		return Optional.of(ActionResultType.SUCCESS);
	}

}
