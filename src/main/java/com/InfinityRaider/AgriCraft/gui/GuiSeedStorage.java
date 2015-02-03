package com.InfinityRaider.AgriCraft.gui;

import com.InfinityRaider.AgriCraft.container.ContainerSeedStorage;
import com.InfinityRaider.AgriCraft.container.SlotSeedStorage;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedStorage;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.*;

@SideOnly(Side.CLIENT)
public class GuiSeedStorage extends GuiContainer {
    public static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/GuiSeedStorage.png");
    private ArrayList<SlotSeedStorage> activeEntries;

    private int scrollPositionVertical;
    private int scrollPositionHorizontal;
    private static final int maxNrVerticalSeeds = 10;
    private static final int maxNrHorizontalSeeds = 6;

    private static final int buttonIdGrowth = 0;
    private static final int buttonIdGain = 1;
    private static final int buttonIdStrength = 2;
    private static final int buttonIdScrollDown = 3;
    private static final int buttonIdScrollUp = 4;
    private static final int buttonIdScrollLeft = 5;
    private static final int buttonIdScrollRight = 6;


    public GuiSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(new ContainerSeedStorage(inventory, te));
        this.xSize = 250;
        this.ySize = 176;
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawScreen(x, y, f);
        //this.drawActiveSeedSlots();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    //draw foreground
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        this.loadButtons();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        LogHelper.debug("Pressed button: " + button.id);
        if(button.id<=buttonIdStrength) {
            this.sortByStat(button.id);
        }
        else if(button.id<=buttonIdScrollRight) {
            switch(button.id) {
                case buttonIdScrollDown: this.scrollPositionVertical = this.scrollPositionVertical +1; break;
                case buttonIdScrollUp: this.scrollPositionVertical = this.scrollPositionVertical +1; break;
                case buttonIdScrollRight: this.scrollPositionHorizontal = this.scrollPositionHorizontal +1; break;
                case buttonIdScrollLeft: this.scrollPositionHorizontal = this.scrollPositionHorizontal-1; break;
            }
        }
        else {
            if(button instanceof SeedButton) {
                this.setActiveEntries(((SeedButton) button).stack);
            }
        }
        this.updateScreen();
    }

    protected void sortByStat(int id) {
        String stat=null;
        switch(id) {
            case buttonIdGrowth: stat = Names.NBT.growth; break;
            case buttonIdGain: stat = Names.NBT.gain; break;
            case buttonIdStrength: stat = Names.NBT.strength; break;
        }
        if(stat!=null) {

        }
    }

    protected void setActiveEntries(ItemStack stack) {
        ContainerSeedStorage container= (ContainerSeedStorage) this.inventorySlots;
        //disable the previous active slots
        if(this.activeEntries!=null) {
            for (SlotSeedStorage slot : this.activeEntries) {
                slot.reset();
                container.inventorySlots.remove(slot);
            }
        }
        /*
        for(int i=container.inventoryItemStacks.size()-1;i>=container.PLAYER_INVENTORY_SIZE;i--) {
            container.inventoryItemStacks.remove(i);
        }
        */
        //get the new entries
        if(stack!=null && stack.getItem()!=null) {
            this.activeEntries = new ArrayList<SlotSeedStorage>();
            HashMap<ItemSeeds, HashMap<Integer, ArrayList<SlotSeedStorage>>> entries = ((ContainerSeedStorage) this.inventorySlots).entries;
            ItemSeeds seed = (ItemSeeds) stack.getItem();
            int seedMeta = stack.getItemDamage();
            this.activeEntries = entries.get(seed).get(seedMeta);
            //activate the new active slots
            if (this.activeEntries != null) {
                int xOffset = 82;
                int yOffset = 8;
                for (int i = 0; i < this.activeEntries.size(); i++) {
                    SlotSeedStorage slot = this.activeEntries.get(i);
                    slot.set(xOffset + 16 * i, yOffset, container.PLAYER_INVENTORY_SIZE+i);
                    container.inventorySlots.add(slot);
                    //container.inventoryItemStacks.add(slot.getStack());
                }
            }
        }
    }

    private void loadButtons() {
        this.buttonList.clear();
        //buttons
        int buttonX = 184;
        int buttonY = 7;
        int buttonWidth = 60;
        int buttonHeight = 12;
        this.buttonList.add(new GuiButton(buttonIdGrowth, this.guiLeft + buttonX, this.guiTop + buttonY, buttonWidth, buttonHeight, "Growth"));
        this.buttonList.add(new GuiButton(buttonIdGain, this.guiLeft + buttonX, this.guiTop + buttonY+buttonHeight+1, buttonWidth, buttonHeight, "Gain"));
        this.buttonList.add(new GuiButton(buttonIdStrength, this.guiLeft +  buttonX, this.guiTop + buttonY+2*(buttonHeight+1), buttonWidth, buttonHeight, "Strength"));
        //seed buttons
        int xOffset = 7;
        int yOffset = 8;
        ArrayList<ItemStack> seedStacks = this.getSeedEntries();
        for(int i=0;i<seedStacks.size();i++) {
            this.buttonList.add(new SeedButton(buttonIdScrollRight+1+i, this.guiLeft + xOffset + (16*i)%64, this.guiTop + yOffset + 16*(i/4), seedStacks.get(i)));
        }
    }

    //gets an arraylist of all the seed instances in the container
    public ArrayList<ItemStack> getSeedEntries() {
        ArrayList<ItemStack> seeds = new ArrayList<ItemStack>();
        HashMap<ItemSeeds, HashMap<Integer, ArrayList<SlotSeedStorage>>> entries = ((ContainerSeedStorage) this.inventorySlots).entries;
        for(Map.Entry<ItemSeeds, HashMap<Integer, ArrayList<SlotSeedStorage>>> seedEntry:entries.entrySet()) {
            if(seedEntry!=null && seedEntry.getKey()!=null && seedEntry.getValue()!=null) {
                for(Map.Entry<Integer, ArrayList<SlotSeedStorage>> metaEntry:seedEntry.getValue().entrySet()) {
                    if(metaEntry!=null && metaEntry.getKey()!=null) {
                        seeds.add(new ItemStack(seedEntry.getKey(), 1, metaEntry.getKey()));
                    }
                }
            }
        }
        return seeds;
    }

    //opening the gui doesn't pause the game
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    //seed button class
    protected static class SeedButton extends GuiButton {
        public ItemStack stack;

        public SeedButton(int id, int xPos, int yPos, ItemStack seedStack) {
            super(id, xPos, yPos, 16, 16, "");
            this.stack = seedStack;
        }

        private ResourceLocation getTexture() {
            return RenderHelper.getItemResource(stack.getItem().getIconFromDamage(stack.getItemDamage()));
        }

        private String getTooltip() {
            return stack.getDisplayName();
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
    }
}
