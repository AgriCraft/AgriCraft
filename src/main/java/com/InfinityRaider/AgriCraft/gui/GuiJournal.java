package com.InfinityRaider.AgriCraft.gui;

import com.InfinityRaider.AgriCraft.farming.GrowthRequirements;
import com.InfinityRaider.AgriCraft.farming.mutation.Mutation;
import com.InfinityRaider.AgriCraft.farming.mutation.MutationHandler;
import com.InfinityRaider.AgriCraft.items.ItemJournal;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.BlockWithMeta;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiJournal extends GuiScreen {
    //the gui textures
    public static final ResourceLocation textureBackground = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/journal/GuiJournalBackground.png");
    public static final ResourceLocation textureFrontPage = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/journal/GuiJournalFrontPage.png");
    public static final ResourceLocation textureTableOfContents = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/journal/GuiJournalTableOfContents.png");
    public static final ResourceLocation textureIntroduction = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/journal/GuiJournalIntroduction.png");
    public static final ResourceLocation textureSeedPage = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/journal/GuiJournalSeedPage.png");
    //needed data
    protected EntityPlayer player;
    protected ItemStack[] discoveredSeeds;
    protected ItemStack[][] discoveredParents;
    protected ItemStack[] discoveredCoParents;
    protected ItemStack[] discoveredMutations;
    protected ItemStack[] fruits;
    //some gui properties and dimensions
    protected int currentPage = 0;
    protected final int standardPages = 2;
    private int xSize;
    private int ySize;
    private int guiLeft;
    private int guiTop;
    private final int black = 1644054;
    private int textStart;
    //the textures for a seed page
    protected IIcon[] plantIcons;
    protected IIcon seedIcon;
    protected IIcon[][] parentsIcons;
    protected IIcon[] coParentsIcons;
    protected IIcon[] mutationIcons;
    protected IIcon[] fruitIcons;
    //list of buttons
    protected List buttonList = new ArrayList();


    public GuiJournal(EntityPlayer player) {
        super();
        this.player = player;
        int pageWidth = 128;
        this.xSize = pageWidth *2;
        this.ySize = pageWidth *3/2;
    }

    //gets the array of discovered seeds
    private void setDataFromNBT() {
        if(this.player.getCurrentEquippedItem()!=null && this.player.getCurrentEquippedItem().stackSize>0 && this.player.getCurrentEquippedItem().getItem() instanceof ItemJournal && this.player.getCurrentEquippedItem().hasTagCompound()) {
            NBTTagCompound tag = this.player.getCurrentEquippedItem().getTagCompound();
            if(tag.hasKey(Names.NBT.discoveredSeeds)) {
                NBTTagList tagList = tag.getTagList(Names.NBT.discoveredSeeds, 10);      //10 for tagCompound
                this.discoveredSeeds = new ItemStack[tagList.tagCount()];
                for(int i=0;i<this.discoveredSeeds.length;i++) {
                    this.discoveredSeeds[i] = ItemStack.loadItemStackFromNBT(tagList.getCompoundTagAt(i));
                }

            }
            else {
                this.discoveredSeeds = new ItemStack[0];
            }
            if(tag.hasKey(Names.NBT.currentPage)) {
                this.currentPage = tag.getShort(Names.NBT.currentPage);
            }
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int  rightClick) {
        boolean flipPage = false;
        //clicked for next page or previous page
        if(y>this.guiTop+172 && y<=this.guiTop+172+16 && rightClick==0) {
            if(x>this.guiLeft+221 && x<=this.guiLeft+221+16) {
                //next page
                if(this.currentPage<this.standardPages-1) {
                    this.currentPage = this.currentPage + 1;
                    flipPage = true;
                }
                else if(this.discoveredSeeds!=null && this.currentPage<this.discoveredSeeds.length+this.standardPages-1) {
                    this.currentPage = this.currentPage + 1;
                    flipPage = true;
                }
            }
            else if(x>this.guiLeft+19 && x<=this.guiLeft+19+16 && this.currentPage>0) {
                //prev page
                this.currentPage = this.currentPage-1;
                flipPage = true;
            }
        }
        else if(this.getSeedAtCoordinates(x, y)!=null && this.getSeedAtCoordinates(x,y).getItem()!=null) {
            ItemStack seed = this.getSeedAtCoordinates(x, y);
            int page = this.getPage(seed);
            this.currentPage = page>=0?page:this.currentPage;
            flipPage = true;
        }

        if(flipPage) {
            initPage(currentPage - standardPages);
        }
    }

    //initializes some values when the gui is opened
    @Override
    public void initGui() {
        super.initGui();
        //half of the screen size minus the gui size to centre the gui, the -16 is to ignore the players item bar
        this.guiLeft = (this.width - this.xSize)/2;
        this.guiTop = (this.height - 16 - this.ySize)/2;
        this.textStart = this.guiLeft + 29;
        //gets the discovered seeds
        this.setDataFromNBT();

    }

    //loads textures and initializes buttons
    private void initPage(int index) {
        this.buttonList = new ArrayList();
        if(currentPage>=standardPages) {
            getSeedTextures(index);
        }
    }

    //draws the gui
    @Override
    public void drawScreen(int x, int y, float opacity) {
        //draw the background
        this.drawBackground(0);
        //draw the contents of a page based on the current page
        switch(currentPage) {
            case 0: this.drawTitleScreen(); break;
            //case 1: this.drawTableOfContents(); break;
            case 1: this.drawIntroduction(); break;
            default: this.drawSeedPage(currentPage - standardPages);
        }
        //draw the arrows if the mouse is hovering over the flip page area
        if(y>this.guiTop+172 && y<=this.guiTop+172+16) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(textureBackground);
            GL11.glColor3f(1, 1, 1);
            GL11.glDisable(GL11.GL_LIGHTING);
            if(x>this.guiLeft+221 && x<=this.guiLeft+221+16) {
                drawTexturedModalRect(this.guiLeft+223, this.guiTop+178, 224, 239, 32, 17);
            }
            else if(x>this.guiLeft+19 && x<=this.guiLeft+19+16 && this.currentPage>0) {
                drawTexturedModalRect(this.guiLeft+1, this.guiTop+178, 0, 239, 32, 17);
            }
            GL11.glEnable(GL11.GL_LIGHTING);
        }
        //draws a tooltip if the mouse is hovering over an icon
        this.drawTooltip(x, y);
    }

    //draws a tooltip
    private void drawTooltip(int x, int y) {
        String[] tooltip = new String[1];
        //mutation tooltips
        if(this.currentPage>=this.standardPages) {
            //check if hovering over a seed
            ItemStack seed = getSeedAtCoordinates(x, y);
            if(seed!=null && seed.getItem()!=null) {
                tooltip[0] = seed.getDisplayName();
            }
            //check if hovering over fruit
            else if(y>this.guiTop+91 && y<=this.guiTop+91+16) {
                int xOffset = x-this.guiLeft-30;
                int index = (int) Math.floor(((float) xOffset) / 24);
                if(index>=0 && index<this.fruits.length) {
                    if(this.fruits[index]!=null && this.fruits[index].getItem()!=null) {
                        tooltip[0] = this.fruits[index].getDisplayName();
                    }
                }
            }
        }

        if(tooltip[0]!=null && !tooltip[0].equals("")) {
            @SuppressWarnings("rawtypes")
            List list = Arrays.asList(tooltip);
            drawHoveringText(list, x, y, fontRendererObj);
        }
    }

    //returns the page nr for a given seed
    private int getPage(ItemStack seed) {
        if(this.isSeedDiscovered(seed)) {
            for(int i=0;i<this.discoveredSeeds.length;i++) {
                if(this.discoveredSeeds[i].getItem()==seed.getItem() && this.discoveredSeeds[i].getItemDamage()==seed.getItemDamage()) {
                    return (i+standardPages);
                }
            }
        }
        return -1;
    }

    //checks what seed icon is drawn on a given x- and y-coordinate, returns null if no seed is found
    private ItemStack getSeedAtCoordinates(int x, int y) {
        ItemStack output = null;
        if(this.currentPage>=this.standardPages) {
            int xOffset = this.guiLeft + 132;
            int yOffset = 20;
            int column = 0;
            //check if the xPosition is over one of the three columns
            if (x > xOffset && x <= xOffset + 16) {
                column = 1;
            } else if (x > xOffset + 35 && x <= xOffset + 35 + 16) {
                column = 2;
            } else if (x > xOffset + 69 && x <= xOffset + 69 + 16) {
                column = 3;
            }
            if (column > 0) {
                int correctedY = (y - 1 - this.guiTop - yOffset);
                int arrayIndex = (int) Math.floor(((float) correctedY) / 20);
                if (arrayIndex >= 0 && arrayIndex < discoveredParents.length) {
                    output = column == 3 ? discoveredSeeds[currentPage - standardPages] : discoveredParents[arrayIndex][column - 1];
                } else if (arrayIndex >= discoveredParents.length && arrayIndex < discoveredParents.length + discoveredCoParents.length) {
                    switch (column) {
                        case 1:
                            output = discoveredSeeds[currentPage - standardPages];
                            break;
                        case 2:
                            output = discoveredCoParents[arrayIndex - discoveredParents.length];
                            break;
                        case 3:
                            output = discoveredMutations[arrayIndex - discoveredParents.length];
                            break;
                    }
                }
            }
        }
        return output;
    }

    //draws the title screen
    private void drawTitleScreen() {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(textureFrontPage);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    //draws the table of contents
    private void drawTableOfContents() {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(textureTableOfContents);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    //draws the introduction
    private void drawIntroduction() {
        //background
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(textureIntroduction);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        //the text
        int xOffset = this.guiLeft + 24;
        int yOffset = this.guiTop + 28;
        float scale = 0.5F;
        String introduction[] = IOHelper.getLinesArrayFromData(StatCollector.translateToLocal("agricraft_journal.introduction"));
        int yRenderPos = (int) ((float) yOffset / scale);
        GL11.glScalef(scale, scale, scale);
        for(String paragraph:introduction) {
            String[] write = IOHelper.getLinesArrayFromData(this.splitInLines(paragraph, scale));
            for (String line:write) {
                this.fontRendererObj.drawString(line, (int) (xOffset / scale), yRenderPos, this.black);
                yRenderPos = yRenderPos + this.fontRendererObj.FONT_HEIGHT;
            }
            yRenderPos = yRenderPos+ (int) ((float) this.fontRendererObj.FONT_HEIGHT/scale);
        }
        GL11.glScalef(1/scale, 1/scale, 1/scale);
    }

    //draws the page for a discovered seed
    private void drawSeedPage(int index) {
        if(this.discoveredSeeds!=null && this.discoveredSeeds.length>index) {
            //draw the pages
            GL11.glColor4f(1F, 1F, 1F, 1F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(textureSeedPage);
            drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
            //draw the title bar
            drawSeedTitle(index);
            //write some information
            writeSeedInformation(index);
            //write the tier
            writeSeedTier(index);
            //draw the fruits
            drawSeedFruits();
            //draw the plant stages
            drawSeedGrowthStages();
            //draw the mutations
            drawSeedMutations();
        }
    }

    //loads seed textures for a seed page
    private void getSeedTextures(int index) {
        ItemStack seed = discoveredSeeds[index];
        //get the seed icon
        seedIcon = RenderHelper.getIcon(seed.getItem(), seed.getItemDamage());
        //get the fruit icons
        //ArrayList<ItemStack> fruitDrops = SeedHelper.getPlantFruits((ItemSeeds) seed.getItem(), player.getEntityWorld(), player.serverPosX, player.serverPosY, player.serverPosZ, 1, seed.getItemDamage());     //serverPosX, Y, Z are client only, but this gui only gets called client side, so no problem
        ArrayList<ItemStack> fruitDrops = SeedHelper.getAllPlantFruits((ItemSeeds) seed.getItem(), player.getEntityWorld(), player.serverPosX, player.serverPosY, player.serverPosZ, 1, seed.getItemDamage());
        fruitIcons = new IIcon[fruitDrops.size()];
        this.fruits = new ItemStack[fruitDrops.size()];
        for(int i=0;i< fruitIcons.length;i++) {
            this.fruits[i] = fruitDrops.get(i);
            if(fruits[i]!=null && fruits[i].getItem()!=null) {
                this.fruitIcons[i] = RenderHelper.getIcon(fruitDrops.get(i).getItem(), fruitDrops.get(i).getItemDamage());
            }
        }
        //get the growth stage icons
        plantIcons = new IIcon[8];
        for(int i=0;i< plantIcons.length;i++) {
            plantIcons[i] = RenderHelper.getIcon(SeedHelper.getPlant((ItemSeeds) seed.getItem()), RenderHelper.plantIconIndex((ItemSeeds) seed.getItem(), seed.getItemDamage(), i));
        }
        //get the icons for the parents that mutate into this seed
        Mutation[] parents = MutationHandler.getParentMutations(seed);
        ArrayList<IIcon> iconList0 = new ArrayList<IIcon>();
        ArrayList<IIcon> IconList1 = new ArrayList<IIcon>();
        ArrayList<ItemStack> list0 = new ArrayList<ItemStack>();
        ArrayList<ItemStack> list1 = new ArrayList<ItemStack>();
        for (Mutation parentCouple:parents) {
            if (this.isSeedDiscovered(parentCouple.parent1) && this.isSeedDiscovered(parentCouple.parent2)) {
                iconList0.add(RenderHelper.getIcon(parentCouple.parent1.getItem(), parentCouple.parent1.getItemDamage()));
                IconList1.add(RenderHelper.getIcon(parentCouple.parent2.getItem(), parentCouple.parent2.getItemDamage()));
                list0.add(parentCouple.parent1);
                list1.add(parentCouple.parent2);
            }
        }
        parentsIcons = new IIcon[iconList0.size()][2];
        discoveredParents = new ItemStack[list0.size()][2];
        for(int i=0;i<iconList0.size();i++) {
            parentsIcons[i][0]=iconList0.get(i);
            parentsIcons[i][1]=IconList1.get(i);
            discoveredParents[i][0]=list0.get(i);
            discoveredParents[i][1]=list1.get(i);
        }
        //get the icons for the co parents and the mutations this seed can mutate to
        Mutation[] mutations = MutationHandler.getMutations(seed);
        ArrayList<IIcon> coParentsIconList = new ArrayList<IIcon>();
        ArrayList<IIcon> mutationsIconList = new ArrayList<IIcon>();
        ArrayList<ItemStack> coParentsList = new ArrayList<ItemStack>();
        ArrayList<ItemStack> mutationsList = new ArrayList<ItemStack>();
        for (Mutation mutation:mutations) {
            ItemStack coParent = (mutation.parent1.getItem()==seed.getItem() && mutation.parent1.getItemDamage()==seed.getItemDamage())?mutation.parent2:mutation.parent1;
            if (this.isSeedDiscovered(coParent) && this.isSeedDiscovered(mutation.result)) {
                coParentsIconList.add(RenderHelper.getIcon(coParent.getItem(), coParent.getItemDamage()));
                mutationsIconList.add(RenderHelper.getIcon(mutation.result.getItem(), mutation.result.getItemDamage()));
                coParentsList.add(coParent);
                mutationsList.add(mutation.result);
            }
        }
        this.coParentsIcons = coParentsIconList.toArray(new IIcon[coParentsIconList.size()]);
        this.mutationIcons = mutationsIconList.toArray(new IIcon[mutationsIconList.size()]);
        this.discoveredCoParents = coParentsList.toArray(new ItemStack[coParentsList.size()]);
        this.discoveredMutations = mutationsList.toArray(new ItemStack[mutationsList.size()]);
    }

    //draw the seed page title bar
    private void drawSeedTitle(int index) {
        ItemStack seed = discoveredSeeds[index];
        String title = seed.getDisplayName();
        BlockWithMeta soil = GrowthRequirements.getGrowthRequirement((ItemSeeds) seed.getItem(), seed.getItemDamage()).getSoil();
        Minecraft.getMinecraft().getTextureManager().bindTexture(RenderHelper.getItemResource(seedIcon));
        if(soil!=null) {
            this.renderIconInGui(this.guiLeft + 26, this.guiTop + 11, RenderHelper.getBlockResource(soil.getBlock().getIcon(1, soil.getMeta())));
        }
        GuiScreen.itemRender.renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), seed, this.guiLeft + 25, this.guiTop + 11);
        //this.renderIconInGui(this.guiLeft + 25, this.guiTop + 11, RenderHelper.getItemResource(seedIcon));
        float scale = 0.8F;
        while(this.fontRendererObj.getStringWidth(title)*scale>74) {
            scale = scale - 0.1F;
        }
        GL11.glScalef(scale, scale, scale);
        this.fontRendererObj.drawString(title, (int) ((this.guiLeft+46+36)/scale)-this.fontRendererObj.getStringWidth(title)/2, (int) ((this.guiTop+17)/scale), this.black);
        GL11.glScalef(1/scale, 1/scale, 1/scale);
    }

    //writes the seed information
    private void writeSeedInformation(int index) {
        float scale = 0.5F;
        GL11.glScalef(scale, scale, scale);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("agricraft_journal.information")+": ", (int) (this.textStart/scale), (int) ((this.guiTop+31)/scale), this.black);
        String seedData = splitInLines(SeedHelper.getSeedInformation(discoveredSeeds[index]), scale);
        if(seedData!=null && !seedData.equals("")) {
            String[] write = IOHelper.getLinesArrayFromData(seedData);
            for (int i = 0; i < write.length; i++) {
                if(write[i]!=null && !write[i].equals("")) {
                    this.fontRendererObj.drawString(write[i], (int) (this.textStart / scale), (int) ((this.guiTop + 38) / scale + i * this.fontRendererObj.FONT_HEIGHT), this.black);
                }
            }
        }
        GL11.glScalef(1/scale, 1/scale, 1/scale);
    }

    //writes the seed tier
    private void writeSeedTier(int index) {
        int tier = SeedHelper.getSeedTier((ItemSeeds) discoveredSeeds[index].getItem(), discoveredSeeds[index].getItemDamage());
        String write = StatCollector.translateToLocal("agricraft_journal.tier")+": "+tier;
        float scale = 0.5F;
        GL11.glScalef(scale, scale, scale);
        this.fontRendererObj.drawString(write, (int) (this.textStart / scale), (int) ((this.guiTop + 70) / scale), this.black);
        GL11.glScalef(1 / scale, 1 / scale, 1 / scale);
    }

    //draws the plant fruits
    private void drawSeedFruits() {
        int xOffset = this.guiLeft+30;
        int yOffset = this.guiTop+91;
        float scale;
        for(int i=0;i<fruitIcons.length;i++) {
            if(fruitIcons[i]!=null) {
            //draw an outline
            GL11.glDisable(GL11.GL_LIGHTING);
            Minecraft.getMinecraft().getTextureManager().bindTexture(textureSeedPage);
            GL11.glColor3f(1,1,1);
            drawTexturedModalRect(xOffset-1+i*24, yOffset-1, 0, 238, 18, 18);
            GL11.glEnable(GL11.GL_LIGHTING);
            //draw the fruit texture
            GuiScreen.itemRender.renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), fruits[i], xOffset+i*24, yOffset);
            }
        }
        scale = 0.5F;
        GL11.glScalef(scale, scale, scale);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("agricraft_journal.fruits")+": ", (int) (this.textStart/scale), (int) ((yOffset-7)/scale), this.black);
        GL11.glScalef(1/scale, 1/scale, 1/scale);
    }

    //draws the plant growth stages
    private void drawSeedGrowthStages() {
        int xOffset = this.guiLeft + 30;
        int yOffset = this.guiTop + 124;
        float scale = 1F;
        for(int i=0;i< plantIcons.length;i++) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(RenderHelper.getBlockResource(plantIcons[i]));
            this.renderIconInGui((int) ((xOffset+24*(i%4))/scale), (int) ((yOffset+24*(i/4))/scale), RenderHelper.getBlockResource(plantIcons[i]));
        }
        scale = 0.5F;
        GL11.glScalef(scale, scale, scale);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("agricraft_journal.growthStages")+": ", (int) (this.textStart/scale), (int) ((yOffset-7)/scale), this.black);
        GL11.glScalef(1/scale, 1/scale, 1/scale);
    }

    //draws the mutations
    private void drawSeedMutations() {
        int xOffset = this.guiLeft+132;
        int yOffset = 20;
        int yPosition;
        //draw the mutations that can produce this seed
        for(int i=0;i<parentsIcons.length;i++) {
            yPosition = this.guiTop+yOffset+i*20;
            //render the recipe template
            Minecraft.getMinecraft().getTextureManager().bindTexture(textureSeedPage);
            GL11.glColor3f(1, 1, 1);
            GL11.glDisable(GL11.GL_LIGHTING);
            drawTexturedModalRect(xOffset, yPosition, 0, 217, 86, 18);
            GL11.glEnable(GL11.GL_LIGHTING);
            //render the icons
            Minecraft.getMinecraft().getTextureManager().bindTexture(RenderHelper.getItemResource(parentsIcons[i][0]));
            this.renderIconInGui(xOffset, yPosition+1, RenderHelper.getItemResource(parentsIcons[i][0]));
            Minecraft.getMinecraft().getTextureManager().bindTexture(RenderHelper.getItemResource(parentsIcons[i][1]));
            this.renderIconInGui(xOffset+35, yPosition+1, RenderHelper.getItemResource(parentsIcons[i][1]));
            Minecraft.getMinecraft().getTextureManager().bindTexture(RenderHelper.getItemResource(seedIcon));
            this.renderIconInGui(xOffset+69, yPosition+1, RenderHelper.getItemResource(seedIcon));
        }
        //draw the mutations that this seed mutates into
        for(int i=0;i<coParentsIcons.length;i++) {
            yPosition = this.guiTop+yOffset+(i+parentsIcons.length)*20;
            //render the recipe template
            Minecraft.getMinecraft().getTextureManager().bindTexture(textureSeedPage);
            GL11.glColor3f(1, 1, 1);
            GL11.glDisable(GL11.GL_LIGHTING);
            drawTexturedModalRect(xOffset, yPosition, 0, 217, 86, 18);
            GL11.glEnable(GL11.GL_LIGHTING);
            //render the icons and sets buttons
            Minecraft.getMinecraft().getTextureManager().bindTexture(RenderHelper.getItemResource(seedIcon));
            this.renderIconInGui(xOffset, yPosition+1, RenderHelper.getItemResource(seedIcon));
            Minecraft.getMinecraft().getTextureManager().bindTexture(RenderHelper.getItemResource(coParentsIcons[i]));
            this.renderIconInGui(xOffset+35, yPosition+1, RenderHelper.getItemResource(coParentsIcons[i]));
            Minecraft.getMinecraft().getTextureManager().bindTexture(RenderHelper.getItemResource(mutationIcons[i]));
            this.renderIconInGui(xOffset+69, yPosition+1, RenderHelper.getItemResource(mutationIcons[i]));
        }
        //draw the text
        float scale = 0.5F;
        GL11.glScalef(scale, scale, scale);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("agricraft_journal.mutations")+": ", (int) ((xOffset/scale)), (int) ((this.guiTop+yOffset-7)/scale), this.black);
        GL11.glScalef(1/scale, 1/scale, 1/scale);
    }

    //draws the background
    @Override
    public void drawBackground(int i) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(textureBackground);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    //opening the book doesn't pause the game
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    //utility method: renders an icon in the gui because the superclass method doesn't seem to work
    private void renderIconInGui(int x, int y, ResourceLocation resource) {
        int xSize = 16;
        int ySize = 16;
        Tessellator tessellator = Tessellator.instance;
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
        GL11.glColor3f(1,1,1);      //if I'm not doing this the icons render very dark for some reason
        GL11.glDisable(GL11.GL_LIGHTING);
        tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(x, y+ySize, this.zLevel, 0, 1);
            tessellator.addVertexWithUV(x+xSize, y+ySize, this.zLevel, 1, 1);
            tessellator.addVertexWithUV(x+xSize, y, this.zLevel, 1, 0);
            tessellator.addVertexWithUV(x, y, this.zLevel, 0, 0);
        tessellator.draw();
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    //utility method: splits the string in different lines so it will fit on the page
    private String splitInLines(String input, float scale) {
        float maxWidth = 95/scale;
        String notProcessed = input;
        String output = "";
        while(this.fontRendererObj.getStringWidth(notProcessed)>maxWidth) {
            int index = 0;
            if(notProcessed!=null && !notProcessed.equals("")) {
                //find the first index at which the string exceeds the size limit
                while (notProcessed.length()-1>index && this.fontRendererObj.getStringWidth(notProcessed.substring(0, index)) < maxWidth) {
                    index=(index+1)<notProcessed.length()?index+1:index;
                }
                //go back to the first space to cut the string in two lines
                while (notProcessed.charAt(index) != ' ') {
                    index--;
                }
                //update the data for the next iteration
                output = output.equals("") ? output : output + '\n';
                output = output + notProcessed.substring(0, index);
                notProcessed = notProcessed.length() > index + 1 ? notProcessed.substring(index + 1) : notProcessed;
            }
        }
        return output+'\n'+notProcessed;
    }

    //utility method: check if a seed has been discovered
    private boolean isSeedDiscovered(ItemStack seed) {
        for(ItemStack arrayElement:this.discoveredSeeds) {
            if(seed.getItem()==arrayElement.getItem() && seed.getItemDamage()==arrayElement.getItemDamage()) {
                return true;
            }
        }
        return false;
    }

    private static final String introduction =
            "Welcome to Agricultural farming, today I " +
            "will teach you the basics of farming with " +
            "crops. To start you will need to put 4 " +
            "sticks in a crafting grid, this will make " +
            "crops. These can be put on farmland " +
            "and seeds can then be planted in them. " +
            "You can also apply a second set of " +
            "crops to an existing crop, this will " +
            "create a cross-crop. If you plant two " +
            "or more plants in crops adjacent to a " +
            "cross-crop, there is a chance for a " +
            "mutation to occur. " +
            "\n" +
            "In this journal you can keep track of " +
            "all your discovered crop mutations. To " +
            "register a discovered seed, put the " +
            "journal in a seed-analyser and then " +
            "analyse the seed. This will reveal to you " +
            "the properties of the seed as well as " +
            "registering it in this journal. If you ever " +
            "wish to copy the journal, put it in a " +
            "crafting grid together with a blank book " +
            "and quill.";
}
