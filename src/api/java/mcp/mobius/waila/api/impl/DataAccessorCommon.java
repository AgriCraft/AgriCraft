package mcp.mobius.waila.api.impl;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.utils.NBTUtil;

public class DataAccessorCommon implements IWailaCommonAccessor, IWailaDataAccessor, IWailaEntityAccessor{

	public World world;
	public EntityPlayer player;
	public MovingObjectPosition mop;
	public Vec3 renderingvec = null;
	public Block block;
	public int blockID;
	public String blockResource;
	public int metadata;
	public TileEntity tileEntity;
	public Entity entity;
	public NBTTagCompound remoteNbt = null;
	public long timeLastUpdate = System.currentTimeMillis();
	public double partialFrame;
	public ItemStack stack;	
	
	public static DataAccessorCommon instance = new DataAccessorCommon();
	
	public void set(World _world, EntityPlayer _player, MovingObjectPosition _mop) {
		this.set(_world, _player, _mop, null, 0.0);
	}
	
	public void set(World _world, EntityPlayer _player, MovingObjectPosition _mop, EntityLivingBase viewEntity, double partialTicks) {
		this.world      = _world;
		this.player     = _player;
		this.mop        = _mop;
		
		if (this.mop.typeOfHit == MovingObjectType.BLOCK){
			this.block      = world.getBlock(_mop.blockX, _mop.blockY, _mop.blockZ);
			this.metadata   = world.getBlockMetadata(_mop.blockX, _mop.blockY, _mop.blockZ);
			this.tileEntity = world.getTileEntity(_mop.blockX, _mop.blockY, _mop.blockZ);
			this.entity     = null;
			this.blockID       = Block.getIdFromBlock(this.block);
			this.blockResource = GameData.getBlockRegistry().getNameForObject(this.block);
			try{ this.stack = new ItemStack(this.block, 1, this.metadata); } catch (Exception e) {}
			
		} else if (this.mop.typeOfHit == MovingObjectType.ENTITY){
			this.block      = null;
			this.metadata   = -1;
			this.tileEntity = null;
			this.stack      = null;
			this.entity     = _mop.entityHit;
		}
		
		if (viewEntity != null){
			double px = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
			double py = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
			double pz = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;		
			this.renderingvec = Vec3.createVectorHelper(_mop.blockX - px, _mop.blockY - py, _mop.blockZ - pz);
			this.partialFrame = partialTicks;
		}
	}	
	
	@Override
	public World getWorld() {return this.world;}

	@Override
	public EntityPlayer getPlayer() {return this.player;}

	@Override
	public Block getBlock() {return this.block;}

	@Override
	public int getBlockID() {return this.blockID;}

	@Override
	public int getMetadata() {return this.metadata;}

	@Override
	public TileEntity getTileEntity() {	return this.tileEntity;}

	@Override
	public Entity getEntity() {return this.entity;}

	@Override
	public MovingObjectPosition getPosition() {return this.mop;}

	@Override
	public Vec3 getRenderingPosition() {return this.renderingvec;}

	@Override
	public NBTTagCompound getNBTData() {
		if ((this.tileEntity != null) && this.isTagCorrectTileEntity(this.remoteNbt)) 
			return remoteNbt;
		
		if ((this.entity != null) && this.isTagCorrectEntity(this.remoteNbt)) 
			return remoteNbt;		
		
		if (this.tileEntity != null){
			NBTTagCompound tag = new NBTTagCompound();		
			try{
				this.tileEntity.writeToNBT(tag);
			} catch (Exception e){}
			return tag;			
		}
		
		if (this.entity != null){
			NBTTagCompound tag = new NBTTagCompound();
			this.entity.writeToNBT(tag);
			return tag;
		}
		
		return null;
	}

	public void setNBTData(NBTTagCompound tag){
		this.remoteNbt = tag;
	}	
	
	private boolean isTagCorrectTileEntity(NBTTagCompound tag){
		if (tag == null || !tag.hasKey("WailaX")){
			this.timeLastUpdate = System.currentTimeMillis() - 250;
			return false;
		}
		
		int x = tag.getInteger("WailaX");
		int y = tag.getInteger("WailaY");
		int z = tag.getInteger("WailaZ");
		
		if (x == this.mop.blockX && y == this.mop.blockY && z == this.mop.blockZ)
			return true;
		else {
			this.timeLastUpdate = System.currentTimeMillis() - 250;			
			return false;
		}
	}	
	
	private boolean isTagCorrectEntity(NBTTagCompound tag){
		if (tag == null || !tag.hasKey("WailaEntityID")){
			this.timeLastUpdate = System.currentTimeMillis() - 250;
			return false;
		}
		
		int id = tag.getInteger("WailaEntityID");
	
		if (id == this.entity.getEntityId())
			return true;
		else {
			this.timeLastUpdate = System.currentTimeMillis() - 250;			
			return false;
		}
	}		
	
	@Override
	public int getNBTInteger(NBTTagCompound tag, String keyname) {
		return NBTUtil.getNBTInteger(tag, keyname);
	}

	@Override
	public double getPartialFrame() {
		return this.partialFrame;
	}

	@Override
	public ForgeDirection getSide() {
		return ForgeDirection.getOrientation(this.getPosition().sideHit);
	}

	@Override
	public ItemStack getStack() {
		return this.stack;
	}

	public boolean isTimeElapsed(long time){
		return System.currentTimeMillis() - this.timeLastUpdate >= time;
	}
	
	public void resetTimer(){
		this.timeLastUpdate = System.currentTimeMillis();
	}

	@Override
	public String getBlockQualifiedName() {
		return this.blockResource;
	}	
	
}
