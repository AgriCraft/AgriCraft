package com.infinityraider.agricraft.handler;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.event.JournalRenderEvent;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthRequirement;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.capability.CapabilityResearchedPlants;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.network.MessagePlantResearched;
import com.infinityraider.agricraft.util.PlayerAngleLocker;
import com.infinityraider.infinitylib.modules.dynamiccamera.IDynamicCameraController;
import com.infinityraider.infinitylib.modules.dynamiccamera.ModuleDynamicCamera;
import com.infinityraider.infinitylib.modules.playeranimations.IAnimatablePlayerModel;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.IngameMenuScreen;
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
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
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
    public static final double DY = -0.95;
    public static final double DZ = 0.425;

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
        this.mainHandActive = status;
        if(status) {
            PlayerAngleLocker.storePlayerAngles();
        }
    }

    public void setMainHandActive(boolean status) {
        this.mainHandActive = status;
        if(status) {
            PlayerAngleLocker.storePlayerAngles();
        }
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
        double d = ((0.35/2)/Math.tan(0.5*Math.PI * fov / 360));
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
            // Check if a journal is still being held
            if(this.isOffHandActive()) {
                if(!(this.getObserver().getHeldItem(Hand.OFF_HAND).getItem() instanceof IAgriJournalItem)) {
                    AgriCraft.instance.proxy().toggleDynamicCamera(this, false);
                    this.setOffHandActive(false);
                    this.journal = null;
                    return;
                }
            } else if(this.isMainHandActive()) {
                if(!(this.getObserver().getHeldItem(Hand.MAIN_HAND).getItem() instanceof IAgriJournalItem)) {
                    AgriCraft.instance.proxy().toggleDynamicCamera(this, false);
                    this.setMainHandActive(false);
                    this.journal = null;
                    return;
                }
            }
            // Check for movement inputs
            boolean up = Minecraft.getInstance().gameSettings.keyBindForward.isKeyDown();
            boolean down = Minecraft.getInstance().gameSettings.keyBindBack.isKeyDown();
            boolean left = Minecraft.getInstance().gameSettings.keyBindLeft.isKeyDown();
            boolean right = Minecraft.getInstance().gameSettings.keyBindRight.isKeyDown();
            if (up || down || left || right) {
                // Stop observing
                ModuleDynamicCamera.getInstance().stopObserving();
            }
            // Force player orientation
            PlayerAngleLocker.forcePlayerAngles();
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

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onMouseClick(InputEvent.ClickInputEvent event) {
        // Check if the handler is active
        if(this.isActive() && !event.isUseItem()) {
            // If this is active, we do not want any click behaviour (except right clicks)
            event.setSwingHand(false);
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Pre event) {
        if(event.getPlayer() == AgriCraft.instance.getClientPlayer()) {
            if (event.getRenderer().getEntityModel() instanceof IAnimatablePlayerModel) {
                IAnimatablePlayerModel model = (IAnimatablePlayerModel) event.getRenderer().getEntityModel();
                model.setDoArmWobble(!this.isActive());
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onMovement(InputUpdateEvent event) {
        // Check if the handler is active
        if(this.isActive()) {
            // If this is active, we do not want any jumping or sneaking
            event.getMovementInput().sneaking = false;
            event.getMovementInput().jump = false;
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onGuiOpened(GuiOpenEvent event) {
        // Check if the handler is active
        if(this.isActive() && event.getGui() != null) {
            // Allow chatting
            if(event.getGui() instanceof ChatScreen) {
                return;
            }
            // Stop observing
            ModuleDynamicCamera.getInstance().stopObserving();
            // Cancel the event in case of pause
            if(event.getGui() instanceof IngameMenuScreen) {
                event.setResult(Event.Result.DENY);
                event.setCanceled(true);
            }
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
            this.pages = initializePages(journalStack);
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
                    this.pages.get(this.page).onPageOpened();
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
                    this.pages.get(this.page).onPageOpened();
                }
            }
        }

        public float getFlippingProgress(float partialTicks) {
            return MathHelper.lerp(partialTicks, this.prevAnimationCounter, this.animationCounter)/FLIPPING_DURATION;
        }

        public static List<Page> initializePages(ItemStack journal) {
            ImmutableList.Builder<Page> builder = new ImmutableList.Builder<>();
            builder.add(FRONT_PAGE);
            builder.add(INTRODUCTION_PAGE);
            if (journal.getItem() instanceof IAgriJournalItem) {
                IAgriJournalItem journalItem = (IAgriJournalItem) journal.getItem();
                builder.addAll(getPlantPages(
                        journalItem.getDiscoveredSeeds(journal).stream()
                                .sorted(Comparator.comparing(plant -> plant.getPlantName().getString()))
                                .collect(Collectors.toList())));
            }
            return builder.build();
        }

        public static abstract class Page {
            protected static final float SHADE_LEFT = 0.7F;
            protected static final float SHADE_RIGHT = 1.0F;

            public abstract void drawLeftSheet(IPageRenderer renderer, MatrixStack transforms);

            public abstract void drawRightSheet(IPageRenderer renderer, MatrixStack transforms);

            public void onPageOpened() {}
        }

        public static final Page FRONT_PAGE = new Page() {
            private final ResourceLocation BACKGROUND_FRONT_RIGHT = new ResourceLocation(
                    AgriCraft.instance.getModId().toLowerCase(),
                    "textures/journal/front_page.png"
            );

            @Override
            public void drawLeftSheet(IPageRenderer renderer, MatrixStack transforms) {
                // Draw nothing
            }

            @Override
            public void drawRightSheet(IPageRenderer renderer, MatrixStack transforms) {
                renderer.drawFullPageTexture(transforms, BACKGROUND_FRONT_RIGHT, SHADE_RIGHT);
            }
        };

        public static final Page INTRODUCTION_PAGE = new Page() {
            private final ITextComponent INTRODUCTION = new TranslationTextComponent("agricraft.journal.introduction");
            private final ITextComponent PARAGRAPH_1 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_1");
            private final ITextComponent PARAGRAPH_2 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_2");
            private final ITextComponent PARAGRAPH_3 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_3");

            @Override
            public void drawLeftSheet(IPageRenderer renderer, MatrixStack transforms) {
                // Draw Nothing
            }

            @Override
            public void drawRightSheet(IPageRenderer renderer, MatrixStack transforms) {
                float dy = 10;
                float dx = 6;
                float spacing = 4;
                // Title
                dy += renderer.drawText(transforms, INTRODUCTION, dx, dy);
                dy += spacing;
                // First paragraph
                dy += renderer.drawText(transforms, PARAGRAPH_1, dx, dy, 0.70F);
                dy += spacing;
                // Second paragraph
                dy += renderer.drawText(transforms, PARAGRAPH_2, dx, dy, 0.70F);
                dy += spacing;
                // Third paragraph
                renderer.drawText(transforms, PARAGRAPH_3, dx, dy, 0.70F);
            }
        };

        public static List<Page> getPlantPages(List<IAgriPlant> plants) {
            ImmutableList.Builder<Page> pages = ImmutableList.builder();
            plants.forEach(plant -> {
                PlantPage page = new PlantPage(plant, plants);
                pages.add(page);
                List<List<TextureAtlasSprite>> mutations = page.getOffPageMutations();
                int size = mutations.size();
                if (size > 0) {
                    int remaining = size;
                    int from = 0;
                    int to = Math.min(remaining, MutationPage.LIMIT);
                    while (remaining > 0) {
                        pages.add(new MutationPage(mutations.subList(from, to)));
                        remaining -= (to - from);
                        from = to;
                        to = from + Math.min(remaining, MutationPage.LIMIT);
                    }
                }
            });
            return pages.build();
        }

        private static final class PlantPage extends BasePage implements IRenderUtilities {
            private static final ITextComponent GROWTH_STAGES = new TranslationTextComponent("agricraft.tooltip.growth_stages");
            private static final ITextComponent GROWTH_REQUIREMENTS = new TranslationTextComponent("agricraft.tooltip.growth_requirements");
            private static final ITextComponent PRODUCTS = new TranslationTextComponent("agricraft.tooltip.products");
            private static final ITextComponent MUTATIONS = new TranslationTextComponent("agricraft.tooltip.mutations");

            private final IAgriPlant plant;
            private final List<IAgriPlant> all;

            private final TextureAtlasSprite seed;
            private final List<TextureAtlasSprite> stages;

            private final boolean[] brightnessMask;
            private final boolean[] humidityMask;
            private final boolean[] acidityMask;
            private final boolean[] nutrientsMask;
            private final boolean[] seasonMask;

            private final List<ItemStack> drops;
            private final List<List<TextureAtlasSprite>> mutationsOnPage;
            private final List<List<TextureAtlasSprite>> mutationsOffPage;

            public PlantPage(IAgriPlant plant, List<IAgriPlant> all) {
                this.plant = plant;
                this.all = all;
                this.seed = this.getSprite(plant.getSeedTexture());
                this.stages = plant.getGrowthStages().stream()
                        .sorted((a, b) -> (int) (100 * (a.growthPercentage() - b.growthPercentage())))
                        .map(plant::getTexturesFor)
                        .filter(textures -> textures.size() > 0)
                        .map(textures -> textures.get(0))
                        .map(this::getSprite)
                        .collect(Collectors.toList());
                this.brightnessMask = new boolean[16];
                IAgriGrowthRequirement req = this.plant.getGrowthRequirement(this.plant.getInitialGrowthStage());
                for(int light = 0; light < this.brightnessMask.length; light++) {
                    this.brightnessMask[light] = req.getLightLevelResponse(light, 1).isFertile();
                }
                this.humidityMask = new boolean[IAgriSoil.Humidity.values().length - 1];
                for(int humidity = 0; humidity < this.humidityMask.length; humidity++) {
                    this.humidityMask[humidity] = req.getSoilHumidityResponse(IAgriSoil.Humidity.values()[humidity], 1).isFertile();
                }
                this.acidityMask = new boolean[IAgriSoil.Acidity.values().length  - 1];
                for(int acidity = 0; acidity < this.acidityMask.length; acidity++) {
                    this.acidityMask[acidity] = req.getSoilAcidityResponse(IAgriSoil.Acidity.values()[acidity], 1).isFertile();
                }
                this.nutrientsMask = new boolean[IAgriSoil.Nutrients.values().length - 1];
                for(int nutrients = 0; nutrients < this.nutrientsMask.length; nutrients++) {
                    this.nutrientsMask[nutrients] = req.getSoilNutrientsResponse(IAgriSoil.Nutrients.values()[nutrients], 1).isFertile();
                }
                this.seasonMask = new boolean[AgriSeason.values().length - 1];
                for(int season = 0; season < this.seasonMask.length; season++) {
                    this.seasonMask[season] = req.getSeasonResponse(AgriSeason.values()[season], 1).isFertile();
                }
                ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
                this.plant.getAllPossibleProducts(builder::add);
                this.drops = builder.build();
                List<List<TextureAtlasSprite>> mutations = Stream.concat(
                        this.gatherMutationSprites(mutation -> mutation.hasParent(this.plant)),
                        this.gatherMutationSprites(mutation -> mutation.hasChild(this.plant))
                ).collect(Collectors.toList());
                int count = mutations.size();
                if(count <= 6) {
                    this.mutationsOnPage = mutations.subList(0, count);
                    this.mutationsOffPage = ImmutableList.of();
                } else {
                    this.mutationsOnPage = mutations.subList(0, 6);
                    this.mutationsOffPage = mutations.subList(6, count);
                }
            }

            protected Stream<List<TextureAtlasSprite>> gatherMutationSprites(Predicate<IAgriMutation> filter) {
                return AgriApi.getMutationRegistry().stream()
                        .filter(filter).map(mutation ->
                                Stream.of(mutation.getParents().get(0), mutation.getParents().get(1), mutation.getChild())
                                        .map(plant -> {
                                            if (this.isPlantKnown(plant)) {
                                                return plant.getSeedTexture();
                                            } else {
                                                return NoPlant.getInstance().getSeedTexture();
                                            }
                                        })
                                        .map(this::getSprite).collect(Collectors.toList()));
            }

            protected boolean isPlantKnown(IAgriPlant plant) {
                if(AgriCraft.instance.getConfig().progressiveJEI()) {
                    return all.contains(plant)
                            ||CapabilityResearchedPlants.getInstance().isPlantResearched(AgriCraft.instance.getClientPlayer(), plant);
                }
                return true;
            }

            public void onPageOpened() {
                if(!CapabilityResearchedPlants.getInstance().isPlantResearched(AgriCraft.instance.getClientPlayer(), this.plant)) {
                    new MessagePlantResearched(this.plant).sendToServer();
                }
            }

            public List<List<TextureAtlasSprite>> getOffPageMutations() {
                return this.mutationsOffPage;
            }

            @Override
            public void drawLeftSheet(IPageRenderer renderer, MatrixStack transforms) {
                // Title
                renderer.drawTexture(transforms, Textures.TITLE, 0, 2, 128, 20, SHADE_LEFT);
                renderer.drawText(transforms, this.plant.getSeedName(), 30, 10);
                // Description
                float offset = renderer.drawText(transforms, this.plant.getInformation(), 10, 30, 0.70F);
                // Growth requirements
                this.drawGrowthRequirements(renderer, transforms,35 + offset);
                // Seed
                renderer.drawTexture(transforms, this.seed, 4, 5, 16, 16, SHADE_LEFT);
                // Growth stages
                this.drawGrowthStages(renderer, transforms);
            }

            @Override
            public void drawRightSheet(IPageRenderer renderer, MatrixStack transforms) {
                // Products
                this.drawProducts(renderer, transforms);
                // Mutations
                this.drawMutations(renderer, transforms);
            }

            protected void drawGrowthRequirements(IPageRenderer renderer, MatrixStack transforms, float offset) {
                float dy = Math.max(offset, 60);
                dy += renderer.drawText(transforms, GROWTH_REQUIREMENTS, 10, dy, 0.80F) + 1;
                // Light level
                renderer.drawTexture(transforms, Textures.BRIGHTNESS_BAR, 6, dy, 66, 8, SHADE_LEFT);
                transforms.push();
                transforms.translate(0, 0, -0.001F);
                for (int i = 0; i < this.brightnessMask.length; i++) {
                    boolean current = this.brightnessMask[i];
                    if(current) {
                        boolean prev = i > 0 && this.brightnessMask[i - 1];
                        boolean next = i < (this.brightnessMask.length - 1) && this.brightnessMask[i + 1];
                        renderer.drawTexture(transforms, Textures.BRIGHTNESS_HIGHLIGHT, 6 + 4*i + 1, dy, 4, 8,
                                0.25F, 0, 0.75F, 1, SHADE_LEFT);
                        if(!prev) {
                            renderer.drawTexture(transforms, Textures.BRIGHTNESS_HIGHLIGHT, 6 + 4 * i, dy, 1, 8,
                                    0, 0, 0.25F, 1, SHADE_LEFT);
                        }
                        if(!next) {
                            renderer.drawTexture(transforms, Textures.BRIGHTNESS_HIGHLIGHT, 6 + 4*i + 5, dy, 1, 8,
                                    0.75F, 0, 1, 1, SHADE_LEFT);
                        }
                    }
                }
                dy += 9;
                transforms.pop();
                // Seasons
                if(AgriApi.getSeasonLogic().isActive()) {
                    for(int i = 0; i < this.seasonMask.length; i++) {
                        int dx = 70;
                        int w = 10;
                        int h = 12;
                        int x = (i%2)*(w + 2) + 5;
                        int y = (i/2)*(h + 2) + 6;
                        float v1 = (0.0F + i*h)/48;
                        float v2 = (0.0F + (i + 1)*h)/48;
                        if(this.seasonMask[i]) {
                            renderer.drawTexture(transforms, Textures.SEASONS_FILLED, x + dx, y + dy, w, h, 0, v1, 1, v2, SHADE_LEFT);
                        } else {
                            renderer.drawTexture(transforms, Textures.SEASONS_EMPTY, x + dx, y + dy, w, h, 0, v1, 1, v2, SHADE_LEFT);
                        }
                    }
                }
                // Humidity
                for(int i = 0; i < this.humidityMask.length; i++) {
                    int dx = Textures.HUMIDITY_OFFSETS[i];
                    int w = Textures.HUMIDITY_OFFSETS[i + 1] - Textures.HUMIDITY_OFFSETS[i];
                    float u1 = (dx + 0.0F)/53.0F;
                    float u2 = (dx + w + 0.0F)/53.0F;
                    if(this.humidityMask[i]) {
                        renderer.drawTexture(transforms, Textures.HUMIDITY_FILLED, 10 + dx, dy, w, 12, u1, 0, u2, 1, SHADE_LEFT);
                    } else {
                        renderer.drawTexture(transforms, Textures.HUMIDITY_EMPTY, 10 + dx, dy, w, 12, u1, 0, u2, 1, SHADE_LEFT);
                    }
                }
                dy += 13;
                // Acidity
                for(int i = 0; i < this.acidityMask.length; i++) {
                    int dx = Textures.ACIDITY_OFFSETS[i];
                    int w = Textures.ACIDITY_OFFSETS[i + 1] - Textures.ACIDITY_OFFSETS[i];
                    float u1 = (dx + 0.0F)/53.0F;
                    float u2 = (dx + w + 0.0F)/53.0F;
                    if(this.acidityMask[i]) {
                        renderer.drawTexture(transforms, Textures.ACIDITY_FILLED, 10 + dx, dy, w, 12, u1, 0, u2, 1, SHADE_LEFT);
                    } else {
                        renderer.drawTexture(transforms, Textures.ACIDITY_EMPTY, 10 + dx, dy, w, 12, u1, 0, u2, 1, SHADE_LEFT);
                    }
                }
                dy += 13;
                // Nutrients
                for(int i = 0; i < this.nutrientsMask.length; i++) {
                    int dx = Textures.NUTRIENTS_OFFSETS[i];
                    int w = Textures.NUTRIENTS_OFFSETS[i + 1] - Textures.NUTRIENTS_OFFSETS[i];
                    float u1 = (dx + 0.0F)/53.0F;
                    float u2 = (dx + w + 0.0F)/53.0F;
                    if(this.nutrientsMask[i]) {
                        renderer.drawTexture(transforms, Textures.NUTRIENTS_FILLED, 10 + dx, dy, w, 12, u1, 0, u2, 1, SHADE_LEFT);
                    } else {
                        renderer.drawTexture(transforms, Textures.NUTRIENTS_EMPTY, 10 + dx, dy, w, 12, u1, 0, u2, 1, SHADE_LEFT);
                    }
                }
            }

            protected void drawGrowthStages(IPageRenderer renderer, MatrixStack transforms) {
                // Position data
                int y0 = 170;
                int delta = 20;
                int rows = this.stages.size()/6 + (this.stages.size() % 6 > 0 ? 1 : 0);
                int columns = this.stages.size()/rows + (this.stages.size() % rows > 0 ? 1 : 0);
                // draw stages
                int row = 0;
                int dx = (renderer.getPageWidth() - (16*(columns)))/(columns + 1);
                for(int i = 0; i < this.stages.size(); i++) {
                    int column = i % columns;
                    if(i > 0 && column == 0) {
                        row += 1;
                    }
                    renderer.drawTexture(transforms, Textures.GROWTH_STAGE, dx*(column + 1) + 16*column - 1, y0 - delta*(rows - row - 1) - 1, 18, 18, SHADE_LEFT);
                    transforms.push();
                    transforms.translate(0,0, -0.001F);
                    renderer.drawTexture(transforms, this.stages.get(i), dx*(column + 1) + 16*column, y0 - delta*(rows - row - 1), 16, 16, SHADE_LEFT);
                    transforms.pop();
                }
                // draw text
                renderer.drawText(transforms, GROWTH_STAGES, 10, y0 - delta*rows + 4, 0.90F);
                MinecraftForge.EVENT_BUS.post(new JournalRenderEvent.PostDrawGrothStages(renderer, transforms, plant, stages));
            }

            protected void drawProducts(IPageRenderer renderer, MatrixStack transforms) {
                renderer.drawText(transforms, PRODUCTS, 10, 10, 0.80F);
                for(int i = 0; i < this.drops.size(); i++) {
                    renderer.drawTexture(transforms, Textures.MUTATION, 10 + i*20, 19, 18, 18, 0, 0, 18.0F/86.0F, 1, SHADE_RIGHT);
                    renderer.drawItem(transforms, this.drops.get(i), 11+i*20, 20);
                }
            }

            protected void drawMutations(IPageRenderer renderer, MatrixStack transforms) {
                renderer.drawText(transforms, MUTATIONS, 10, 45, 0.80F);
                int posX = 10;
                int posY = 54;
                int dy = 20;
                for (List<TextureAtlasSprite> sprites : this.mutationsOnPage) {
                    this.drawMutation(renderer, transforms, posX, posY, sprites, SHADE_RIGHT);
                    posY += dy;
                }
            }
        }

        private static final class MutationPage extends BasePage {
            public static final int LIMIT = 18;

            private final List<List<TextureAtlasSprite>> mutationsLeft;
            private final List<List<TextureAtlasSprite>> mutationsRight;

            public MutationPage(List<List<TextureAtlasSprite>> mutations) {
                int count = mutations.size();
                if(count <= LIMIT/2) {
                    this.mutationsLeft = mutations;
                    this.mutationsRight = ImmutableList.of();
                } else {
                    this.mutationsLeft = mutations.subList(0, LIMIT/2 - 1);
                    this.mutationsRight = mutations.subList(LIMIT/2, count - 1);
                }
            }

            @Override
            public void drawLeftSheet(IPageRenderer renderer, MatrixStack transforms) {
                int posX = 10;
                int posY = 6;
                int dy = 20;
                for (List<TextureAtlasSprite> sprites : this.mutationsLeft) {
                    this.drawMutation(renderer, transforms, posX, posY, sprites, SHADE_LEFT);
                    posY += dy;
                }
            }

            @Override
            public void drawRightSheet(IPageRenderer renderer, MatrixStack transforms) {
                int posX = 10;
                int posY = 6;
                int dy = 20;
                for (List<TextureAtlasSprite> sprites : this.mutationsRight) {
                    this.drawMutation(renderer, transforms, posX, posY, sprites, SHADE_RIGHT);
                    posY += dy;
                }
            }
        }

        private static abstract class BasePage extends Page {
            protected void drawMutation(IPageRenderer renderer, MatrixStack transforms, int posX, int posY, List<TextureAtlasSprite> sprites, float c) {
                renderer.drawTexture(transforms, Textures.MUTATION, posX, posY, 86, 18, c);
                transforms.push();
                transforms.translate(0, 0, -0.001F);
                renderer.drawTexture(transforms, sprites.get(0), posX + 1, posY + 1, 16, 16, c);
                renderer.drawTexture(transforms, sprites.get(1), posX + 35, posY + 1, 16, 16, c);
                renderer.drawTexture(transforms, sprites.get(2), posX + 69, posY + 1, 16, 16, c);
                transforms.pop();
            }
        }


        public static final class Textures {
            public static final ResourceLocation TITLE = new ResourceLocation(
                    AgriCraft.instance.getModId().toLowerCase(),
                    "textures/journal/template_title.png"
            );
            public static final ResourceLocation GROWTH_STAGE = new ResourceLocation(
                    AgriCraft.instance.getModId().toLowerCase(),
                    "textures/journal/template_growth_stage.png"
            );
            public static final ResourceLocation BRIGHTNESS_BAR = new ResourceLocation(
                    AgriCraft.instance.getModId().toLowerCase(),
                    "textures/journal/template_brightness_bar.png"
            );
            public static final ResourceLocation BRIGHTNESS_HIGHLIGHT = new ResourceLocation(
                    AgriCraft.instance.getModId().toLowerCase(),
                    "textures/journal/template_brightness_highlight.png"
            );
            public static final ResourceLocation HUMIDITY_EMPTY = new ResourceLocation(
                    AgriCraft.instance.getModId().toLowerCase(),
                    "textures/journal/template_humidity_empty.png"
            );
            public static final ResourceLocation HUMIDITY_FILLED = new ResourceLocation(
                    AgriCraft.instance.getModId().toLowerCase(),
                    "textures/journal/template_humidity_filled.png"
            );
            public static final ResourceLocation ACIDITY_EMPTY = new ResourceLocation(
                    AgriCraft.instance.getModId().toLowerCase(),
                    "textures/journal/template_acidity_empty.png"
            );
            public static final ResourceLocation ACIDITY_FILLED = new ResourceLocation(
                    AgriCraft.instance.getModId().toLowerCase(),
                    "textures/journal/template_acidity_filled.png"
            );
            public static final ResourceLocation NUTRIENTS_FILLED = new ResourceLocation(
                    AgriCraft.instance.getModId().toLowerCase(),
                    "textures/journal/template_nutrients_filled.png"
            );
            public static final ResourceLocation NUTRIENTS_EMPTY = new ResourceLocation(
                    AgriCraft.instance.getModId().toLowerCase(),
                    "textures/journal/template_nutrients_empty.png"
            );
            public static final ResourceLocation SEASONS_FILLED = new ResourceLocation(
                    AgriCraft.instance.getModId().toLowerCase(),
                    "textures/journal/template_seasons_filled.png"
            );
            public static final ResourceLocation SEASONS_EMPTY = new ResourceLocation(
                    AgriCraft.instance.getModId().toLowerCase(),
                    "textures/journal/template_seasons_empty.png"
            );
            public static final ResourceLocation MUTATION = new ResourceLocation(
                    AgriCraft.instance.getModId().toLowerCase(),
                    "textures/journal/template_mutation.png"
            );

            public static final int[] HUMIDITY_OFFSETS = new int[]{0, 8, 16, 26, 36, 46, 53};
            public static final int[] ACIDITY_OFFSETS = new int[]{0, 7, 15, 22, 30, 38, 46, 53};
            public static final int[] NUTRIENTS_OFFSETS = new int[]{0, 6, 14, 23, 32, 43, 53};

            private Textures() {}
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

        default float drawText(MatrixStack transforms, ITextComponent text, float x, float y) {
            return this.drawText(transforms, text, x, y, 1.0F);
        }

        float drawText(MatrixStack transforms, ITextComponent text, float x, float y, float scale);

        void drawItem(MatrixStack transforms, ItemStack item, float x, float y);
    }
}
