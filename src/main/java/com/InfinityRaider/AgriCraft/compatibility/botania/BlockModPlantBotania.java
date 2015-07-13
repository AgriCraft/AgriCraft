package com.InfinityRaider.AgriCraft.compatibility.botania;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.exception.MissingArgumentsException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockModPlantBotania extends BlockModPlant {
    @SideOnly(Side.CLIENT)
    private IIcon[] alternateIcons;

    public BlockModPlantBotania(Object... arguments) throws MissingArgumentsException {
        super(arguments);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if(BotaniaHelper.useAlternateTextures()) {
            switch (meta) {
                case 0:
                    return this.alternateIcons[0];
                case 1:
                    return this.alternateIcons[0];
                case 2:
                    return this.alternateIcons[1];
                case 3:
                    return this.alternateIcons[1];
                case 4:
                    return this.alternateIcons[1];
                case 5:
                    return this.alternateIcons[2];
                case 6:
                    return this.alternateIcons[2];
                case 7:
                    return this.alternateIcons[3];
            }
            return this.alternateIcons[(int) Math.floor(meta / 5)];
        } else {
            return super.getIcon(side, meta);
        }
    }

    //register icons
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        LogHelper.debug("registering icon for: " + this.getUnlocalizedName());
        super.registerBlockIcons(reg);
        this.alternateIcons = new IIcon[4];
        for(int i=1;i<this.alternateIcons.length+1;i++) {
            this.alternateIcons[i-1] = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.') + 1)+"Alternate"+i);
        }
    }
}
