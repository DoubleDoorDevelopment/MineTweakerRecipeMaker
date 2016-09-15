package net.doubledoordev.mtrm.xml.elements;

import net.doubledoordev.mtrm.client.elements.GuiElement;
import net.doubledoordev.mtrm.xml.XmlParser;
import org.w3c.dom.Element;

/**
 * @author Dries007
 */
@SuppressWarnings("WeakerAccess")
public class Slot implements XmlParser.IStringObject
{
    public final Type type;
    public final boolean optional;
    public final boolean wildcard;
    public final boolean metawildcard;
    public final boolean stacksize;
    public final boolean oredict;

    public Slot(Element node)
    {
        this.type = Type.valueOf(node.getAttribute("type").toUpperCase());
        this.optional = node.hasAttribute("optional") ? Boolean.parseBoolean(node.getAttribute("optional")) : type == Type.INGREDIENT;
        this.wildcard = node.hasAttribute("wildcard") ? Boolean.parseBoolean(node.getAttribute("wildcard")) : type == Type.INGREDIENT;
        this.metawildcard = node.hasAttribute("metawildcard") ? Boolean.parseBoolean(node.getAttribute("metawildcard")) : type == Type.INGREDIENT;
        this.oredict = node.hasAttribute("oredict") ? Boolean.parseBoolean(node.getAttribute("oredict")) : type == Type.INGREDIENT;
        this.stacksize = node.hasAttribute("stacksize") ? Boolean.parseBoolean(node.getAttribute("stacksize")) : type == Type.ITEM; // !! default reversed !!
    }

    @Override
    public String toString()
    {
        return "Slot{type=" + type + ", optional=" + optional + ", wildcard=" + wildcard + ", metawildcard=" + metawildcard + ", stacksize=" + stacksize + ", oredict=" + oredict + '}';
    }

    @Override
    public String toHumanText()
    {
        return "<slot>";
    }

    @Override
    public GuiElement toGuiElement(GuiElement.GuiElementCallback callback)
    {
        return null;
    }

    public enum Type
    {
        ITEM, INGREDIENT
    }

    public static class InstanceCreator implements XmlParser.IInstanceCreator<Slot>
    {
        @Override
        public Slot create(Element node)
        {
            return new Slot(node);
        }
    }
}
