package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class BlockAgriCraftFarmland extends BlockFarmland implements ITileEntityProvider {
    public BlockAgriCraftFarmland() {
        super();
        this.stepSound = soundTypeStone;
        this.blockParticleGravity = 1.0F;
        this.slipperiness = 0.6F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.opaque = this.isOpaqueCube();
        this.lightOpacity = this.isOpaqueCube() ? 255 : 0;
        this.canBlockGrass = !Material.ground.getCanBlockGrass();
        this.setTickRandomly(true);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
        this.setLightOpacity(255);
    }

    //return the tile entity
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySeedData();
    }

    //break the tile entity when broken
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        super.breakBlock(world, x, y, z, block, meta);
    }

    //display as regular farmland (for waila and stuffs)
    @Override
    public String getLocalizedName() {
        return StatCollector.translateToLocal(Blocks.farmland.getUnlocalizedName() + ".name");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.farmland.getIcon(side, meta);
    }
}
