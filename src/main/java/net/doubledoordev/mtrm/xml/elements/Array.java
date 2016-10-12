package net.doubledoordev.mtrm.xml.elements;

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
            if (child.getNodeType() != Node.ELEMENT_NODE) continue;

            if (component != null) throw new IllegalArgumentException("You can't have multiple components per array.");

            XmlParser.IElementObject childObj = XmlParser.parse(child);
            if (!(childObj instanceof XmlParser.IStringObject))
                throw new IllegalArgumentException("Node object not a string replaceable.");

            component = (XmlParser.IStringObject) childObj;
        }

        if (component == null) throw new IllegalArgumentException("Empty array.");

        this.component = component;
    }

    @Override
    public String toHumanText()
    {
        return "[" + component.toHumanText() + ']';
    }

    @Override
    public GuiElement toGuiElement(GuiElement.GuiElementCallback callback)
    {
        return null; // todo
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
