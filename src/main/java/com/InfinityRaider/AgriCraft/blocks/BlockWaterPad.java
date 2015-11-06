package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderWaterPad;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class BlockWaterPad extends BlockAgriCraft {
    public BlockWaterPad() {
        this(Material.ground);
    }

    protected BlockWaterPad(Material mat) {
        super(mat);
        this.setHardness(0.5F);
        this.setStepSound(soundTypeGravel);
        this.maxY = Constants.UNIT * (Constants.WHOLE / 2);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return new RenderWaterPad(this);
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockWaterPad.class;
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.waterPad;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fX, float fY, float fZ) {
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null || stack.getItem() == null) {
            return false;
        }
        if (FluidContainerRegistry.isContainer(stack)) {
            FluidStack waterBucket = new FluidStack(FluidRegistry.WATER, 1000);
            if (!FluidContainerRegistry.containsFluid(stack, waterBucket)) {
                return false;
            }
            if (!player.capabilities.isCreativeMode) {
                player.inventory.addItemStackToInventory(FluidContainerRegistry.drainFluidContainer(stack));
                player.getCurrentEquippedItem().stackSize = player.getCurrentEquippedItem().stackSize - 1;
            }
            if (!world.isRemote) {
                world.setBlock(x, y, z, com.InfinityRaider.AgriCraft.init.Blocks.blockWaterPadFull, 7, 3);
            }
            return true;

        }
        return false;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float f, int i) {
        if(!world.isRemote) {
            ItemStack drop = new ItemStack(Blocks.dirt, 1);
            this.dropBlockAsItem(world, x, y, z, drop);
        }
    }

    //creative item picking
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        return new ItemStack(Blocks.dirt);
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }

    //render methods
    //--------------
    @Override
    public boolean isOpaqueCube() {return false;}           //tells minecraft that this is not a block (no levers can be placed on it, it's transparent, ...)

    @Override
    public boolean renderAsNormalBlock() {return false;}    //tells minecraft that this has custom rendering

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int i) {return true;}

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.dirt.getIcon(side, meta);
    }

    //register icons
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        //NOOP
    }

    public static class ItemBlockWaterPad extends net.minecraft.item.ItemBlock {
        public ItemBlockWaterPad(Block block) {
            super(block);
        }

        @Override
        @SideOnly(Side.CLIENT)
        @SuppressWarnings("unchecked")
        public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
            list.add(StatCollector.translateToLocal("agricraft_tooltip.waterPadDry"));
        }
    }
}
