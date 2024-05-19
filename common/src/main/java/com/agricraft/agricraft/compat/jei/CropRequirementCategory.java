package com.agricraft.agricraft.compat.jei;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.AgriClientApi;
import com.agricraft.agricraft.api.crop.AgriGrowthStage;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriSoilCondition;
import com.agricraft.agricraft.api.requirement.AgriGrowthConditionRegistry;
import com.agricraft.agricraft.api.requirement.AgriSeason;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.util.LangUtils;
import com.agricraft.agricraft.common.util.Platform;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.function.BooleanSupplier;

public class CropRequirementCategory implements IRecipeCategory<CropRequirementCategory.Recipe> {

	public static final ResourceLocation ID = new ResourceLocation(AgriApi.MOD_ID, "jei/requirement");
	public static final RecipeType<CropRequirementCategory.Recipe> TYPE = new RecipeType<>(ID, CropRequirementCategory.Recipe.class);
	public static final IDrawable BACKGROUND = AgriCraftJeiPlugin.createDrawable(new ResourceLocation(AgriApi.MOD_ID, "textures/gui/jei/crop_requirement.png"), 0, 0, 128, 128, 128, 128);
	public static final ResourceLocation COMPONENTS = new ResourceLocation(AgriApi.MOD_ID, "textures/gui/jei/crop_requirement_components.png");
	public static final ResourceLocation GUI_COMPONENTS = new ResourceLocation(AgriApi.MOD_ID, "textures/gui/gui_components.png");
	public static final int[] HUMIDITY_OFFSETS = {8, 8, 10, 10, 10, 7};
	public static final int[] ACIDITY_OFFSETS = {7, 8, 7, 8, 8, 8, 6};
	public static final int[] NUTRIENTS_OFFSETS = {6, 8, 9, 9, 11, 10};

	public final IDrawable icon;
	public long lastTime;

	public CropRequirementCategory(IGuiHelper helper) {
		icon = helper.createDrawableItemStack(new ItemStack(Blocks.FARMLAND));
		lastTime = System.currentTimeMillis();
	}

	@Override
	public RecipeType<CropRequirementCategory.Recipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("agricraft.gui.jei.requirement");
	}

	@Override
	public IDrawable getBackground() {
		return BACKGROUND;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CropRequirementCategory.Recipe recipe, IFocusGroup focuses) {
		// Set shapeless
		builder.setShapeless();
		// Seed
		builder.addSlot(RecipeIngredientRole.INPUT, 56, 3)
				.setSlotName("input_seed")
				.addIngredient(VanillaTypes.ITEM_STACK, AgriSeedItem.toStack(recipe.plant));
	}


	@Override
	public void draw(CropRequirementCategory.Recipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		// render buttons
		recipe.incStrButton.renderWidget(guiGraphics, (int) mouseX, (int) mouseY, 0);
		recipe.decStrButton.renderWidget(guiGraphics, (int) mouseX, (int) mouseY, 0);
		recipe.incStageButton.renderWidget(guiGraphics, (int) mouseX, (int) mouseY, 0);
		recipe.decStageButton.renderWidget(guiGraphics, (int) mouseX, (int) mouseY, 0);
		// render strength increment
		for (int i = 0; i < recipe.currentStrength; ++i) {
			guiGraphics.blit(COMPONENTS, 105, 66 - i * 5, 0, 66, 7, 3, 128, 128);
		}
		// render stage increment
		int maxHeight = 48;
		int stageHeight = (int) (maxHeight * recipe.currentStage.growthPercentage());
		int stageY = 21 + maxHeight - stageHeight;
		guiGraphics.blit(COMPONENTS, 93, stageY, 7, stageHeight, 0, 69, 7, 1, 128, 128);
		// render light levels
		for (int i = 15; i >= 0; --i) {
			boolean fertile = AgriGrowthConditionRegistry.getLight().apply(recipe.plant, recipe.currentStrength, i).isFertile();
			if (fertile) {
				guiGraphics.blit(COMPONENTS, 32, 26 + 3 * (15 - i), 3, 3, 0, 18 + 3 * (15 - i), 3, 3, 128, 128);
			}
		}
		// render soil property icons
		for (AgriSoilCondition.Humidity humidity : AgriSoilCondition.Humidity.values()) {
			if (!humidity.isValid()) {
				continue;
			}
			if (AgriGrowthConditionRegistry.getHumidity().apply(recipe.plant, recipe.currentStrength, humidity).isFertile()) {
				int index = humidity.ordinal();
				int offset = 0;
				for (int i = 0; i < index; ++i) {
					offset += HUMIDITY_OFFSETS[i];
				}
				guiGraphics.blit(GUI_COMPONENTS, 37 + offset, 83, HUMIDITY_OFFSETS[index], 12, offset, 0, HUMIDITY_OFFSETS[index], 12, 128, 128);
			}
		}
		for (AgriSoilCondition.Acidity acidity : AgriSoilCondition.Acidity.values()) {
			if (!acidity.isValid()) {
				continue;
			}
			if (AgriGrowthConditionRegistry.getAcidity().apply(recipe.plant, recipe.currentStrength, acidity).isFertile()) {
				int index = acidity.ordinal();
				int offset = 0;
				for (int i = 0; i < index; ++i) {
					offset += ACIDITY_OFFSETS[i];
				}
				guiGraphics.blit(GUI_COMPONENTS, 37 + offset, 96, ACIDITY_OFFSETS[index], 12, offset, 12, ACIDITY_OFFSETS[index], 12, 128, 128);
			}
		}
		for (AgriSoilCondition.Nutrients nutrients : AgriSoilCondition.Nutrients.values()) {
			if (!nutrients.isValid()) {
				continue;
			}
			if (AgriGrowthConditionRegistry.getNutrients().apply(recipe.plant, recipe.currentStrength, nutrients).isFertile()) {
				int index = nutrients.ordinal();
				int offset = 0;
				for (int i = 0; i < index; ++i) {
					offset += NUTRIENTS_OFFSETS[i];
				}
				guiGraphics.blit(GUI_COMPONENTS, 37 + offset, 109, NUTRIENTS_OFFSETS[index], 12, offset, 24, NUTRIENTS_OFFSETS[index], 12, 128, 128);
			}
		}
		// render seasons
		if (AgriApi.getSeasonLogic().isActive()) {
			for (AgriSeason season : AgriSeason.values()) {
				if (season == AgriSeason.ANY) {
					continue;
				}
				if (AgriGrowthConditionRegistry.getSeason().apply(recipe.plant, recipe.currentStrength, season).isFertile()) {
					guiGraphics.blit(GUI_COMPONENTS, 17, 24 + 13 * season.ordinal(), 10 * season.ordinal(), 44, 10, 12, 128, 128);
				}
			}
		}
		// render soils and plant stage
		long l = System.currentTimeMillis();
		if (lastTime + 1500 <= l && !Screen.hasShiftDown()) {  // change soil to render
			recipe.tick();
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
		if (!recipe.soils.isEmpty() && recipe.soil < recipe.soils.size()) {
			stack.pushPose();
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(recipe.soils.get(recipe.soil).defaultBlockState(), stack, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
			stack.popPose();
		}
		// render plant
		BakedModel model = AgriClientApi.getPlantModel(recipe.plantId, recipe.currentStage.index());
		stack.pushPose();
		stack.translate(0, 1, 0);
		Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(stack.last(), guiGraphics.bufferSource().getBuffer(RenderType.cutoutMipped()), null, model, 1, 1, 1, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
		stack.popPose();
		// TODO: @ketheroth display block below requirement
		bufferSource.endBatch();
		stack.popPose();
	}

	@Override
	public boolean handleInput(Recipe recipe, double mouseX, double mouseY, InputConstants.Key input) {
		if (input.getType() == InputConstants.Type.MOUSE) {
			int mouseButton = input.getValue();
			if (mouseButton == 0) {
				if (recipe.incStrButton.isMouseOver(mouseX, mouseY)) {
					recipe.incStrButton.onPress();
					return true;
				}
				if (recipe.decStrButton.isMouseOver(mouseX, mouseY)) {
					recipe.decStrButton.onPress();
					return true;
				}
				if (recipe.incStageButton.isMouseOver(mouseX, mouseY)) {
					recipe.incStageButton.onPress();
					return true;
				}
				if (recipe.decStageButton.isMouseOver(mouseX, mouseY)) {
					recipe.decStageButton.onPress();
					return true;
				}
			}
		}
		return IRecipeCategory.super.handleInput(recipe, mouseX, mouseY, input);
	}

	@Override
	public List<Component> getTooltipStrings(Recipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		if (104 <= mouseX && mouseX <= 113 && 20 <= mouseY && mouseY <= 70) {
			return List.of(Component.translatable("agricraft.tooltip.jei.strength", recipe.currentStrength));
		}
		if (92 <= mouseX && mouseX <= 101 && 20 <= mouseY && mouseY <= 70) {
			return List.of(Component.translatable("agricraft.tooltip.jei.stage", recipe.currentStage.index()));
		}
		if (32 <= mouseX && mouseX <= 35 && 26 <= mouseY && mouseY <= 73) {
			int light = 15 - ((int) (mouseY - 26) / 3);
			return List.of(Component.translatable("agricraft.tooltip.jei.light", light));
		}
		if (83 <= mouseY && mouseY <= 95) {
			int offset = 0;
			for (int i = 0; i < HUMIDITY_OFFSETS.length; i++) {
				if (37 + offset <= mouseX && mouseX <= 37 + offset + HUMIDITY_OFFSETS[i]) {
					AgriSoilCondition.Humidity humidity = AgriSoilCondition.Humidity.values()[i];
					return List.of(Component.translatable("agricraft.soil.humidity." + humidity.name().toLowerCase()));
				}
				offset += HUMIDITY_OFFSETS[i];
			}
		}
		if (96 <= mouseY && mouseY <= 108) {
			int offset = 0;
			for (int i = 0; i < ACIDITY_OFFSETS.length; i++) {
				if (37 + offset <= mouseX && mouseX <= 37 + offset + ACIDITY_OFFSETS[i]) {
					AgriSoilCondition.Acidity acidity = AgriSoilCondition.Acidity.values()[i];
					return List.of(Component.translatable("agricraft.soil.acidity." + acidity.name().toLowerCase()));
				}
				offset += ACIDITY_OFFSETS[i];
			}
		}
		if (109 <= mouseY && mouseY <= 121) {
			int offset = 0;
			for (int i = 0; i < NUTRIENTS_OFFSETS.length; i++) {
				if (37 + offset <= mouseX && mouseX <= 37 + offset + NUTRIENTS_OFFSETS[i]) {
					AgriSoilCondition.Nutrients nutrients = AgriSoilCondition.Nutrients.values()[i];
					return List.of(Component.translatable("agricraft.soil.nutrients." + nutrients.name().toLowerCase()));
				}
				offset += NUTRIENTS_OFFSETS[i];
			}
		}
		if (50 <= mouseX && mouseX <= 76 && 34 <= mouseY && mouseY <= 58) {
			Component desc = LangUtils.plantDescription(recipe.plantId);
			return desc == null ? List.of(LangUtils.plantName(recipe.plantId)) : List.of(LangUtils.plantName(recipe.plantId), desc);
		}
		if (50 <= mouseX && mouseX <= 76 && 58 <= mouseY && mouseY <= 74) {
			return Screen.getTooltipFromItem(Minecraft.getInstance(), new ItemStack(recipe.soils.get(recipe.soil)));
		}
		if (AgriApi.getSeasonLogic().isActive()) {
			if (17 <= mouseX && mouseX <= 29 && 24 <= mouseY && mouseY <= 36) {
				return List.of(LangUtils.seasonName(AgriSeason.SPRING));
			}
			if (17 <= mouseX && mouseX <= 29 && 37 <= mouseY && mouseY <= 49) {
				return List.of(LangUtils.seasonName(AgriSeason.SUMMER));
			}
			if (17 <= mouseX && mouseX <= 29 && 50 <= mouseY && mouseY <= 62) {
				return List.of(LangUtils.seasonName(AgriSeason.AUTUMN));
			}
			if (17 <= mouseX && mouseX <= 29 && 63 <= mouseY && mouseY <= 74) {
				return List.of(LangUtils.seasonName(AgriSeason.WINTER));
			}
		}
		return IRecipeCategory.super.getTooltipStrings(recipe, recipeSlotsView, mouseX, mouseY);
	}

	public static class Recipe {

		private final AgriPlant plant;
		private final String plantId;
		private final Btn incStrButton;
		private final Btn decStrButton;
		private final Btn incStageButton;
		private final Btn decStageButton;
		private int currentStrength = AgriApi.getStatRegistry().strengthStat().getMin();
		private AgriGrowthStage currentStage;
		private List<Block> soils;
		private int soil;

		public Recipe(AgriPlant plant) {
			this.plant = plant;
			this.plantId = AgriApi.getPlantId(plant).map(ResourceLocation::toString).orElse("");
			this.currentStage = plant.getInitialGrowthStage();
			this.incStrButton = new Btn(104, 10, 9, 9, this::incrementStrength, true);
			this.decStrButton = new Btn(104, 71, 9, 9, this::decrementStrength, false);
			this.incStageButton = new Btn(92, 10, 9, 9, this::incrementStage, true);
			this.decStageButton = new Btn(92, 71, 9, 9, this::decrementStage, false);
			this.updateSoils();
		}

		public boolean incrementStrength() {
			this.currentStrength = Math.min(AgriApi.getStatRegistry().strengthStat().getMax(), currentStrength + 1);
			this.updateSoils();
			return true;
		}

		public boolean decrementStrength() {
			this.currentStrength = Math.max(AgriApi.getStatRegistry().strengthStat().getMin(), currentStrength - 1);
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
			this.soils = AgriApi.getSoilRegistry().map(registry -> registry.stream().filter(soil -> {
								boolean humidity = AgriGrowthConditionRegistry.getHumidity().apply(this.plant, this.currentStrength, soil.humidity()).isFertile();
								boolean acidity = AgriGrowthConditionRegistry.getAcidity().apply(this.plant, this.currentStrength, soil.acidity()).isFertile();
								boolean nutrients = AgriGrowthConditionRegistry.getNutrients().apply(this.plant, this.currentStrength, soil.nutrients()).isFertile();
								return humidity && acidity && nutrients;
							})
							.flatMap(soil -> soil.variants().stream())
							.flatMap(variant -> Platform.get().getBlocksFromLocation(variant.block()).stream())
							.distinct()
							.toList())
					.orElse(List.of());
			this.soil = 0;
		}

		public void tick() {
			this.soil++;
			if (this.soil >= this.soils.size()) {
				this.soil = 0;
			}
		}

	}

	public static class Btn extends AbstractButton {

		private final boolean isIncrement;
		private final BooleanSupplier onPress;

		protected Btn(int x, int y, int width, int height, BooleanSupplier onPress, boolean isIncrement) {
			super(x, y, width, height, Component.empty());
			this.isIncrement = isIncrement;
			this.onPress = onPress;
		}

		@Override
		public void onPress() {
			onPress.getAsBoolean();
		}

		@Override
		protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			int vOffset = isIncrement ? 9 : 0;
			int uOffset = this.getUOffset();
			this.isHovered = this.getX() <= mouseX && mouseX < this.getX() + this.getWidth() && this.getY() <= mouseY && mouseY < this.getY() + this.getHeight();
			guiGraphics.blit(COMPONENTS, this.getX(), this.getY(), uOffset, vOffset, 9, 9, 128, 128);
		}

		private int getUOffset() {
			if (Minecraft.getInstance().mouseHandler.isLeftPressed()) {
				return 9;
			} else if (this.isHovered) {
				return 18;
			}
			return 0;
		}

		@Override
		protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
			this.defaultButtonNarrationText(narrationElementOutput);
		}

	}

}
