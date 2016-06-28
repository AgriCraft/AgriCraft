package com.infinityraider.agricraft.network;

import com.agricraft.agricore.base.AgriItem;
import com.agricraft.agricore.base.AgriRecipe;
import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriPlant;
import com.google.gson.Gson;
import com.infinityraider.agricraft.compat.json.JsonHelper;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSyncRecipeJson extends MessageBase {

	private static final Gson gson = new Gson();

	private AgriRecipe recipe;
	private int index;
	private int count;
	
	public MessageSyncRecipeJson() {
		// Nothing to see here...
	}

	public MessageSyncRecipeJson(AgriRecipe recipe, int index, int count) {
		this.recipe = recipe;
		this.index = index;
		this.count = count;
	}

	@Override
	public Side getMessageHandlerSide() {
		return Side.CLIENT;
	}

	@Override
	protected void processMessage(MessageContext ctx) {
		AgriCore.getLogger("Agri-Net").debug("Recieved Recipe for \"{0}\" ({1} of {2}).", recipe.getResult(), index + 1, count);
		AgriCore.getRecipes().addRecipe(recipe);
		if (this.index == this.count - 1) {
			JsonHelper.initRecipes();
		}
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
		this.recipe = gson.fromJson(json, AgriRecipe.class);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(index);
		buf.writeInt(count);
		final String json = gson.toJson(recipe);
		ByteBufUtils.writeUTF8String(buf, json);
	}

}
