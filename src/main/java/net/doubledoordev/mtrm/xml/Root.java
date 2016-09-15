package net.doubledoordev.mtrm.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dries007
 */
@SuppressWarnings("WeakerAccess")
public class Root implements XmlParser.IElementObject
{
    public final String name;
    public final int version;
    public final List<Modifier> modifierList = new ArrayList<>();
    public final List<Function> functionList = new ArrayList<>();
    private String source;

    public Root(Element node) throws Exception
    {
        name = node.getAttribute("name");
        version = Integer.parseInt(node.getAttribute("version"));

        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            Node child = list.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) continue;

            XmlParser.IElementObject childObj = XmlParser.parse(child);
            if (childObj instanceof Modifier) modifierList.add((Modifier) childObj);
            else if (childObj instanceof Function) functionList.add((Function) childObj);
            else throw new IllegalArgumentException("Node object not Modifier or Function");
        }
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        s.append("Source: ").append(source).append('\n');
        s.append("ModifierList: \n");
        for (Modifier modifier : modifierList) s.append(modifier).append('\n');
        s.append("\nFunctionList: \n");
        for (Function function : functionList) s.append(function).append('\n');
        s.append('\n');
        return s.toString();
    }

    public final void setSource(String source)
    {
        if (this.source != null) throw new IllegalStateException("Cannot modify source after object creation.");
        this.source = source;
    }

    public final String getSource()
    {
        return source;
    }

    public boolean isOverride()
    {
        if (source == null) throw new IllegalStateException("Someone forgot to call setSource!");
        return source.charAt(0) != '!';
    }

    public static class InstanceCreator implements XmlParser.IInstanceCreator<Root>
    {
        @Override
        public Root create(Element node) throws Exception
        {
            return new Root(node);
        }
    }
}
