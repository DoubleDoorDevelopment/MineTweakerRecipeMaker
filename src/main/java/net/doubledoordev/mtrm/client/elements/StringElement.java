/*
 * Copyright (c) 2015 - 2016, Dries007 & Double Door Development
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
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

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

/**
 * @author Dries007
 */
public class StringElement extends GuiElement
{
    private final String string;
    private final int color;
    private final FontRenderer fontRendererObj;

    public StringElement(GuiElementCallback callback, String string)
    {
        this(callback, string, 0x000000);
    }

    public StringElement(GuiElementCallback callback, String string, int color)
    {
        super(callback, false);
        this.string = string;
        this.color = color;
        fontRendererObj = mc.fontRendererObj;
    }

    @Override
    public void initGui()
    {
        height = 1 + fontRendererObj.listFormattedStringToWidth(string, width).size() * fontRendererObj.FONT_HEIGHT;
    }

    @Override
    public void setSize(int width, int height)
    {
        super.setSize(width, height);
        initGui();
    }

    @Override
    public String save()
    {
        return string;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.enableAlpha();
        fontRendererObj.drawSplitString(string, posX, posY + 1, maxWidth, color);
    }
}
