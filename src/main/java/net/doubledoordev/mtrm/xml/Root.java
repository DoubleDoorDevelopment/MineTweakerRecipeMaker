package net.doubledoordev.mtrm.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class Root implements XmlParser.IElementObject
{
    private final List<Modifier> modifierList = new ArrayList<>();
    private final List<Function> functionList = new ArrayList<>();

    public Root(Element node)
    {
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            Node child = list.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) continue;

            XmlParser.IElementObject childObj = XmlParser.parse(child);
            if (childObj instanceof Modifier) add((Modifier) childObj);
            else if (childObj instanceof Function) add((Function) childObj);
            else throw new IllegalArgumentException("Node object not Modifier or Function");
        }
    }

    public void add(Modifier modifier)
    {
        modifierList.add(modifier);
    }

    public void add(Function function)
    {
        functionList.add(function);
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        s.append("modifierList: \n");
        for (Modifier modifier : modifierList) s.append(modifier).append('\n');
        s.append("\nfunctionList: \n");
        for (Function function : functionList) s.append(function).append('\n');
        s.append('\n');
        return s.toString();
    }

    public static class InstanceCreator implements XmlParser.IInstanceCreator<Root>
    {
        @Override
        public Root create(Element node)
        {
            return new Root(node);
        }
    }
}
