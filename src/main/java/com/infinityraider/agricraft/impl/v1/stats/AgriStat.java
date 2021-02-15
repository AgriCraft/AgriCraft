package com.infinityraider.agricraft.impl.v1.stats;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.*;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class AgriStat implements IAgriStat {
    private final String name;
    private final int min;
    private final int max;
    private final String key;
    private final TranslationTextComponent tooltip;
    private final Vector3f color;

    protected AgriStat(String name, int min, int max, Vector3f color) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.key = "stat." + this.name;
        this.tooltip = new TranslationTextComponent(AgriCraft.instance.getModId() + "." + this.key);
        this.color = color;
    }

    @Override
    public int getMin() {
        return this.min;
    }

    @Override
    public int getMax() {
        return this.max;
    }

    @Override
    public void writeValueToNBT(CompoundNBT tag, byte value) {
        tag.putInt(this.key, value);
    }

    @Override
    public int readValueFromNBT(CompoundNBT tag) {
        return tag.getInt(this.key);
    }

    @Override
    public void addTooltip(@Nonnull Consumer<ITextComponent> consumer, int value) {
        consumer.accept(new StringTextComponent("")
                .append(this.getDescription())
                .append(new StringTextComponent(": " + value))
                .mergeStyle(TextFormatting.DARK_GRAY));
    }

    @Nonnull
    @Override
    public TranslationTextComponent getDescription() {
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
