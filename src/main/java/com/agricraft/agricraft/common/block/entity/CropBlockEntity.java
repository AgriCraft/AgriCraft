package com.agricraft.agricraft.common.block.entity;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.LangUtils;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.config.AgriCraftConfig;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.crop.AgriGrowthStage;
import com.agricraft.agricraft.api.fertilizer.AgriFertilizer;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.api.genetic.Chromosome;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.plant.AgriWeed;
import com.agricraft.agricraft.api.registries.AgriCraftStats;
import com.agricraft.agricraft.api.requirement.AgriGrowthResponse;
import com.agricraft.agricraft.api.tools.magnifying.MagnifyingInspectable;
import com.agricraft.agricraft.common.block.CropBlock;
import com.agricraft.agricraft.common.block.CropState;
import com.agricraft.agricraft.common.registry.AgriBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CropBlockEntity extends BlockEntity implements AgriCrop, MagnifyingInspectable {

	private final Map<Integer, VoxelShape> shapeByAge = new HashMap<>();
	private AgriGenome genome;
	private Holder<AgriPlant> plant;
	private AgriGrowthStage growthStage = null;
	private Holder<AgriWeed> weed;
	private AgriGrowthStage weedGrowthStage = null;

	public CropBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(AgriBlockEntities.CROP.get(), blockPos, blockState);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if (tag.contains("genome")) {
			AgriGenome.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("genome"))
					.resultOrPartial(message -> AgriCraft.LOGGER.error("Failed to parse crop genome: '{}'", message))
					.ifPresent(genome -> this.genome = genome);
			if (this.genome != null) {
				ResourceLocation location = ResourceLocation.parse(this.genome.species().trait());
				this.plant = AgriApi.get().getPlantRegistry((RegistryAccess) registries).flatMap(lookup -> lookup.getHolder(location)).orElse(null);
				int[] growth = tag.getIntArray("growth");
				this.growthStage = new AgriGrowthStage(growth[0], growth[1]);
			}
		}
		if (tag.contains("weed")) {
			ResourceLocation location = ResourceLocation.tryParse(tag.getString("weed"));
			if (location != null) {
				this.weed = AgriApi.get().getWeedRegistry((RegistryAccess) registries).flatMap(lookup -> lookup.getHolder(location)).orElse(null);
			}
			int[] growth = tag.getIntArray("weed_growth");
			this.weedGrowthStage = new AgriGrowthStage(growth[0], growth[1]);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (this.genome != null) {
			tag.put("genome", AgriGenome.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), this.genome).getOrThrow());
			tag.putIntArray("growth", new int[]{this.growthStage.index(), this.growthStage.total()});
		}
		if (this.weed != null) {
			this.weed.unwrapKey().ifPresent(key -> tag.putString("weed", key.location().toString()));
			tag.putIntArray("weed_growth", new int[]{this.weedGrowthStage.index(), this.weedGrowthStage.total()});
		}
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return this.saveWithoutMetadata(registries);
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public boolean removeGenome() {
		if (this.genome != null) {
			if (this.getBlockState().getValue(CropBlock.CROP_STATE) == CropState.PLANT_STICKS) {
				this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(CropBlock.CROP_STATE, CropState.SINGLE_STICKS));
			} else {
				if (this.getBlockState().getValue(BlockStateProperties.WATERLOGGED)) {
					level.setBlockAndUpdate(this.getBlockPos(), Fluids.WATER.defaultFluidState().createLegacyBlock());
				} else {
					level.setBlockAndUpdate(this.getBlockPos(), Blocks.AIR.defaultBlockState());
				}
			}
			this.getPlant().onRemoved(this);
			this.plant = null;
			this.growthStage = null;
			this.genome = null;
			return true;
		}
		return false;
	}

	@Override
	public AgriGenome getGenome() {
		return genome;
	}

	@Override
	public void plantGenome(AgriGenome genome, @Nullable LivingEntity entity) {
		if (genome == null) {
			return;
		}
		this.genome = genome;
		AgriApi.get().getPlantRegistry(this.level.registryAccess())
				.flatMap(reg -> reg.getHolder(ResourceLocation.parse(genome.species().trait())))
				.ifPresent(holder -> this.plant = holder);
		if (this.plant != null) {
			this.growthStage = this.plant.value().getInitialGrowthStage();
		}
		level.setBlockAndUpdate(this.getBlockPos(), this.hasCropSticks() ?
				this.getBlockState().setValue(CropBlock.CROP_STATE, CropState.PLANT_STICKS).setValue(CropBlock.LIGHT, this.plant.value().getBrightness(this))
				: this.getBlockState().setValue(CropBlock.LIGHT, this.plant.value().getBrightness(this)));
		this.plant.value().onPlanted(this, null);
	}

	@Override
	public boolean hasPlant() {
		return this.getBlockState().getValue(CropBlock.CROP_STATE).hasPlant() && this.genome != null;
	}

	@Override
	public boolean hasWeeds() {
		return this.weed != null;
	}

	@Override
	public boolean hasCropSticks() {
		return this.getBlockState().getValue(CropBlock.CROP_STATE).hasSticks();
	}

	@Override
	public boolean isCrossCropSticks() {
		return this.getBlockState().getValue(CropBlock.CROP_STATE) == CropState.DOUBLE_STICKS;
	}

	@Override
	public ResourceLocation getPlantId() {
		return this.plant.unwrapKey().orElseThrow().location();
	}

	@Override
	public AgriPlant getPlant() {
		return this.plant.value();
	}

	@Override
	public AgriGrowthStage getGrowthStage() {
		return this.growthStage;
	}

	@Override
	public ResourceLocation getWeedId() {
		return this.weed.unwrapKey().orElseThrow().location();
	}

	@Override
	public AgriWeed getWeed() {
		return this.weed.value();
	}

	@Override
	public AgriGrowthStage getWeedGrowthStage() {
		return this.weedGrowthStage;
	}

	@Override
	public void setGrowthStage(AgriGrowthStage stage) {
		this.growthStage = stage;
		this.setChanged();
		this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState().setValue(CropBlock.LIGHT, this.plant.value().getBrightness(this)), Block.UPDATE_ALL);
	}

	@Override
	public void setWeedGrowthStage(AgriGrowthStage stage) {
		this.weedGrowthStage = stage;
		this.setChanged();
		this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.hasPlant() ? this.getBlockState().setValue(CropBlock.LIGHT, this.plant.value().getBrightness(this)) : this.getBlockState(), Block.UPDATE_ALL);
	}

	@Override
	public void removeWeeds() {
		if (this.weed != null) {
			this.weed = null;
			this.weedGrowthStage = null;
			this.setChanged();
			this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.hasPlant() ? this.getBlockState().setValue(CropBlock.LIGHT, this.plant.value().getBrightness(this)) : this.getBlockState(), Block.UPDATE_ALL);
		}
	}

	@Override
	public AgriGrowthResponse getFertilityResponse() {
		// TODO: should we cache the fertility response ?
		// check growth space
		if (!this.checkGrowthSpace(this.plant.value().getPlantHeight(this.growthStage))) {
			return AgriGrowthResponse.INFERTILE;
		}
		// if there is no condition registered
		if (AgriApi.get().getGrowthConditionRegistry().size() <= 0) {
			return AgriGrowthResponse.FERTILE;
		}
		// check every growth condition registered and take the one with the highest priority
		int strength = this.genome.getStatChromosome(AgriCraftStats.STRENGTH.get()).trait();
		Optional<AgriGrowthResponse> optional = AgriApi.get().getGrowthConditionRegistry().stream()
				.map(condition -> condition.check(this, this.level, this.getBlockPos(), strength))
				.reduce((result, element) -> result.priority() >= element.priority() ? result : element);
		return optional.orElse(AgriGrowthResponse.FERTILE);
	}

	@Override
	public Optional<AgriSoil> getSoil() {
		return AgriApi.get().getSoil(this.level, getBlockPos().below(), this.level.registryAccess());
	}

	/**
	 * @return the block shape of the crop
	 */
	public VoxelShape getShape() {
		double yoffset = 0;
		if (level.getBlockState(this.getBlockPos().below()).is(Blocks.FARMLAND)) {
			// TODO: should we change this to allow other soil to offsets the plant shape ?
			yoffset = -1.0D / 16.0D;
		}
		if (!this.hasPlant()) {
			if (this.hasWeeds()) {
				return shapeByAge.computeIfAbsent(this.weedGrowthStage.index(), stage -> Block.box(0, 0, 0, 16, this.weed.value().getWeedHeight(this.weedGrowthStage), 16)).move(0, yoffset, 0);
			}
			return Shapes.empty();
		}
		return shapeByAge.computeIfAbsent(this.growthStage.index(), stage -> Block.box(0, 0, 0, 16, this.plant.value().getPlantHeight(this.growthStage), 16)).move(0, yoffset, 0);
	}

	/**
	 * Compute the harvest products for the crop
	 *
	 * @param addToHarvest consumer to add the products to
	 */
	public void getHarvestProducts(Consumer<ItemStack> addToHarvest) {
		if (!this.hasPlant() || !this.isFullyGrown()) {
			return;
		}
		for (int trials = (this.genome.gain().trait() + 3) / 3; trials > 0; --trials) {
			this.plant.value().getHarvestProducts(addToHarvest, this.growthStage, this.genome, this.level.random);
		}
	}

	@Override
	public void getClippingProducts(Consumer<ItemStack> addToClipping, ItemStack clipper) {
		if (!this.hasPlant() || !this.isFullyGrown()) {
			return;
		}
		this.plant.value().getClipProducts(addToClipping, clipper, this.growthStage, this.genome, this.level.random);
	}

	@Override
	public boolean acceptsFertilizer(AgriFertilizer fertilizer) {
		if (this.isCrossCropSticks()) {
			return AgriCraftConfig.ALLOW_FERTILIZER_MUTATION.get() && fertilizer.canTriggerMutation();
		} else if (this.hasPlant()) {
			return !this.isFullyGrown() && fertilizer.canFertilize(this);
		} else {
			return fertilizer.canTriggerWeeds();
		}
	}

	/**
	 * Apply a growth tick to the crop (plant+weed)
	 */
	@Override
	public void applyGrowthTick() {
		if (this.level == null || this.level.isClientSide()) {
			return;
		}
		if (this.shouldWeedsActivate()) {
			this.executeWeedsGrowthTick();
		} else if (this.getBlockState().getValue(CropBlock.CROP_STATE) == CropState.DOUBLE_STICKS) {
			// mutation tick
			AgriApi.get().getMutationHandler().getCrossBreedEngine().handleCrossBreedTick(this, this.streamNeighbours(), this.level.random);
		} else {
			if (!this.hasPlant()) {
				return;
			}
			AgriGrowthResponse fertility = this.getFertilityResponse();
			if (fertility.isInstantKill()) {
				// kill plant
				fertility.onPlantKilled(this);
				this.removeGenome();
			} else if (fertility.isLethal()) {
				// reverse growth stage
				this.revertGrowthStage();
			} else if (fertility.isFertile()) {
				// plant growth tick
				this.executePlantGrowthTick();
			}
		}
	}

	protected void executeWeedsGrowthTick() {
			if (!this.hasWeeds()) {
				//There aren't weeds yet, try to spawn new weeds
				AgriApi.get().getWeedRegistry().flatMap(registry -> registry.entrySet().stream()
						.filter(entry -> this.level.getRandom().nextDouble() < entry.getValue().getSpawnChance(this))
						.findAny()).ifPresent(entry -> this.setWeed(entry.getKey()));
			} else {
				// There are weeds already, apply the growth tick
				if (this.weedGrowthStage.isFinal()) {
					//Weeds are mature, try killing the plant
					this.tryWeedKillPlant();
					//Weeds are mature, try spreading
					this.spreadWeeds();
				} else {
					// Weeds are not mature yet, increment their growth
					double f = this.getSoil().map(AgriSoil::growthModifier).orElse(1.0D);
					if (this.level.getRandom().nextDouble() < f * this.weed.value().getGrowthChance(this.weedGrowthStage)) {
						this.weedGrowthStage = this.weedGrowthStage.getNext(this, this.level.getRandom());
						this.setChanged();
						this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.hasPlant()?this.getBlockState().setValue(CropBlock.LIGHT, this.plant.value().getBrightness(this)):this.getBlockState(), Block.UPDATE_ALL);
					}
				}
			}
	}

	@Override
	public void setWeed(ResourceKey<AgriWeed> weed) {
		this.level.registryAccess().registry(AgriWeed.REGISTRY_KEY).flatMap(reg -> reg.getHolder(weed)).ifPresent(holder -> this.weed = holder);
		if (this.weed != null && this.checkGrowthSpace(this.weed.value().getWeedHeight(this.weed.value().getInitialGrowthStage()))) {
			this.weedGrowthStage = this.weed.value().getInitialGrowthStage();
			this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.hasPlant()?this.getBlockState().setValue(CropBlock.LIGHT, this.plant.value().getBrightness(this)):this.getBlockState(), Block.UPDATE_ALL);
		}
	}
	protected void tryWeedKillPlant() {
		if (AgriCraftConfig.MATURE_WEEDS_KILL_PLANTS.get() && this.weed.value().isLethal()
				&& this.hasPlant() && this.shouldWeedsActivate()) {
			this.revertGrowthStage();
		}
	}
	protected void spreadWeeds() {
		if (AgriCraftConfig.WEEDS_SPREADING.get() && this.weed.value().isAggressive()) {
			this.streamNeighbours().filter(crop -> !crop.hasWeeds())
					.filter(AgriCrop::shouldWeedsActivate)
					.forEach(crop -> crop.setWeed(this.weed.unwrapKey().orElseThrow()));
		}
	}

	/**
	 * Check if the plant/weed has enough space to grow
	 * @param height the height of the plant (in 1/16th of a block)
	 * @return true if it can grow
	 */
	public boolean checkGrowthSpace(int height) {
		if (level == null) {
			return false;
		}
		while (height > 16) {
			int offset = height / 16;
			BlockPos up = this.getBlockPos().above(offset);
			if (!level.getBlockState(up).isAir()) {
				return false;
			}
			height -= 16;
		}
		return true;
	}

	public Stream<AgriCrop> streamNeighbours() {
		return Direction.Plane.HORIZONTAL.stream()
				.map(dir -> this.getBlockPos().relative(dir))
				.map(pos -> AgriApi.get().getCrop(this.level, pos))
				.filter(Optional::isPresent)
				.map(Optional::get);
	}

	protected void revertGrowthStage() {
		AgriGrowthStage current = this.growthStage;
		AgriGrowthStage prev = this.growthStage.getPrevious(this, this.level.random);
		if (current.equals(prev)) {
			this.removeGenome();
		} else {
			this.setGrowthStage(prev);
		}
	}

	/**
	 * Apply a growth tick to the plant
	 */
	protected void executePlantGrowthTick() {
		if (this.isFullyGrown()) {
			return;
		}
		double a = this.calculateGrowthRate();
		double b = this.level.random.nextDouble();
		if (a > b) {
			this.setGrowthStage(this.growthStage.getNext(this, this.level.random));
			this.getPlant().onGrowth(this);
		}
	}

	protected double calculateGrowthRate() {
		int growth = this.genome.getStatChromosome(AgriCraftStats.GROWTH.get()).trait();
		double soilFactor = this.getSoil().map(AgriSoil::growthModifier).orElse(1.0D);
		return soilFactor * (this.plant.value().getGrowthChance(this.growthStage) + growth * this.plant.value().getBonusGrowthChance(this.growthStage) * AgriCraftConfig.GROWTH_MULTIPLIER.get());
	}

	@Override
	public void addMagnifyingTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		if (this.hasPlant()) {
			tooltip.add(Component.translatable("agricraft.tooltip.magnifying.crop"));
			// crop species
			tooltip.add(Component.literal("  ").plainCopy().append(Component.translatable("agricraft.tooltip.magnifying.species"))
					.append(LangUtils.plantName(genome.species().dominant()))
					.append(Component.literal(" - ").plainCopy())
					.append(LangUtils.plantName(genome.species().recessive()))
			);
			// crop stats
			AgriApi.get().getStatRegistry().holders()
					.filter(stat -> !stat.value().isHidden())
					.sorted(Comparator.comparing(p -> p.key().location()))
					.forEach(holder -> {
						Chromosome<Integer> stat = this.genome.getStatChromosome(holder.value());
						tooltip.add(Component.literal("  ").append(Component.translatable("agricraft.tooltip.magnifying.stat." + holder.key().location().getPath(), stat.dominant(), stat.recessive())));
					});
			if (isPlayerSneaking) {
				tooltip.add(Component.literal("  ").append(Component.translatable("agricraft.tooltip.magnifying.growth", this.growthStage.index() + 1, this.growthStage.total())));
			}
			// TODO: @unilock is this still necessary?
			if (this.level.isClientSide) {
				// somehow the sky brightness is not updated on tick on the client level
				this.level.updateSkyBrightness();
			}
			// crop fertility
			AgriGrowthResponse response = this.getFertilityResponse();
			tooltip.add(Component.literal("  ").append(Component.translatable("agricraft.tooltip.magnifying.requirement." + (response.isLethal() ? "lethal" : response.isFertile() ? "fertile" : "not_fertile"))));
			if (!response.isFertile()) {
				// crop conditions
				if (!this.checkGrowthSpace(this.plant.value().getPlantHeight(this.growthStage))) {
					tooltip.add(Component.literal("  ").append(Component.translatable("agricraft.tooltip.condition.growth_space")));
				}
				int strength = this.genome.getStrength().trait();
				AgriApi.get().getGrowthConditionRegistry().stream()
						.filter(condition -> !condition.check(this, this.level, this.getBlockPos(), strength).isFertile())
						.forEach(condition -> condition.notMetDescription(component ->tooltip.add(Component.literal("  ").append(component))));
			}
		} else {
			tooltip.add(Component.translatable("agricraft.tooltip.magnifying.no_plant"));
		}
		// weeds
		if (this.hasWeeds()) {
			tooltip.add(Component.translatable("agricraft.tooltip.magnifying.weeds").append(LangUtils.weedName(this.weed.unwrapKey().orElseThrow().location().toString())));
			if (isPlayerSneaking) {
				tooltip.add(Component.literal("  ").append(Component.translatable("agricraft.tooltip.magnifying.growth", this.weedGrowthStage.index() + 1, this.weedGrowthStage.total())));
			}
		}
	}

}
