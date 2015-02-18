package com.InfinityRaider.AgriCraft.farming.mutation;


import com.InfinityRaider.AgriCraft.init.Seeds;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.test.util.MutationEngineWorld;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
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

public class MutationEngineTest {

    @BeforeClass
    public static void setup() {
        Seeds.seedSugarcane = mock(ItemModSeed.class);
        Seeds.seedShroomBrown = mock(ItemModSeed.class);
    }

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
}
