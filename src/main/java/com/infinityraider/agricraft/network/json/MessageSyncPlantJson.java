package com.infinityraider.agricraft.network.json;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.json.AgriSaver;
import com.agricraft.agricore.plant.AgriPlant;
import com.google.gson.Gson;
import com.infinityraider.agricraft.apiimpl.PlantRegistry;
import com.infinityraider.agricraft.core.CoreHandler;
import com.infinityraider.agricraft.core.JsonPlant;
import java.nio.file.Path;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSyncPlantJson extends MessageSyncElement<AgriPlant> {

	private static final Gson gson = new Gson();

	public MessageSyncPlantJson() {
	}
	
	public MessageSyncPlantJson(AgriPlant plant, int index, int count) {
		super(plant, index, count);
	}

	@Override
	protected String toString(AgriPlant element) {
		return gson.toJson(element);
	}

	@Override
	protected AgriPlant fromString(String element) {
		return gson.fromJson(element, AgriPlant.class);
	}
	
	@Override
	public void onSyncStart(MessageContext ctx) {
		AgriCore.getPlants().clearElements();
	}

	@Override
	protected void onMessage(MessageContext ctx) {
		AgriCore.getPlants().addPlant(this.element);
	}
	
	@Override
	public void onFinishSync(MessageContext ctx) {
		final Path worldDir = CoreHandler.getPlantDir().resolve(this.getServerId());
		AgriSaver.saveElements(worldDir, AgriCore.getPlants().getAll());
		AgriCore.getPlants().getAll().stream()
				.map(JsonPlant::new)
				.forEach(PlantRegistry.getInstance()::addPlant);
	}

}
