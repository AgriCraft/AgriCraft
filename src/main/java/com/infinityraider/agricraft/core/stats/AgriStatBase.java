package com.infinityraider.agricraft.core.stats;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class AgriStatBase implements IAgriStat {
    private final String name;
    private final byte min;
    private final byte max;
    private final String key;
    private final ITextComponent tooltip;

    protected AgriStatBase(String name, byte min, byte max) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.key = "stat." + this.name;
        this.tooltip = new TranslationTextComponent(AgriCraft.instance.getModId() + "." + this.key);
    }

    @Override
    public byte getMin() {
        return this.min;
    }

    @Override
    public byte getMax() {
        return this.max;
    }

    @Override
    public void writeValueToNBT(CompoundNBT tag, byte value) {
        tag.putByte(this.key, value);
    }

    @Override
    public byte readValueFromNBT(CompoundNBT tag) {
        return tag.getByte(this.key);
    }

    @Nonnull
    @Override
    public boolean addStat(@Nonnull Consumer<ITextComponent> consumer, byte value) {
        consumer.accept(new StringTextComponent("").append(this.tooltip).append(new StringTextComponent(": " + value)));
        return true;
    }

    @Nonnull
    @Override
    public String getId() {
        return this.name;
    }
}
