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

package net.doubledoordev.mtrm.client.elements;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.doubledoordev.mtrm.client.ClientHelper;
import net.minecraft.client.gui.GuiScreen;
import org.apache.commons.lang3.StringEscapeUtils;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Dries007
 */
public class StringInputElement extends ButtonElement
{
    private final String emptyDisplayText;
    protected String text = "";
    protected int blinkTimer = 0;

    public StringInputElement(GuiElementCallback callback, boolean optional, String displayText)
    {
        super(callback, optional, displayText);
        this.emptyDisplayText = displayText;
    }

    @Override
    protected void onClick()
    {
        // actuation happens on focus here.
    }

    @Override
    public String save()
    {
        return enabled ? '"' + StringEscapeUtils.escapeJava(text) + '"' : null;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        if (!isFocused())
        {
            super.draw(mouseX, mouseY, partialTicks);
        }
        else
        {
            int right = posX + width;
            int bottom = posY + height;
            drawRect(posX, posY, right, bottom, 0xFF000000);

            ClientHelper.drawSplit(fontRendererObj, text, width, 0xFFFFFF, posX, posY);

            if (blinkTimer / 6 % 2 == 0)
            {
                drawVerticalLine((int) fontRendererObj.posX, (int) fontRendererObj.posY - 1, (int) fontRendererObj.posY + fontRendererObj.FONT_HEIGHT - 1, 0xFFFFFFFF);
            }
        }
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode)
    {
        if (super.keyTyped(typedChar, keyCode))
        {
            return true;
        }
        if (GuiScreen.isKeyComboCtrlV(keyCode) || typedChar == 22)
        {
            text += GuiScreen.getClipboardString();
        }
        else if (GuiScreen.isCtrlKeyDown())
        {
            return false;
        }
        else
        {
            switch (keyCode)
            {
            case Keyboard.KEY_ESCAPE:
                return false;
            case Keyboard.KEY_BACK:
                int len = text.length();
                if (len > 0)
                {
                    text = text.substring(0, len - 1);
                }
                break;
            case Keyboard.KEY_NUMPADENTER: //fallthrough
            case Keyboard.KEY_RETURN:
                text += '\n';
                break;
            case Keyboard.KEY_DELETE:
                text = "";
                break;
            default:
                if (typedChar != 0)
                {
                    text += typedChar;
                }
                else
                {
                    return false;
                }
                break;
            }
        }
        calcHeight();
        return true;
    }

    @Override
    public ArrayList<String> getHoverLines()
    {
        if (isFocused())
        {
            return new ArrayList<>();
        }
        ArrayList<String> list = super.getHoverLines();
        if (!enabled)
        {
            list.add(ChatFormatting.RED + "Disabled!");
        }
        else
        {
            list.add(ChatFormatting.AQUA + "Current value:");
            Collections.addAll(list, text.split("\n"));
        }
        return list;
    }

    private void calcHeight()
    {
        if (!isFocused())
        {
            height = defaultHeight;
            if (text.isEmpty())
            {
                setDisplayText(emptyDisplayText);
            }
            else
            {
                setDisplayText(text.replace('\n', '↲'));
            }
        }
        else
        {
            height = (Math.max(ClientHelper.split(fontRendererObj, text, width).size(), 1) * fontRendererObj.FONT_HEIGHT + 6);
        }
        resizeCallback();
    }

    @Override
    public void update()
    {
        if (!isFocused())
        {
            super.update();
            blinkTimer = 0;
        }
        else
        {
            blinkTimer++;
        }
    }

    @Override
    public void setFocus(boolean focus)
    {
        boolean oldFocus = this.isFocused();
        super.setFocus(focus);
        if (oldFocus != focus)
        {
            calcHeight();
        }
    }
}
