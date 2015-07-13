package com.InfinityRaider.AgriCraft.gui.journal;

import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.gui.Component;
import com.InfinityRaider.AgriCraft.items.ItemJournal;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class GuiJournal extends GuiScreen {
    private static final int MINIMUM_PAGES = 2;
    private int xSize;
    private int ySize;
    private int guiLeft;
    private int guiTop;

    private int currentPage;

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
    }

    @Override
    public void drawScreen(int x, int y, float opacity) {
        JournalPage page = getCurrentPage();
        //draw background
        drawBackground(0);
        //draw foreground
        drawTexture(page.getForeground());
        //draw text components
        ArrayList<Component<String>> textComponents = page.getTextComponents();
        if(textComponents != null) {
            for(Component<String> textComponent:textComponents) {
                drawTextComponent(textComponent);
            }
        }
        //draw icon components
        ArrayList<Component<ResourceLocation>> iconComponents = page.getTextureComponents();
        if(iconComponents != null) {
            for(Component<ResourceLocation> iconComponent:iconComponents) {
                drawTextureComponent(iconComponent);
            }
        }
        //draw item components
        ArrayList<Component<ItemStack>> itemComponents = page.getItemComponents();
        if(itemComponents != null) {
            for(Component<ItemStack> itemComponent:itemComponents) {
                drawItemComponent(itemComponent);
            }
        }
        //draw navigation arrows
        drawNavigationArrows(x, y);
        //draw tooltip
        ArrayList<String> toolTip = page.getTooltip(x-this.guiLeft, y-this.guiTop);
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
            } else if (x > this.guiLeft + 19 && x <= this.guiLeft + 19 + 16 && this.currentPage > 0) {
                //prev page
                pageIncrement = -1;
            }
        //clicked to browse from within the page
        } else {
            pageIncrement = getCurrentPage().getPagesToBrowseOnMouseClick(x-this.guiLeft, y-this.guiTop);
        }
        //go to new page
        int newPage = currentPage + pageIncrement;
        newPage = Math.max(0, newPage); //don't go negative
        newPage = Math.min(newPage, getNumberOfPages()-1); //don't go outside array bounds
        if(newPage != currentPage) {
            this.currentPage = newPage;
        }
    }

    private JournalPage getCurrentPage() {
        switch(currentPage) {
            case 0: return new JournalPageTitle();
            case 1: return new JournalPageIntroduction();
        }
        ArrayList<ItemStack> discoveredSeeds = getDiscoveredSeeds();
        return new JournalPageSeed(discoveredSeeds, currentPage - MINIMUM_PAGES);
    }

    private ArrayList<ItemStack> getDiscoveredSeeds() {
        ArrayList<ItemStack> seeds = new ArrayList<ItemStack>();
        NBTTagCompound tag = null;
        if (journal != null && journal.stackSize > 0 && journal.getItem() instanceof ItemJournal && journal.hasTagCompound()) {
            tag = journal.getTagCompound();
        }
        if(tag != null) {
            if (tag.hasKey(Names.NBT.discoveredSeeds)) {
                NBTTagList tagList = tag.getTagList(Names.NBT.discoveredSeeds, 10);      //10 for tagCompound
                for (int i = 0; i < tagList.tagCount(); i++) {
                    ItemStack seed = ItemStack.loadItemStackFromNBT(tagList.getCompoundTagAt(i));
                    if(CropPlantHandler.isValidSeed(seed)) {
                        seeds.add(seed);
                    }
                }
            }
        }
        return seeds;
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
                String[] write = IOHelper.getLinesArrayFromData(this.splitInLines(paragraph, scale));
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
            } else if (x > this.guiLeft + 19 && x <= this.guiLeft + 19 + 16 && this.currentPage > 0) {
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

    //utility method: splits the string in different lines so it will fit on the page
    private String splitInLines(String input, float scale) {
        float maxWidth = 95 / scale;
        String notProcessed = input;
        String output = "";
        while (this.fontRendererObj.getStringWidth(notProcessed) > maxWidth) {
            int index = 0;
            if (notProcessed != null && !notProcessed.equals("")) {
                //find the first index at which the string exceeds the size limit
                while (notProcessed.length() - 1 > index && this.fontRendererObj.getStringWidth(notProcessed.substring(0, index)) < maxWidth) {
                    index = (index + 1) < notProcessed.length() ? index + 1 : index;
                }
                //go back to the first space to cut the string in two lines
                while (index>0 && notProcessed.charAt(index) != ' ') {
                    index--;
                }
                //update the data for the next iteration
                output = output.equals("") ? output : output + '\n';
                output = output + notProcessed.substring(0, index);
                notProcessed = notProcessed.length() > index + 1 ? notProcessed.substring(index + 1) : notProcessed;
            }
        }
        return output + '\n' + notProcessed;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
