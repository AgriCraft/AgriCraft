package com.infinityraider.agricraft.api.v1.event;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * This class contains an event hierarchy for events fired on the MinecraftForge.EVENT_BUS related to AgriCraft crops,
 * These events cover actions such as
 *  - Harvesting
 *  - Growth Ticks
 *  - Planting
 *  - Spawning
 *  - Breaking
 *  - Raking
 *  - Clipping
 *  - Trowelling
 */
public abstract class AgriCropEvent extends Event {
    private final IAgriCrop crop;

    protected AgriCropEvent(@Nonnull IAgriCrop crop) {
        this.crop = Objects.requireNonNull(crop);
    }

    /**
     * @return the concerned crop
     */
    @Nonnull
    public IAgriCrop getCrop() {
        return crop;
    }

    /**
     * Events fired in the context of a harvest action
     */
    public static abstract class Harvest extends AgriCropEvent {
        private final LivingEntity entity;

        protected Harvest(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {
            super(crop);
            this.entity = entity;
        }

        /**
         * @return The entity who is attempting to harvest, or has harvested the crop (may be null in case of harvesting through automation)
         */
        @Nullable
        public LivingEntity getEntity() {
            return entity;
        }

        /**
         * Fired before the harvest action, can be cancelled
         */
        @Cancelable
        public static final class Pre extends AgriCropEvent.Harvest {
            public Pre(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {
                super(crop, entity);
            }
        }

        /**
         * Fired right after the harvest action
         */
        public static final class Post extends AgriCropEvent.Harvest {
            public Post(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {
                super(crop, entity);
            }
        }
    }

    /**
     * Fired in the context of growth ticks
     */
    public static abstract class Grow extends AgriCropEvent {
        private final Type type;
        
        protected Grow(@Nonnull IAgriCrop crop, @Nonnull Type type) {
            super(crop);
            this.type = type;
        }

        /**
         * @return The type of growth tick: Plant, Weeds, or Cross
         */
        @Nonnull
        public Type getType() {
            return this.type;
        }

        /**
         * Fired when a plant grows
         */
        public static abstract class Plant extends AgriCropEvent.Grow {
            protected Plant(@Nonnull IAgriCrop crop) {
                super(crop, Type.PLANT);
            }

            /**
             * Fired before the growth tick, can be cancelled
             */
            @Cancelable
            public static final class Pre extends AgriCropEvent.Grow.Plant {
                public Pre(@Nonnull IAgriCrop crop) {
                    super(crop);
                }
            }

            /**
             * Fired after the growth tick
             */
            public static final class Post extends AgriCropEvent.Grow.Plant {
                public Post(@Nonnull IAgriCrop crop) {
                    super(crop);
                }
            }
        }

        /**
         * Fired when a weed grows
         */
        public static abstract class Weeds extends AgriCropEvent.Grow {
            protected Weeds(@Nonnull IAgriCrop crop) {
                super(crop, Type.WEEDS);
            }

            /**
             * Fired before the growth tick, can be cancelled
             */
            @Cancelable
            public static final class Pre extends AgriCropEvent.Grow.Weeds {
                public Pre(@Nonnull IAgriCrop crop) {
                    super(crop);
                }
            }

            /**
             * Fired after the growth tick
             */
            public static final class Post extends AgriCropEvent.Grow.Weeds {
                public Post(@Nonnull IAgriCrop crop) {
                    super(crop);
                }
            }
        }

        /**
         * Fired when a cross crop receives a growth tick
         */
        public static abstract class Cross extends AgriCropEvent.Grow {
            protected Cross(@Nonnull IAgriCrop crop) {
                super(crop, Type.CROSS);
            }

            /**
             * Fired before the growth tick, can be cancelled
             */
            @Cancelable
            public static final class Pre extends AgriCropEvent.Grow.Cross {
                public Pre(@Nonnull IAgriCrop crop) {
                    super(crop);
                }
            }

            /**
             * Fired after the growth tick
             */
            public static final class Post extends AgriCropEvent.Grow.Cross {
                public Post(@Nonnull IAgriCrop crop) {
                    super(crop);
                }
            }
        }

        /**
         * Enumeration holding the different types of growth ticks
         */
        public enum Type {
            /** Plant growth tick, when a growth tick is applied to the plant */
            PLANT,
            /** Weeds growth tick, when a growth tick is applied to the weeds */
            WEEDS,
            /** Cross growth tick, when a growth tick is applied to a cross crop */
            CROSS
        }
    }

    /**
     * Fired in the context of planting
     */
    public static abstract class Plant extends AgriCropEvent {
        private final AgriSeed seed;
        private final LivingEntity entity;

        protected Plant(@Nonnull IAgriCrop crop, @Nonnull AgriSeed seed, @Nullable LivingEntity entity) {
            super(crop);
            this.seed = Objects.requireNonNull(seed);
            this.entity = entity;
        }

        /**
         * @return The seed which is being, or has been planted
         */
        @Nonnull
        public AgriSeed getSeed() {
            return seed;
        }

        /**
         * @return The entity who is attempting to plant, or has planted the seed (may be null in case of harvesting through automation)
         */
        @Nullable
        public LivingEntity getEntity() {
            return entity;
        }

        /**
         * Fired before the planting action, may be cancelled
         */
        @Cancelable
        public static final class Pre extends AgriCropEvent.Plant {
            public Pre(@Nonnull IAgriCrop crop, @Nonnull AgriSeed seed, @Nullable LivingEntity entity) {
                super(crop, seed, entity);
            }
        }

        /**
         * Fired after the planting action
         */
        public static final class Post extends AgriCropEvent.Plant {
            public Post(@Nonnull IAgriCrop crop, @Nonnull AgriSeed seed, @Nullable LivingEntity entity) {
                super(crop, seed, entity);
            }
        }
    }

    /**
     * Fired in the context of spawning (e.g. through spreading, mutating, or simply weeds)
     */
    public static abstract class Spawn extends AgriCropEvent {
        private final Type type;

        protected Spawn(@Nonnull IAgriCrop crop, @Nonnull Type type) {
            super(crop);
            this.type = type;
        }

        /**
         * @return The type of spawn: Plant or Weeds
         */
        @Nonnull
        public Type getType() {
            return this.type;
        }

        /**
         * Fired when a plant spawns
         */
        public static abstract class Plant extends AgriCropEvent.Spawn {
            private final IAgriGenome genome;

            protected Plant(@Nonnull IAgriCrop crop, @Nonnull IAgriGenome genome) {
                super(crop, Type.PLANT);
                this.genome = genome;
            }

            /**
             * @return The genome being spawned
             */
            public IAgriGenome getGenome() {
                return this.genome;
            }

            /**
             * @return The plant being spawned
             */
            public IAgriPlant getPlant() {
                return this.getGenome().getPlant();
            }

            /**
             * Fired before the spawning, can be cancelled
             */
            @Cancelable
            public static final class Pre extends AgriCropEvent.Spawn.Plant {
                public Pre(@Nonnull IAgriCrop crop, @Nonnull IAgriGenome genome) {
                    super(crop, genome);
                }
            }

            /**
             * Fired after the spawning
             */
            public static final class Post extends AgriCropEvent.Spawn.Plant {
                public Post(@Nonnull IAgriCrop crop, @Nonnull IAgriGenome genome) {
                    super(crop, genome);
                }
            }
        }

        /**
         * Fired when weeds spawn
         */
        public static abstract class Weed extends AgriCropEvent.Spawn {
            private final IAgriWeed weed;

            protected Weed(@Nonnull IAgriCrop crop, @Nonnull IAgriWeed plant) {
                super(crop, Type.WEEDS);
                this.weed = plant;
            }

            /**
             * @return The plant being spawned
             */
            public IAgriWeed getWeed() {
                return this.weed;
            }

            /**
             * Fired before the spawning, can be cancelled
             */
            @Cancelable
            public static final class Pre extends AgriCropEvent.Spawn.Weed {
                public Pre(@Nonnull IAgriCrop crop, @Nonnull IAgriWeed weed) {
                    super(crop, weed);
                }
            }

            /**
             * Fired after the spawning
             */
            public static final class Post extends AgriCropEvent.Spawn.Weed {
                public Post(@Nonnull IAgriCrop crop, @Nonnull IAgriWeed weed) {
                    super(crop, weed);
                }
            }
        }

        /**
         * Enumeration holding the different types of spawning
         */
        public enum Type {
            /** Plant, when a plant spawns */
            PLANT,
            /** Weeds, when a weed spawns */
            WEEDS,
        }
    }

    /**
     * Events fired in the context of breaking the crop
     */
    public static abstract class Break extends AgriCropEvent {
        private final LivingEntity entity;

        protected Break(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {
            super(crop);
            this.entity = entity;
        }

        /**
         * @return The entity who is attempting to break, or has broken the crop (may be null in case of breaking through automation)
         */
        @Nullable
        public LivingEntity getEntity() {
            return entity;
        }

        /**
         * Fired before the break action, can be cancelled
         */
        @Cancelable
        public static final class Pre extends AgriCropEvent.Break {
            public Pre(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {
                super(crop, entity);
            }
        }

        /**
         * Fired right after the break action
         */
        public static final class Post extends AgriCropEvent.Break {
            public Post(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {
                super(crop, entity);
            }
        }
    }

    /**
     * Events fired in the context of a rake action
     */
    public static abstract class Rake extends AgriCropEvent {
        private final ItemStack rake;
        private final LivingEntity entity;

        protected Rake(@Nonnull IAgriCrop crop, @Nonnull ItemStack rake, @Nullable LivingEntity entity) {
            super(crop);
            this.rake = rake;
            this.entity = entity;
        }

        /**
         * @return The ItemStack holding the rake used to rake the crops
         */
        public ItemStack getRake() {
            return this.rake;
        }

        /**
         * @return The entity who is attempting to rake, or has raked the crop (may be null in case of raking through automation)
         */
        @Nullable
        public LivingEntity getEntity() {
            return entity;
        }

        /**
         * Fired before the rake action, can be cancelled
         */
        @Cancelable
        public static final class Pre extends AgriCropEvent.Rake {
            public Pre(@Nonnull IAgriCrop crop, @Nonnull ItemStack rake, @Nullable LivingEntity entity) {
                super(crop, rake, entity);
            }
        }

        /**
         * Fired right after the rake action
         */
        public static final class Post extends AgriCropEvent.Rake {
            public Post(@Nonnull IAgriCrop crop, @Nonnull ItemStack rake, @Nullable LivingEntity entity) {
                super(crop, rake, entity);
            }
        }
    }

    /**
     * Events fired in the context of a clipping action
     */
    public static abstract class Clip extends AgriCropEvent {
        private final ItemStack clippers;
        private final LivingEntity entity;

        protected Clip(@Nonnull IAgriCrop crop, @Nonnull ItemStack clippers, @Nullable LivingEntity entity) {
            super(crop);
            this.clippers = clippers;
            this.entity = entity;
        }

        /**
         * @return The ItemStack holding the clippers used to clip the crops
         */
        public ItemStack getClippers() {
            return this.clippers;
        }

        /**
         * @return The entity who is attempting to clip, or has clipped the crop (may be null in case of clipping through automation)
         */
        @Nullable
        public LivingEntity getEntity() {
            return entity;
        }

        /**
         * Fired before the clipping action, can be cancelled
         */
        @Cancelable
        public static final class Pre extends AgriCropEvent.Clip {
            public Pre(@Nonnull IAgriCrop crop, @Nonnull ItemStack clipper, @Nullable LivingEntity entity) {
                super(crop, clipper, entity);
            }
        }

        /**
         * Fired right after the clipping action
         */
        public static final class Post extends AgriCropEvent.Clip {
            private final ItemStack clipping;
            
            public Post(@Nonnull IAgriCrop crop, @Nonnull ItemStack clipper, @Nullable LivingEntity entity, @Nonnull ItemStack clipping) {
                super(crop, clipper, entity);
                this.clipping = clipping;
            }

            /**
             * @return The clipping item obtained from clipping the crop
             */
            public ItemStack getClipping() {
                return this.clipping;
            }
        }
    }

    /**
     * Events fired in the context of a trowel action
     */
    public static abstract class Trowel extends AgriCropEvent {
        private final ItemStack trowel;
        private final LivingEntity entity;

        protected Trowel(@Nonnull IAgriCrop crop, @Nonnull ItemStack trowel, @Nullable LivingEntity entity) {
            super(crop);
            this.trowel = trowel;
            this.entity = entity;
        }

        /**
         * @return The ItemStack holding the trowel used to trowel the crops
         */
        public ItemStack getTrowel() {
            return this.trowel;
        }

        /**
         * @return The entity who is attempting to trowel, or has troweled the crop (may be null in case of trowelling through automation)
         */
        @Nullable
        public LivingEntity getEntity() {
            return entity;
        }

        /**
         * Fired before the trowel action, can be cancelled
         */
        @Cancelable
        public static final class Pre extends AgriCropEvent.Trowel {
            public Pre(@Nonnull IAgriCrop crop, @Nonnull ItemStack trowel, @Nullable LivingEntity entity) {
                super(crop, trowel, entity);
            }
        }

        /**
         * Fired right after the trowel action
         */
        public static final class Post extends AgriCropEvent.Trowel {
            public Post(@Nonnull IAgriCrop crop, @Nonnull ItemStack trowel, @Nullable LivingEntity entity) {
                super(crop, trowel, entity);
            }
        }
    }
}
