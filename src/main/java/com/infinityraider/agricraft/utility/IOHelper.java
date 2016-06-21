package com.infinityraider.agricraft.utility;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.util.BlockWithMeta;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

//helper class to read, write and parse data to and from the config files
public abstract class IOHelper {
    public static String getModId(ItemStack stack) {
        String name = Item.itemRegistry.getNameForObject(stack.getItem()).getResourcePath();
        int split = name.indexOf(':');
        if(split>=0) {
            name = name.substring(0, split);
        }
        return name;
    }
	
	/**
	 * <p>
	 * Attempts to read or write data from or to a file.
	 * </p><p>
	 * If the file is found to be missing,
	 * the file will be written with the default data, and the default data will be returned.
	 * </p><p>
	 * If the file is found to be a regular, readable file, the file is read in, and its contents returned.
	 * </p><p>
	 * If the method runs into an error while attempting any of this, a message is printed to the log,
	 * and the default data is returned.
	 * </p>
	 * @param directory the directory the file is in.
	 * @param fileName the name of the file, without the .txt ending.
	 * @param defaultData the data to write to the file, or default to.
	 * @return the data contained in the file, or the default data.
	 */
    public static String readOrWrite(String directory, String fileName, String defaultData) {
        return readOrWrite(directory, fileName, defaultData, false);
    }

	/**
	 * <p>
	 * Attempts to read or write data from or to a file.
	 * </p><p>
	 * If the file is found to be missing or the reset parameter is set to true,
	 * the file will be written with the default data, and the default data will be returned.
	 * </p><p>
	 * If the file is found to be a regular, readable file, the file is read in, and its contents returned.
	 * </p><p>
	 * If the method runs into an error while attempting any of this, a message is printed to the log,
	 * and the default data is returned.
	 * </p>
	 * @param directory the directory the file is in.
	 * @param fileName the name of the file, without the .txt ending.
	 * @param defaultData the data to write to the file, or default to.
	 * @param reset if the file should be overwritten with the default data.
	 * @return the data contained in the file, or the default data.
	 */
	public static String readOrWrite(String directory, String fileName, String defaultData, boolean reset) {
		Path path = Paths.get(directory, fileName + ".txt");
		try {
			if (Files.isRegularFile(path) && !reset) {
				return new String(Files.readAllBytes(path));
			} else {
				Files.write(path, defaultData.getBytes());
			}
		} catch (IOException e) {
			AgriCore.getLogger("AgriCraft").info("Caught IOException when reading " + path.getFileName());
		}
		return defaultData;
	}

    /**
     * Utility method: splits the string in different lines so it will fit on the page.
     *
     * @param fontRendererObj the font renderer to check against.
     * @param input the line to split up.
     * @param maxWidth the maximum allowable width of the line before being wrapped.
     * @param scale the scale of the text to the width.
     * @return the string split up into lines by the '\n' character.
     */
    public static String splitInLines(FontRenderer fontRendererObj, String input, float maxWidth, float scale) {
        maxWidth = maxWidth / scale;
        String notProcessed = input;
        String output = "";
        while (fontRendererObj.getStringWidth(notProcessed) > maxWidth) {
            int index = 0;
            if (notProcessed != null && !notProcessed.equals("")) {
                //find the first index at which the string exceeds the size limit
                while (notProcessed.length() - 1 > index && fontRendererObj.getStringWidth(notProcessed.substring(0, index)) < maxWidth) {
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

    //get the grass drops file contents
    public static String getGrassDrops() {
    	//This is bad...
        return grassDropInstructions;
    }

    //mutation chances overrides file contents
    public static String getSpreadChancesOverridesInstructions() {
        return spreadChancesOverridesInstructions;
    }

    //seed tier overrides file contents
    public static String getSeedTierOverridesInstructions() {
        return seedTierOverridesInstructions;
    }

    //seed black list
    public static String getSeedBlackListInstructions() {
        return seedBlackListInstructions;
    }

    //vanilla planting overrides
    public static String getPlantingExceptionsInstructions() {
        return plantingExceptionsInstructions;
    }

    public static String getSoilwhitelistData() {
        return soilWhitelistInstructions;
    }

    //turns the raw data string into an array (each array element is a line from the string)
    public static String[] getLinesArrayFromData(String input) {
        int count = 0;
        String unprocessed = input;
        for (int i=0;i<unprocessed.length();i++) {
            if (unprocessed.charAt(i) == '\n') {
                count++;
            }
        }
        ArrayList<String> data = new ArrayList<>(count + 1); // There will be no more than count plus + lines, thereby preventing resizing.
        if (unprocessed.length()>0) {
            for (int i=0;i<count;i++) {
                String line = (unprocessed.substring(0,unprocessed.indexOf('\n'))).trim();
                if (line.length() > 0 && line.charAt(0) != '#') {
                    data.add(line); // The string line was already trimmed in its declaration.
                }
                unprocessed = unprocessed.substring(unprocessed.indexOf('\n')+1);
            }
        }
        
        unprocessed = unprocessed.trim();
        
        if (unprocessed.length()>0 && unprocessed.charAt(0)!='#') {
            data.add(unprocessed);
        }
        return data.toArray(new String[data.size()]);
    }

    //splits a comma seperated string into an array
    public static String[] getData(String input) {
        ArrayList<String> output = new ArrayList<>();
        int start = 0;
        for(int i=0;i<input.length();i++) {
            if(input.charAt(i)==',') {
                if(input.charAt(i+1)==' ') {
                    continue;
                }
                String element = (input.substring(start, i)).trim();
                if(element.length()>0) {
                    output.add(element);
                }
                start = i+1;
            }
        }
        String element = (input.substring(start)).trim();
        if(element.length()>0) {
            output.add(element);
        }
        return output.toArray(new String[output.size()]);
    }

    /**
     * Retrieves an itemstack from a string representation.
     * The string must be formatted as "modid:name:meta".
     * The meta is not required in all cases.
     * 
     * @param input the string representation of the item to get.
     * @return the item as an itemstack, or null.
     */
    public static ItemStack getStack(String input) {
		String[] data = input.split(":");
		if (data.length <= 1) {
			return null;
		}
		int meta = data.length==3?Integer.parseInt(data[2]):0;
        Item item = Item.getByNameOrId(data[0] + ":" +data[1]);
        if(item == null) {
            return null;
        }
        return new ItemStack(item, 1, meta);
    }

    /**
     * Retrieves a a block from a string representation.
     * The string must be formatted as "modid:name:meta".
     * The meta is not required in all cases.
     *
     * @param input the string representation of the block to get.
     * @return the block as a blockWithMeta, or null.
     */
    public static BlockWithMeta getBlock(String input) {
        String[] data = input.split(":");
        if (data.length <= 1) {
            return null;
        }
        int meta = data.length==3?Integer.parseInt(data[2]):-1;
        Block block = GameRegistry.findBlock(data[0], data[1]);
        if(block == null) {
            return null;
        }
        return meta<0?new BlockWithMeta(block):new BlockWithMeta(block, meta);
    }

    //TODO: move all this crap to asset files
    private static final String grassDropInstructions =
            "#Put a list of seeds here that will drop from tall grass with the following schematic: <seedname:seedmeta>,<weight>\n" +
            "#The seedname should be the name NEI gives you, the weight is the weighted chance for this seed to drop (for reference, minecraft wheat seeds have weight 10)\n" +
            "#Only define one seed per line, meta is optional. Example: minecraft:melon_seeds,10\n" + 
            "AgriCraft:seedCarrot,10\n" +
            "AgriCraft:seedPotato,10\n";

    private static final String seedBlackListInstructions =
            "#Define blacklisted seeds here: <mod>:<seedname>:<seedmeta>\n" +
            "#You can get these values from NEI\n" +
            "#Blacklisted seeds will not be able to planted on crops\n" +
            "#For example: AgriCraft:seedDandelion";

    private static final String plantingExceptionsInstructions =
            "#Define seeds that will ignore the vanilla planting rule here: <mod>:<seedname>:<seedmeta>\n" +
            "#You can get these values from NEI\n" +
            "#All seeds defined here will still be able to be planted outside of crops when vanilla farming is disabled\n" +
            "#For example: AgriCraft:seedDandelion";

    private static final String soilWhitelistInstructions =
            "#Define blocks to whitelist as fertile soil here: <mod>:<blockname>:<blockmeta>\n" +
            "#You can get these values from NEI\n" +
            "#Whitelisting a block as a fertile soil means you can plant crops on them\n" +
            "#Note that this only works for crops that can be planted on farmland, crops that require a specific soil (e.g. nether wart or cactus) will still need that particular soil\n" +
            "#For example: Forestry:soil:0 (this will add forestry humus to the whitelist)";

    private static final String spreadChancesOverridesInstructions =
            "#Define overrides for spreading chances here: <mod>:<seedname>:<seedmeta>,<chance>\n" +
            "#You can get these values from NEI (example: minecraft:wheat_seeds is the vanilla seeds)\n" +
            "#The chance is an integer specified in percent, minimum is 0, maximum a 100. Spread chance is the chance that crops will spread to empty crosscrops\n" +
            "#For example: AgriCraft:seedDandelion,85";

    private static final String seedTierOverridesInstructions =
            "#Define overrides for seed tiers here: <mod>:<seedname>:<seedmeta>,<tier>\n" +
            "#You can get these values from NEI (example: minecraft:wheat_seeds is the vanilla seeds)\n" +
            "#The tier is an integer between 1 and 5 (1 and 5 included). The higher the tier, the slower it will grow, mutate, be analysed, ...\n" +
            "#For example: AgriCraft:seedDandelion,2";

}
