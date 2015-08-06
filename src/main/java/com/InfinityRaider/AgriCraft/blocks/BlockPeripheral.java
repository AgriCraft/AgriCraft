package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.compatibility.computercraft.ComputerCraftHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.tileentity.peripheral.TileEntityPeripheral;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

@Optional.Interface(modid = Names.Mods.computerCraft, iface = "dan200.computercraft.api.peripheral.IPeripheralProvider")
public class BlockPeripheral extends BlockContainerAgriCraft implements IPeripheralProvider {
    public BlockPeripheral() {
        super(Material.iron);
        ComputerCraftHelper.registerPeripheralProvider(this);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityPeripheral();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return null;
    }

    @Override
    public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te==null || !(te instanceof TileEntityPeripheral)) {
            return null;
        }
        return (TileEntityPeripheral) te;
    }
}
