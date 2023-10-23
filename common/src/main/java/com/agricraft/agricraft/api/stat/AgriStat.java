package com.agricraft.agricraft.api.stat;

import com.agricraft.agricraft.api.AgriRegistrable;
import com.agricraft.agricraft.common.util.LangUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.joml.Vector3f;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

public class AgriStat implements AgriRegistrable {

	private final String id;
	private final IntSupplier min;
	private final IntSupplier max;
	private final BooleanSupplier hidden;
	private final Vector3f color;

	public AgriStat(String id, IntSupplier min, IntSupplier max, BooleanSupplier hidden, Vector3f color) {
		this.id = id;
		this.min = min;
		this.max = max;
		this.hidden = hidden;
		this.color = color;
	}

	public int getMin() {
		return this.min.getAsInt();
	}

	public int getMax() {
		return this.max.getAsInt();
	}

	public boolean isHidden() {
		return this.hidden.getAsBoolean();
	}

	public Vector3f getColor() {
		return color;
	}

	public void addTooltip(Consumer<Component> consumer, int value) {
		if(!this.isHidden()) {
			consumer.accept(LangUtils.statName(this)
					.append(": " + value)
					.withStyle(ChatFormatting.DARK_GRAY));
		}
	}

	@Override
	public String getId() {
		return this.id;
	}

}
