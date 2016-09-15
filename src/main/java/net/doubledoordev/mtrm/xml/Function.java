package net.doubledoordev.mtrm.xml;

import com.google.common.collect.ImmutableList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Dries007
 */
@SuppressWarnings("WeakerAccess")
public class Function implements XmlParser.IElementObject
{
    public final String name;
    public final int weight;
    public final ImmutableList<Object> parts;

    public Function(Element element) throws Exception
    {
        name = element.getAttribute("name");
        weight = Integer.parseInt(element.getAttribute("weight"));

        ImmutableList.Builder<Object> parts = ImmutableList.builder();
        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            switch (node.getNodeType())
            {
                default:
                    System.out.println("Ignored node: " + node);
                    break;
                case Node.TEXT_NODE:
                    String str = node.getTextContent().trim();
                    if (!str.isEmpty()) parts.add(str);
                    break;
                case Node.ELEMENT_NODE:
                    XmlParser.IElementObject child = XmlParser.parse(node);
                    if (!(child instanceof XmlParser.IStringObject))
                        throw new IllegalArgumentException("Child of unknown type in '" + name + "': " + node);
                    parts.add(child);
                    break;
            }
        }
        this.parts = parts.build();
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("Function{");
        sb.append("name='").append(name).append('\'');
        sb.append(", parts=[");
        for (int i = 0; i < parts.size(); i++)
        {
            Object o = parts.get(i);
            if (o instanceof String) sb.append('\'').append(o).append('\'');
            else sb.append(o);
            if (i + 1 < parts.size()) sb.append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }

    public static class InstanceCreator implements XmlParser.IInstanceCreator<Function>
    {
        @Override
        public Function create(Element node) throws Exception
        {
            return new Function(node);
        }
    }
}
