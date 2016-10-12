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

import net.doubledoordev.mtrm.xml.elements.Slot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

/**
 * @author Dries007
 */
public class OredictElement extends SlotElement
{
    public OredictElement(GuiElementCallback callback, boolean optional, Slot.Type type, boolean wildcard, boolean metawildcard, boolean oredict, boolean stacksize)
    {
        super(callback, optional, type, wildcard, metawildcard, oredict, stacksize);
    }

    @Override
    public void initGui()
    {
        super.initGui();
    }

    @Override
    protected void focusStatusChanged()
    {
        super.focusStatusChanged();
    }

    @Override
    protected void setItemStack(ItemStack input)
    {
        super.setItemStack(input);
    }

    @Override
    public String save()
    {
        return super.save();
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        super.draw(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawItemStack(ItemStack stack, int x, int y, String altText)
    {
        super.drawItemStack(stack, x, y, altText);
    }

    @Override
    public void update()
    {
        super.update();
    }

    @Override
    protected ArrayList<String> getHoverLines()
    {
        return super.getHoverLines();
    }

    @Override
    public void drawHover(int mouseX, int mouseY, int maxWidth, int maxHeight)
    {
        super.drawHover(mouseX, mouseY, maxWidth, maxHeight);
    }

    @Override
    protected void onClickOn(int mouseX, int mouseY, int mouseButton)
    {
        super.onClickOn(mouseX, mouseY, mouseButton);
    }

    @Override
    public void setFocus(boolean focus)
    {
        super.setFocus(focus);
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode)
    {
        return super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean isValid()
    {
        return super.isValid();
    }

    @Override
    public void onClick(int mouseX, int mouseY, int mouseButton)
    {
        super.onClick(mouseX, mouseY, mouseButton);
    }
}
