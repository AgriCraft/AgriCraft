package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.api.v1.items.IAgriSeedItem;
import com.infinityraider.agricraft.content.core.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.render.plant.AgriGenomeRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class SeedAnalyzerGenomeOverlayHandler {
    /** Overlay dimensions */
    public static double DEFAULT_WIDTH = 0.25;
    public static double DEFAULT_HEIGHT = 0.7;
    public static double DEFAULT_OFFSET_X = 0.65;
    public static double DEFAULT_OFFSET_Y = 0;

    /** Dominant helix color */
    public static final float D_R = 0.75F;
    public static final float D_G = 0.0F;
    public static final float D_B = 0.5F;
    public static final float D_A = 1.0F;
    public static final Vector4f COLOR_DOMINANT = new Vector4f(D_R, D_G, D_B, D_A);

    /** Recessive helix color */
    public static final float R_R = 0.5F;
    public static final float R_G = 0.0F;
    public static final float R_B = 0.75F;
    public static final float R_A = 1.0F;
    public static final Vector4f COLOR_RECESSIVE = new Vector4f(R_R, R_G, R_B, R_A);

    private static final SeedAnalyzerGenomeOverlayHandler INSTANCE = new SeedAnalyzerGenomeOverlayHandler();

    public static SeedAnalyzerGenomeOverlayHandler getInstance() {
        return INSTANCE;
    }

    /** The genome renderer */
    private AgriGenomeRenderer renderer;

    /** The width and height for rendering the overlay, as percentage of the screen size */
    private double width;
    private double height;

    /** The offset from the center of the screen for rendering the overlay, as percentage of the screen size */
    private double offsetX;
    private double offsetY;

    /** Smooth scroll progress tracker */
    private int scrollDuration;
    private final AnimatedScrollPosition scrollPosition;

    /** Volatile fields caching the current genome being rendered and the current position being looked at */
    private volatile List<IAgriGenePair<?>> genome; //use gene pairs to avoid rebuilding this list every render tick
    private volatile BlockPos lastTarget;

    private SeedAnalyzerGenomeOverlayHandler() {
        this.setRenderer(AgriGenomeRenderer.getInstance());
        this.setScrollDuration(10);
        this.scrollPosition = new AnimatedScrollPosition(this::getScrollDuration, () -> AgriApi.getGeneRegistry().count());
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setOffset(DEFAULT_OFFSET_X, DEFAULT_OFFSET_Y);
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getOffsetX() {
        return this.offsetX;
    }

    public double getOffsetY() {
        return this.offsetY;
    }

    public void setOffset(double x, double y) {
        this.offsetX = x;
        this.offsetY = y;
    }

    public int getScrollDuration() {
        return this.scrollDuration;
    }

    public void setScrollDuration(int duration) {
        this.scrollDuration = duration;
    }

    public AnimatedScrollPosition getScrollPosition() {
        return this.scrollPosition;
    }

    public AgriGenomeRenderer getRenderer() {
        return this.renderer;
    }

    public void setRenderer(AgriGenomeRenderer renderer) {
        this.renderer = renderer;
    }

    public boolean isActive() {
        return this.getGenome() != null;
    }

    @Nullable
    protected List<IAgriGenePair<?>> getGenome() {
        return this.genome;
    }

    protected void setGenome(@Nullable List<IAgriGenePair<?>> genome) {
        this.genome = genome;
    }

    protected int getScrollIndex() {
        return this.getScrollPosition().getIndex();
    }

    protected float getScrollProgress() {
        return this.getScrollPosition().getProgress();
    }

    protected void reset() {
        this.scrollPosition.reset();
        this.setGenome(null);
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
        // Check if the player is looking at a block
        if(!(Minecraft.getInstance().objectMouseOver instanceof BlockRayTraceResult)) {
            // If not, reset and return
            this.reset();
            return;
        }
        // Tick the scroll position to increment its animation timer
        this.getScrollPosition().tick();
        // Fetch the player's target
        BlockRayTraceResult target = (BlockRayTraceResult) Minecraft.getInstance().objectMouseOver;
        // If the player's target is the same as before, nothing needs to change
        if(this.lastTarget == null || !this.lastTarget.equals(target.getPos())) {
            // The player is observing a new position, reset the cached data
            this.reset();
            // Set the new target
            this.lastTarget = target.getPos();
            // Check if the new target is a seed analyzer with a valid seed
            TileEntity tile = AgriCraft.instance.getClientWorld().getTileEntity(target.getPos());
            if (!(tile instanceof TileEntitySeedAnalyzer)) {
                return;
            }
            TileEntitySeedAnalyzer analyzer = (TileEntitySeedAnalyzer) tile;
            if (!analyzer.hasSeed() || !(analyzer.getSeed().getItem() instanceof IAgriSeedItem)) {
                return;
            }
            // Fetch and cache the genome
            ItemStack stack = analyzer.getSeed();
            IAgriSeedItem seed = (IAgriSeedItem) stack.getItem();
            seed.getGenome(stack).ifPresent(genome -> this.setGenome(
                    AgriApi.getGeneRegistry().stream().map(genome::getGenePair).collect(Collectors.toList()))
            );
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onMouseScroll(InputEvent.MouseScrollEvent event) {
        if(this.isActive()) {
            // Tell the scroll animation object that the player has scrolled
            this.getScrollPosition().scroll((int) event.getScrollDelta());
            // If this is active, we do not want any other scroll behaviour
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void renderOverlay(RenderGameOverlayEvent.Pre event) {
        // Only run logic for the ALL element type
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        // Store the genome in a temporary variable here as it might be changed by a different thread
        List<IAgriGenePair<?>> genome = this.getGenome();
        if (genome == null) {
            // If the genome is null, we do not need to render anything
            return;
        }
        // Fetch position and dimensions
        int width = this.getRenderWidth();
        int height = this.getRenderHeight();
        int x = this.getRenderPosX();
        int y = this.getRenderPosY();
        // Translate to desired position
        event.getMatrixStack().push();
        event.getMatrixStack().translate(x, y, 0);
        // Draw the overlay
        this.getRenderer().renderGenome(
                genome, event.getMatrixStack(),
                this.getScrollIndex(), this.getScrollProgress(),
                (width + 0.0F)/2, height,
                COLOR_DOMINANT, COLOR_RECESSIVE);
        // Pop the transformation we did
        event.getMatrixStack().pop();
    }

    protected int getRenderWidth() {
        return (int) (this.getWidth() * this.getRenderer().getScaledWindowWidth());
    }

    protected int getRenderHeight() {
        return (int) (this.getHeight() * this.getRenderer().getScaledWindowHeight());
    }

    protected int getRenderPosX() {
        return ((int) ((2*this.getOffsetX() + 1) * this.getRenderer().getScaledWindowWidth()) - this.getRenderWidth())/2;
    }

    protected int getRenderPosY() {
        return ((int) ((2*this.getOffsetY() + 1) * this.getRenderer().getScaledWindowHeight()) - this.getRenderHeight())/2;
    }

    /**
     * Utility class which keeps track of the scroll position, and allows to interpolate for smooth transitions
     */
    public static class AnimatedScrollPosition {
        private final IntSupplier durationSupplier;
        private final IntSupplier maxSupplier;

        private int current;
        private int target;
        private int counter;

        public AnimatedScrollPosition(IntSupplier durationSupplier, IntSupplier maxSupplier) {
            this.durationSupplier = durationSupplier;
            this.maxSupplier = maxSupplier;
            this.reset();
        }

        public int getIndex() {
            return this.current;
        }

        public float getProgress() {
            return (this.target > this.current ? 1 : -1) * ((this.counter + 0.0F) / this.getDuration());
        }

        public int getDuration() {
            return this.durationSupplier.getAsInt();
        }

        public int getMax() {
            return this.maxSupplier.getAsInt();
        }

        public void tick() {
            if(this.current != this.target) {
                this.counter += 1;
                if(counter >= this.getDuration()) {
                    this.counter = 0;
                    this.current += (this.target > this.current ? 1 : -1);
                }
            } else {
                this.counter = 0;
            }
        }

        public void scroll(int delta) {
            this.target = Math.min(this.getMax(), Math.max(0, this.target - delta));
        }

        public void reset() {
            this.current = 0;
            this.target = 0;
            this.counter = 0;
        }
    }
}
