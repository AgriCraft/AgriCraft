package com.infinityraider.agricraft.plugins.botania;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowCondition;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthRequirement;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthResponse;
import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.IManaPool;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

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

    private final ManaGrowthCondition condition;

    private JsonPlantCallBackManaConsumer(int cost) {
        this.condition = new ManaGrowthCondition(cost);
    }

    @Override
    public void onGrowth(@Nonnull IAgriCrop crop) {
        // Consume mana
        CompoundTag tag = crop.asTile().getTileData();
        int mana = tag.contains(ID) ? tag.getInt(ID) : 0;
        tag.putInt(ID, Math.max(0, mana - this.condition.getCost()));
        // spawn a particle
        BotaniaAPI.instance().sparkleFX(
                crop.world(),
                crop.getPosition().getX() + 0.5 + 0.5*Math.random(),
                crop.getPosition().getY() + 0.5 + 0.5*Math.random(),
                crop.getPosition().getZ() + 0.5 + 0.5*Math.random(),
                67.0F/255.0F,
                180.0F/255.0F,
                1.0F,
                (float) Math.random(),
                5
        );
    }

    @Override
    public void onGrowthReqInitialization(IAgriGrowthRequirement.Builder builder) {
        builder.addCondition(this.condition);
    }

    private static class ManaGrowthCondition implements IAgriGrowCondition {
        private static final Component TOOLTIP = new TranslatableComponent(AgriCraft.instance.getModId() + ".tooltip.growth_req.mana");

        private final int cost;

        private ManaGrowthCondition(int cost) {
            this.cost = cost;
        }

        public int getCost() {
            return this.cost;
        }

        @Override
        public RequirementType getType() {
            return RequirementType.MANA;
        }

        @Override
        public IAgriGrowthResponse check(IAgriCrop crop, @Nonnull Level world, @Nonnull BlockPos pos, int strength) {
            // Fetch custom tile data
            BlockEntity tile = crop.asTile();
            CompoundTag tag = tile.getTileData();
            // Fetch mana stored on the crop
            int mana = tag.contains(ID) ? tag.getInt(ID) : 0;
            // If there is not enough, try to fetch more
            if(mana < this.getCost()) {
                mana = mana + this.tryCollectMana(crop, 5*this.getCost() - mana);
                tag.putInt(ID, mana);
            }
            // If there is enough mana, the crop may grow
            // Note that we do not consume mana here, as this is just a fertility check, mana is consumed after the growth tick
            return mana >= this.getCost() ? IAgriGrowthResponse.FERTILE : IAgriGrowthResponse.INFERTILE;
        }

        protected int tryCollectMana(IAgriCrop crop, int amount) {
            return POOL_CACHE.computeIfAbsent(crop.getPosition(), pos -> new PoolCache()).tryCollectMana(crop, amount);
        }

        @Override
        public Set<BlockPos> offsetsToCheck() {
            return Collections.emptySet();
        }

        @Override
        public void notMetDescription(@Nonnull Consumer<Component> consumer) {
            consumer.accept(TOOLTIP);
        }

        @Override
        public int getComplexity() {
            return 1;
        }

        @Override
        public CacheType getCacheType() {
            return CacheType.NONE;
        }
    }

    private static class PoolCache {
        private  WeakReference<IManaPool> pool;

        private PoolCache() {}

        public int tryCollectMana(IAgriCrop crop, int amount) {
            // Fetch current cached pool
            IManaPool pool = this.pool == null ? null : this.pool.get();
            // Update cached pool if none is cached
            pool = pool == null ? this.findNewPool(crop) : pool;
            // If there is no pool, no mana can be collected
            if(pool == null) {
                return 0;
            }
            // If the pool is not providing mana, no mana can be consumed
            if(!pool.isOutputtingPower()) {
                return 0;
            }
            // Fetch the amount of mana, or the amount remaining in the pool if it is less
            int collected = Math.min(pool.getCurrentMana(), amount);
            pool.receiveMana(-collected);
            return collected;
        }

        protected IManaPool findNewPool(IAgriCrop crop) {
            IManaPool pool = BotaniaAPI.instance().getManaNetworkInstance().getClosestPool(crop.getPosition(), crop.world(), LIMIT);
            this.pool = pool == null ? null : new WeakReference<>(pool);
            return pool;
        }

    }
}
