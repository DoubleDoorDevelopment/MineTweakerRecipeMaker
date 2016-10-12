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

import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @author Dries007
 */
public class NumberElement extends StringInputElement
{
    public final double min;
    public final double max;
    public final double stepsize;
    public final String format;
    protected boolean error = false;
    private double value;

    public NumberElement(GuiElementCallback callback, boolean optional, double min, double max, double stepsize)
    {
        super(callback, optional, "Number");
        this.min = min;
        this.max = max;
        this.value = min;
        this.stepsize = stepsize;
        if (stepsize == 0) this.format = "%f";
        else
        {
            int i = 0;
            while (stepsize < 1)
            {
                stepsize *= 10;
                i ++;
            }
            this.format = "%." + i + "f";
        }
    }

    protected void validate()
    {
        try
        {
            value = Double.parseDouble(text);
            error = false;
        }
        catch (NumberFormatException ignored)
        {
            error = true;
        }
        updateButtonsCallback();
    }

    @Override
    public void setFocus(boolean focus)
    {
        validate();
        if (!focus) text = save();
        super.setFocus(focus);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        validate();
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        validate();
    }

    @Override
    public String save()
    {
        return String.format(Locale.ROOT, format, value);
    }

    public ArrayList<String> getHoverLines()
    {
        ArrayList<String> list = super.getHoverLines();
        list.add("Minimum: " + min);
        list.add("Maximum: " + max);
        list.add("Resolution: " + stepsize);
        return list;
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode)
    {
        if (GuiScreen.isKeyComboCtrlV(keyCode) || typedChar == 22) text = GuiScreen.getClipboardString();
        else if (GuiScreen.isCtrlKeyDown()) return false;
        else
        {
            switch (keyCode)
            {
                case Keyboard.KEY_ESCAPE:
                    return false;
                case Keyboard.KEY_NUMPADENTER:
                case Keyboard.KEY_RETURN:
                    setFocus(false);
                    break;
                case Keyboard.KEY_BACK:
                    int len = text.length();
                    if (len > 0) text = text.substring(0, len - 1);
                    break;
                case Keyboard.KEY_DELETE:
                    text = "0"; break;
                default:
                    switch (typedChar)
                    {
                        case '-':
                        case '.':
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            text += typedChar;
                        default:
                            return false;
                    }
            }
        }
        validate();
        return true;
    }

    @Override
    public boolean isValid()
    {
        if (!enabled) return optional;
        return !error && value >= min && value <= max;
    }
}
