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

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.doubledoordev.mtrm.Helper;
import net.doubledoordev.mtrm.MineTweakerRecipeMaker;
import net.doubledoordev.mtrm.client.elements.GuiElement;
import net.doubledoordev.mtrm.xml.elements.*;
import net.doubledoordev.mtrm.xml.elements.Number;
import net.minecraft.util.ResourceLocation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;


/**
 * Public API (kinda)
 * You should use reflection and a try-catch when calling to avoid hard dependencies.
 *
 * @author Dries007
 */
@SuppressWarnings("WeakerAccess")
public final class XmlParser
{
    private static final Map<String, IInstanceCreator<? extends IElementObject>> TYPES = new HashMap<>();
    private static final Map<ResourceLocation, Root> DATA = new LinkedHashMap<>();
    private static final List<ResourceLocation> RELOAD_LIST = new ArrayList<>();// For removed override XMLs

    private static String currentlyProcessing;
    private static DocumentBuilder builder;

    static
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            factory.setValidating(true);
            factory.setIgnoringComments(true);
            builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new EntityResolver()
            {
                @Override
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
                {
                    InputSource is = null;
                    if (systemId.contains("mtrm.dtd"))
                    {
                        InputStream iss = MineTweakerRecipeMaker.class.getResourceAsStream(Helper.DTD);
                        is = new InputSource(iss);
                    }
                    return is;
                }
            });
            builder.setErrorHandler(new ErrorHandler()
            {
                @Override
                public void warning(SAXParseException exception) throws SAXException
                {
                    MineTweakerRecipeMaker.log().warn("Caught warning trying to parse XML: ", currentlyProcessing);
                    MineTweakerRecipeMaker.log().warn("XML warning", exception);
                }

                @Override
                public void error(SAXParseException exception) throws SAXException
                {
                    MineTweakerRecipeMaker.log().error("Caught error trying to parse XML: ", currentlyProcessing);
                    MineTweakerRecipeMaker.log().error("XML error", exception);
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException
                {
                    MineTweakerRecipeMaker.log().error("Caught fatal error trying to parse XML: ", currentlyProcessing);
                    Throwables.propagate(exception);
                }
            });
        }
        catch (Exception e)
        {
            Throwables.propagate(e);
        }

        registerType("mtrm", new Root.InstanceCreator());

        registerType("modifier", new Modifier.InstanceCreator());
        registerType("function", new Function.InstanceCreator());

        registerType("array", new Array.InstanceCreator());
        registerType("nbt", new Nbt.InstanceCreator());
        registerType("string", new ManualString.InstanceCreator());
        registerType("number", new Number.InstanceCreator());
        registerType("oredictAllowed", new Oredict.InstanceCreator());
        registerType("slot", new Slot.InstanceCreator());
    }

    private XmlParser()
    {

    }

    public static <T extends IElementObject> void registerType(String name, IInstanceCreator<T> creator)
    {
        name = name.toLowerCase();
        if (TYPES.containsKey(name)) throw new IllegalArgumentException("Duplicate type name: " + name);
        TYPES.put(name, creator);
    }

    public static <T extends IElementObject> T parse(Node node) throws Exception
    {
        if (node.getNodeType() != Node.ELEMENT_NODE) throw new IllegalArgumentException("Node isn't an element.");
        //noinspection unchecked
        IInstanceCreator<T> creator = (IInstanceCreator<T>) TYPES.get(node.getNodeName().toLowerCase());
        if (creator == null) throw new IllegalArgumentException("Node type unknown: " + node.getNodeName());
        return creator.create((Element) node);
    }

    private static Root parseRootXml(ResourceLocation rl) throws Exception
    {
        String path = "/assets/" + rl.getResourceDomain() + "/" + rl.getResourcePath();
        if (!path.toLowerCase().endsWith(".xml")) path += ".xml";
        InputStream is = MineTweakerRecipeMaker.class.getResourceAsStream(path);
        if (is == null) is = MineTweakerRecipeMaker.class.getResourceAsStream(path.replace(".xml", ".XML"));
        if (is == null) throw new FileNotFoundException(path);
        return parseRootXml('!' + path, is);
    }

    private static Root parseRootXml(File file) throws Exception
    {
        FileInputStream is = new FileInputStream(file);
        return parseRootXml(file.getPath(), is);
    }

    private static Root parseRootXml(String path, InputStream stream) throws Exception
    {
        currentlyProcessing = path;
        Document document = builder.parse(stream);
        stream.close();
        Node elementNode = null;
        if (document.getNodeType() == Node.ELEMENT_NODE)
        {
            elementNode = document;
        }
        else
        {
            for (Node child = document.getFirstChild(); child != null; child = child.getNextSibling())
            {
                if (child.getNodeType() != Node.ELEMENT_NODE) continue;
                if (elementNode != null) throw new IllegalStateException("More then 1 root element? File: " + path);
                elementNode = child;
            }
        }
        Root root = parse(elementNode);
        root.setSource(path);
        return root;
    }

    /**
     * INTERNAL
     * POST INIT OR LATER ONLY!
     * Must be called AFTER all normal XML files are loaded.
     */
    public static void addOverrideXml(ResourceLocation location, File file) throws Exception
    {
        ResourceLocation key = Helper.normalize(location);
        if (DATA.containsKey(key))
            MineTweakerRecipeMaker.log().info("Loading XML from filesystem [OVERRIDING!] {} ({})", location, file);
        else MineTweakerRecipeMaker.log().info("Loading XML from filesystem {} ({})", location, file);
        DATA.put(key, parseRootXml(file));
    }

    /**
     * IMC OR INIT!
     * Must be called BEFORE overrides are loaded.
     */
    public static void addRootXml(ResourceLocation location) throws Exception
    {
        ResourceLocation key = Helper.normalize(location);
        if (DATA.containsKey(key))
            throw new IllegalArgumentException("Duplicate ResourceLocation location. This is not allowed.");
        RELOAD_LIST.add(location);
        DATA.put(key, parseRootXml(location));
    }

    public static Root getRootXML(ResourceLocation location)
    {
        return DATA.get(Helper.normalize(location));
    }

    public static List<ResourceLocation> getLoadedRootXmlNames()
    {
        return ImmutableList.copyOf(DATA.keySet());
    }

    public static List<Root> getLoadedRootXmls()
    {
        return ImmutableList.copyOf(DATA.values());
    }

    /**
     * Debug function
     */
    public static void dump()
    {
        StringBuilder sb = new StringBuilder("XML PARSER DATA DUMP\n");
        for (Map.Entry<ResourceLocation, Root> e : DATA.entrySet())
        {
            sb.append("Location: ").append(e.getKey());
            if (e.getValue().isOverride()) sb.append("[OVERRIDE]");
            sb.append('\n');
            sb.append(e.getValue()).append('\n');
        }
        MineTweakerRecipeMaker.log().info(sb);
    }

    /**
     * Internal only pl0x
     */
    public static boolean reload()
    {
        Map<ResourceLocation, Root> oldMap = ImmutableMap.copyOf(DATA);
        List<ResourceLocation> oldReloadList = ImmutableList.copyOf(RELOAD_LIST);
        try
        {
            DATA.clear();
            RELOAD_LIST.clear();

            // Do the original XMLs (in order)
            for (ResourceLocation location : oldReloadList)
            {
                addRootXml(location);
            }

            // Do any known overrides first
            List<File> loadedOverrides = new ArrayList<>();
            for (Map.Entry<ResourceLocation, Root> old : oldMap.entrySet())
            {
                if (!old.getValue().isOverride()) continue;
                File f = new File(old.getValue().getSource());
                if (f.exists())
                {
                    addOverrideXml(old.getKey(), f);
                    loadedOverrides.add(f);
                }
            }
            // Now poke around for new XML files
            Helper.loadOverrides(loadedOverrides);

            return true;
        }
        catch (Exception e)
        {
            MineTweakerRecipeMaker.log().error("RELOAD FAILED WITH EXCEPTION:");
            MineTweakerRecipeMaker.log().catching(e);
            MineTweakerRecipeMaker.log().error("Restoring old data map!");
            DATA.clear();
            RELOAD_LIST.clear();
            DATA.putAll(oldMap);
            RELOAD_LIST.addAll(oldReloadList);
            return false;
        }
    }

    public interface IInstanceCreator<T>
    {
        T create(Element node) throws Exception;
    }

    public interface IElementObject
    {

    }

    public interface IStringObject extends IElementObject
    {
        String toHumanText();

        GuiElement toGuiElement(GuiElement.GuiElementCallback callback);
    }
}
