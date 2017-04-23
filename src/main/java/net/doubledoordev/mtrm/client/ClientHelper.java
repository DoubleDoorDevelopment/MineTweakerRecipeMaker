/*
 * Copyright (c) 2015 - 2017, Dries007 & Double Door Development
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * + Redistributions via the Curse or CurseForge platform are not allowed without
 *   written prior approval.
 *
 * + Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * + Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.doubledoordev.mtrm.client;

import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dries007
 */
public class ClientHelper
{
    public static final int[] BTN_COLORS = {0x505050, 0xAAAAAA, 0xBDC6FF, 0xFF9090};
    public static final int[] TEXT_COLORS = {0xA0A0A0, 0xE0E0E0, 0xFFFFA0, 0xF0F0F0};

    private ClientHelper()
    {
    }

    public static List<String> split(FontRenderer renderer, String text, int width)
    {
        return split(renderer, text, width, Integer.MAX_VALUE);
    }

    public static List<String> split(FontRenderer renderer, String text, int width, int lineLimit)
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
                if (list.size() >= lineLimit--) return list;
                line = new StringBuilder();
            }
            else if (w >= width)
            {
                list.add(line.substring(0, line.length() - 1) + '→');
                if (list.size() >= lineLimit--) return list;
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
