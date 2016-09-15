package net.doubledoordev.mtrm.xml.elements;

import net.doubledoordev.mtrm.client.elements.GuiElement;
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
        min = node.hasAttribute("min") ? Double.parseDouble(node.getAttribute("min")) : 1;
        max = node.hasAttribute("max") ? Double.parseDouble(node.getAttribute("max")) : Integer.MAX_VALUE;
        stepsize = node.hasAttribute("stepsize") ? Double.parseDouble(node.getAttribute("stepsize")) : 1;
        optional = node.hasAttribute("optional") && Boolean.parseBoolean(node.getAttribute("optional"));
    }

    @Override
    public String toString()
    {
        return "Number{min=" + min + ", max=" + max + ", stepsize=" + stepsize + '}';
    }

    @Override
    public String toHumanText()
    {
        return "<Number>";
    }

    @Override
    public GuiElement toGuiElement(GuiElement.GuiElementCallback callback)
    {
        return null;
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
