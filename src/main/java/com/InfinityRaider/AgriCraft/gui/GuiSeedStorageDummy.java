package com.InfinityRaider.AgriCraft.gui;

import com.InfinityRaider.AgriCraft.container.ContainerSeedStorageDummy;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.storage.SeedStorageSlot;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.*;

public abstract class GuiSeedStorageDummy extends GuiContainer {
    //the container for this gui
    public ContainerSeedStorageDummy container;
    //data for the active buttons
    ItemSeeds activeSeed;
    int activeMeta;
    private int scrollPositionVertical;
    private int scrollPositionHorizontal;
    private int sortStatId = -1;
    //button id constants
    protected static final int buttonIdGrowth = 0;
    protected static final int buttonIdGain = 1;
    protected static final int buttonIdStrength = 2;
    protected static final int buttonIdScrollDown = 3;
    protected static final int buttonIdScrollUp = 4;
    protected static final int buttonIdScrollLeft = 5;
    protected static final int buttonIdScrollRight = 6;
    protected static final int buttonIdDownEnd = 7;
    protected static final int buttonIdUpEnd = 8;
    protected static final int buttonIdLeftEnd = 9;
    protected static final int buttonIdRightEnd = 10;
    //other button stuff
    private final int maxVertSlots;
    private final int maxHorSlots;
    private final int sortButtonX;
    private final int sortButtonY;
    private int lastButtonId = buttonIdRightEnd;
    private final int setActiveSeedButtonOffset_X;
    private final int setActiveSeedButtonOffset_Y;
    protected List<ButtonSeedStorage.SetActiveSeed> setActiveSeedButtons;
    private final int seedSlotButtonOffset_X;
    private final int seedSlotButtonOffset_Y;
    protected List<ButtonSeedStorage.SeedSlot> seedSlotButtons;

    public GuiSeedStorageDummy(ContainerSeedStorageDummy container, int maxVertSlots, int maxHorSlots, int sortButtonX, int sortButtonY, int setActiveSeedButtonsX, int setActiveSeedButtonsY, int seedSlotsX, int seedSlotsY) {
        super(container);
        this.container = container;
        this.maxVertSlots = maxVertSlots;
        this.maxHorSlots = maxHorSlots;
        this.sortButtonX = sortButtonX;
        this.sortButtonY = sortButtonY;
        this.setActiveSeedButtonOffset_X = setActiveSeedButtonsX;
        this.setActiveSeedButtonOffset_Y = setActiveSeedButtonsY;
        this.seedSlotButtonOffset_X = seedSlotsX;
        this.seedSlotButtonOffset_Y = seedSlotsY;
    }

    protected void loadButtons() {
        this.buttonList.clear();
        int buttonWidth = 60;
        int buttonHeight = 12;
        this.buttonList.add(new GuiButton(buttonIdGrowth, this.guiLeft + sortButtonX, this.guiTop + sortButtonY, buttonWidth, buttonHeight, "Growth"));
        this.buttonList.add(new GuiButton(buttonIdGain, this.guiLeft + sortButtonX, this.guiTop + sortButtonY+buttonHeight+1, buttonWidth, buttonHeight, "Gain"));
        this.buttonList.add(new GuiButton(buttonIdStrength, this.guiLeft + sortButtonX, this.guiTop + sortButtonY + 2 * (buttonHeight + 1), buttonWidth, buttonHeight, "Strength"));
        this.initSetActiveSeedButtons();
        this.initSeedSlotButtons();
    }

    private void initSetActiveSeedButtons() {
        if(this.setActiveSeedButtonOffset_X<0 || this.setActiveSeedButtonOffset_Y<0) {
            return;
        }
        this.setActiveSeedButtons = new ArrayList<ButtonSeedStorage.SetActiveSeed>();
        List<ItemStack> list = container.getSeedEntries();
        if(list!=null) {
            for (int i = 0; i < list.size(); i++) {
                int xOffset = this.guiLeft + this.setActiveSeedButtonOffset_X + (16*i)%64;
                int yOffset = this.guiTop + this.setActiveSeedButtonOffset_Y + 16*(i/4);
                this.setActiveSeedButtons.add(new ButtonSeedStorage.SetActiveSeed(this.lastButtonId+i+1, xOffset, yOffset, list.get(i)));
                this.lastButtonId++;
            }
            this.buttonList.addAll(this.setActiveSeedButtons);
        }
    }

    private void initSeedSlotButtons() {
        if(this.activeSeed==null) {
            return;
        }
        this.seedSlotButtons = new ArrayList<ButtonSeedStorage.SeedSlot>();
        List<SeedStorageSlot> list = this.container.getSeedSlots(this.activeSeed, this.activeMeta);
        this.sortByStat(list);
        for(int i=scrollPositionHorizontal;i<Math.min(list.size(),scrollPositionHorizontal+maxHorSlots);i++) {
            SeedStorageSlot slot = list.get(i);
            seedSlotButtons.add(new ButtonSeedStorage.SeedSlot(slot.getId(), seedSlotButtonOffset_X+i*16, seedSlotButtonOffset_Y, slot.getStack(this.activeSeed, this.activeMeta)));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button instanceof ButtonSeedStorage.SetActiveSeed) {
            this.setActiveSeed((ButtonSeedStorage.SetActiveSeed) button);
        }
        else if(button instanceof ButtonSeedStorage.SeedSlot) {
            this.onSeedSlotClick(button.id);
        }
        else if (button.id <= buttonIdStrength && this.activeSeed != null) {
            this.sortStatId = button.id;

        } else {
            switch (button.id) {
                case buttonIdScrollDown:
                    this.scrollVertical(-1);
                    break;
                case buttonIdScrollUp:
                    this.scrollVertical(1);
                    break;
                case buttonIdScrollRight:
                    this.scrollHorizontal(1);
                    break;
                case buttonIdScrollLeft:
                    this.scrollHorizontal(-1);
                    break;
                case buttonIdDownEnd:
                    this.scrollVertical(this.getMaxVerticalScroll());
                    break;
                case buttonIdUpEnd:
                    this.scrollVertical(-this.getMaxVerticalScroll());
                    break;
                case buttonIdLeftEnd:
                    this.scrollHorizontal(-this.getMaxHorizontalScroll());
                    break;
                case buttonIdRightEnd:
                    this.scrollHorizontal(this.getMaxHorizontalScroll());
                    break;
            }
        }
        this.updateScreen();
    }

    private void onSeedSlotClick(int slotIndex) {

    }

    //TODO: rewrite method
    private void sortByStat(List<SeedStorageSlot> list) {
        String stat=null;
        switch(this.sortStatId) {
            case buttonIdGrowth: stat = Names.NBT.growth; break;
            case buttonIdGain: stat = Names.NBT.gain; break;
            case buttonIdStrength: stat = Names.NBT.strength; break;
        }
        if(stat!=null && this.activeSeed!=null) {

        }
    }

    private void scrollVertical(int amount) {
        int newPos = this.scrollPositionVertical+amount;
        newPos = newPos<0?0:newPos;
        int maxScrollY = this.getMaxVerticalScroll();
        newPos = newPos>maxScrollY?maxScrollY:newPos;
        this.scrollPositionVertical = newPos;
    }

    private int getMaxVerticalScroll() {
        int nrSeedButtons = this.setActiveSeedButtons.size();
        int nrRows = (nrSeedButtons%4>0?1:0) + nrSeedButtons/4;
        if(nrRows<=this.maxVertSlots) {
            return 0;
        }
        else {
            return nrRows-maxVertSlots;
        }
    }

    private void scrollHorizontal(int amount) {
        if(this.activeSeed!=null) {
            int newPos = this.scrollPositionHorizontal + amount;
            newPos = newPos < 0 ? 0 : newPos;
            int maxScrollX = this.getMaxHorizontalScroll();
            newPos = newPos > maxScrollX ? maxScrollX : newPos;
            this.scrollPositionHorizontal = newPos;
        }
    }

    private int getMaxHorizontalScroll() {
        int nrSlots = this.seedSlotButtons.size();
        if(nrSlots<=maxHorSlots) {
            return 0;
        }
        else {
            return nrSlots-maxHorSlots;
        }
    }

    protected void drawActiveEntries(ResourceLocation texture, int xOffset, int yOffset) {
        int textureSize = 256;
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        for(int i=0;i<this.seedSlotButtons.size();i++) {
            ButtonSeedStorage.SeedSlot slot = seedSlotButtons.get(i);
            if(slot!=null && slot.stack!=null) {
                int growth = slot.stack.stackTagCompound.getInteger(Names.NBT.growth);
                int gain = slot.stack.stackTagCompound.getInteger(Names.NBT.gain);
                int strength = slot.stack.stackTagCompound.getInteger(Names.NBT.strength);
                this.drawTexturedModalRect(xOffset+i*16+1,  yOffset-growth,   0, textureSize-growth,   3, growth);
                this.drawTexturedModalRect(xOffset+i*16+6,  yOffset-gain,     0, textureSize-gain,     3, gain);
                this.drawTexturedModalRect(xOffset+i*16+11, yOffset-strength, 0, textureSize-strength, 3, strength);
            }
        }
    }

    protected void setActiveSeed(ButtonSeedStorage.SetActiveSeed button) {
        this.setActiveSeed(button.stack);
    }

    protected void setActiveSeed(ItemStack stack) {
        this.activeSeed = (ItemSeeds) stack.getItem();
        this.activeMeta = stack.getItemDamage();
    }

    //opening the gui doesn't pause the game
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    //set active seed button class
    private static abstract class ButtonSeedStorage extends GuiButton {
        public ItemStack stack;

        public ButtonSeedStorage(int id, int xPos, int yPos, ItemStack stack) {
            super(id, xPos, yPos, 16, 16, "");
            this.stack = stack;
        }

        protected ResourceLocation getTexture() {
            return RenderHelper.getItemResource(stack.getItem().getIconFromDamage(stack.getItemDamage()));
        }

        protected String getTooltip() {
            return stack.getDisplayName();
        }


        /**
         * Draws a tooltip (copied from Vanilla).
         */
        protected void drawHoveringText(List list, int x, int y, FontRenderer font) {
            if (!list.isEmpty()) {
                GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                int k = 0;
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    String s = (String)iterator.next();
                    int l = font.getStringWidth(s);
                    if (l > k) {
                        k = l;
                    }
                }
                int j2 = x + 12;
                int k2 = y - 12;
                int i1 = 8;
                if (list.size() > 1) {
                    i1 += 2 + (list.size() - 1) * 10;
                }
                if (j2 + k > this.width) {
                    j2 -= 28 + k;
                }
                if (k2 + i1 + 6 > this.height) {
                    k2 = this.height - i1 - 6;
                }
                this.zLevel = 300.0F;
                itemRender.zLevel = 300.0F;
                int j1 = -267386864;
                this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
                this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
                this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
                this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
                this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
                int k1 = 1347420415;
                int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
                this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
                this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
                this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
                this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);
                for (int i2 = 0; i2 < list.size(); ++i2) {
                    String s1 = (String)list.get(i2);
                    font.drawStringWithShadow(s1, j2, k2, -1);
                    if (i2 == 0) {
                        k2 += 2;
                    }
                    k2 += 10;
                }
                this.zLevel = 0.0F;
                itemRender.zLevel = 0.0F;
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            }
        }

        /**
         * Overridden to correctly render icons
         */
        @Override
        public void drawTexturedModalRect(int xPos, int yPos, int u, int v, int width, int height) {
            float f = Constants.unit;
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(xPos, yPos + height, this.zLevel, (double) u*f, (double) (v+height)*f);
            tessellator.addVertexWithUV(xPos + width, yPos + height, this.zLevel, (double) (u+width)*f, (double) (v+height)*f);
            tessellator.addVertexWithUV(xPos + width, yPos, this.zLevel, (double) (u+width)*f, (double) v*f);
            tessellator.addVertexWithUV(xPos, yPos, this.zLevel, (double) u*f, (double) v*f);
            tessellator.draw();
        }

        protected static final class SetActiveSeed extends ButtonSeedStorage {
            public SetActiveSeed(int id, int xPos, int yPos, ItemStack seedStack) {
                super(id, xPos, yPos, seedStack);
            }

            /**
             * Draws this button to the screen.
             */
            public void drawButton(Minecraft minecraft, int x, int y) {
                if (this.visible) {
                    //render the button
                    FontRenderer fontrenderer = minecraft.fontRenderer;
                    minecraft.getTextureManager().bindTexture(this.getTexture());
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    this.field_146123_n = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
                    int k = this.getHoverState(this.field_146123_n);
                    GL11.glEnable(GL11.GL_BLEND);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 16, 16, 16);
                    this.mouseDragged(minecraft, x, y);
                    //render the tooltip if the mouse is over the button
                    if(this.getHoverState(true)==2) {
                        String[] tooltip = new String[1];
                        tooltip[0] = this.getTooltip();
                        this.drawHoveringText(Arrays.asList(tooltip), this.xPosition, this.yPosition, Minecraft.getMinecraft().fontRenderer);
                    }
                }
            }
        }

        protected static final class SeedSlot extends ButtonSeedStorage {
            public SeedSlot(int id, int xPos, int yPos, ItemStack seedStack) {
                super(id, xPos, yPos, seedStack);
            }

            /**
             * Draws this button to the screen.
             */
            public void drawButton(Minecraft minecraft, int x, int y) {
                if (this.visible) {
                    //render the button
                    FontRenderer fontrenderer = minecraft.fontRenderer;
                    minecraft.getTextureManager().bindTexture(this.getTexture());
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    this.field_146123_n = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
                    int k = this.getHoverState(this.field_146123_n);
                    GL11.glEnable(GL11.GL_BLEND);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 16, 16, 16);
                    this.mouseDragged(minecraft, x, y);
                    //render the tooltip if the mouse is over the button
                    if(this.getHoverState(true)==2) {
                        String[] tooltip = new String[1];
                        tooltip[0] = this.getTooltip();
                        this.drawHoveringText(Arrays.asList(tooltip), this.xPosition, this.yPosition, Minecraft.getMinecraft().fontRenderer);
                    }
                }
            }
        }
    }
}
