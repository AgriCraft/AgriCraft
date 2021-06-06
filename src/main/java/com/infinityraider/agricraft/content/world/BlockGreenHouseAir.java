package com.infinityraider.agricraft.content.world;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import java.util.Map;

public class BlockGreenHouseAir extends BlockBase {
    public static final InfProperty<Boolean> SOLID_NORTH = InfProperty.Creators.create("north", false);
    public static final InfProperty<Boolean> SOLID_EAST = InfProperty.Creators.create("east", false);
    public static final InfProperty<Boolean> SOLID_SOUTH = InfProperty.Creators.create("south", false);
    public static final InfProperty<Boolean> SOLID_WEST = InfProperty.Creators.create("west", false);
    public static final InfProperty<Boolean> SOLID_UP = InfProperty.Creators.create("up", false);
    public static final InfProperty<Boolean> SOLID_DOWN = InfProperty.Creators.create("down", false);

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(SOLID_NORTH)
            .add(SOLID_EAST)
            .add(SOLID_SOUTH)
            .add(SOLID_WEST)
            .add(SOLID_UP)
            .add(SOLID_DOWN)
            .build();

    private static final Map<Direction, InfProperty<Boolean>> DIRECTION_TO_PROPERTY = Maps.newEnumMap(Direction.class);

    static {
        DIRECTION_TO_PROPERTY.put(Direction.NORTH, SOLID_NORTH);
        DIRECTION_TO_PROPERTY.put(Direction.EAST, SOLID_EAST);
        DIRECTION_TO_PROPERTY.put(Direction.SOUTH, SOLID_SOUTH);
        DIRECTION_TO_PROPERTY.put(Direction.WEST, SOLID_WEST);
        DIRECTION_TO_PROPERTY.put(Direction.UP, SOLID_UP);
        DIRECTION_TO_PROPERTY.put(Direction.DOWN, SOLID_DOWN);
    }

    public static InfProperty<Boolean> getSolidProperty(Direction dir) {
        return DIRECTION_TO_PROPERTY.get(dir);
    }

    public BlockGreenHouseAir() {
        super(Names.Blocks.GREENHOUSE_AIR,  Properties.create(Material.AIR)
                .doesNotBlockMovement().setAir().noDrops().notSolid().variableOpacity()
        );
    }

    @Nonnull
    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }
}
