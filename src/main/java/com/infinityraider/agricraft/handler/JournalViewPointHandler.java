package com.infinityraider.agricraft.handler;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.api.v1.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.infinitylib.modules.dynamiccamera.IDynamicCameraController;
import com.infinityraider.infinitylib.modules.dynamiccamera.ModuleDynamicCamera;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class JournalViewPointHandler implements IDynamicCameraController {
    private static final JournalViewPointHandler INSTANCE = new JournalViewPointHandler();

    public static final int TRANSITION_DURATION = 10;
    private static final int OPENING_DURATION = 20;
    private static final int FLIPPING_DURATION = 20;

    public static final double DX = 0.35;
    public static final double DY = -1.0;
    public static final double DZ = 0.45;

    public static final float PITCH = 80.0F;
    public static final float YAW = 0.0F;


    public static JournalViewPointHandler getInstance() {
        return INSTANCE;
    }

    /** Status flags  */
    private boolean offHandActive;
    private boolean mainHandActive;
    private boolean observed;

    /** Observer data */
    private Vector3d observerStart;
    private Vector3d cameraPosition;
    private Vector2f cameraOrientation;
    private float yawOffset;

    /** Journal data */
    private JournalData journal;

    /** Animation counter */
    private int openingCounter;
    private int openingCounterPrev;

    private JournalViewPointHandler() {}

    public boolean toggle(ItemStack journal, Hand hand) {
        if(this.isActive(hand)) {
            this.setActive(hand, false);
            if(AgriCraft.instance.proxy().toggleDynamicCamera(this, false)) {
                this.journal = null;
                return true;
            } else {
                this.setActive(hand, true);
                this.journal = new JournalData(journal);
            }
        } else {
            Hand other = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
            if(!this.isActive(other)) {
                this.setActive(hand, true);
                if(AgriCraft.instance.proxy().toggleDynamicCamera(this, true)) {
                    this.journal = new JournalData(journal);
                    return true;
                } else {
                    this.setActive(hand, false);
                    this.journal = null;
                }
            }
        }
        return false;
    }

    public void setActive(Hand hand, boolean status) {
        if(hand == Hand.MAIN_HAND) {
            this.setMainHandActive(status);
        } else {
            this.setOffHandActive(status);
        }
    }

    public void setOffHandActive(boolean status) {
        this.offHandActive = status;
    }

    public void setMainHandActive(boolean status) {
        this.mainHandActive = status;
    }

    public boolean isActive() {
        return this.isMainHandActive() || this.isOffHandActive();
    }

    public boolean isActive(Hand hand) {
        return hand == Hand.MAIN_HAND ? this.isMainHandActive() : this.isOffHandActive();
    }

    public boolean isActive(HandSide hand) {
        HandSide main = Minecraft.getInstance().gameSettings.mainHand;
        if(main == hand) {
            return this.isMainHandActive();
        } else {
            return this.isOffHandActive();
        }
    }

    public boolean isMainHandActive() {
        return this.mainHandActive;
    }

    public boolean isOffHandActive() {
        return this.offHandActive;
    }

    public boolean isObserved() {
        return this.observed;
    }

    protected PlayerEntity getObserver() {
        return AgriCraft.instance.getClientPlayer();
    }

    @Nullable
    public JournalData getJournalData() {
        return this.journal;
    }

    @Nullable
    public ItemStack getJournal() {
        return this.getJournalData() == null ? null : this.getJournalData().getJournal();
    }

    public float getOpeningProgress(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.openingCounterPrev, this.openingCounter) / OPENING_DURATION;
    }

    public float getFlippingProgress(float partialTicks) {
        return this.getJournalData() == null ? 0 : this.getJournalData().getFlippingProgress(partialTicks);
    }

    public void renderViewedPageLeft(IPageRenderer renderer, MatrixStack transforms) {
        if(this.getJournalData() != null) {
            this.getJournalData().getCurrentPage().drawLeftSheet(renderer, transforms);
        }
    }

    public void renderViewedPageRight(IPageRenderer renderer, MatrixStack transforms) {
        if(this.getJournalData() != null) {
            this.getJournalData().getCurrentPage().drawRightSheet(renderer, transforms);
        }
    }

    public void renderFlippedPageLeft(IPageRenderer renderer, MatrixStack transforms) {
        if(this.getJournalData() != null) {
            this.getJournalData().getFlippedPage().drawLeftSheet(renderer, transforms);
        }
    }

    public void renderFlippedPageRight(IPageRenderer renderer, MatrixStack transforms) {
        if(this.getJournalData() != null) {
            this.getJournalData().getFlippedPage().drawRightSheet(renderer, transforms);
        }
    }

    @Override
    public int getTransitionDuration() {
        return TRANSITION_DURATION;
    }

    @Override
    public void onCameraActivated() {
        this.observerStart = this.getObserver().getPositionVec();
    }

    @Override
    public void onObservationStart() {
        this.observed = true;
    }

    @Override
    public void onObservationEnd() {
        this.observed = false;
    }

    @Override
    public void onCameraDeactivated() {
        this.setMainHandActive(false);
        this.setOffHandActive(false);
        this.observerStart = null;
        this.cameraPosition = null;
        this.cameraOrientation = null;
        this.journal = null;
    }

    @Override
    public boolean shouldContinueObserving() {
        return this.observerStart != null && this.observerStart.equals(this.getObserver().getPositionVec());
    }

    @Override
    public Vector3d getObserverPosition() {
        if(this.cameraPosition == null || this.yawOffset != this.getObserver().renderYawOffset) {
            this.cameraPosition = this.calculateObserverPosition(AgriCraft.instance.proxy().getFieldOfView());
        }
        return this.cameraPosition;
    }

    protected Vector3d calculateObserverPosition(double fov) {
        // calculate offset based on fov
        double d = 0.75 * (0.5/Math.tan(Math.PI * fov / 360));
        // Determine x, y and z offsets
        double dx, dz;
        double dy = DY + d*MathHelper.sin((float) Math.PI * PITCH / 180);
        double dHor = d*MathHelper.cos((float) Math.PI * PITCH / 180);
        float yaw = this.getObserverOrientation().y;
        double cosYaw = MathHelper.cos((float) Math.PI * yaw / 180);
        double sinYaw = MathHelper.sin((float) Math.PI * yaw / 180);
        if (this.isActive(HandSide.RIGHT)) {
            dx = (dHor * cosYaw) - (DX * cosYaw) - (DZ * sinYaw);
            dz = (dHor * sinYaw) - (DX * sinYaw) + (DZ * cosYaw);
        } else {
            dx = (dHor * cosYaw) + (DX * cosYaw) - (DZ * sinYaw);
            dz = (dHor * sinYaw) + (DX * sinYaw) + (DZ * cosYaw);
        }
        return this.getObserver().getPositionVec().add(dx, dy + this.getObserver().getEyeHeight(), dz);
    }

    @Override
    public Vector2f getObserverOrientation() {
        if(this.cameraOrientation == null || this.yawOffset != this.getObserver().renderYawOffset) {
            this.cameraOrientation = this.calculateObserverOrientation();
        }
        return this.cameraOrientation;
    }

    protected Vector2f calculateObserverOrientation() {
        this.yawOffset = this.getObserver().renderYawOffset;
        float yaw = this.yawOffset;
        if (this.isActive(HandSide.RIGHT)) {
            yaw += YAW;
        } else if (this.isActive(HandSide.LEFT)) {
            yaw -= YAW;
        }
        return new Vector2f(PITCH, yaw);
    }

    @Override
    public void onFieldOfViewChanged(float fov) {
        this.cameraPosition = this.calculateObserverPosition(fov);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Check if the handler is active
        if (this.isActive()) {
            // Check for movement inputs
            boolean up = Minecraft.getInstance().gameSettings.keyBindForward.isKeyDown();
            boolean down = Minecraft.getInstance().gameSettings.keyBindBack.isKeyDown();
            boolean left = Minecraft.getInstance().gameSettings.keyBindLeft.isKeyDown();
            boolean right = Minecraft.getInstance().gameSettings.keyBindRight.isKeyDown();
            if (up || down || left || right) {
                // Stop observing
                ModuleDynamicCamera.getInstance().stopObserving();
            }
        }
        // Tick animation timer
        this.openingCounterPrev = this.openingCounter;
        if (this.isObserved()) {
            // Tick the book opening counter to open
            this.openingCounter = (this.openingCounter < OPENING_DURATION) ? this.openingCounter + 1 : OPENING_DURATION;
        } else {
            // Tick the book opening counter to close
            this.openingCounter = (this.openingCounter > 0) ? this.openingCounter - 1 : 0;
        }
        // Tick the scroll position to increment its animation timer
        if(this.getJournalData() != null) {
            this.getJournalData().tick();
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onMouseScroll(InputEvent.MouseScrollEvent event) {
        // Check if the handler is active
        if(this.isActive()) {
            // Tell the journal data that the player has scrolled, and a page must be flipped
            if(this.getJournalData() != null) {
                if(event.getScrollDelta() < 0) {
                    this.getJournalData().incrementPage();
                } else if(event.getScrollDelta() > 0) {
                    this.getJournalData().decrementPage();
                }
            }
            // If this is active, we do not want any other scroll behaviour
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    public static class JournalData {
        private final ItemStack journal;
        private final List<Page> pages;

        private int page;
        private int target;

        private int animationCounter;
        private int prevAnimationCounter;

        protected JournalData(ItemStack journalStack) {
            this.journal = journalStack;
            ImmutableList.Builder<Page> builder = new ImmutableList.Builder<>();
            builder.add(FRONT_PAGE);
            if(journalStack.getItem() instanceof IAgriJournalItem) {
                IAgriJournalItem journalItem = (IAgriJournalItem) journalStack.getItem();
                journalItem.getDiscoveredSeeds(journalStack).stream()
                        .sorted(Comparator.comparing(IAgriRegisterable::getId))
                        .map(PlantPage::new)
                        .forEach(builder::add);
            }
            this.pages = builder.build();
        }

        public ItemStack getJournal() {
            return this.journal;
        }

        public Page getCurrentPage() {
            if(this.target >= this.page) {
                return this.pages.get(this.page);
            } else {
                return this.pages.get(this.page - 1);
            }
        }

        public Page getFlippedPage() {
            if(this.target > this.page) {
                return this.pages.get(this.page + 1);
            }else  {
                return this.pages.get(this.page);
            }
        }

        public void incrementPage() {
            this.target = Math.min(this.pages.size() - 1, this.target + 1);
        }

        public void decrementPage() {
            this.target = Math.max(0, this.target - 1);
        }

        public void tick() {
            this.prevAnimationCounter = this.animationCounter;
            if(this.target > this.page) {
                if(this.animationCounter == 0) {
                    this.animationCounter = FLIPPING_DURATION;
                    this.prevAnimationCounter = this.animationCounter;
                }
                this.animationCounter -= 1;
                if(this.animationCounter <= 0) {
                    this.animationCounter = 0;
                    this.page += 1;
                }
            } else if(this.target < this.page) {
                if(this.animationCounter == 0) {
                    this.animationCounter = -FLIPPING_DURATION;
                    this.prevAnimationCounter = this.animationCounter;
                }
                this.animationCounter += 1;
                if(this.animationCounter >= 0) {
                    this.animationCounter = 0;
                    this.page -= 1;
                }
            }
        }

        public float getFlippingProgress(float partialTicks) {
            return MathHelper.lerp(partialTicks, this.prevAnimationCounter, this.animationCounter)/FLIPPING_DURATION;
        }

        public static abstract class Page {
            public abstract void drawLeftSheet(IPageRenderer renderer, MatrixStack transforms);

            public abstract void drawRightSheet(IPageRenderer renderer, MatrixStack transforms);
        }

        private static final float SHADE_LEFT = 0.7F;
        private static final float SHADE_RIGHT = 1.0F;

        private static final ResourceLocation BACKGROUND_FRONT_RIGHT = new ResourceLocation(
                AgriCraft.instance.getModId().toLowerCase(),
                "textures/journal/front_page.png"
        );

        public static final Page FRONT_PAGE = new Page() {
            @Override
            public void drawLeftSheet(IPageRenderer renderer, MatrixStack transforms) {
                // Draw nothing
            }

            @Override
            public void drawRightSheet(IPageRenderer renderer, MatrixStack transforms) {
                renderer.drawFullPageTexture(transforms, BACKGROUND_FRONT_RIGHT, SHADE_RIGHT);
            }
        };

        private static final class PlantPage extends Page implements IRenderUtilities {

            private static final ResourceLocation TITLE_TEMPLATE = new ResourceLocation(
                    AgriCraft.instance.getModId().toLowerCase(),
                    "textures/journal/template_title.png"
            );
            private static final ResourceLocation GROWTH_STAGE_TEMPLATE = new ResourceLocation(
                    AgriCraft.instance.getModId().toLowerCase(),
                    "textures/journal/template_growth_stage.png"
            );
            private static final ResourceLocation MUTATION_TEMPLATE = new ResourceLocation(
                    AgriCraft.instance.getModId().toLowerCase(),
                    "textures/journal/template_mutation.png"
            );

            private static final ITextComponent GROWTH_STAGES = new TranslationTextComponent("agricraft.tooltip.growth_stages");

            private final IAgriPlant plant;

            TextureAtlasSprite seed;
            List<TextureAtlasSprite> stages;

            List<List<TextureAtlasSprite>> mutationsFrom;
            List<List<TextureAtlasSprite>> mutationsTo;

            public PlantPage(IAgriPlant plant) {
                this.plant = plant;
                this.seed = this.getSprite(plant.getSeedTexture());
                this.stages = plant.getGrowthStages().stream()
                        .sorted((a, b) -> (int) (100 * (a.growthPercentage() - b.growthPercentage())))
                        .map(plant::getTexturesFor)
                        .filter(textures -> textures.size() > 0)
                        .map(textures -> textures.get(0))
                        .map(this::getSprite)
                        .collect(Collectors.toList());

                this.mutationsFrom = this.gatherMutationSprites(mutation -> mutation.hasParent(this.plant));
                this.mutationsTo = this.gatherMutationSprites(mutation -> mutation.hasChild(this.plant));
            }

            protected List<List<TextureAtlasSprite>> gatherMutationSprites(Predicate<IAgriMutation> filter) {
                return AgriApi.getMutationRegistry().stream()
                        .filter(filter)
                        .map(mutation ->
                                Stream.of(
                                        mutation.getParents().get(0),
                                        mutation.getParents().get(1),
                                        mutation.getChild())
                                        .map(IAgriPlant::getSeedTexture)
                                        .map(this::getSprite).collect(Collectors.toList())
                        ).collect(Collectors.toList());
            }

            @Override
            public void drawLeftSheet(IPageRenderer renderer, MatrixStack transforms) {
                // Title
                renderer.drawTexture(transforms, TITLE_TEMPLATE, 0, 2, 128, 20, SHADE_LEFT);
                renderer.drawText(transforms, this.plant.getSeedName(), 23, 2);
                // Description
                renderer.drawText(transforms, this.plant.getTooltip(), 2, 24);
                // Seed
                renderer.drawTexture(transforms, this.seed, 4, 5, 16, 16, SHADE_LEFT);
                // Growth stages
                this.drawGrowthStages(renderer, transforms);
            }

            @Override
            public void drawRightSheet(IPageRenderer renderer, MatrixStack transforms) {
                // Mutations
                this.drawMutations(renderer, transforms);
            }

            protected void drawGrowthStages(IPageRenderer renderer, MatrixStack transforms) {
                // Position data
                int y0 = 170;
                int delta = 20;
                int rows = this.stages.size()/6 + (this.stages.size() % 6 > 0 ? 1 : 0);
                int columns = this.stages.size()/rows + (this.stages.size() % rows > 0 ? 1 : 0);
                // draw stages
                int row = 0;
                int dx = (renderer.getPageWidth() - (16*(columns + 1)))/rows;
                for(int i = 0; i < this.stages.size(); i++) {
                    int column = i % columns;
                    if(i > 0 && column == 0) {
                        row += 1;
                    }
                    renderer.drawTexture(transforms, GROWTH_STAGE_TEMPLATE, dx*(column + 1) - 1, y0 - delta*(rows - row - 1) - 1, 18, 18, SHADE_LEFT);
                    transforms.push();
                    transforms.translate(0,0, -0.001F);
                    renderer.drawTexture(transforms, this.stages.get(i), dx*(column + 1), y0 - delta*(rows - row - 1), 16, 16, SHADE_LEFT);
                    transforms.pop();
                }
                // draw text
                renderer.drawText(transforms, GROWTH_STAGES, dx, y0 - delta*rows);
            }

            protected void drawMutations(IPageRenderer renderer, MatrixStack transforms) {
                int posX = 21;
                int posY = 6;
                int dy = 20;
                for (List<TextureAtlasSprite> sprites : this.mutationsTo) {
                    this.drawMutation(renderer, transforms, posX, posY, sprites);
                    posY += dy;
                }
                for (List<TextureAtlasSprite> sprites : this.mutationsFrom) {
                    this.drawMutation(renderer, transforms, posX, posY, sprites);
                    posY += dy;
                }
            }

            protected void drawMutation(IPageRenderer renderer, MatrixStack transforms, int posX, int posY, List<TextureAtlasSprite> sprites) {
                renderer.drawTexture(transforms, MUTATION_TEMPLATE, posX, posY, 86, 18, 0.7F);
                transforms.push();
                transforms.translate(0, 0, -0.001F);
                renderer.drawTexture(transforms, sprites.get(0), posX + 1, posY + 1, 16, 16, SHADE_RIGHT);
                renderer.drawTexture(transforms, sprites.get(1), posX + 35, posY + 1, 16, 16, SHADE_RIGHT);
                renderer.drawTexture(transforms, sprites.get(2), posX + 69, posY + 1, 16, 16, SHADE_RIGHT);
                transforms.pop();
            }
        }
    }

    public interface IPageRenderer {
        int getPageWidth();

        int getPageHeight();

        default void drawFullPageTexture(MatrixStack transforms, ResourceLocation texture, float c) {
            this.drawTexture(transforms, texture, 0, 0, this.getPageWidth(), this.getPageHeight(), c);
        }

        default void drawTexture(MatrixStack transforms, ResourceLocation texture, float x, float y, float w, float h, float c) {
            this.drawTexture(transforms, texture, x, y, w, h, 0, 0, 1, 1, c);
        }

        void drawTexture(MatrixStack transforms, ResourceLocation texture,
                         float x, float y, float w, float h, float u1, float v1, float u2, float v2, float c);

        void drawTexture(MatrixStack transforms, TextureAtlasSprite texture,
                         float x, float y, float w, float h, float c);

        void drawText(MatrixStack transforms, ITextComponent text, float x, float y);
    }
}
