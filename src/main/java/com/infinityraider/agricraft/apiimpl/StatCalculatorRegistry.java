/*
 */
package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.api.stat.IAgriStatCalculator;
import com.infinityraider.agricraft.farming.mutation.statcalculator.StatCalculatorHardcore;
import com.infinityraider.agricraft.farming.mutation.statcalculator.StatCalculatorNormal;
import com.infinityraider.agricraft.api.stat.IAgriStatCalculatorRegistry;

/**
 *
 * @author RlonRyan
 */
public class StatCalculatorRegistry extends AdapterRegistry<IAgriStatCalculator> implements IAgriStatCalculatorRegistry {

	private static final StatCalculatorRegistry INSTANCE = new StatCalculatorRegistry();

	static {
        INSTANCE.registerAdapter(new StatCalculatorNormal());
		INSTANCE.registerAdapter(new StatCalculatorHardcore());
	}

	public static IAgriStatCalculatorRegistry getInstance() {
		return INSTANCE;
    }

}
