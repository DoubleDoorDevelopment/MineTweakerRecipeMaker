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

package net.doubledoordev.mtrm.client.parts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import static net.doubledoordev.mtrm.client.ClientHelper.BTN_COLORS;
import static net.doubledoordev.mtrm.client.ClientHelper.TEXT_COLORS;
import static net.minecraft.client.renderer.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA;
import static net.minecraft.client.renderer.GlStateManager.DestFactor.ZERO;
import static net.minecraft.client.renderer.GlStateManager.SourceFactor.ONE;
import static net.minecraft.client.renderer.GlStateManager.SourceFactor.SRC_ALPHA;

/**
 * @author Dries007
 */
public class GuiIconButton extends GuiButton
{
    private final Icon icon;

    public GuiIconButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, Icon icon)
    {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.icon = icon;
    }

    public GuiIconButton(int buttonId, int x, int y, String buttonText, Icon icon)
    {
        this(buttonId, x, y, 20, 20, buttonText, icon);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (!visible)
        {
            return;
        }

        hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
        // 0 = disabled, 1 = normal, 2 = hover
        int state = getHoverState(hovered);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO);
        GlStateManager.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA);

        int right = xPosition + width;
        int bottom = yPosition + height;

        drawRect(xPosition, yPosition, right, bottom, 0xFF000000);

        drawRect(xPosition + 1, yPosition + 1, right - 2, bottom - 3, 0xA0000000 | BTN_COLORS[state]);

        drawHorizontalLine(xPosition + 1, right - 2, yPosition + 1, 0x80555555 | BTN_COLORS[state]);
        drawVerticalLine(xPosition + 1, yPosition + 1, bottom - 1, 0x80555555 | BTN_COLORS[state]);
        drawRect(xPosition + 1, bottom - 3, right - 1, bottom - 1, 0x80000000 | BTN_COLORS[state]);
        drawVerticalLine(right - 2, yPosition + 1, bottom - 3, 0x80000000 | BTN_COLORS[state]);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO);
        GlStateManager.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA);
        mc.getTextureManager().bindTexture(Icon.TEXTURE);
        int iconId = icon.hasAlt() && !enabled ? icon.getAlt().ordinal() : icon.ordinal();
        drawTexturedModalRect(xPosition - 8 + width / 2, yPosition - 8 + height / 2, 16 * (iconId % 16), 16 * (iconId / 16), 16, 16);

        if (hovered)
        {
            FontRenderer fr = mc.fontRendererObj;
            int centerX = width / 2;
            int centerY = height / 2;
            int width = fr.getStringWidth(displayString) / 2 + 2;
            int height = fr.FONT_HEIGHT / 2 + 2;
            drawRect(xPosition + centerX - width, yPosition + centerY - height, xPosition + centerX + width, yPosition + centerY + height, 0xA0000000);
            drawCenteredString(fr, displayString, xPosition + this.width / 2, yPosition + (this.height - 8) / 2, TEXT_COLORS[state]);
        }
    }
}
