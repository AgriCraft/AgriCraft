package com.infinityraider.agricraft.plugins.botania;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.event.AgriCropEvent;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.IManaPool;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Map;

public class JsonPlantCallBackManaConsumer implements IJsonPlantCallback {
    public static final String ID = AgriCraft.instance.getModId() + ":" + "botania_mana";

    private static final Map<Integer, IJsonPlantCallback> CALLBACKS = Maps.newHashMap();

    private static final IJsonPlantCallback.Factory FACTORY = new IJsonPlantCallback.Factory() {
        @Override
        public String getId() {
            return ID;
        }

        @Override
        public IJsonPlantCallback makeCallBack(JsonElement json) throws JsonParseException {
            return CALLBACKS.computeIfAbsent(json.getAsJsonObject().get("cost").getAsInt(), JsonPlantCallBackManaConsumer::new);
        }
    };

    public static IJsonPlantCallback.Factory getFactory() {
        return FACTORY;
    }

    private static final int LIMIT = 16;
    private static final Map<BlockPos, PoolCache> POOL_CACHE = Maps.newHashMap();

    private final int amount;

    private JsonPlantCallBackManaConsumer(int amount) {
        AgriCraft.instance.proxy().registerEventHandler(this);
        this.amount = amount;
    }

    protected boolean tryConsumeMana(IAgriCrop crop) {
        return POOL_CACHE.computeIfAbsent(crop.getPosition(), pos -> new PoolCache()).consumeMana(crop, this.amount);
    }

    @Override
    public void onPlanted(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {
        markCrop(crop);
    }

    @Override
    public void onSpawned(@Nonnull IAgriCrop crop) {
        markCrop(crop);
    }

    @Override
    public void onBroken(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {
        unMarkCrop(crop);
    }

    @Override
    public void onRemoved(@Nonnull IAgriCrop crop) {
        unMarkCrop(crop);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void preCropGrowth(AgriCropEvent.Grow.Plant.Pre event) {
        // Fetch crop
        IAgriCrop crop = event.getCrop();
        // If the crop is not marked as a mana consumer, return
        if(!isCropMarked(crop)) {
            return;
        }
        // If the crop is marked as a mana consumer, but does not have a plant, unmark it and return
        if(!crop.hasPlant()) {
            unMarkCrop(crop);
            return;
        }
        // Try to consume mana
        if(!this.tryConsumeMana(crop)) {
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    protected static void markCrop(IAgriCrop crop) {
        crop.asTile().getTileData().putBoolean(ID, true);
    }

    protected static void unMarkCrop(IAgriCrop crop) {
        crop.asTile().getTileData().remove(ID);
        POOL_CACHE.remove(crop.getPosition());
    }

    protected static boolean isCropMarked(IAgriCrop crop) {
        return crop.asTile().getTileData().contains(ID) && crop.asTile().getTileData().getBoolean(ID);
    }

    private static class PoolCache {
        private  WeakReference<IManaPool> pool;

        private PoolCache() {}

        public boolean consumeMana(IAgriCrop crop, int amount) {
            // Fetch current cached pool
            IManaPool pool = this.pool == null ? null : this.pool.get();
            // Update cached pool if none is cached
            pool = pool == null ? this.findNewPool(crop) : pool;
            // If there is no pool, no mana can be consumed
            if(pool == null) {
                return false;
            }
            // If the pool is not providing mana, no mana can be consumed
            if(!pool.isOutputtingPower()) {
                return false;
            }
            // If the pool does not have sufficient mana, no mana can be consumed
            if(pool.getCurrentMana() < amount) {
                return false;
            }
            // drain the required mana and return true
            pool.receiveMana(-amount);
            return true;
        }

        protected IManaPool findNewPool(IAgriCrop crop) {
            IManaPool pool = BotaniaAPI.instance().getManaNetworkInstance().getClosestPool(crop.getPosition(), crop.world(), LIMIT);
            this.pool = pool == null ? null : new WeakReference<>(pool);
            return pool;
        }

    }
}
