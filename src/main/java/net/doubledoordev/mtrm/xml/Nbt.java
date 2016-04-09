package net.doubledoordev.mtrm.xml;

import org.w3c.dom.Element;

@SuppressWarnings("WeakerAccess")
public class Nbt implements XmlParser.IStringObject
{
    @Override
    public String toString()
    {
        return "Nbt{}";
    }

    public static class InstanceCreator implements XmlParser.IInstanceCreator<Nbt>
    {
        @Override
        public Nbt create(Element node)
        {
            return new Nbt();
        }
    }
}
