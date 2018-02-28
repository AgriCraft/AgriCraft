package com.infinityraider.agricraft.gui;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.tiles.TileEntityPeripheral;
import com.infinityraider.agricraft.container.ContainerSeedAnalyzer;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.utility.GuiHelper;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.infinitylib.render.tessellation.TessellatorVertexBuffer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import com.infinityraider.agricraft.api.v1.misc.IAgriPeripheralMethod;

@SideOnly(Side.CLIENT)
public class GuiPeripheral extends GuiContainer {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_peripheral.png");

    public static final int WHITE = 4210752;

    private final TileEntityPeripheral peripheral;
    private final IAgriPeripheralMethod[] methods;

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
        super(new ContainerSeedAnalyzer(peripheral, inventory, ContainerSeedAnalyzer.SeedAnalyzerLayout.PERIPHERAL));
        this.xSize = 172;
        this.ySize = 176;
        this.peripheral = peripheral;
        this.methods = AgriApi.getPeripheralMethodRegistry().all().toArray(new IAgriPeripheralMethod[0]);
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
        String name = AgriCore.getTranslator().translate("agricraft_gui.peripheral");
        //write name: x coordinate is in the middle, 6 down from the top, and setting color to white
        float scale = 0.8F;
        GL11.glScalef(scale, scale, scale);
        this.fontRenderer.drawString(name, (int) ((9 + this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2) / scale), (int) (6 / scale), WHITE);
        GL11.glScalef(1 / scale, 1 / scale, 1 / scale);
        this.fontRenderer.drawString(net.minecraft.client.resources.I18n.format("container.inventory"), 4, this.ySize - 94 + 2, WHITE);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if (this.peripheral.getProgress() > 0) {
            int state = this.peripheral.getProgressScaled(40);
            drawTexturedModalRect(this.guiLeft + 65, this.guiTop + 78, 0, this.ySize + 71, state, 6);
        }
        if (guideActive) {
            drawTexturedModalRect(this.guiLeft + guideOffset, this.guiTop, 172, 0, 83, this.ySize);
            drawScrollBar();
            drawMethodHelp(getActiveMethod());
        }
    }

    protected void drawScrollBar() {
        int total = 160;
        int slotWidth = 16;
        int fullLength = slotWidth * methods.length;
        float unit = ((float) slotWidth) / ((float) fullLength) * total;
        int offset = (int) (scrollPosition * unit);
        int length = (int) (BUTTON_AMOUNT * unit);
        int xOffset = this.guiLeft + 242;
        int yOffset = this.guiTop + 8;
        //top part
        this.drawTexturedModalRect(xOffset, yOffset + offset, 0, 253, 5, 1);
        //middle part
        float f = 0.00390625F;
        ITessellator tessellator = TessellatorVertexBuffer.getInstance();
        tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);
        int xMax = xOffset + 5;
        int yMin = yOffset + offset + 1;
        int yMax = yOffset + length + offset;
        float uMin = 0 * f;
        float uMax = 5 * f;
        float vMin = 254 * f;
        float vMax = 254 * f;
        tessellator.addVertexWithUV(xOffset, yMax, this.zLevel, uMin, vMax);
        tessellator.addVertexWithUV(xMax, yMax, this.zLevel, uMax, vMax);
        tessellator.addVertexWithUV(xMax, yMin, this.zLevel, uMax, vMin);
        tessellator.addVertexWithUV(xOffset, yMin, this.zLevel, uMin, vMin);
        tessellator.draw();
        //bottom part
        this.drawTexturedModalRect(xOffset, yOffset + offset + length, 0, 255, 5, 1);
    }

    private void drawMethodHelp(IAgriPeripheralMethod method) {
        if (method != null) {
            drawTexturedModalRect(this.guiLeft, this.guiTop + this.ySize - 4, 0, this.ySize, 252, 70);
            this.fontRenderer.drawString(AgriCore.getTranslator().translate("agricraft_description.peripheralHelp") + ": " + method.getSignature(), this.guiLeft + 7, this.guiTop + 175, WHITE);
            float scale = 0.9F;
            GL11.glScalef(scale, scale, scale);
            List<String> write = GuiHelper.getLinesFromData(GuiHelper.splitInLines(this.fontRenderer, method.getDescription(), 230, scale));
            int x = 4 + this.guiLeft + 7;
            int y = this.guiTop + 175 + fontRenderer.FONT_HEIGHT;
            for (int i = 0; i < write.size(); i++) {
                String line = write.get(i);
                int yOffset = i * fontRenderer.FONT_HEIGHT;
                this.fontRenderer.drawString(line, (int) (x / scale), (int) (y / scale) + yOffset, WHITE);    //1644054 means black
            }
            GL11.glScalef(1 / scale, 1 / scale, 1 / scale);
        }
    }

    private void loadButtonList() {
        this.buttonList.add(new GuiButton(BUTTON_ID_OPEN_GUIDE, this.guiLeft + 154, this.guiTop + 7, 12, 12, "?"));
        this.buttonList.add(new GuiButton(BUTTON_ID_SCROLL_TOP, this.guiLeft + 154, this.guiTop + 20, 10, 10, "\u219F"));
        this.buttonList.add(new GuiButton(BUTTON_ID_SCROLL_UP, this.guiLeft + 154, this.guiTop + 31, 10, 10, "\u2191"));
        this.buttonList.add(new GuiButton(BUTTON_ID_SCROLL_DOWN, this.guiLeft + 154, this.guiTop + 42, 10, 10, "\u2193"));
        this.buttonList.add(new GuiButton(BUTTON_ID_SCROLL_BOTTOM, this.guiLeft + 154, this.guiTop + 53, 10, 10, "\u21A1"));
        for (int i = 0; i < methods.length; i++) {
            this.buttonList.add(new GuiButtonMethod(BUTTON_METHOD_OFFSET + i, this.guiLeft + guideOffset + 3, this.guiTop + 8 + 16 * i, 68, 16, methods[i].getId()));
        }
        updateButtons();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id > BUTTON_ID_SCROLL_TOP) {
            if (button instanceof GuiButtonMethod) {
                ((GuiButtonMethod) button).onMouseClicked();
            }
        } else {
            switch (button.id) {
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
                default:
                    // Do nothing.
                    break;
            }
        }
    }

    private void scroll(int amount) {
        int newPosition = scrollPosition + amount;
        int max = maxScrollPosition();
        this.scrollPosition = newPosition < 0 ? 0 : newPosition > max ? max : newPosition;
        updateButtons();
    }

    private void updateButtons() {
        for (int i = 1; i < buttonList.size(); i++) {
            Object obj = buttonList.get(i);
            if (obj == null) {
                continue;
            }
            GuiButton button = (GuiButton) obj;
            if (button instanceof GuiButtonMethod) {
                if (!guideActive) {
                    ((GuiButtonMethod) button).disable();
                } else {
                    int index = i - BUTTON_ID_SCROLL_TOP - 1;
                    if (index >= scrollPosition && index < scrollPosition + BUTTON_AMOUNT) {
                        ((GuiButtonMethod) button).enable(this.guiTop + 8 + 16 * (index - scrollPosition));
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
        return methods.length - BUTTON_AMOUNT;
    }

    private IAgriPeripheralMethod getActiveMethod() {
        GuiButtonMethod button = GuiButtonMethod.activeButton;
        if (button == null) {
            return null;
        }
        return methods[button.id - BUTTON_METHOD_OFFSET];
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
            activeButton = activeButton == this ? null : this;
        }

        public boolean isActive() {
            return this == activeButton;
        }

        public void enable(int posY) {
            this.y = posY;
            this.visible = true;
        }

        public void disable() {
            this.visible = false;
        }

        public void drawButton(Minecraft minecraft, int x, int y) {
            if (this.visible) {
                FontRenderer fontrenderer = minecraft.fontRenderer;
                minecraft.getTextureManager().bindTexture(BUTTON_TEXTURES);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
                int k = this.getHoverState(this.isMouseOver());
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                int vOffset = isActive() ? 86 : 46 + k * 20;
                this.drawTexturedModalRect(this.x, this.y, 0, vOffset, this.width / 2, this.height);
                this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, vOffset, this.width / 2, this.height);
                this.mouseDragged(minecraft, x, y);
                int l = 14737632;
                if (packedFGColour != 0) {
                    l = packedFGColour;
                } else if (!this.enabled) {
                    l = 10526880;
                } else if (this.hovered) {
                    l = 16777120;
                }
                float scale = 0.6F;
                GL11.glScalef(scale, scale, scale);
                this.drawCenteredString(fontrenderer, this.displayString, (int) ((this.x + this.width / 2) / scale), (int) ((this.y + (this.height - 4) / 2) / scale), l);
                GL11.glScalef(1 / scale, 1 / scale, 1 / scale);
            }
        }
    }
}
