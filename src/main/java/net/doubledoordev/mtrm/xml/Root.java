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

package net.doubledoordev.mtrm.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dries007
 */
@SuppressWarnings("WeakerAccess")
public class Root implements XmlParser.IElementObject
{
    public final String name;
    public final int version;
    public final List<Modifier> modifierList = new ArrayList<>();
    public final List<Function> functionList = new ArrayList<>();
    private String source;

    public Root(Element node) throws Exception
    {
        name = node.getAttribute("name");
        version = Integer.parseInt(node.getAttribute("version"));

        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            Node child = list.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) continue;

            XmlParser.IElementObject childObj = XmlParser.parse(child);
            if (childObj instanceof Modifier) modifierList.add((Modifier) childObj);
            else if (childObj instanceof Function) functionList.add((Function) childObj);
            else throw new IllegalArgumentException("Node object not Modifier or Function");
        }
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        s.append("Source: ").append(source).append('\n');
        s.append("ModifierList: \n");
        for (Modifier modifier : modifierList) s.append(modifier).append('\n');
        s.append("\nFunctionList: \n");
        for (Function function : functionList) s.append(function).append('\n');
        s.append('\n');
        return s.toString();
    }

    public final String getSource()
    {
        return source;
    }

    public final void setSource(String source)
    {
        if (this.source != null) throw new IllegalStateException("Cannot modify source after object creation.");
        this.source = source;
    }

    public boolean isOverride()
    {
        if (source == null) throw new IllegalStateException("Someone forgot to call setSource!");
        return source.charAt(0) != '!';
    }

    public static class InstanceCreator implements XmlParser.IInstanceCreator<Root>
    {
        @Override
        public Root create(Element node) throws Exception
        {
            return new Root(node);
        }
    }
}
