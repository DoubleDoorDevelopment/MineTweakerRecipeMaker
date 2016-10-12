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
//public class MessageReload implements IMessage
//{
//    public MessageReload()
//    {
//
//    }
//
//    @Override
//    public void fromBytes(ByteBuf buf)
//    {
//
//    }
//
//    @Override
//    public void toBytes(ByteBuf buf)
//    {
//
//    }
//
//    public static class Handler implements IMessageHandler<MessageReload, IMessage>
//    {
//        @Override
//        public IMessage onMessage(MessageReload message, MessageContext ctx)
//        {
//            if (ctx.side.isClient())
//            {
////                try
////                {
////                    MineTweakerRecipeMaker.instance.xmlData = Helper.parseRootXml();
////                    Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new TextComponentString("XML Reload done.").setChatStyle(new Style().setColor(TextFormatting.GRAY)));
////                }
////                catch (ParserConfigurationException | SAXException | IOException e)
////                {
////                    Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new TextComponentString("Tried, and failed, to reload the XML. Please check console for stacktrace.").setChatStyle(new Style().setColor(TextFormatting.RED)));
////                    e.printStackTrace();
////                }
//            }
//            return null;
//        }
//    }
//}
