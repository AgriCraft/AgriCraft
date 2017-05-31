package com.infinityraider.agricraft.farming;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.util.MathHelper;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import static com.infinityraider.agricraft.reference.AgriCraftConfig.STAT_FORMAT;
import static com.infinityraider.agricraft.reference.AgriCraftConfig.cropStatCap;
import com.infinityraider.agricraft.utility.NBTHelper;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.nbt.NBTTagCompound;

public class PlantStats implements IAgriStat, IAgriAdapter<IAgriStat> {

    public static final String NBT_GROWTH = "agri_growth";
    public static final String NBT_GAIN = "agri_gain";
    public static final String NBT_STRENGTH = "agri_strength";
    public static final String NBT_ANALYZED = "agri_analyzed";

    private static final byte MAX = (byte) AgriCraftConfig.cropStatCap;
    private static final byte MIN = 1;

    private final byte growth;
    private final byte gain;
    private final byte strength;
    private final boolean analyzed;

    public PlantStats() {
        this(MIN, MIN, MIN, false);
    }

    public PlantStats(int growth, int gain, int strength) {
        this(growth, gain, strength, false);
    }

    public PlantStats(int growth, int gain, int strength, boolean analyzed) {
        this.growth = (byte) MathHelper.inRange(growth, MIN, MAX);
        this.gain = (byte) MathHelper.inRange(gain, MIN, MAX);
        this.strength = (byte) MathHelper.inRange(strength, MIN, MAX);
        this.analyzed = analyzed;
    }

    @Override
    public boolean isAnalyzed() {
        return this.analyzed;
    }

    @Override
    public byte getGrowth() {
        return growth;
    }

    @Override
    public byte getGain() {
        return gain;
    }

    @Override
    public byte getStrength() {
        return strength;
    }

    @Override
    public byte getMaxGrowth() {
        return MAX;
    }

    @Override
    public byte getMaxGain() {
        return MAX;
    }

    @Override
    public byte getMaxStrength() {
        return MAX;
    }

    @Override
    public IAgriStat withAnalyzed(boolean analyzed) {
        return new PlantStats(growth, gain, strength, analyzed);
    }

    @Override
    public IAgriStat withGrowth(int growth) {
        return new PlantStats(growth, gain, strength, analyzed);
    }

    @Override
    public IAgriStat withGain(int gain) {
        return new PlantStats(growth, gain, strength, analyzed);
    }

    @Override
    public IAgriStat withStrength(int strength) {
        return new PlantStats(growth, gain, strength, analyzed);
    }

    @Override
    public boolean writeToNBT(NBTTagCompound tag) {
        tag.setByte(NBT_GROWTH, growth);
        tag.setByte(NBT_GAIN, gain);
        tag.setByte(NBT_STRENGTH, strength);
        tag.setBoolean(NBT_ANALYZED, analyzed);
        return true;
    }

    @Override
    public boolean addStats(Consumer<String> lines) {
        try {
            lines.accept(MessageFormat.format(STAT_FORMAT, AgriCore.getTranslator().translate("agricraft_tooltip.growth"), getGrowth(), cropStatCap));
            lines.accept(MessageFormat.format(STAT_FORMAT, AgriCore.getTranslator().translate("agricraft_tooltip.gain"), getGain(), cropStatCap));
            lines.accept(MessageFormat.format(STAT_FORMAT, AgriCore.getTranslator().translate("agricraft_tooltip.strength"), getStrength(), cropStatCap));
            return true;
        } catch (IllegalArgumentException e) {
            lines.accept("Invalid Stat Format!");
            return false;
        }
    }

    @Override
    public boolean accepts(Object obj) {
        NBTTagCompound tag = NBTHelper.asTag(obj);
        return tag != null && NBTHelper.hasKey(tag, NBT_GROWTH, NBT_GAIN, NBT_STRENGTH, NBT_ANALYZED);
    }

    @Override
    public Optional<IAgriStat> valueOf(Object obj) {
        NBTTagCompound tag = NBTHelper.asTag(obj);
        if (tag != null) {
            return Optional.of(new PlantStats(
                    tag.getByte(NBT_GROWTH),
                    tag.getByte(NBT_GAIN),
                    tag.getByte(NBT_STRENGTH),
                    tag.getBoolean(NBT_ANALYZED)
            ));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + this.growth;
        hash = 41 * hash + this.gain;
        hash = 41 * hash + this.strength;
        hash = 41 * hash + (this.analyzed ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof PlantStats)) {
            return false;
        }
        final PlantStats other = (PlantStats) obj;
        if (this.growth != other.growth) {
            return false;
        } else if (this.gain != other.gain) {
            return false;
        } else if (this.strength != other.strength) {
            return false;
        } else if (this.analyzed != other.analyzed) {
            return false;
        } else {
            return true;
        }
    }

}
