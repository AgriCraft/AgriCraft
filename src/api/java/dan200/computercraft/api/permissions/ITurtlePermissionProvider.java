/**
 * This file is part of the public ComputerCraft API - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2015. This API may be redistributed unmodified and in full only.
 * For help using the API, and posting your mods, visit the forums at computercraft.info.
 */

package dan200.computercraft.api.permissions;

import net.minecraft.world.World;

/**
 * This interface is used to restrict where turtles can move or build
 * @see dan200.computercraft.api.ComputerCraftAPI#registerPermissionProvider(ITurtlePermissionProvider)
 */
public interface ITurtlePermissionProvider
{
    public boolean isBlockEnterable( World world, int x, int y, int z );
    public boolean isBlockEditable( World world, int x, int y, int z );
}
