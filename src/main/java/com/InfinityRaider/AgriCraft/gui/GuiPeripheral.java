package com.InfinityRaider.AgriCraft.gui;

import com.InfinityRaider.AgriCraft.tileentity.peripheral.method.IMethod;
import com.InfinityRaider.AgriCraft.container.ContainerPeripheral;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.peripheral.TileEntityPeripheral;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiPeripheral extends GuiContainer {
    public static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/GuiPeripheral.png");

    public static final int WHITE = 4210752;

    private final TileEntityPeripheral peripheral;
    private final IMethod[] methods;

    private static final int BUTTON_ID_OPEN_GUIDE = 0;
    private static final int BUTTON_ID_SCROLL_DOWN = 1;
    private static final int BUTTON_ID_SCROLL_UP = 2;
    private static final int BUTTON_ID_SCROLL_BOTTOM = 3;
    private static final int BUTTON_ID_SCROLL_TOP = 4;

    private static final int BUTTON_METHOD_OFFSET = 5;
    private static final int BUTTON_AMOUNT = 10;

    private int scrollPosition = 0;
    private boolean guideActive = false;
    private final int guideOffset;

    public GuiPeripheral(InventoryPlayer inventory, TileEntityPeripheral peripheral) {
        super(new ContainerPeripheral(inventory, peripheral));
        this.xSize = 172;
        this.ySize = 176;
        this.peripheral = peripheral;
        this.methods = peripheral.getMethods();
        guideOffset = this.xSize - 4;
    }

    @Override
    public void initGui() {
        super.initGui();
        loadButtonList();
    }

    //draw foreground
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        String name = StatCollector.translateToLocal("agricraft_gui.peripheral");
        //write name: x coordinate is in the middle, 6 down from the top, and setting color to white
        float scale = 0.8F;
        GL11.glScalef(scale, scale, scale);
        this.fontRendererObj.drawString(name, (int) ((9 + this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2) / scale), (int) (6 / scale), WHITE);
        GL11.glScalef(1 / scale, 1 / scale, 1 / scale);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 4, this.ySize - 94 + 2, WHITE);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if(this.peripheral.getProgress() > 0) {
            int state = this.peripheral.getProgressScaled(40);
            drawTexturedModalRect(this.guiLeft + 65, this.guiTop + 78, 0, this.ySize+71, state, 6);
        }
        if(guideActive) {
            drawTexturedModalRect(this.guiLeft + guideOffset, this.guiTop, 172, 0, 83, this.ySize);
            drawScrollBar();
            drawMethodHelp(getActiveMethod());
        }
    }

    protected void drawScrollBar() {
        int total = 160;
        int slotWidth = 16;
        int fullLength = slotWidth*methods.length;
        float unit = ((float) slotWidth)/((float) fullLength)*total;
        int offset = (int) (scrollPosition*unit);
        int length = (int) (BUTTON_AMOUNT*unit);
        int xOffset = this.guiLeft + 242;
        int yOffset = this.guiTop + 8;
        //top part
        this.drawTexturedModalRect(xOffset, yOffset + offset, 0, 253, 5, 1);
        //middle part
        float f = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        int xMax = xOffset + 5;
        int yMin = yOffset + offset + 1;
        int yMax = yOffset + length + offset;
        float uMin = 0*f;
        float uMax = 5*f;
        float vMin = 254*f;
        float vMax = 254*f;
        tessellator.addVertexWithUV(xOffset, yMax, this.zLevel, uMin, vMax);
        tessellator.addVertexWithUV(xMax, yMax, this.zLevel, uMax, vMax);
        tessellator.addVertexWithUV(xMax, yMin, this.zLevel, uMax, vMin);
        tessellator.addVertexWithUV(xOffset, yMin, this.zLevel, uMin, vMin);
        tessellator.draw();
        //bottom part
        this.drawTexturedModalRect(xOffset, yOffset + offset + length, 0, 255, 5, 1);
    }

    private void drawMethodHelp(IMethod method) {
        if(method != null) {
            drawTexturedModalRect(this.guiLeft, this.guiTop + this.ySize - 4, 0, this.ySize, 252, 70);
            int height = fontRendererObj.FONT_HEIGHT;
            this.fontRendererObj.drawString(StatCollector.translateToLocal("agricraft_description.peripheralHelp") + ": " + method.signature(), this.guiLeft + 7, this.guiTop + 175, WHITE);
            float scale = 0.9F;
            GL11.glScalef(scale, scale, scale);
            String[] write = IOHelper.getLinesArrayFromData(IOHelper.splitInLines(this.fontRendererObj, method.getDescription(), 230, scale));
            int x = 4+this.guiLeft+7;
            int y = this.guiTop+175+height;
            for (int i = 0; i < write.length; i++) {
                String line = write[i];
                int yOffset = i * height;
                this.fontRendererObj.drawString(line, (int) (x / scale), (int) (y / scale) + yOffset, WHITE);    //1644054 means black
            }
            GL11.glScalef(1 / scale, 1 / scale, 1 / scale);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadButtonList() {
        this.buttonList.add(new GuiButton(BUTTON_ID_OPEN_GUIDE, this.guiLeft + 154, this.guiTop + 7, 12, 12, "?"));
        this.buttonList.add( new GuiButton(BUTTON_ID_SCROLL_TOP, this.guiLeft + 154, this.guiTop + 20, 10, 10, "\u219F"));
        this.buttonList.add(new GuiButton(BUTTON_ID_SCROLL_UP, this.guiLeft + 154, this.guiTop + 31, 10, 10, "\u2191"));
        this.buttonList.add(new GuiButton(BUTTON_ID_SCROLL_DOWN, this.guiLeft + 154, this.guiTop + 42, 10, 10, "\u2193"));
        this.buttonList.add(new GuiButton(BUTTON_ID_SCROLL_BOTTOM, this.guiLeft + 154, this.guiTop + 53, 10, 10, "\u21A1"));
        for (int i = 0; i < methods.length; i++) {
            this.buttonList.add(new GuiButtonMethod(BUTTON_METHOD_OFFSET + i, this.guiLeft + guideOffset + 3, this.guiTop + 8 + 16 * i, 68, 16, methods[i].getName()));
        }
        updateButtons();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.id > BUTTON_ID_SCROLL_TOP) {
            if(button instanceof GuiButtonMethod) {
                ((GuiButtonMethod) button).onMouseClicked();
            }
        } else {
            switch(button.id) {
                case BUTTON_ID_OPEN_GUIDE:
                    this.guideActive = !guideActive;
                    updateButtons();
                    break;
                case BUTTON_ID_SCROLL_DOWN:
                    scroll(1);
                    break;
                case BUTTON_ID_SCROLL_UP:
                    scroll(-1);
                    break;
                case BUTTON_ID_SCROLL_BOTTOM:
                    scroll(maxScrollPosition());
                    break;
                case BUTTON_ID_SCROLL_TOP:
                    scroll(-scrollPosition);
                    break;
            }
        }
    }

    private void scroll(int amount) {
        int newPosition = scrollPosition + amount;
        int max = maxScrollPosition();
        this.scrollPosition = newPosition<0?0:newPosition>max?max:newPosition;
        updateButtons();
    }

    private void updateButtons() {
        for(int i=1;i<buttonList.size();i++) {
            Object obj = buttonList.get(i);
            if(obj==null || !(obj instanceof GuiButton)) {
                continue;
            }
            GuiButton button = (GuiButton) obj;
            if(button instanceof GuiButtonMethod) {
                if(!guideActive) {
                    ((GuiButtonMethod) button).disable();
                } else {
                    int index = i-BUTTON_ID_SCROLL_TOP-1;
                    if(index>=scrollPosition && index <scrollPosition+BUTTON_AMOUNT) {
                        ((GuiButtonMethod) button).enable(this.guiTop+8+16*(index-scrollPosition));
                    } else {
                        ((GuiButtonMethod) button).disable();
                    }
                }
            } else {
                button.visible = guideActive;
            }
        }
    }

    private int maxScrollPosition() {
        return methods.length-BUTTON_AMOUNT;
    }

    private IMethod getActiveMethod() {
        GuiButtonMethod button = GuiButtonMethod.activeButton;
        if(button == null) {
            return null;
        }
        return methods[button.id-BUTTON_METHOD_OFFSET];
    }

    //opening the gui doesn't pause the game
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        GuiButtonMethod.activeButton = null;
        super.onGuiClosed();
    }

    private static class GuiButtonMethod extends GuiButton {
        private static GuiButtonMethod activeButton;

        public GuiButtonMethod(int id, int x, int y, int xSize, int ySize, String display) {
            super(id, x, y, xSize, ySize, display);
        }

        public void onMouseClicked() {
            activeButton = activeButton==this?null:this;
        }

        public boolean isActive() {
            return this == activeButton;
        }

        public void enable(int posY) {
            this.yPosition = posY;
            this.visible = true;
        }

        public void disable() {
            this.visible = false;
        }

        @Override
        public void drawButton(Minecraft minecraft, int x, int y) {
            if (this.visible) {
                FontRenderer fontrenderer = minecraft.fontRenderer;
                minecraft.getTextureManager().bindTexture(buttonTextures);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.field_146123_n = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
                int k = this.getHoverState(this.field_146123_n);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                int vOffset = isActive()?86:46 + k * 20;
                this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, vOffset, this.width / 2, this.height);
                this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, vOffset, this.width / 2, this.height);
                this.mouseDragged(minecraft, x, y);
                int l = 14737632;
                if (packedFGColour != 0) {
                    l = packedFGColour;
                }
                else if (!this.enabled) {
                    l = 10526880;
                }
                else if (this.field_146123_n) {
                    l = 16777120;
                }
                float scale = 0.6F;
                GL11.glScalef(scale, scale, scale);
                this.drawCenteredString(fontrenderer, this.displayString, (int) ((this.xPosition+this.width/2)/scale), (int) ((this.yPosition+(this.height-4)/2)/ scale), l);
                GL11.glScalef(1/scale, 1/scale, 1/scale);
            }
        }
    }
}
