package net.doubledoordev.mtrm.xml;

import org.w3c.dom.Element;

@SuppressWarnings("WeakerAccess")
public class Oredict implements XmlParser.IStringObject
{
    public final boolean optional;

    public Oredict(Element node)
    {
        this.optional = node.hasAttribute("optional") && Boolean.parseBoolean(node.getAttribute("optional"));
    }

    @Override
    public String toString()
    {
        return "Oredict{optional=" + optional + '}';
    }

    public static class InstanceCreator implements XmlParser.IInstanceCreator<Oredict>
    {
        @Override
        public Oredict create(Element node)
        {
            return new Oredict(node);
        }
    }
}
