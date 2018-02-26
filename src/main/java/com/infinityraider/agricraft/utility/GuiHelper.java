package com.infinityraider.agricraft.utility;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;

//helper class to read, write and parse data to and from the config files
/**
 * This class is candidate to be moved to InfinityLib!
 */
public abstract class GuiHelper {

    /**
     * Utility method: splits the string in different lines so it will fit on the page.
     *
     * @param fontRendererObj the font renderer to check against.
     * @param input the line to split up.
     * @param maxWidth the maximum allowable width of the line before being wrapped.
     * @param scale the scale of the text to the width.
     * @return the string split up into lines by the '\n' character.
     */
    public static String splitInLines(FontRenderer fontRendererObj, String input, double maxWidth, double scale) {
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
                while (index > 0 && notProcessed.charAt(index) != ' ') {
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

    //turns the raw data string into an array (each array element is a line from the string)
    public static List<String> getLinesFromData(String input) {
        int count = 0;
        String unprocessed = input;
        for (int i = 0; i < unprocessed.length(); i++) {
            if (unprocessed.charAt(i) == '\n') {
                count++;
            }
        }
        List<String> data = new ArrayList<>(count + 1); // There will be no more than count plus + lines, thereby preventing resizing.
        if (unprocessed.length() > 0) {
            for (int i = 0; i < count; i++) {
                String line = (unprocessed.substring(0, unprocessed.indexOf('\n'))).trim();
                if (line.length() > 0 && line.charAt(0) != '#') {
                    data.add(line); // The string line was already trimmed in its declaration.
                }
                unprocessed = unprocessed.substring(unprocessed.indexOf('\n') + 1);
            }
        }

        unprocessed = unprocessed.trim();

        if (unprocessed.length() > 0 && unprocessed.charAt(0) != '#') {
            data.add(unprocessed);
        }
        return data;
    }

    // Grass Drops to be Moved to AgriCore
}
