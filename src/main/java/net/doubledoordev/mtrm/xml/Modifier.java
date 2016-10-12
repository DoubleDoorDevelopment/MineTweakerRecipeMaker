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

package net.doubledoordev.mtrm.xml;

import com.google.common.collect.ImmutableList;
import net.doubledoordev.mtrm.xml.elements.Slot;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Dries007
 */
@SuppressWarnings("WeakerAccess")
public class Modifier implements XmlParser.IElementObject
{
    public final String name;
    public final Slot.Type in;
    public final Slot.Type out;
    public final ImmutableList<Object> parts;

    public Modifier(Element element) throws Exception
    {
        name = element.getAttribute("name");
        in = element.hasAttribute("in") ? Slot.Type.valueOf(element.getAttribute("in").toUpperCase()) : Slot.Type.ITEM;
        out = element.hasAttribute("out") ? Slot.Type.valueOf(element.getAttribute("out").toUpperCase()) : Slot.Type.INGREDIENT;

        ImmutableList.Builder<Object> parts = ImmutableList.builder();
        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            switch (node.getNodeType())
            {
                default:
                    System.out.println("Ignored node: " + node);
                    break;
                case Node.TEXT_NODE:
                    String str = node.getTextContent().trim();
                    if (!str.isEmpty()) parts.add(str);
                    break;
                case Node.ELEMENT_NODE:
                    XmlParser.IElementObject child = XmlParser.parse(node);
                    if (!(child instanceof XmlParser.IStringObject))
                        throw new IllegalArgumentException("Child of unknown type in '" + name + "': " + node);
                    parts.add(child);
                    break;
            }
        }
        this.parts = parts.build();
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("Modifier{");
        sb.append("name='").append(name).append('\'');
        sb.append(", in=").append(in);
        sb.append(", out=").append(out);
        sb.append(", parts=[");
        for (int i = 0; i < parts.size(); i++)
        {
            Object o = parts.get(i);
            if (o instanceof String) sb.append('\'').append(o).append('\'');
            else sb.append(o);
            if (i + 1 < parts.size()) sb.append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }

    public static class InstanceCreator implements XmlParser.IInstanceCreator<Modifier>
    {
        @Override
        public Modifier create(Element node) throws Exception
        {
            return new Modifier(node);
        }
    }
}
