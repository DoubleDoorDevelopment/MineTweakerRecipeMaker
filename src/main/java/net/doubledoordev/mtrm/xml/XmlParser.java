package net.doubledoordev.mtrm.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public final class XmlParser
{
    private static final Map<String, IInstanceCreator<? extends IElementObject>> TYPEMAP = new HashMap<>();

    static
    {
        registerType("mtrm", new Root.InstanceCreator());

        registerType("modifier", new Modifier.InstanceCreator());
        registerType("function", new Function.InstanceCreator());

        registerType("array", new Array.InstanceCreator());
        registerType("nbt", new Nbt.InstanceCreator());
        registerType("string", new ManualString.InstanceCreator());
        registerType("number", new Number.InstanceCreator());
        registerType("oredict", new Oredict.InstanceCreator());
        registerType("slot", new Slot.InstanceCreator());
    }

    private XmlParser()
    {

    }

    public static <T extends IElementObject> void registerType(String name, IInstanceCreator<T> creator)
    {
        name = name.toLowerCase();
        if (TYPEMAP.containsKey(name)) throw new IllegalArgumentException("Duplicate name: " + name);
        TYPEMAP.put(name, creator);
    }

    public static <T extends IElementObject> T parse(Node node)
    {
        if (node.getNodeType() != Node.ELEMENT_NODE) throw new IllegalArgumentException("Node isn't an element.");
        //noinspection unchecked
        IInstanceCreator<T> creator = (IInstanceCreator<T>) TYPEMAP.get(node.getNodeName().toLowerCase());
        if (creator == null) throw new IllegalArgumentException("Node type unknown: " + node.getNodeName());
        return creator.create((Element) node);
    }

    public interface IInstanceCreator<T>
    {
        T create(Element node);
    }

    public interface IElementObject
    {

    }

    public interface IStringObject extends IElementObject
    {

    }
}
