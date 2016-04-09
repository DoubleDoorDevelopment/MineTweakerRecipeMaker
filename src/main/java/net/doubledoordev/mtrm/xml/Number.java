package net.doubledoordev.mtrm.xml;

import org.w3c.dom.Element;

@SuppressWarnings("WeakerAccess")
public class Number implements XmlParser.IStringObject
{
    public final double min;
    public final double max;
    public final double stepsize;

    public Number(Element node)
    {
        min = node.hasAttribute("min") ? Double.parseDouble(node.getAttribute("min")) : 1;
        max = node.hasAttribute("max") ? Double.parseDouble(node.getAttribute("max")) : Integer.MAX_VALUE;
        stepsize = node.hasAttribute("stepsize") ? Double.parseDouble(node.getAttribute("stepsize")) : 1;
    }

    @Override
    public String toString()
    {
        return "Number{min=" + min + ", max=" + max + ", stepsize=" + stepsize + '}';
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
