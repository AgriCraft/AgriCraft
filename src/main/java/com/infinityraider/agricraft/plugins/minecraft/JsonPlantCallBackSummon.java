package com.infinityraider.agricraft.plugins.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FungusBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class JsonPlantCallBackSummon implements IJsonPlantCallback {

	public static final String ID = AgriCraft.instance.getModId() + ":" + "summon";
	public static final String ENTITY = "entity";

	private final EntityType<?> entityType;

	private static final IJsonPlantCallback.Factory FACTORY = new IJsonPlantCallback.Factory() {
		@Override
		public String getId() {
			return ID;
		}

		@Override
		public IJsonPlantCallback makeCallBack(JsonElement json) throws JsonParseException {
			if (json instanceof JsonObject) {
				JsonObject obj = json.getAsJsonObject();
				if (obj.has(ENTITY)) {
					EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(obj.get(ENTITY).getAsString()));
					if (entityType != null) {
						return new JsonPlantCallBackSummon(entityType);
					}
					throw new JsonParseException("Invalid entity id.");
				}
				throw new JsonParseException("Json object does not contain a \"" + ENTITY + "\" field.");
			}
			throw new JsonParseException("Not a json object.");
		}
	};

	private JsonPlantCallBackSummon(EntityType<?> entityType) {
		this.entityType = entityType;
	}


	public static IJsonPlantCallback.Factory getFactory() {
		return FACTORY;
	}


	@Override
	public void onHarvest(@NotNull IAgriCrop crop, @Nullable LivingEntity entity) {
		Level world = crop.world();
		if (world instanceof ServerLevel serverLevel) {
			Player player = null;
			if (entity instanceof Player) {
				player = (Player) entity;
			}
			if (entityType.spawn(serverLevel, null, player, crop.getPosition(), MobSpawnType.MOB_SUMMONED, true, false) != null) {
				serverLevel.gameEvent(player, GameEvent.ENTITY_PLACE, crop.getPosition());
			}
		}
	}

}
