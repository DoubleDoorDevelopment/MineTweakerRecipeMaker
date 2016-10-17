package net.doubledoordev.mtrm;

import net.doubledoordev.mtrm.gui.MTRMGuiHandler;
import net.doubledoordev.mtrm.network.MessageResponse;
import net.doubledoordev.mtrm.network.MessageSend;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

/**
 * @author Dries007
 */
@Mod(modid = MineTweakerRecipeMaker.MODID, name = MineTweakerRecipeMaker.NAME)
public class MineTweakerRecipeMaker
{
    public static final String MODID = "mtrm";
    public static final String NAME = "MineTweakerRecipeMaker";

    @Mod.Instance(MODID)
    public static MineTweakerRecipeMaker instance;

    private SimpleNetworkWrapper snw;

    public static SimpleNetworkWrapper getSnw()
    {
        return instance.snw;
    }

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new MTRMGuiHandler());

        int id = 0;
        snw = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        snw.registerMessage(MessageSend.Handler.class, MessageSend.class, id++, Side.SERVER);
        snw.registerMessage(MessageResponse.Handler.class, MessageResponse.class, id++, Side.CLIENT);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new MTRMCommand());
    }

    @NetworkCheckHandler
    public boolean networkCheckHandler(Map<String, String> map, Side side)
    {
        return side.isClient() || map.containsKey(MODID);
    }
}
