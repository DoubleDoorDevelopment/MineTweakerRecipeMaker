package net.doubledoordev.mtrm;

import net.doubledoordev.mtrm.gui.GuiHandler;
import net.doubledoordev.mtrm.network.MessageConfirmEdit;
import net.doubledoordev.mtrm.network.MessageList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * @author Dries007
 */
@Mod(modid = Helper.MODID, name = Helper.NAME)
public class MineTweakerRecipeMaker
{
    @SuppressWarnings("WeakerAccess")
    @Mod.Instance(Helper.MODID)
    public static MineTweakerRecipeMaker instance;
    public static Logger log;
    private SimpleNetworkWrapper snw;

    public static SimpleNetworkWrapper getSnw()
    {
        return instance.snw;
    }

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        log = event.getModLog();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        int id = 0;
        snw = NetworkRegistry.INSTANCE.newSimpleChannel(Helper.MODID);
        snw.registerMessage(MessageList.Handler.class, MessageList.class, id++, Side.CLIENT);
        snw.registerMessage(MessageList.Handler.class, MessageList.class, id++, Side.SERVER);
        snw.registerMessage(MessageConfirmEdit.Handler.class, MessageConfirmEdit.class, id++, Side.CLIENT);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        //todo: load xml and or check github for updated version
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new Command());
    }

    @NetworkCheckHandler
    public boolean networkCheckHandler(Map<String, String> map, Side side)
    {
        return side.isClient() || map.containsKey(Helper.MODID);
    }
}
