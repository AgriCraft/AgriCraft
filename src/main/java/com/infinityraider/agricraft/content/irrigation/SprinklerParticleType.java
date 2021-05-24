package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.agricraft.render.particles.SprinklerParticle;
import com.infinityraider.infinitylib.particle.ParticleTypeBase;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Objects;

public class SprinklerParticleType extends ParticleTypeBase<SprinklerParticleType.Data> {
    public static final Codec<SprinklerParticleType.Data> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("fluid").forGetter(data -> data.getFluid().getRegistryName().toString()),
                    Codec.FLOAT.fieldOf("scale").forGetter(Data::getScale),
                    Codec.FLOAT.fieldOf("gravity").forGetter(Data::getGravity)
            ).apply(instance, (fluid, scale, gravity) -> new Data(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluid)), scale, gravity))
    );

    public SprinklerParticleType() {
        super(Names.Blocks.SPRINKLER + "_particle", true);
    }

    @Override
    public Data deserializeData(@Nonnull StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(reader.readStringUntil(' ')));
        reader.expect(' ');
        float scale = reader.readFloat();
        reader.expect(' ');
        float gravity = reader.readFloat();
        if(fluid == null) {
            throw new SimpleCommandExceptionType(() -> "Invalid Fluid key").create();
        }
        return new Data(fluid, scale, gravity);
    }

    @Override
    public Data readData(@Nonnull PacketBuffer buffer) {
        return new Data(
                ForgeRegistries.FLUIDS.getValue(buffer.readResourceLocation()),
                buffer.readFloat(),
                buffer.readFloat()
        );
    }

    @Nonnull
    @Override
    public ParticleFactorySupplier<Data> particleFactorySupplier() {
        return new FactorySupplier();
    }

    @Nonnull
    @Override
    public Codec<Data> func_230522_e_() {
        return CODEC;
    }

    public static class Data implements IParticleData {
        private final Fluid fluid;
        private final float scale;
        private final float gravity;

        public Fluid getFluid() {
            return this.fluid;
        }

        public float getScale() {
            return this.scale;
        }

        public float getGravity() {
            return this.gravity;
        }

        public Data(Fluid fluid, float scale, float gravity) {
            this.fluid = fluid;
            this.scale = scale;
            this.gravity = gravity;
        }

        @Nonnull
        @Override
        public ParticleType<?> getType() {
            return AgriCraft.instance.getModParticleRegistry().sprinkler;
        }

        @Override
        public void write(@Nonnull PacketBuffer buffer) {
            buffer.writeResourceLocation(Objects.requireNonNull(this.getFluid().getRegistryName()));
            buffer.writeFloat(this.getScale());
            buffer.writeFloat(this.getGravity());
        }

        @Nonnull
        @Override
        public String getParameters() {
            return String.format(Locale.ROOT, "%s %f %f",
                    Objects.requireNonNull(this.getFluid().getRegistryName()).toString(),
                    this.getScale(),
                    this.getGravity()
            );
        }
    }

    private static final class FactorySupplier implements ParticleFactorySupplier<Data> {
        @Override
        @OnlyIn(Dist.CLIENT)
        public IParticleFactory<Data> supplyFactory() {
            return (data, world, x, y, z, vX, vY, vZ) -> new SprinklerParticle(
                    world, data.getFluid(), x, y, z, data.getScale(), data.getGravity(), new Vector3d(vX, vY, vZ));
        }
    }
}
