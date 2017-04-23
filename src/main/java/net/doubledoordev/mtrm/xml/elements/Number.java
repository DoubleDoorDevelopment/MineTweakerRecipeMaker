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

package net.doubledoordev.mtrm.xml.elements;

import net.doubledoordev.mtrm.client.elements.GuiElement;
import net.doubledoordev.mtrm.client.elements.NumberElement;
import net.doubledoordev.mtrm.xml.XmlParser;
import org.w3c.dom.Element;

/**
 * @author Dries007
 */
@SuppressWarnings("WeakerAccess")
public class Number implements XmlParser.IStringObject
{
    public final double min;
    public final double max;
    public final double stepsize;
    public final boolean optional;

    public Number(Element node)
    {
        min = node.hasAttribute("min") ? Double.parseDouble(node.getAttribute("min")) : 0;
        max = node.hasAttribute("max") ? Double.parseDouble(node.getAttribute("max")) : Short.MAX_VALUE;
        stepsize = node.hasAttribute("stepsize") ? Double.parseDouble(node.getAttribute("stepsize")) : 1;
        optional = node.hasAttribute("optional") && Boolean.parseBoolean(node.getAttribute("optional"));
    }

    @Override
    public String toString()
    {
        return "Number{" +
                "min=" + min +
                ", max=" + max +
                ", stepsize=" + stepsize +
                ", optional=" + optional +
                '}';
    }

    @Override
    public String toHumanText()
    {
        return "<Number>";
    }

    @Override
    public GuiElement toGuiElement(GuiElement.GuiElementCallback callback)
    {
        return new NumberElement(callback, optional, min, max, stepsize);
    }

    public static class InstanceCreator implements XmlParser.IInstanceCreator<Number>
    {
        @Override
        public Number create(Element node)
        {
            return new Number(node);
        }
    }
}
