package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.container.ContainerSeedAnalyzer;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.renderers.models.ModelSeedAnalyzer;
import com.InfinityRaider.AgriCraft.renderers.models.ModelSeedAnalyzerBook;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderSeedAnalyzer extends RenderBlockBase {
    private ResourceLocation texture;
    private ResourceLocation bookTexture;
    private final ModelSeedAnalyzer modelSeedAnalyzer;
    private final ModelSeedAnalyzerBook modelBook;
    //dummy TileEntity for inventory rendering
    private TileEntitySeedAnalyzer seedAnalyzerDummy;

    public RenderSeedAnalyzer() {
        super(Blocks.blockSeedAnalyzer, new TileEntitySeedAnalyzer(), true);
        this.texture = new ResourceLocation(Reference.MOD_ID.toLowerCase()+":textures/blocks/seedAnalyzer.png");
        this.bookTexture = new ResourceLocation(Reference.MOD_ID.toLowerCase()+":textures/blocks/seedAnalyzerBook.png");
        this.modelSeedAnalyzer = new ModelSeedAnalyzer();
        this.modelBook = new ModelSeedAnalyzerBook();
        seedAnalyzerDummy = new TileEntitySeedAnalyzer();
        seedAnalyzerDummy.setOrientation(ForgeDirection.SOUTH);
    }

    @Override
    protected void doInventoryRender(ItemRenderType type, ItemStack item, Object... data) {
        this.doWorldRender(Tessellator.instance, Minecraft.getMinecraft().theWorld, 0, 0, 0, seedAnalyzerDummy, Blocks.blockSeedAnalyzer, 0, 0, RenderBlocks.getInstance(), false);
    }

    @Override
    protected boolean doWorldRender(Tessellator tessellator, IBlockAccess world, double x, double y, double z, TileEntity tile, Block block, float f, int modelId, RenderBlocks renderer, boolean callFromTESR) {
        if(tile==null || !(tile instanceof TileEntitySeedAnalyzer)) {
            return false;
        }
        TileEntitySeedAnalyzer analyzer= (TileEntitySeedAnalyzer) tile;
        //render the model
        GL11.glPushMatrix();
        GL11.glTranslatef(0.5F, 1.5F, 0.5F);
        Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
        GL11.glRotatef(180, 0F, 0F, 1F);
        this.modelSeedAnalyzer.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        if(analyzer.hasJournal() && ConfigurationHandler.renderBookInAnalyzer) {
            Minecraft.getMinecraft().renderEngine.bindTexture(this.bookTexture);
            this.modelBook.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        }
        GL11.glPopMatrix();
        if(analyzer.hasSeed() || analyzer.hasTrowel()) {
            renderSeed(analyzer);
        }
        return false;
    }

    //renders the seed
    private void renderSeed(TileEntitySeedAnalyzer analyzer) {
        //set up the tessellator
        Tessellator tessellator = Tessellator.instance;
        //grab the texture
        ItemStack stack = analyzer.getStackInSlot(ContainerSeedAnalyzer.seedSlotId);
        IIcon icon = stack.getItem().getIconFromDamage(stack.getItemDamage());
        if(icon==null) {
            return;
        }
        //define rotation angle in function of system time
        float angle = (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);   //credits to Pahimar
        GL11.glPushMatrix();
        //translate to the desired position
        GL11.glTranslated(Constants.UNIT * 8,Constants.UNIT * 4, Constants.UNIT * 8);
        //resize the texture to half the size
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        //rotate the renderer
        GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-8 * Constants.UNIT, 0, 0);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
        ItemRenderer.renderItemIn2D(tessellator, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), Constants.UNIT);
        GL11.glTranslatef(8 * Constants.UNIT, 0, 0);
        GL11.glRotatef(-angle, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(2, 2, 2);
        GL11.glPopMatrix();
    }

    @Override
    public boolean shouldBehaveAsTESR() {
        return true;
    }

    @Override
    public boolean shouldBehaveAsISBRH() {
        return false;
    }
}
