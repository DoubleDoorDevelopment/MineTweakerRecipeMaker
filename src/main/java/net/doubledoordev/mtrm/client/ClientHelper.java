package net.doubledoordev.mtrm.client;

import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dries007
 */
public class ClientHelper
{
    public static final int[] BTN_COLORS = {0x505050, 0xAAAAAA, 0xBDC6FF};
    public static final int[] TEXT_COLORS = {0xA0A0A0, 0xE0E0E0, 0xFFFFA0};

    private ClientHelper()
    {
    }

    public static List<String> split(FontRenderer renderer, String text, int width)
    {
        if (text.length() == 0) return new ArrayList<>();
        List<String> list = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            int w = renderer.getStringWidth(line.append(c).toString());
            if (c == '\n')
            {
                list.add(line.substring(0, line.length() - 1));
                line = new StringBuilder();
            }
            else if (w >= width)
            {
                list.add(line.substring(0, line.length() - 1) + '→');
                line = new StringBuilder();
                line.append(c);
            }
        }
        list.add(line.toString());
        return list;
    }

    public static void drawSplit(FontRenderer renderer, String text, int width, final int color, int x, int y)
    {
        List<String> lines = split(renderer, text, width);
        renderer.posX = x;
        renderer.posY = y;
        if (lines.isEmpty()) return;
        renderer.drawString(lines.remove(0), x, y, color);
        for (String line : lines)
        {
            renderer.posY += renderer.FONT_HEIGHT;
            renderer.posX = x;
            renderer.renderStringAtPos(line, false);
        }
    }
}
