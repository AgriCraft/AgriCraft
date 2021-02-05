package com.infinityraider.agricraft.render.overlay;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.items.IAgriSeedItem;
import com.infinityraider.agricraft.content.core.TileEntitySeedAnalyzer;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class SeedAnalyzerOverlayRenderer implements IRenderUtilities {
    private static final SeedAnalyzerOverlayRenderer INSTANCE = new SeedAnalyzerOverlayRenderer();

    public static SeedAnalyzerOverlayRenderer getInstance() {
        return INSTANCE;
    }

    private volatile IAgriGenome genome;
    private int scrollIndex = 0;
    private BlockPos lastTarget;

    private SeedAnalyzerOverlayRenderer() {}

    public boolean isActive() {
        return this.getGenome() != null;
    }

    @Nullable
    protected IAgriGenome getGenome() {
        return this.genome;
    }

    protected void setGenome(@Nullable IAgriGenome genome) {
        this.genome = genome;
    }

    protected int getScrollIndex() {
        return this.scrollIndex;
    }

    protected void reset() {
        this.setGenome(null);
        this.scrollIndex = 0;
        this.lastTarget = null;
    }

    public void onSeedAnalyzerUpdate(BlockPos pos) {
        if(this.lastTarget.equals(pos)) {
            this.reset();
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(!(Minecraft.getInstance().objectMouseOver instanceof BlockRayTraceResult)) {
            this.reset();
            return;
        }
        BlockRayTraceResult target = (BlockRayTraceResult) Minecraft.getInstance().objectMouseOver;
        if(this.lastTarget == null || !this.lastTarget.equals(target.getPos())) {
            this.reset();
            this.lastTarget = target.getPos();
            TileEntity tile = AgriCraft.instance.getClientWorld().getTileEntity(target.getPos());
            if(!(tile instanceof TileEntitySeedAnalyzer)) {
                return;
            }
            TileEntitySeedAnalyzer analyzer = (TileEntitySeedAnalyzer) tile;
            if(!analyzer.hasSeed() || !(analyzer.getSeed().getItem() instanceof IAgriSeedItem)) {
                return;
            }
            ItemStack stack = analyzer.getSeed();
            IAgriSeedItem seed = (IAgriSeedItem) stack.getItem();
            seed.getGenome(stack).ifPresent(this::setGenome);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onMouseScroll(InputEvent.MouseScrollEvent event) {
        if(this.isActive()) {
            int min = 0;
            int max = AgriApi.getGeneRegistry().all().size();
            this.scrollIndex = Math.min(max, Math.max(min, this.scrollIndex - (int) event.getScrollDelta()));
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void renderOverlay(RenderGameOverlayEvent.Pre event) {
        // Only run logic for the ALL element type
        if(event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        // Store the genome in a temporary variable here as it might become null
        IAgriGenome genome = this.getGenome();
        if(genome == null) {
            return;
        }
        // Draw the overlay
        this.drawOverlay(event.getMatrixStack(), genome, this.getScrollIndex());
    }

    protected void drawOverlay(MatrixStack transforms, IAgriGenome genome, int index) {
        this.preRender(transforms,0.5);

        //TODO: render the actual overlay

        this.postRender(transforms);
    }

    protected void preRender(MatrixStack transforms, double f) {
        transforms.push();
        this.configureRenderSystem(f);
    }

    protected void postRender(MatrixStack transforms) {
        this.configureRenderSystem(1);
        transforms.pop();
    }

    @SuppressWarnings("deprecation")
    protected void configureRenderSystem(double scale) {
        double w = scale * scale*Minecraft.getInstance().getMainWindow().getScaledWidth();
        double h = scale * Minecraft.getInstance().getMainWindow().getScaledHeight();
        RenderSystem.clear(256, true);
        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0.0D, w, h, 0.0D, 1000.0D, 3000.0D);
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        RenderSystem.loadIdentity();
        RenderSystem.translatef(0.0F, 0.0F, -2000.0F);

    }
}
