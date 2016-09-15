package net.doubledoordev.mtrm.xml.elements;

import net.doubledoordev.mtrm.client.elements.GuiElement;
import net.doubledoordev.mtrm.client.elements.StringInputElement;
import net.doubledoordev.mtrm.xml.XmlParser;
import org.w3c.dom.Element;

/**
 * @author Dries007
 */
@SuppressWarnings("WeakerAccess")
public class ManualString implements XmlParser.IStringObject
{
    public final boolean optional;

    public ManualString(Element node)
    {
        optional = node.hasAttribute("optional") && Boolean.parseBoolean(node.getAttribute("optional"));
    }

    @Override
    public String toString()
    {
        return "ManualString{}";
    }

    @Override
    public String toHumanText()
    {
        return "\"Text\"";
    }

    @Override
    public GuiElement toGuiElement(GuiElement.GuiElementCallback callback)
    {
        return new StringInputElement(callback, optional, "String");
    }

    public static class InstanceCreator implements XmlParser.IInstanceCreator<ManualString>
    {
        @Override
        public ManualString create(Element node)
        {
            return new ManualString(node);
        }
    }
}
