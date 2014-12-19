package net.doubledoordev.mtrm.network;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Dries007
 */
public class MessageSend implements IMessage
{
    public boolean remove;
    public boolean shapeless;
    public boolean mirrored;
    public String[] data = new String[10];

    public MessageSend()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        remove = buf.readBoolean();
        shapeless = buf.readBoolean();
        mirrored = buf.readBoolean();
        data = new String[buf.readInt()];
        for (int i = 0; i < data.length; i++)
        {
            if (buf.readBoolean()) data[i] = ByteBufUtils.readUTF8String(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(remove);
        buf.writeBoolean(shapeless);
        buf.writeBoolean(mirrored);
        buf.writeInt(data.length);
        for (String item : data)
        {
            boolean tag = item != null && !item.equals("null");
            buf.writeBoolean(tag);
            if (tag) ByteBufUtils.writeUTF8String(buf, item);
        }
    }

    private String getName(boolean noIngredients)
    {
        StringBuilder name = new StringBuilder();
        name.append(remove ? "remove" : "add").append('_');
        if (!noIngredients)
        {
            name.append(shapeless ? "shapeless" : "shaped").append('_');
            if (mirrored) name.append("mirrored").append('_');
        }
        name.append(data[0]);
        return name.toString().replaceAll("<|>|\\s", "").replace(':', '-').replace('*', 'X');
    }

    private String getScript(boolean noIngredients, ArrayList<ArrayList<String>> ingredients)
    {
        StringBuilder script = new StringBuilder();
        script.append("recipes.").append(remove ? "remove" : "add");
        if (!noIngredients) script.append(shapeless ? "Shapeless" : "Shaped");
        script.append('(').append(data[0]);
        if (!noIngredients)
        {
            script.append(", ");
            if (shapeless) script.append(ingredients.get(0).toString());
            else script.append(ingredients.toString());
        }
        return script.append(");").toString();
    }

    private MessageResponse makeScript()
    {
        ArrayList<ArrayList<String>> ingredients = new ArrayList<>();
        boolean noIngredients = true;
        if (shapeless)
        {
            ArrayList<String> lst = new ArrayList<>();
            for (int i = 1; i < data.length; i++)
            {
                if (data[i] != null) lst.add(data[i]);
            }
            noIngredients = lst.isEmpty();
            ingredients.add(lst);
        }
        else
        {
            String[][] rawIngredients = new String[][]{new String[3], new String[3], new String[3]};
            boolean rowsNull[] = new boolean[]{true, true, true};
            boolean colsNull[] = new boolean[]{true, true, true};
            for (int i = 1; i < data.length; i++)
            {
                if (data[i] != null)
                {
                    int row = (i - 1) % 3;
                    int col = (i - 1) / 3;
                    noIngredients = false;
                    rowsNull[row] = false;
                    colsNull[col] = false;
                    rawIngredients[row][col] = data[i];
                }
            }
            for (int i = 0; i < rowsNull.length; i++)
            {
                if (!rowsNull[i])
                {
                    ArrayList<String> row = new ArrayList<>();
                    for (int j = 0; j < colsNull.length; j++)
                    {
                        if (!colsNull[j]) row.add(rawIngredients[i][j]);
                    }
                    ingredients.add(row);
                }
            }
        }
        if (data[0] == null) return new MessageResponse(MessageResponse.Status.NO_OUT);
        if (!remove && noIngredients) return new MessageResponse(MessageResponse.Status.NO_IN);
        int nameId = 0;
        String name = getName(noIngredients);
        File file;
        do
        {
            file = new File("scripts", name + nameId++ + ".zs");
        }
        while (file.exists());
        if (!file.getParentFile().exists()) file.getParentFile().mkdir();
        try
        {
            FileUtils.writeStringToFile(file, getScript(noIngredients, ingredients), "utf-8", false);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return new MessageResponse(MessageResponse.Status.WRITE_ERROR, e.toString());
        }
        return new MessageResponse(MessageResponse.Status.OK);
    }

    public static class Handler implements IMessageHandler<MessageSend, IMessage>
    {
        @Override
        public IMessage onMessage(MessageSend message, MessageContext ctx)
        {
            return message.makeScript();
        }
    }
}
