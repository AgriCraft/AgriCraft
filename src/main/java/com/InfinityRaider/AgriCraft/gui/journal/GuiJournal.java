package com.InfinityRaider.AgriCraft.gui.journal;

import com.InfinityRaider.AgriCraft.gui.Component;
import com.InfinityRaider.AgriCraft.items.ItemJournal;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class GuiJournal extends GuiScreen {
    /** Some dimensions and constants */
    private static final int MINIMUM_PAGES = 2;
    private int xSize;
    private int ySize;
    private int guiLeft;
    private int guiTop;

    /** Current page */
    private int currentPageNumber;
    private JournalPage currentPage;

    /**Stuff to render */
    ArrayList<Component<String>> textComponents;
    ArrayList<Component<ResourceLocation>> textureComponents;
    ArrayList<Component<ItemStack>> itemComponents;
    HashMap<ResourceLocation, ArrayList<Component<IIcon>>> iconComponents;

    private ItemStack journal;

    public GuiJournal(ItemStack journal) {
        super();
        this.journal = journal;
        int pageWidth = 128;
        this.xSize = pageWidth * 2;
        this.ySize = pageWidth * 3 / 2;
    }

    @Override
    public void initGui() {
        //half of the screen size minus the gui size to centre the gui, the -16 is to ignore the players item bar
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - 16 - this.ySize) / 2;
        currentPage = getCurrentPage();
        textComponents = currentPage.getTextComponents();
        textureComponents = currentPage.getTextureComponents();
        itemComponents = currentPage.getItemComponents();
        iconComponents = new HashMap<ResourceLocation, ArrayList<Component<IIcon>>>();
        for(ResourceLocation textureMap:currentPage.getTextureMaps()) {
            if(textureMap == null) {
                continue;
            }
            iconComponents.put(textureMap, currentPage.getIconComponents(textureMap));
        }
    }

    @Override
    public void drawScreen(int x, int y, float opacity) {
        //draw background
        drawBackground(0);
        //draw foreground
        drawTexture(currentPage.getForeground());
        //draw text components
        if(textComponents != null) {
            for(Component<String> textComponent:textComponents) {
                drawTextComponent(textComponent);
            }
        }
        //draw icon components
        if(textureComponents != null) {
            for(Component<ResourceLocation> iconComponent: textureComponents) {
                drawTextureComponent(iconComponent);
            }
        }
        if(iconComponents != null) {
            for(Map.Entry<ResourceLocation, ArrayList<Component<IIcon>>> entry:iconComponents.entrySet()) {
                if(entry.getValue() == null) {
                    continue;
                }
                Minecraft.getMinecraft().renderEngine.bindTexture(entry.getKey());
                for(Component<IIcon> component: entry.getValue()) {
                    drawIconComponent(component);
                }
            }
        }
        //draw item components
        if(itemComponents != null) {
            for(Component<ItemStack> itemComponent:itemComponents) {
                drawItemComponent(itemComponent);
            }
        }
        //draw navigation arrows
        drawNavigationArrows(x, y);
        //draw tooltip
        ArrayList<String> toolTip = currentPage.getTooltip(x-this.guiLeft, y-this.guiTop);
        if(toolTip != null) {
            this.drawTooltip(toolTip, x, y);
        }
    }

    @Override
    public void mouseClicked(int x, int y, int rightClick) {
        //find number of pages to browse
        int pageIncrement = 0;
        //clicked for next page or previous page
        if (y > this.guiTop + 172 && y <= this.guiTop + 172 + 16 && rightClick == 0) {
            if (x > this.guiLeft + 221 && x <= this.guiLeft + 221 + 16) {
                //next page
                pageIncrement = 1;
            } else if (x > this.guiLeft + 19 && x <= this.guiLeft + 19 + 16 && this.currentPageNumber > 0) {
                //prev page
                pageIncrement = -1;
            }
        //clicked to browse from within the page
        } else {
            pageIncrement = getCurrentPage().getPagesToBrowseOnMouseClick(x - this.guiLeft, y - this.guiTop);
        }
        //go to new page
        int newPage = currentPageNumber + pageIncrement;
        newPage = Math.max(0, newPage); //don't go negative
        newPage = Math.min(newPage, getNumberOfPages()-1); //don't go outside array bounds
        if(newPage != currentPageNumber) {
            this.currentPageNumber = newPage;
            this.initGui();
        }
    }

    private JournalPage getCurrentPage() {
        switch(currentPageNumber) {
            case 0: return new JournalPageTitle();
            case 1: return new JournalPageIntroduction();
        }
        ArrayList<ItemStack> discoveredSeeds = getDiscoveredSeeds();
        return new JournalPageSeed(discoveredSeeds, currentPageNumber - MINIMUM_PAGES);
    }

    private ArrayList<ItemStack> getDiscoveredSeeds() {
        return ((ItemJournal) journal.getItem()).getDiscoveredSeeds(journal);
    }

    private int getNumberOfPages() {
        return MINIMUM_PAGES+getDiscoveredSeeds().size();
    }

    @Override
    public void drawBackground(int i) {
        this.drawTexture(JournalPage.getBackground());
    }

    private void drawTexture(ResourceLocation texture) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    private void drawTextComponent(Component<String> component) {
        if(component != null) {
            float scale = component.scale();
            int x = this.guiLeft + component.xOffset();
            int y = this.guiTop + component.yOffset();
            String text[] = IOHelper.getLinesArrayFromData(component.getComponent());
            GL11.glScalef(scale, scale, scale);
            for (String paragraph : text) {
                String[] write = IOHelper.getLinesArrayFromData(IOHelper.splitInLines(this.fontRendererObj, paragraph, 95, scale));
                for (int i = 0; i < write.length; i++) {
                    String line = write[i];
                    int xOffset = component.centered() ? -fontRendererObj.getStringWidth(line) / 2 : 0;
                    int yOffset = i * this.fontRendererObj.FONT_HEIGHT;
                    this.fontRendererObj.drawString(line, (int) (x / scale) + xOffset, (int) (y / scale) + yOffset, 1644054);    //1644054 means black
                }
                y = y + (int) ((float) this.fontRendererObj.FONT_HEIGHT / scale);
            }
            GL11.glScalef(1 / scale, 1 / scale, 1 / scale);
        }
    }

    private void drawTextureComponent(Component<ResourceLocation> component) {
        if(component != null) {
            ResourceLocation texture = component.getComponent();
            int xSize = component.xSize();
            int ySize = component.ySize();
            int x = guiLeft + component.xOffset();
            int y = guiTop + component.yOffset();
            Tessellator tessellator = Tessellator.instance;
            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
            GL11.glColor3f(1, 1, 1);
            GL11.glDisable(GL11.GL_LIGHTING);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(x, y + ySize, this.zLevel, 0, 1);
            tessellator.addVertexWithUV(x + xSize, y + ySize, this.zLevel, 1, 1);
            tessellator.addVertexWithUV(x + xSize, y, this.zLevel, 1, 0);
            tessellator.addVertexWithUV(x, y, this.zLevel, 0, 0);
            tessellator.draw();
            GL11.glEnable(GL11.GL_LIGHTING);
        }
    }

    private void drawItemComponent(Component<ItemStack> component) {
        if(component != null) {
            int x = this.guiLeft + component.xOffset();
            int y = this.guiTop + component.yOffset();
            ItemStack stack = component.getComponent();
            GuiScreen.itemRender.renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), stack, x, y);
        }
    }

    private void drawIconComponent(Component<IIcon> component) {
        if(component != null) {
            IIcon icon = component.getComponent();
            int xSize = component.xSize();
            int ySize = component.ySize();
            int x = guiLeft + component.xOffset();
            int y = guiTop + component.yOffset();
            Tessellator tessellator = Tessellator.instance;
            GL11.glColor3f(1, 1, 1);
            GL11.glDisable(GL11.GL_LIGHTING);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(x, y + ySize, this.zLevel, icon.getMinU(), icon.getMaxV());
            tessellator.addVertexWithUV(x + xSize, y + ySize, this.zLevel, icon.getMaxU(), icon.getMaxV());
            tessellator.addVertexWithUV(x + xSize, y, this.zLevel, icon.getMaxU(), icon.getMinV());
            tessellator.addVertexWithUV(x, y, this.zLevel, icon.getMinU(), icon.getMinV());
            tessellator.draw();
            GL11.glEnable(GL11.GL_LIGHTING);
        }
    }

    private void drawNavigationArrows(int x, int y) {
        if (y > this.guiTop + 172 && y <= this.guiTop + 172 + 16) {
            if (x > this.guiLeft + 221 && x <= this.guiLeft + 221 + 16) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(JournalPage.getBackground());
                GL11.glColor3f(1, 1, 1);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                drawTexturedModalRect(this.guiLeft + 223, this.guiTop + 178, 224, 239, 32, 17);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_LIGHTING);
            } else if (x > this.guiLeft + 19 && x <= this.guiLeft + 19 + 16 && this.currentPageNumber > 0) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(JournalPage.getBackground());
                GL11.glColor3f(1, 1, 1);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                drawTexturedModalRect(this.guiLeft + 1, this.guiTop + 178, 0, 239, 32, 17);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_LIGHTING);
            }
        }
    }

    private void drawTooltip(ArrayList<String> toolTip, int x, int y) {
        drawHoveringText(toolTip, x, y, fontRendererObj);
    }


    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
