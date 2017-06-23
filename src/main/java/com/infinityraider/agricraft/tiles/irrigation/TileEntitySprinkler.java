package com.infinityraider.agricraft.tiles.irrigation;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.irrigation.IConnectable;
import com.infinityraider.agricraft.api.v1.irrigation.IIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IrrigationConnectionType;
import com.infinityraider.agricraft.api.v1.util.BlockRange;
import com.infinityraider.agricraft.api.v1.misc.IAgriDisplayable;
import com.infinityraider.agricraft.blocks.irrigation.BlockWaterChannel;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.particles.LiquidSprayFX;
import com.infinityraider.agricraft.utility.BaseIcons;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.utility.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.function.Consumer;

public class TileEntitySprinkler extends TileEntityBase implements ITickable, IIrrigationComponent, IAgriDisplayable {

    public static final int BUFFER_CAP = 100;
    public static final int RADIUS = 3;
    public static final int HEIGHT = 5;

    private int buffer = 0;
    private int counter = 0;
    private float angle = 0.0F;
    private boolean active = false;

    private final BlockRange range;

    public TileEntitySprinkler() {
        this.range = new BlockRange(this.getPos().add(-RADIUS, 1, -RADIUS), this.getPos().add(RADIUS, HEIGHT, RADIUS));
    }

    /**
     * Retrieves the current angle of the sprinkler head.
     *
     * @return The sprinkler head angle.
     */
    public float getAngle() {
        return angle;
    }

    //this saves the data on the tile entity
    @Override
    public void writeTileNBT(NBTTagCompound tag) {
        if (this.counter > 0) {
            tag.setInteger(AgriNBT.LEVEL, this.counter);
        }
        if (this.active) {
            tag.setBoolean(AgriNBT.ACTIVE, active);
        }
        if (this.buffer > 0) {
            tag.setInteger(AgriNBT.BUFFER, this.buffer);
        }
    }

    //this loads the saved data for the tile entity
    // Note: tag.get* methods will return zero/false if key does not exist.
    @Override
    public void readTileNBT(NBTTagCompound tag) {
        this.counter = tag.getInteger(AgriNBT.LEVEL);
        this.active  = tag.getBoolean(AgriNBT.ACTIVE);
        this.buffer  = tag.getInteger(AgriNBT.BUFFER);
    }

    //checks if the sprinkler is CONNECTED to an irrigation channel
    public boolean isConnected() {
        return this.worldObj != null && this.worldObj.getBlockState(getPos().up()).getBlock() instanceof BlockWaterChannel;
    }

    @Override
    public void update() {
        if (!worldObj.isRemote && this.isActive()) {
            this.counter = (counter + 1) % AgriCraftConfig.sprinklerGrowthIntervalTicks;
            this.buffer -= 10;
            this.range.stream().forEach(p -> this.irrigate(p, false));
        } else if (this.active) {
            this.renderLiquidSpray();
        }
    }

    @Override
    public boolean canConnectTo(EnumFacing side, IConnectable component) {
        return side.equals(EnumFacing.UP) && component instanceof TileEntityChannel;
    }

    @Override
    public boolean canAcceptFluid(int y, int amount, boolean partial) {
        if (buffer + amount <= BUFFER_CAP) {
            return true;
        } else {
            return partial;
        }
    }

    @Override
    public int acceptFluid(int y, int amount, boolean partial) {
        if (canAcceptFluid(y, amount, partial)) {
            this.buffer += amount;
            if (this.buffer > BUFFER_CAP) {
                amount = this.buffer - BUFFER_CAP;
                this.buffer = BUFFER_CAP;
            } else {
                amount = 0;
            }
        }
        return amount;
    }

    @Override
    public int getFluidAmount(int y) {
        return this.buffer;
    }

    @Override
    public int getCapacity() {
        return BUFFER_CAP;
    }

    @Override
    public void setFluidLevel(int lvl) {
        // This can be skipped... Shhh!
    }

    @Override
    public void syncFluidLevel() {
        // This can be skipped... Shhh!
    }

    @Override
    public int getFluidHeight() {
        return this.buffer;
    }

    @Override
    public float getFluidHeight(int lvl) {
        return (this.buffer * 16.0f / BUFFER_CAP);
    }

    @Override
    public IrrigationConnectionType getConnectionType(EnumFacing side) {
        if (side == EnumFacing.UP) {
            return IrrigationConnectionType.PRIMARY;
        } else {
            return IrrigationConnectionType.NONE;
        }
    }

    public boolean canSprinkle() {
        return WorldHelper
                .getTile(worldObj, pos.add(0, 1, 0), TileEntityChannel.class)
                .filter(c -> c.getFluidAmount(0) > AgriCraftConfig.sprinklerRatePerHalfSecond)
                .isPresent();
    }

    private boolean isActive() {
        boolean newState = this.canSprinkle();
        if (newState != this.active) {
            this.active = newState;
            this.markForUpdate();
        }
        return this.active;
    }

    /**
     * Depending on the block type either irrigates farmland or forces plant
     * GROWTH (based on chance)
     */
    private void irrigate(BlockPos pos, boolean farmlandOnly) {
        IBlockState state = this.getWorld().getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof BlockFarmland && block.getMetaFromState(state) < 7) {
            // irrigate farmland
            int flag = counter == 0 ? 2 : 6;
            worldObj.setBlockState(pos, block.getStateFromMeta(7), flag);
        } else if (!farmlandOnly && ((block instanceof IPlantable) || (block instanceof IGrowable))) {
            // X1 chance to force GROWTH tick on plant every Y1 ticks
            if (counter == 0 && worldObj.rand.nextDouble() <= AgriCraftConfig.sprinklerGrowthChancePercent) {
                block.updateTick(this.getWorld(), pos, state, worldObj.rand);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getChannelIcon() {
        // Fetch the Icon using the handy world helper class.
        return WorldHelper
                .getTile(worldObj, pos.up(), TileEntityChannel.class)
                .map(c -> c.getIcon())
                .orElse(BaseIcons.OAK_PLANKS.getIcon());
    }

    @SideOnly(Side.CLIENT)
    private void renderLiquidSpray() {
        if (AgriCraftConfig.disableParticles) {
            return;
        }
        this.angle = (this.angle + 5F) % 360;
        int particleSetting = Minecraft.getMinecraft().gameSettings.particleSetting;    //0 = all, 1 = decreased; 2 = minimal;
        counter = (counter + 1) % (particleSetting + 1);
        if (counter == 0) {
            for (int i = 0; i < 4; i++) {
                float alpha = -(this.angle + 90 * i) * ((float) Math.PI) / 180;
                double xOffset = (4 * Constants.UNIT) * Math.cos(alpha);
                double zOffset = (4 * Constants.UNIT) * Math.sin(alpha);
                float radius = 0.3F;
                for (int j = 0; j <= 4; j++) {
                    float beta = -j * ((float) Math.PI) / (8.0F);
                    Vec3d vector = new Vec3d(radius * Math.cos(alpha), radius * Math.sin(beta), radius * Math.sin(alpha));
                    this.spawnLiquidSpray(xOffset * (4 - j) / 4, zOffset * (4 - j) / 4, vector);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnLiquidSpray(double xOffset, double zOffset, Vec3d vector) {
        LiquidSprayFX liquidSpray = new LiquidSprayFX(this.worldObj, this.xCoord() + 0.5F + xOffset, this.yCoord() + 8 * Constants.UNIT, this.zCoord() + 0.5F + zOffset, 0.3F, 0.7F, vector);
        Minecraft.getMinecraft().effectRenderer.addEffect(liquidSpray);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addDisplayInfo(Consumer<String> information) {
        information.accept(AgriCore.getTranslator().translate("agricraft_tooltip.waterLevel") + ": " + this.getFluidAmount(0) + "/" + BUFFER_CAP);
    }
}
