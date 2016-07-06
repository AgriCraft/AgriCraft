package com.infinityraider.agricraft.network;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriMutation;
import com.google.gson.Gson;
import com.infinityraider.agricraft.apiimpl.MutationRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSyncMutationJson extends MessageBase {

	private static final Gson gson = new Gson();

	private AgriMutation mutation;
	private int index;
	private int count;

	public MessageSyncMutationJson() {
		// Nothing to see here...
	}

	public MessageSyncMutationJson(AgriMutation mutation, int index, int count) {
		this.mutation = mutation;
		this.index = index;
		this.count = count;
	}

	@Override
	public Side getMessageHandlerSide() {
		return Side.CLIENT;
	}

	@Override
	protected void processMessage(MessageContext ctx) {
		AgriCore.getLogger("Agri-Net").debug("Recieved Mutation: ({0} of {1})", index + 1, count);
		MutationRegistry.getInstance().addMutation(
				mutation.getChance(),
				mutation.getChild().getId(),
				mutation.getParent1().getId(),
				mutation.getParent2().getId()
		);
	}

	@Override
	protected IMessage getReply(MessageContext ctx) {
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.index = buf.readInt();
		this.count = buf.readInt();
		final String json = ByteBufUtils.readUTF8String(buf);
		this.mutation = gson.fromJson(json, AgriMutation.class);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(index);
		buf.writeInt(count);
		final String json = gson.toJson(mutation);
		ByteBufUtils.writeUTF8String(buf, json);
	}

}
