package com.InfinityRaider.AgriCraft.farming.mutation;


import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MutationEngineTest {

    @BeforeClass
    public static void setup() {
        ConfigurationHandler.mutationChance = Constants.defaultMutationChance;
    }

    /**
     * Injects a mock random object into <code>MutationEngine</code> and verifies that the configured
     * chances are respected when choosing a strategy for spreading / mutating seeds.
     */
    @Test
    public void testMutationVsSpreadChance() {
        MutationEngine engineSpread = new MutationEngine(null, mockRandom(0.5));
        MutationEngine engineMutate = new MutationEngine(null, mockRandom(0.2));

        assertThat(engineSpread.rollStrategy(), instanceOf(SpreadStrategy.class));
        assertThat(engineMutate.rollStrategy(), instanceOf(MutateStrategy.class));
    }

    private Random mockRandom(double nextDouble) {
        Random random = mock(Random.class);
        when(random.nextDouble()).thenReturn(nextDouble);
        return random;
    }
}
