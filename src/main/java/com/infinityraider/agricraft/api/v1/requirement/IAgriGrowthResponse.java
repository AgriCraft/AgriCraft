package com.infinityraider.agricraft.api.v1.requirement;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Interface representing how plants behave under certain conditions
 */
public interface IAgriGrowthResponse {
    /**
     * Check for fertility, if this returns true, the condition is fulfilled and a plant can grow
     * @return true if growing is allowed
     */
    boolean isFertile();

    /**
     * If this method returns true, plants will slowly revert their growth stage, and eventually wither away
     * @return true if growing is reversed
     */
    boolean isLethal();

    /**
     * Some grow conditions can be so severe they kill a plant instantly, e.g. lava
     * @return true if a plant should be killed instantly
     */
    default boolean killInstantly() {
        return false;
    }

    /**
     * When a plant is killed instantly, this is called, which allows to perform certain actions,
     * this includes spawning particles and/or playing a sound
     * @param crop the crop where the plant was killed
     */
    default void onPlantKilled(IAgriCrop crop) {}

    /**
     * Default implementation representing a fertile response
     */
    IAgriGrowthResponse FERTILE = new IAgriGrowthResponse() {
        @Override
        public boolean isFertile() {
            return true;
        }

        @Override
        public boolean isLethal() {
            return false;
        }
    };

    /**
     * Default implementation representing an infertile response
     */
    IAgriGrowthResponse INFERTILE = new IAgriGrowthResponse() {
        @Override
        public boolean isFertile() {
            return false;
        }

        @Override
        public boolean isLethal() {
            return false;
        }
    };

    /**
     * Default implementation representing a lethal response
     */
    IAgriGrowthResponse LETHAL = new IAgriGrowthResponse() {
        @Override
        public boolean isFertile() {
            return false;
        }

        @Override
        public boolean isLethal() {
            return true;
        }
    };

    /**
     * Default implementation representing a response where lava destroys the plant
     */
    IAgriGrowthResponse KILL_IT_WITH_FIRE = new IAgriGrowthResponse() {
        @Override
        public boolean isFertile() {
            return false;
        }

        @Override
        public boolean isLethal() {
            return true;
        }

        @Override
        public boolean killInstantly() {
            return true;
        }

        @Override
        public void onPlantKilled(IAgriCrop crop) {
            Level world = crop.world();
            if(world instanceof ServerLevel) {
                double x = crop.getPosition().getX() + 0.5;
                double y = crop.getPosition().getY() + 0.5;
                double z = crop.getPosition().getZ() + 0.5;
                for(int i = 0; i < 3; i++) {
                    world.addParticle(ParticleTypes.LARGE_SMOKE,
                            x + 0.25*world.getRandom().nextDouble(), y, z + 0.25*world.getRandom().nextDouble(),
                            1, 0, 1);
                }
                world.playSound(null, x, y, z, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS,
                        0.2F + world.getRandom().nextFloat() * 0.2F, 0.9F + world.getRandom().nextFloat() * 0.15F);
            }
        }
    };

    /**
     * Utility collector to compress streams of growth responses into a single response,
     * this will always be the most severe one.
     */
    Collector<IAgriGrowthResponse, MutableObject<IAgriGrowthResponse>, IAgriGrowthResponse> COLLECTOR =
            new Collector<IAgriGrowthResponse, MutableObject<IAgriGrowthResponse>, IAgriGrowthResponse>() {
                @Override
                public Supplier<MutableObject<IAgriGrowthResponse>> supplier() {
                    return Collection.SUPPLIER;
                }

                @Override
                public BiConsumer<MutableObject<IAgriGrowthResponse>, IAgriGrowthResponse> accumulator() {
                    return Collection.ACCUMULATOR;
                }

                @Override
                public BinaryOperator<MutableObject<IAgriGrowthResponse>> combiner() {
                    return Collection.COMBINER;
                }

                @Override
                public Function<MutableObject<IAgriGrowthResponse>, IAgriGrowthResponse> finisher() {
                    return Collection.FINISHER;
                }

                @Override
                public Set<Characteristics> characteristics() {
                    return Collections.emptySet();
                }
            };

    // class holding utility objects for the collector
    final class Collection {
        public static final Supplier<MutableObject<IAgriGrowthResponse>> SUPPLIER =
                () -> new MutableObject<>(IAgriGrowthResponse.FERTILE);

        public static final BiConsumer<MutableObject<IAgriGrowthResponse>, IAgriGrowthResponse> ACCUMULATOR =
                (champion, contestant) -> {
                    // check for instant kills
                    if(champion.getValue().killInstantly()) {
                        return;
                    }
                    if(contestant.killInstantly()) {
                        champion.setValue(contestant);
                        return;
                    }
                    // check for lethality
                    if(champion.getValue().isLethal()) {
                        return;
                    }
                    if(contestant.isLethal()) {
                        champion.setValue(contestant);
                        return;
                    }
                    // check for infertility
                    if(!champion.getValue().isFertile()) {
                        return;
                    }
                    if(!contestant.isFertile()) {
                        champion.setValue(contestant);
                    }
                    // both are fertile so it doesnt matter
                };

        public static final BinaryOperator<MutableObject<IAgriGrowthResponse>> COMBINER =
                (a, b) -> {
                    // check for instant kills
                    if(a.getValue().killInstantly()) {
                        return a;
                    }
                    if(b.getValue().killInstantly()) {
                        return b;
                    }
                    // check for lethality
                    if(a.getValue().isLethal()) {
                        return a;
                    }
                    if(b.getValue().isLethal()) {
                        return b;
                    }
                    // check for infertility
                    if(!a.getValue().isFertile()) {
                        return a;
                    }
                    if(!b.getValue().isFertile()) {
                        return b;
                    }
                    // both are fertile so it doesnt matter
                    return a;
                };

        public static final Function<MutableObject<IAgriGrowthResponse>, IAgriGrowthResponse> FINISHER =
                MutableObject::getValue;

        // why would you even want to instantiate this class...
        private Collection () {}
    }


}
