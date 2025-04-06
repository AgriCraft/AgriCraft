package com.agricraft.agricraft.compat.emi;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.AgriClientApi;
import com.agricraft.agricraft.api.LangUtils;
import com.agricraft.agricraft.api.TagUtils;
import com.agricraft.agricraft.api.codecs.AgriSoilCondition;
import com.agricraft.agricraft.api.crop.AgriGrowthStage;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.registries.AgriCraftGrowthConditions;
import com.agricraft.agricraft.api.registries.AgriCraftStats;
import com.agricraft.agricraft.api.requirement.AgriSeason;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.ButtonWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class CropRequirementRecipe implements EmiRecipe {

	public static final EmiTexture BACKGROUND = new EmiTexture(ResourceLocation.fromNamespaceAndPath(AgriApi.MOD_ID, "textures/gui/jei/crop_requirement.png"), 0, 0, 128, 128, 128, 128, 128, 128);
	public static final EmiTexture COMPONENTS = new EmiTexture(ResourceLocation.fromNamespaceAndPath(AgriApi.MOD_ID, "textures/gui/jei/crop_requirement.png"), 0, 0, 128, 128, 128, 128, 128, 128);
	public static final EmiTexture GUI_COMPONENTS = new EmiTexture(ResourceLocation.fromNamespaceAndPath(AgriApi.MOD_ID, "textures/gui/jei/crop_requirement_components.png"), 0, 0, 128, 128, 128, 128, 128, 128);

	public static final ResourceLocation R_COMPONENTS = ResourceLocation.fromNamespaceAndPath(AgriApi.MOD_ID, "textures/gui/jei/crop_requirement_components.png");
	public static final ResourceLocation R_GUI_COMPONENTS = ResourceLocation.fromNamespaceAndPath(AgriApi.MOD_ID, "textures/gui/gui_components.png");

	public static final int[] HUMIDITY_OFFSETS = {8, 8, 10, 10, 10, 7};
	public static final int[] ACIDITY_OFFSETS = {7, 8, 7, 8, 8, 8, 6};
	public static final int[] NUTRIENTS_OFFSETS = {6, 8, 9, 9, 11, 10};
	public final Btn incStrButton;
	public final Btn decStrButton;
	public final Btn incStageButton;
	public final Btn decStageButton;
	private final ResourceLocation id;
	private final List<EmiIngredient> input;
	private final AgriPlant plant;
	private final List<EmiStack> output;
	private final String plantId;
	public long lastTime;
	private int currentStrength = AgriCraftStats.STRENGTH.value().getMin();
	private AgriGrowthStage currentStage;
	private List<Block> soils;
	private int soil;

	public CropRequirementRecipe(ResourceLocation id, AgriPlant plant) {
		this.id = id;
		input = List.of(EmiStack.of(AgriSeedItem.toStack(plant)).comparison(AgriCraftEmiPlugin.compareSeeds()));
		this.plant = plant;
		output = new ArrayList<>();
		this.plantId = plant.getId().map(ResourceLocation::toString).orElse("");
		this.currentStage = plant.getInitialGrowthStage();
		this.incStrButton = new Btn(104, 10, 9, 9, this::incrementStrength, true);
		this.decStrButton = new Btn(104, 71, 9, 9, this::decrementStrength, false);
		this.incStageButton = new Btn(92, 10, 9, 9, this::incrementStage, true);
		this.decStageButton = new Btn(92, 71, 9, 9, this::decrementStage, false);
		this.updateSoils();
		lastTime = System.currentTimeMillis();
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return AgriCraftEmiPlugin.REQUIREMENT_CATEGORY;
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return input;
	}

	@Override
	public List<EmiStack> getOutputs() {
		return output;
	}

	@Override
	public int getDisplayWidth() {
		return 128;
	}

	@Override
	public int getDisplayHeight() {
		return 128;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(BACKGROUND, 0, 0);
		widgets.addSlot(input.get(0), 55, 2).drawBack(false);
		widgets.add(incStrButton);
		widgets.add(decStrButton);
		widgets.add(incStageButton);
		widgets.add(decStageButton);


		widgets.addDrawable(0, 0, 128, 128, (guiGraphics, mouseX, mouseY, partialTick) -> {
			// render strength increment
			for (int i = 0; i < currentStrength; ++i) {
				guiGraphics.blit(R_COMPONENTS, 105, 66 - i * 5, 0, 66, 7, 3, 128, 128);
			}
			// render stage increment
			int maxHeight = 48;
			int stageHeight = (int) (maxHeight * currentStage.growthPercentage());
			int stageY = 21 + maxHeight - stageHeight;
			guiGraphics.blit(R_COMPONENTS, 93, stageY, 7, stageHeight, 0, 69, 7, 1, 128, 128);
			// render light levels
			for (int i = 15; i >= 0; --i) {
				boolean fertile = AgriCraftGrowthConditions.LIGHT.get().apply(plant, currentStrength, i).isFertile();
				if (fertile) {
					guiGraphics.blit(R_COMPONENTS, 32, 26 + 3 * (15 - i), 3, 3, 0, 18 + 3 * (15 - i), 3, 3, 128, 128);
				}
			}
			// render soil property icons
			for (AgriSoilCondition.Humidity humidity : AgriSoilCondition.Humidity.values()) {
				if (!humidity.isValid()) {
					continue;
				}
				if (AgriCraftGrowthConditions.HUMIDITY.get().apply(plant, currentStrength, humidity).isFertile()) {
					int index = humidity.ordinal();
					int offset = 0;
					for (int i = 0; i < index; ++i) {
						offset += HUMIDITY_OFFSETS[i];
					}
					guiGraphics.blit(R_GUI_COMPONENTS, 37 + offset, 83, HUMIDITY_OFFSETS[index], 12, offset, 0, HUMIDITY_OFFSETS[index], 12, 128, 128);
				}
			}
			for (AgriSoilCondition.Acidity acidity : AgriSoilCondition.Acidity.values()) {
				if (!acidity.isValid()) {
					continue;
				}
				if (AgriCraftGrowthConditions.ACIDITY.get().apply(plant, currentStrength, acidity).isFertile()) {
					int index = acidity.ordinal();
					int offset = 0;
					for (int i = 0; i < index; ++i) {
						offset += ACIDITY_OFFSETS[i];
					}
					guiGraphics.blit(R_GUI_COMPONENTS, 37 + offset, 96, ACIDITY_OFFSETS[index], 12, offset, 12, ACIDITY_OFFSETS[index], 12, 128, 128);
				}
			}
			for (AgriSoilCondition.Nutrients nutrients : AgriSoilCondition.Nutrients.values()) {
				if (!nutrients.isValid()) {
					continue;
				}
				if (AgriCraftGrowthConditions.NUTRIENTS.get().apply(plant, currentStrength, nutrients).isFertile()) {
					int index = nutrients.ordinal();
					int offset = 0;
					for (int i = 0; i < index; ++i) {
						offset += NUTRIENTS_OFFSETS[i];
					}
					guiGraphics.blit(R_GUI_COMPONENTS, 37 + offset, 109, NUTRIENTS_OFFSETS[index], 12, offset, 24, NUTRIENTS_OFFSETS[index], 12, 128, 128);
				}
			}
			// render seasons
			if (AgriApi.get().getSeasonLogic().isActive()) {
				for (AgriSeason season : AgriSeason.values()) {
					if (season == AgriSeason.ANY) {
						continue;
					}
					if (AgriCraftGrowthConditions.SEASON.get().apply(plant, currentStrength, season).isFertile()) {
						guiGraphics.blit(R_GUI_COMPONENTS, 17, 24 + 13 * season.ordinal(), 10 * season.ordinal(), 44, 10, 12, 128, 128);
					}
				}
			}
			// render soils and plant stage
			long l = System.currentTimeMillis();
			if (lastTime + 1500 <= l && !Screen.hasShiftDown()) {  // change soil to render
				this.soil++;
				if (this.soil >= this.soils.size()) {
					this.soil = 0;
				}
				lastTime = l;
			}
			PoseStack stack = guiGraphics.pose();
			stack.pushPose();
			Lighting.setupForFlatItems();
			stack.translate(56, 53, 0);
			stack.translate(-4, 12, 0);
			stack.scale(16, -16, 1);
			stack.mulPose(Axis.XP.rotationDegrees(45));
			stack.mulPose(Axis.YP.rotationDegrees(45));
			MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
			// render soil
			if (!soils.isEmpty() && soil < soils.size()) {
				stack.pushPose();
				Minecraft.getInstance().getBlockRenderer().renderSingleBlock(soils.get(soil).defaultBlockState(), stack, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
				stack.popPose();
			}
			// render plant
			BakedModel model = AgriClientApi.get().getPlantModel(plantId, currentStage.index());
			stack.pushPose();
			stack.translate(0, 1, 0);
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(stack.last(), guiGraphics.bufferSource().getBuffer(RenderType.cutoutMipped()), null, model, 1, 1, 1, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
			stack.popPose();
			// TODO: @ketheroth display block below requirement
			bufferSource.endBatch();
			stack.popPose();
		});


		widgets.addTooltip((mouseX, mouseY) -> List.of(ClientTooltipComponent.create(Component.translatable("agricraft.tooltip.jei.strength", currentStrength).getVisualOrderText())), 104, 20, 9, 50);
		widgets.addTooltip((mouseX, mouseY) -> List.of(ClientTooltipComponent.create(Component.translatable("agricraft.tooltip.jei.stage", currentStage.index()).getVisualOrderText())), 92, 20, 9, 50);
		widgets.addTooltip((mouseX, mouseY) -> List.of(ClientTooltipComponent.create(Component.translatable("agricraft.tooltip.jei.light", 15 - (mouseY - 26) / 3).getVisualOrderText())), 32, 26, 3, 47);
		widgets.addTooltip((mouseX, mouseY) -> {
			int offset = 0;
			for (int i = 0; i < HUMIDITY_OFFSETS.length; i++) {
				if (37 + offset <= mouseX && mouseX <= 37 + offset + HUMIDITY_OFFSETS[i]) {
					AgriSoilCondition.Humidity humidity = AgriSoilCondition.Humidity.values()[i];
					return List.of(ClientTooltipComponent.create(Component.translatable("agricraft.soil.humidity." + humidity.name().toLowerCase()).getVisualOrderText()));
				}
				offset += HUMIDITY_OFFSETS[i];
			}
			return List.of();
		}, 37, 83, 53, 12);
		widgets.addTooltip((mouseX, mouseY) -> {
			int offset = 0;
			for (int i = 0; i < ACIDITY_OFFSETS.length; i++) {
				if (37 + offset <= mouseX && mouseX <= 37 + offset + ACIDITY_OFFSETS[i]) {
					AgriSoilCondition.Acidity acidity = AgriSoilCondition.Acidity.values()[i];
					return List.of(ClientTooltipComponent.create(Component.translatable("agricraft.soil.acidity." + acidity.name().toLowerCase()).getVisualOrderText()));
				}
				offset += ACIDITY_OFFSETS[i];
			}
			return List.of();
		}, 37, 96, 52, 12);
		widgets.addTooltip((mouseX, mouseY) -> {
			int offset = 0;
			for (int i = 0; i < NUTRIENTS_OFFSETS.length; i++) {
				if (37 + offset <= mouseX && mouseX <= 37 + offset + NUTRIENTS_OFFSETS[i]) {
					AgriSoilCondition.Nutrients nutrients = AgriSoilCondition.Nutrients.values()[i];
					return List.of(ClientTooltipComponent.create(Component.translatable("agricraft.soil.nutrients." + nutrients.name().toLowerCase()).getVisualOrderText()));
				}
				offset += NUTRIENTS_OFFSETS[i];
			}
			return List.of();
		}, 37, 109, 6 + 8 + 9 + 9 + 11 + 10, 12);
		Component desc = LangUtils.plantDescription(plantId);
		widgets.addTooltipText(desc == null ? List.of(LangUtils.plantName(plantId)) : List.of(LangUtils.plantName(plantId), desc), 50, 30, 26, 24);
		if (!soils.isEmpty() && soil < soils.size()) {
			widgets.addTooltip((mouseX, mouseY) -> Screen.getTooltipFromItem(Minecraft.getInstance(), new ItemStack(soils.get(soil))).stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).toList(), 50, 58, 26, 16);
		}
		if (AgriApi.get().getSeasonLogic().isActive()) {
			widgets.addTooltipText(List.of(LangUtils.seasonName(AgriSeason.SPRING)), 17, 24, 12, 12);
			widgets.addTooltipText(List.of(LangUtils.seasonName(AgriSeason.SUMMER)), 17, 37, 12, 12);
			widgets.addTooltipText(List.of(LangUtils.seasonName(AgriSeason.AUTUMN)), 17, 50, 12, 12);
			widgets.addTooltipText(List.of(LangUtils.seasonName(AgriSeason.WINTER)), 17, 63, 12, 12);
		}
	}

	public boolean incrementStrength() {
		this.currentStrength = Math.min(AgriCraftStats.STRENGTH.value().getMax(), currentStrength + 1);
		this.updateSoils();
		return true;
	}

	public boolean decrementStrength() {
		this.currentStrength = Math.max(AgriCraftStats.STRENGTH.value().getMin(), currentStrength - 1);
		this.updateSoils();
		return true;
	}

	public boolean incrementStage() {
		this.currentStage = this.currentStage.getNext(null, null);
		return true;
	}

	public boolean decrementStage() {
		this.currentStage = this.currentStage.getPrevious(null, null);
		return true;
	}

	public void updateSoils() {
		this.soils = AgriApi.get().getSoilRegistry().map(registry -> registry.stream().filter(soil -> {
							boolean humidity = AgriCraftGrowthConditions.HUMIDITY.get().apply(this.plant, this.currentStrength, soil.humidity()).isFertile();
							boolean acidity = AgriCraftGrowthConditions.ACIDITY.get().apply(this.plant, this.currentStrength, soil.acidity()).isFertile();
							boolean nutrients = AgriCraftGrowthConditions.NUTRIENTS.get().apply(this.plant, this.currentStrength, soil.nutrients()).isFertile();
							return humidity && acidity && nutrients;
						})
						.flatMap(soil -> soil.variants().stream())
						.flatMap(variant -> TagUtils.blocks(variant.block()).stream())
						.distinct()
						.toList())
				.orElse(List.of());
		this.soil = 0;
	}

	public static class Btn extends ButtonWidget {

		private final boolean isIncrement;

		protected Btn(int x, int y, int width, int height, BooleanSupplier onPress, boolean isIncrement) {
			super(x, y, width, height, 0, 0, () -> true, (mouseX, mouseY, button) -> onPress.getAsBoolean());
			this.isIncrement = isIncrement;
		}

		private int getUOffset(boolean isHovered) {
			if (Minecraft.getInstance().mouseHandler.isLeftPressed()) {
				return 9;
			} else if (isHovered) {
				return 18;
			}
			return 0;
		}


		@Override
		public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
			int vOffset = isIncrement ? 9 : 0;
			boolean isHovered = this.x <= mouseX && mouseX < this.x + this.width && this.y <= mouseY && mouseY < this.y + this.height;
			int uOffset = this.getUOffset(isHovered);
			guiGraphics.blit(R_COMPONENTS, this.x, this.y, uOffset, vOffset, 9, 9, 128, 128);
		}

	}

}
