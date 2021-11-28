package com.infinityraider.agricraft.render.items.magnfiyingglass;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.client.IMagnifyingGlassInspector;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneCarrierItem;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.render.plant.AgriGenomeRenderer;
import com.infinityraider.agricraft.util.AnimatedScrollPosition;
import com.infinityraider.infinitylib.reference.Constants;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MagnifyingGlassGenomeInspector implements IMagnifyingGlassInspector {
    public static void init() {
        IMagnifyingGlassInspector.registerInspector(new MagnifyingGlassGenomeInspector());
    }

    private static final float GENOME_SCALE = 0.075F;
    private static final double TEXT_OFFSET = -0.025;
    private static final float TEXT_SCALE = 0.75F;
    private static final Quaternion TEXT_FLIPPER = Vector3f.ZP.rotationDegrees(180);
    private static final int SCROLL_DURATION = 10;

    private final AnimatedScrollPosition scrollPosition;
    private List<IAgriGenePair<?>> genomeCache;

    protected MagnifyingGlassGenomeInspector() {
        this.scrollPosition = new AnimatedScrollPosition(() -> SCROLL_DURATION, () -> this.genomeCache == null ? 0 : this.genomeCache.size());
    }

    public int getScrollIndex() {
        return this.scrollPosition.getIndex();
    }

    public float getScrollProgress(float partialTick) {
        return this.scrollPosition.getProgress(partialTick);
    }

    @Override
    public boolean canInspect(World world, BlockPos pos, PlayerEntity player) {
        return AgriApi.getCrop(world, pos).isPresent();
    }

    @Override
    public boolean canInspect(World world, Entity entity, PlayerEntity player) {
        if(entity.isAlive() && entity instanceof ItemEntity) {
            ItemEntity item = (ItemEntity) entity;
            return item.getItem().getItem() instanceof IAgriGeneCarrierItem;
        }
        return false;
    }

    @Override
    public void onInspectionStart(World world, BlockPos pos, PlayerEntity player) {
        this.genomeCache = AgriApi.getCrop(world, pos)
                .map(crop -> crop.getGenome().map(IAgriGenome::getGeneList).orElse(ImmutableList.of()))
                .orElse(ImmutableList.of());
    }

    @Override
    public void onInspectionStart(World world, Entity entity, PlayerEntity player) {
        ItemEntity item = (ItemEntity) entity;
        IAgriGeneCarrierItem seed = (IAgriGeneCarrierItem) item.getItem().getItem();
        this.genomeCache = seed.getGenome(item.getItem()).map(IAgriGenome::getGeneList).orElse(ImmutableList.of());
    }

    @Override
    public boolean onInspectionTick(World world, BlockPos pos, PlayerEntity player) {
        this.scrollPosition.tick();
        return this.genomeCache != null && (!this.genomeCache.isEmpty()) && this.canInspect(world, pos, player);
    }

    @Override
    public boolean onInspectionTick(World world, Entity entity, PlayerEntity player) {
        this.scrollPosition.tick();
        return this.genomeCache != null && (!this.genomeCache.isEmpty()) && this.canInspect(world, entity, player);
    }

    @Override
    public void onInspectionEnd(World world, BlockPos pos, PlayerEntity player) {
        this.scrollPosition.reset();
        this.genomeCache = null;
    }

    @Override
    public void onInspectionEnd(World world, @Nullable Entity entity, PlayerEntity player) {
        this.scrollPosition.reset();
        this.genomeCache = null;
    }

    @Override
    public void onMouseScroll(int delta) {
        this.scrollPosition.scroll(delta);
    }

    @Override
    public void doInspectionRender(MatrixStack transforms, float partialTick, @Nullable Entity entity) {
        // Check if the genome can be rendered
        List<IAgriGenePair<?>> genome = this.genomeCache;
        if(genome == null || genome.size() <= 0) {
            return;
        }
        // Render helix
        this.renderDoubleHelix(genome, transforms, partialTick);
        // Render text overlay
        this.renderTextOverlay(genome, transforms);
    }


    protected void renderDoubleHelix(List<IAgriGenePair<?>> genome, MatrixStack transforms, float partialTicks) {
        // Push matrix for helix render
        transforms.push();

        // helix dimensions
        float h = Constants.HALF;
        float r = h / 10;

        // Scale down
        transforms.scale(GENOME_SCALE, GENOME_SCALE, GENOME_SCALE);

        // Make sure the helix is centered
        transforms.translate(0, -h/2, 0);

        // Render helix
        AgriGenomeRenderer.getInstance().renderDoubleHelix(
                genome, transforms, this.getScrollIndex(), this.getScrollProgress(partialTicks), r, h, 1.0F, false);

        // Pop matrix after helix render
        transforms.pop();
    }

    protected void renderTextOverlay(List<IAgriGenePair<?>> genome, MatrixStack transforms) {
        // Push matrix for overlay render
        transforms.push();

        // Translate down
        transforms.translate(0, TEXT_OFFSET, 0);

        // Flip text
        transforms.rotate(TEXT_FLIPPER);

        // Scale down
        float width = AgriGenomeRenderer.getInstance().getScaledWindowWidth();
        float height = AgriGenomeRenderer.getInstance().getScaledWindowHeight();
        float scale = TEXT_SCALE/Math.max(width, height);
        transforms.scale(scale, scale, scale);

        // Render overlay
        int index = Math.max(0, Math.min(genome.size() - 1, this.getScrollIndex()));
        AgriGenomeRenderer.getInstance().renderTextOverlay(transforms, genome.get(index));

        // Pop matrix for overlay render
        transforms.pop();
    }
}
