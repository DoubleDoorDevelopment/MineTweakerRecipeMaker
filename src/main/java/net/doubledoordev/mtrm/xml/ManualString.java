package net.doubledoordev.mtrm.xml;

import org.w3c.dom.Element;

@SuppressWarnings("WeakerAccess")
public class ManualString implements XmlParser.IStringObject
{
    @Override
    public String toString()
    {
        return "ManualString{}";
    }

    public static class InstanceCreator implements XmlParser.IInstanceCreator<ManualString>
    {
        @Override
        public ManualString create(Element node)
        {
            return new ManualString();
        }
    }
}
