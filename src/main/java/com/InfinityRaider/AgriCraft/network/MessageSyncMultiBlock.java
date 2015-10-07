package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.multiblock.IMultiBlockComponent;
import com.InfinityRaider.AgriCraft.utility.multiblock.MultiBlockLogic;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MessageSyncMultiBlock extends MessageAgriCraft {
    private int rootX;
    private int rootY;
    private int rootZ;
    private int xSize;
    private int ySize;
    private int zSize;

    public MessageSyncMultiBlock() {}

    public MessageSyncMultiBlock(IMultiBlockComponent component) {
        MultiBlockLogic logic = component.getMultiBlockLogic();
        TileEntity root = logic.getRootComponent().getTileEntity();
        this.rootX = root.xCoord;
        this.rootY = root.yCoord;
        this.rootZ = root.zCoord;
        this.xSize = logic.sizeX();
        this.ySize = logic.sizeY();
        this.zSize = logic.sizeZ();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.rootX = buf.readInt();
        this.rootY = buf.readInt();
        this.rootZ = buf.readInt();
        this.xSize = buf.readInt();
        this.ySize = buf.readInt();
        this.zSize = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.rootX);
        buf.writeInt(this.rootY);
        buf.writeInt(this.rootZ);
        buf.writeInt(this.xSize);
        buf.writeInt(this.ySize);
        buf.writeInt(this.zSize);
    }

    public static class MessageHandler implements IMessageHandler<MessageSyncMultiBlock, IMessage> {
        @Override
        public IMessage onMessage(MessageSyncMultiBlock message, MessageContext ctx) {
            World world = AgriCraft.proxy.getClientWorld();
            if(world != null) {
                TileEntity te = world.getTileEntity(message.rootX, message.rootY, message.rootZ);
                if(te != null && te instanceof IMultiBlockComponent) {
                    IMultiBlockComponent component = (IMultiBlockComponent) te;
                    MultiBlockLogic logic = component.getMultiBlockLogic();
                    logic.setDimensions(message.xSize, message.ySize, message.zSize);
                    logic.setRootComponent(world, message.rootX, message.rootY, message.rootZ);
                    logic.createMultiBlock();
                }
            }
            return null;
        }
    }
}
