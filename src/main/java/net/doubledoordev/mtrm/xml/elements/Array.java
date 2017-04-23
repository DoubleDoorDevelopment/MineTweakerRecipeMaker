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

import net.doubledoordev.mtrm.client.elements.ArrayElement;
import net.doubledoordev.mtrm.client.elements.GuiElement;
import net.doubledoordev.mtrm.xml.XmlParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Dries007
 */
@SuppressWarnings("WeakerAccess")
public class Array implements XmlParser.IStringObject
{
    public final XmlParser.IStringObject component;
    public final int min;
    public final int max;
    public final boolean optional;
    public final int dim;

    public Array(Element node) throws Exception
    {
        min = node.hasAttribute("min") ? Integer.parseInt(node.getAttribute("min")) : 1;
        max = Integer.parseInt(node.getAttribute("max"));
        optional = node.hasAttribute("optional") && Boolean.parseBoolean(node.getAttribute("optional"));

        XmlParser.IStringObject component = null;

        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            Node child = list.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE)
            {
                continue;
            }

            if (component != null)
            {
                throw new IllegalArgumentException("You can't have multiple components per array.");
            }

            XmlParser.IElementObject childObj = XmlParser.parse(child);
            if (!(childObj instanceof XmlParser.IStringObject))
            {
                throw new IllegalArgumentException("Node object not a string replaceable.");
            }

            component = (XmlParser.IStringObject) childObj;
        }
        if (component == null)
        {
            throw new IllegalArgumentException("Empty array.");
        }

        if (component instanceof Array)
        {
            Array sub = ((Array) component);
            dim = sub.dim + 1;

            // todo: remove this contraint?
            if (!(sub.component instanceof Slot || sub.component instanceof Oredict))
            {
                throw new IllegalArgumentException("MTRM only supports 2d arrays of Items or Oredicts");
            }
            if (dim > 2)
            {
                throw new IllegalArgumentException("MTRM doesn't support more than 2 dimensions to an array.");
            }
        }
        else
        {
            dim = 1;
        }

        this.component = component;
    }

    @Override
    public String toString()
    {
        return "Array{" +
                "component=" + component +
                ", min=" + min +
                ", max=" + max +
                ", optional=" + optional +
                '}';
    }

    @Override
    public String toHumanText()
    {
        return "[" + component.toHumanText() + ']';
    }

    @Override
    public GuiElement toGuiElement(GuiElement.GuiElementCallback callback)
    {
        return new ArrayElement(callback, optional, component, min, max, dim);
    }

    public static class InstanceCreator implements XmlParser.IInstanceCreator<Array>
    {
        @Override
        public Array create(Element node) throws Exception
        {
            return new Array(node);
        }
    }
}
