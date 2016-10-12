package net.doubledoordev.mtrm.xml.elements;

import net.doubledoordev.mtrm.client.elements.GuiElement;
import net.doubledoordev.mtrm.xml.XmlParser;
import org.w3c.dom.Element;

/**
 * @author Dries007
 */
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

    @Override
    public String toHumanText()
    {
        return "<oredict>";
    }

    @Override
    public GuiElement toGuiElement(GuiElement.GuiElementCallback callback)
    {
        return null; // todo
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
