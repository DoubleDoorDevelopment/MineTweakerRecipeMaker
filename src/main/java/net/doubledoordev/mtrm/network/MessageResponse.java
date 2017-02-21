package net.doubledoordev.mtrm.network;

import io.netty.buffer.ByteBuf;
import net.doubledoordev.mtrm.MineTweakerRecipeMaker;
import net.doubledoordev.mtrm.gui.MTRMGui;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author Dries007
 */
public class MessageResponse implements IMessage
{
    private Status status;
    private String message;

    public MessageResponse()
    {
    }

    public MessageResponse(Status status, Object... args)
    {
        this.status = status;
        this.message = status.message == null ? null : String.format(status.message, args);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.status = Status.values()[buf.readByte()];
        if (this.status.message != null) message = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(status.ordinal());
        if (message != null) ByteBufUtils.writeUTF8String(buf, message);
    }

    public static class Handler implements IMessageHandler<MessageResponse, IMessage>
    {
        @Override
        public IMessage onMessage(MessageResponse message, MessageContext ctx)
        {
            if (message.status == Status.OK)
            {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Saved recipe change.").setStyle(new Style().setColor(TextFormatting.GREEN)));
            }
            else
            {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString(message.message).setStyle(new Style().setColor(TextFormatting.RED)));
                MineTweakerRecipeMaker.getLogger().info("Something went wrong: {}. Message: {}", message.status.name(), message.message);
                if (Minecraft.getMinecraft().currentScreen instanceof MTRMGui)
                {
                    // Fixme: IntelliJ are you drunk?
                    //noinspection ConstantConditions
                    ((MTRMGui) Minecraft.getMinecraft().currentScreen).showMessage(message.message);
                }
            }
            return null;
        }
    }

    public enum Status
    {
        OK(null), NO_OUT("There is no output specified."), NO_IN("There is no input specified."), WRITE_ERROR("IOError: %s");
        private final String message;

        Status(String message)
        {
            this.message = message;
        }
    }
}
