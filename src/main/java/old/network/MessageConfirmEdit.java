//package old.network;
//
//import io.netty.buffer.ByteBuf;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
//import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
//
///**
// * @author Dries007
// */
//public class MessageConfirmEdit implements IMessage
//{
//    private long hash;
//
//    @SuppressWarnings("unused")
//    public MessageConfirmEdit()
//    {
//
//    }
//
//    public MessageConfirmEdit(long hash)
//    {
//        this.hash = hash;
//    }
//
//    @Override
//    public void fromBytes(ByteBuf buf)
//    {
//        hash = buf.readLong();
//    }
//
//    @Override
//    public void toBytes(ByteBuf buf)
//    {
//        buf.writeLong(hash);
//    }
//
//    public static class Handler implements IMessageHandler<MessageConfirmEdit, IMessage>
//    {
//        @Override
//        public IMessage onMessage(MessageConfirmEdit message, MessageContext ctx)
//        {
//            if (ctx.side.isClient())
//            {
//                GuiList.confirm(message.hash);
//            }
//            return null;
//        }
//    }
//}
