package net.doubledoordev.mtrm.xml.elements;

import net.doubledoordev.mtrm.client.elements.GuiElement;
import net.doubledoordev.mtrm.xml.XmlParser;
import org.w3c.dom.Element;

/**
 * @author Dries007
 */
@SuppressWarnings("WeakerAccess")
public class Nbt implements XmlParser.IStringObject
{
    public final boolean optional;

    public Nbt(Element node)
    {
        optional = node.hasAttribute("optional") && Boolean.parseBoolean(node.getAttribute("optional"));
    }

    @Override
    public String toString()
    {
        return "Nbt{}";
    }

    @Override
    public String toHumanText()
    {
        return "{NBT}";
    }

    @Override
    public GuiElement toGuiElement(GuiElement.GuiElementCallback callback)
    {
        return null;
    }

    public static class InstanceCreator implements XmlParser.IInstanceCreator<Nbt>
    {
        @Override
        public Nbt create(Element node)
        {
            return new Nbt(node);
        }
    }
}
