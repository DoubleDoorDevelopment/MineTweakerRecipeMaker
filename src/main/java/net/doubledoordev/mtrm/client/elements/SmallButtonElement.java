/*
 * Copyright (c) 2015 - 2017, Dries007 & Double Door Development
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions via the Curse or CurseForge platform are not allowed without
 *   written prior approval.
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
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

package net.doubledoordev.mtrm.client.elements;

import net.doubledoordev.mtrm.client.parts.Icon;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;

import static net.doubledoordev.mtrm.client.ClientHelper.BTN_COLORS;
import static net.minecraft.client.renderer.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA;
import static net.minecraft.client.renderer.GlStateManager.DestFactor.ZERO;
import static net.minecraft.client.renderer.GlStateManager.SourceFactor.ONE;
import static net.minecraft.client.renderer.GlStateManager.SourceFactor.SRC_ALPHA;

/**
 * @author Dries007
 */
public abstract class SmallButtonElement extends ButtonElement
{
    private final String hoverText;
    private final Icon icon;

    protected SmallButtonElement(GuiElementCallback callback, boolean optional, String hoverText, Icon icon)
    {
        super(callback, optional, "");
        this.hoverText = hoverText;
        this.icon = icon;
    }

    @Override
    public ArrayList<String> getHoverLines()
    {
        ArrayList<String> list = super.getHoverLines();
        list.add(0, hoverText);
        return list;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        // 0 = disabled, 1 = normal, 2 = hover, 3 = error
        int state = isValid() ? isEnabled() ? isOver(mouseX, mouseY) ? 2 : 1 : 0 : 3;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO);
        GlStateManager.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA);

        int right = posX + width;
        int bottom = posY + height;

        int color = BTN_COLORS[state];

        drawRect(posX, posY, right, bottom, 0xFF000000);

        drawRect(posX, posY, right - 1, bottom - 1, 0xA0000000 | color);
        drawHorizontalLine(posX + 1, right - 1, posY, 0x80555555 | color);
        drawVerticalLine(posX, posY, bottom - 1, 0x80555555 | color);
        drawRect(posX, bottom - 1, right, bottom, 0x80000000 | color);
        drawVerticalLine(right - 1, posY, bottom - 1, 0x80000000 | color);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO);
        GlStateManager.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA);
        mc.getTextureManager().bindTexture(Icon.TEXTURE);
        int iconId = icon.hasAlt() && !enabled ? icon.getAlt().ordinal() : icon.ordinal();
        drawTexturedModalRect(posX+1, posY+1, 16 * (iconId % 16), 16 * (iconId / 16), width, height);
    }

    @Override
    public boolean isValid()
    {
        return true;
    }
}
