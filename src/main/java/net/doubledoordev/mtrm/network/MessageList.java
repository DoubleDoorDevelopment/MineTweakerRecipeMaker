package net.doubledoordev.mtrm.network;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import io.netty.buffer.ByteBuf;
import net.doubledoordev.mtrm.Helper;
import net.doubledoordev.mtrm.MineTweakerRecipeMaker;
import net.doubledoordev.mtrm.gui.client.GuiList;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dries007
 */
public class MessageList implements IMessage, LineProcessor<MessageList>
{
    private List<String> lines = new ArrayList<>();
    private long hash;

    @SuppressWarnings("unused")
    public MessageList()
    {

    }

    public MessageList(List<String> lines, long hash)
    {
        this.lines = lines;
        this.hash = hash;
    }

    public MessageList(File file) throws IOException
    {
        hash = Files.hash(file, Hashing.md5()).padToLong(); // All we need if the file's fingerprint, md5 is good enough.
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        hash = buf.readLong();
        int i = buf.readInt();
        while (i-- != 0)
        {
            lines.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(hash);
        buf.writeInt(lines.size());
        for (String line : lines)
        {
            ByteBufUtils.writeUTF8String(buf, line);
        }
    }

    @Override
    public boolean processLine(String line) throws IOException
    {
        line = line.trim();
        if (line.isEmpty() || line.startsWith("//") || line.startsWith("#")) return true;
        lines.add(line);
        return true;
    }

    @Override
    public MessageList getResult()
    {
        return this;
    }

    public static class Handler implements IMessageHandler<MessageList, IMessage>
    {
        @Override
        public IMessage onMessage(MessageList message, MessageContext ctx)
        {
            if (ctx.side.isServer())
            {
                try
                {
                    File file = Helper.getScriptFile();
                    if (message.hash != Files.hash(file, Hashing.md5()).padToLong())
                    {
                        //todo: handle conflicts
                        MineTweakerRecipeMaker.log.warn("File signature mismatch. Overwriting (Player: {})", ctx.getServerHandler().playerEntity.getName());
                        ctx.getServerHandler().playerEntity.addChatComponentMessage(new TextComponentString("You just overwrote another file change!!").setChatStyle(new Style().setColor(TextFormatting.RED)));
                    }
                    BufferedWriter br = Files.newWriter(file, Charset.defaultCharset());
                    Helper.writeHeader(br, ctx.getServerHandler().playerEntity.getName());
                    for (String line : message.lines)
                    {
                        br.write(line);
                        br.newLine();
                    }
                    br.close();
                    return new MessageConfirmEdit(Files.hash(file, Hashing.md5()).padToLong());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                GuiList.handle(message.hash, message.lines);
            }
            return null;
        }
    }
}
