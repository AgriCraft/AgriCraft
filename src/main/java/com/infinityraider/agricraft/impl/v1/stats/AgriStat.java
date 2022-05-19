package com.infinityraider.agricraft.impl.v1.stats;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

public class AgriStat implements IAgriStat {
    private final String name;
    private final IntSupplier min;
    private final IntSupplier max;
    private final BooleanSupplier hidden;
    private final String key;
    private final TranslatableComponent tooltip;
    private final Vector3f color;

    protected AgriStat(String name, IntSupplier min, IntSupplier max, BooleanSupplier hidden, Vector3f color) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.hidden = hidden;
        this.key = "stat." + this.name;
        this.tooltip = new TranslatableComponent(AgriCraft.instance.getModId() + "." + this.key);
        this.color = color;
    }

    @Override
    public int getMin() {
        return this.min.getAsInt();
    }

    @Override
    public int getMax() {
        return this.max.getAsInt();
    }

    @Override
    public boolean isHidden() {
        return this.hidden.getAsBoolean();
    }

    @Override
    public void writeValueToNBT(CompoundTag tag, byte value) {
        tag.putInt(this.key, value);
    }

    @Override
    public int readValueFromNBT(CompoundTag tag) {
        return tag.getInt(this.key);
    }

    @Override
    public void addTooltip(@Nonnull Consumer<Component> consumer, int value) {
        if(!this.isHidden()) {
            consumer.accept(new TextComponent("")
                    .append(this.getDescription())
                    .append(new TextComponent(": " + value))
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Nonnull
    @Override
    public TranslatableComponent getDescription() {
        return this.tooltip;
    }

    @Nonnull
    @Override
    public Vector3f getColor() {
        return this.color;
    }

    @Nonnull
    @Override
    public String getId() {
        return this.name;
    }
}
