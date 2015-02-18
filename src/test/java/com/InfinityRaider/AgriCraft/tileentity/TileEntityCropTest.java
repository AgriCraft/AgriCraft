package com.InfinityRaider.AgriCraft.tileentity;


import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Seeds;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.test.util.MutationWorldSimulator;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsSame.theInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class TileEntityCropTest {

    @BeforeClass
    public static void setup() {
        Seeds.seedSugarcane = mock(ItemModSeed.class);
        Seeds.seedShroomBrown = mock(ItemModSeed.class);
    }

    /**
     * Sets up 2 neighbours (one sugarcane, one mushroom) and test that <code>getNeighbours</code>
     * returns them correctly. This test also showcases and verifies the <code>MutationEngineWorld</code> class
     */
    @Test
    public void testRetrieveNeighbours() {
        MutationWorldSimulator mutationWorldSimulator = new MutationWorldSimulator(5, 5, 5);
        mutationWorldSimulator.addNeighbour(ForgeDirection.NORTH, Seeds.seedSugarcane, 0, 1, 1, 1);
        mutationWorldSimulator.addNeighbour(ForgeDirection.WEST, Seeds.seedShroomBrown, 5, 1, 1, 1);

        List<TileEntityCrop> neighbours = mutationWorldSimulator.getTargetCrop().getNeighbours();

        assertThat(neighbours.size(), is(2));
        assertThat(neighbours.get(0).seed, instanceOf(ItemModSeed.class));
        assertThat(neighbours.get(0).seed, theInstance((IPlantable) Seeds.seedSugarcane));
        assertThat(neighbours.get(1).seed, instanceOf(ItemModSeed.class));
        assertThat(neighbours.get(1).seed, theInstance((IPlantable) Seeds.seedShroomBrown));
    }
}
