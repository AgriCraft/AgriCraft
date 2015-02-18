package com.InfinityRaider.AgriCraft.farming.mutation;


import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Seeds;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.test.util.MutationEngineWorld;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsSame.theInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MutationEngineTest {

    @BeforeClass
    public static void setup() {
        Seeds.seedSugarcane = mock(ItemModSeed.class);
        Seeds.seedShroomBrown = mock(ItemModSeed.class);
        ConfigurationHandler.mutationChance = Constants.defaultMutationChance;
    }

    /**
     * Sets up 2 neighbours (one sugarcane, one mushroom) and test that <code>getNeighbours</code>
     * returns them correctly. This test also showcases and verifies the <code>MutationEngineWorld</code> class
     */
    @Test
    public void testRetrieveNeighbours() {
        MutationEngineWorld mutationEngineWorld = new MutationEngineWorld(5, 5, 5);
        mutationEngineWorld.addNeighbour(ForgeDirection.NORTH, Seeds.seedSugarcane, 0, 1, 1, 1);
        mutationEngineWorld.addNeighbour(ForgeDirection.WEST, Seeds.seedShroomBrown, 5, 1, 1, 1);

        TileEntityCrop crop = mutationEngineWorld.getTargetCrop();
        MutationEngine engine = new MutationEngine(crop);
        List<TileEntityCrop> neighbours = engine.getNeighbours();

        assertThat(neighbours.size(), is(2));
        assertThat(neighbours.get(0).seed, instanceOf(ItemModSeed.class));
        assertThat(neighbours.get(0).seed, theInstance((IPlantable) Seeds.seedSugarcane));
        assertThat(neighbours.get(1).seed, instanceOf(ItemModSeed.class));
        assertThat(neighbours.get(1).seed, theInstance((IPlantable) Seeds.seedShroomBrown));
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
