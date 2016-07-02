package com.infinityraider.agricraft.tiles.irrigation;

import com.infinityraider.agricraft.api.v1.irrigation.IConnectable;
import com.infinityraider.agricraft.api.v1.irrigation.IIrrigationComponent;
import com.infinityraider.agricraft.blocks.BlockWaterChannel;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.particles.LiquidSprayFX;
import com.infinityraider.agricraft.tiles.TileEntityBase;
import com.infinityraider.agricraft.utility.BaseIcons;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import com.infinityraider.agricraft.reference.AgriNBT;

public class TileEntitySprinkler extends TileEntityBase implements ITickable, IIrrigationComponent {

	private static final int BUFFER_CAP = 100;

	private int buffer = 0;
	private int counter = 0;
	public float angle = 0.0F;
	private boolean isSprinkled = false;

	//this saves the data on the tile entity
	@Override
	public void writeTileNBT(NBTTagCompound tag) {
		if (this.counter > 0) {
			tag.setInteger(AgriNBT.LEVEL, this.counter);
		}
		tag.setBoolean(AgriNBT.IS_SPRINKLED, isSprinkled);
	}

	//this loads the saved data for the tile entity
	@Override
	public void readTileNBT(NBTTagCompound tag) {
		if (tag.hasKey(AgriNBT.LEVEL)) {
			this.counter = tag.getInteger(AgriNBT.LEVEL);
		} else {
			this.counter = 0;
		}

		if (tag.hasKey(AgriNBT.IS_SPRINKLED)) {
			this.isSprinkled = tag.getBoolean(AgriNBT.IS_SPRINKLED);
		} else {
			this.isSprinkled = false;
		}
	}

	//checks if the sprinkler is CONNECTED to an irrigation channel
	public boolean isConnected() {
		return this.worldObj != null && this.worldObj.getBlockState(getPos().add(0, 1, 0)).getBlock() instanceof BlockWaterChannel;
	}

	@Override
	public void update() {
		if (!worldObj.isRemote) {
			if (this.sprinkle()) {
				counter = ++counter % AgriCraftConfig.sprinklerGrowthIntervalTicks;
				this.buffer -= 10;

				for (int yOffset = 1; yOffset < 6; yOffset++) {
					for (int xOffset = -3; xOffset <= 3; xOffset++) {
						for (int zOffset = -3; zOffset <= 3; zOffset++) {
							this.irrigate(this.xCoord() + xOffset, this.yCoord() - yOffset, this.zCoord() + zOffset, yOffset >= 5);
						}
					}
				}
			}
		} else if (this.isSprinkled) {
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

	public boolean canSprinkle() {
		return this.isConnected() && ((TileEntityChannel) this.worldObj.getTileEntity(getPos().add(0, 1, 0))).getFluidAmount(0)
				> AgriCraftConfig.sprinklerRatePerHalfSecond;
	}

	private boolean sprinkle() {
		boolean newState = this.canSprinkle();
		if (newState != this.isSprinkled) {
			this.isSprinkled = newState;
			this.markForUpdate();
		}
		return this.isSprinkled;
	}

	/**
	 * Depending on the block type either irrigates farmland or forces plant
	 * GROWTH (based on chance)
	 */
	private void irrigate(int x, int y, int z, boolean farmlandOnly) {
		BlockPos pos = new BlockPos(x, y, z);
		IBlockState state = this.getWorld().getBlockState(pos);
		Block block = state.getBlock();
		if (block != null) {
			if (block instanceof BlockFarmland && block.getMetaFromState(state) < 7) {
				// irrigate farmland
				int flag = counter == 0 ? 2 : 6;
				worldObj.setBlockState(pos, block.getStateFromMeta(7), flag);
			} else if (((block instanceof IPlantable) || (block instanceof IGrowable)) && !farmlandOnly) {
				// X1 chance to force GROWTH tick on plant every Y1 ticks
				if (counter == 0 && worldObj.rand.nextDouble() <= AgriCraftConfig.sprinklerGrowthChancePercent) {
					block.updateTick(this.getWorld(), pos, state, worldObj.rand);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getChannelIcon() {

		if (this.getWorld() == null) {
			return BaseIcons.OAK_PLANKS.getIcon();
		}

		TileEntity te = getWorld().getTileEntity(getPos().add(0, 1, 0));

		if (te instanceof TileEntityChannel) {
			//TODO: get channel icon
			return null;
		}

		return BaseIcons.OAK_PLANKS.getIcon();

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
	public boolean isRotatable() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addDisplayInfo(List information) {
		//Nothing to add here. Move along!
	}
}
