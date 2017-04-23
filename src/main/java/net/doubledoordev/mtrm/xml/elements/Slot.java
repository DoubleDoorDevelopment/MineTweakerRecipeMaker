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

package net.doubledoordev.mtrm.xml.elements;

import net.doubledoordev.mtrm.client.elements.GuiElement;
import net.doubledoordev.mtrm.client.elements.SlotElement;
import net.doubledoordev.mtrm.xml.XmlParser;
import org.w3c.dom.Element;

/**
 * @author Dries007
 */
@SuppressWarnings("WeakerAccess")
public class Slot implements XmlParser.IStringObject
{
    public final Type type;
    public final boolean optional;
    public final boolean wildcard;
    public final boolean metawildcard;
    public final boolean stacksize;
    public final boolean oredict;

    public Slot(Element node)
    {
        this.type = Type.valueOf(node.getAttribute("type").toUpperCase());
        this.optional = node.hasAttribute("optional") ? Boolean.parseBoolean(node.getAttribute("optional")) : type == Type.INGREDIENT;
        this.wildcard = node.hasAttribute("wildcardAllowed") ? Boolean.parseBoolean(node.getAttribute("wildcardAllowed")) : type == Type.INGREDIENT;
        this.metawildcard = node.hasAttribute("metaWildcardAllowed") ? Boolean.parseBoolean(node.getAttribute("metaWildcardAllowed")) : type == Type.INGREDIENT;
        this.oredict = node.hasAttribute("oredictAllowed") ? Boolean.parseBoolean(node.getAttribute("oredictAllowed")) : type == Type.INGREDIENT;
        this.stacksize = node.hasAttribute("stacksizeAllowed") ? Boolean.parseBoolean(node.getAttribute("stacksizeAllowed")) : type == Type.ITEM; // !! default reversed !!
    }

    @Override
    public String toString()
    {
        return "Slot{" +
                "type=" + type +
                ", optional=" + optional +
                ", wildcardAllowed=" + wildcard +
                ", metaWildcardAllowed=" + metawildcard +
                ", stacksizeAllowed=" + stacksize +
                ", oredictAllowed=" + oredict +
                '}';
    }

    @Override
    public String toHumanText()
    {
        return "<slot>";
    }

    @Override
    public GuiElement toGuiElement(GuiElement.GuiElementCallback callback)
    {
        return new SlotElement(callback, optional, type, wildcard, metawildcard, oredict, stacksize, false);
    }

    public enum Type
    {
        ITEM, INGREDIENT
    }

    public static class InstanceCreator implements XmlParser.IInstanceCreator<Slot>
    {
        @Override
        public Slot create(Element node)
        {
            return new Slot(node);
        }
    }
}
