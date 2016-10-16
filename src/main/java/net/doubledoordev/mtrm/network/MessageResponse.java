package net.doubledoordev.mtrm.network;

import io.netty.buffer.ByteBuf;
import net.doubledoordev.mtrm.gui.MTRMGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author Dries007
 */
public class MessageResponse implements IMessage
{
    private String message;

    public MessageResponse()
    {
    }

    public MessageResponse(Status status, Object... args)
    {
        this.message = status.message == null ? null : String.format(status.message, args);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        if (buf.readBoolean()) message = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(message != null);
        if (message != null) ByteBufUtils.writeUTF8String(buf, message);
    }

    public static class Handler implements IMessageHandler<MessageResponse, IMessage>
    {
        @Override
        public IMessage onMessage(MessageResponse message, MessageContext ctx)
        {
            if (Minecraft.getMinecraft().currentScreen instanceof MTRMGui)
            {
                MTRMGui gui = ((MTRMGui) Minecraft.getMinecraft().currentScreen);
                gui.showMessage(message.message);
            }
            return null;
        }
    }

    public static enum Status
    {
        OK(null), NO_OUT("There is no output specified."), NO_IN("There is no input specified."), WRITE_ERROR("IOError: %s");
        private final String message;

        Status(String message)
        {
            this.message = message;
        }
    }
}
