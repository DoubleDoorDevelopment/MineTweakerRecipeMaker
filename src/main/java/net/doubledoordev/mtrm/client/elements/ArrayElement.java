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

import net.doubledoordev.mtrm.xml.XmlParser;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dries007
 */
public class ArrayElement extends GuiElement implements GuiElement.GuiElementCallback
{
    private final XmlParser.IStringObject component;
    private final int min;
    private final int max;
    private final int dim;
    private final List<GuiElement> subs = new ArrayList<>();

    public ArrayElement(GuiElementCallback callback, boolean optional, XmlParser.IStringObject component, int min, int max, int dim)
    {
        super(callback, optional);
        this.component = component;
        this.min = min;
        this.max = max;
        this.dim = dim;

        for (int i = 0; i < min; i++)
        {
            subs.add(component.toGuiElement(this));
        }
    }

    @Override
    public void initGui()
    {

    }

    @Override
    public String save()
    {
        final int lastIndex = subs.size() - 1;
        // Arbitrary guess of avg element size, only real reason to use this over Arrays.toString(String[])
        StringBuilder sb = new StringBuilder(50 * dim * lastIndex);
        for (int i = 0; ; i++)
        {
            sb.append(subs.get(i).save());
            if (i == lastIndex)
            {
                return sb.append(']').toString();
            }
            sb.append(", ");
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {

    }

    @Override
    public boolean isValid()
    {
        if (!super.isValid())
        {
            return false;
        }
        if (subs.size() < min && subs.size() > max)
        {
            return false;
        }
        for (GuiElement sub : subs)
        {
            if (!sub.isValid())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void resizeCallback(@Nullable GuiElement element)
    {

    }

    @Override
    public void updateButtons(@Nullable GuiElement element)
    {

    }
}
