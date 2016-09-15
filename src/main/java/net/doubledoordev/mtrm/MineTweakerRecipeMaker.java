package net.doubledoordev.mtrm;

import com.google.common.collect.ImmutableList;
import net.doubledoordev.mtrm.xml.XmlParser;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Dries007
 */
@Mod(modid = Helper.MODID, name = Helper.NAME)
public class MineTweakerRecipeMaker
{
    @Mod.Instance(Helper.MODID)
    public static MineTweakerRecipeMaker instance;

    @Mod.Metadata(Helper.MODID)
    private ModMetadata metadata;

    private Logger log;

    public static Logger log()
    {
        return instance.log;
    }

//    private SimpleNetworkWrapper snw;
//
//    public static SimpleNetworkWrapper getSnw()
//    {
//        return instance.snw;
//    }

    /*
     * If the mod is only installed on the server, we are fine.
     * If the mod version is lower then 2.0, panic.
     */
    @NetworkCheckHandler
    public boolean networkChecker(Map<String, String> mods, Side side)
    {
        if (true) return true; //fixme: doesn't work in dev env?

        if (!mods.containsKey(Helper.MODID)) return side.isClient();
        DefaultArtifactVersion remote = new DefaultArtifactVersion("Remote", mods.get(Helper.MODID));
        if (!new DefaultArtifactVersion("Minimum2.0", "[2.0,)").getRange().containsVersion(remote)) return false;
        DefaultArtifactVersion local = new DefaultArtifactVersion("Local", metadata.version);
        return remote.compareTo(local) >= 0;
    }

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        log = event.getModLog();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

//        int id = 0;
//        snw = NetworkRegistry.INSTANCE.newSimpleChannel(Helper.MODID);
//        snw.registerMessage(MessageList.Handler.class, MessageList.class, id++, Side.CLIENT);
//        snw.registerMessage(MessageList.Handler.class, MessageList.class, id++, Side.SERVER);
//        snw.registerMessage(MessageConfirmEdit.Handler.class, MessageConfirmEdit.class, id++, Side.CLIENT);
//        snw.registerMessage(MessageReload.Handler.class, MessageReload.class, id++, Side.CLIENT);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) throws Exception
    {
        // Register the "vanilla" XML first, to allow others to reference it.
        XmlParser.addRootXml(new ResourceLocation(Helper.MODID.toLowerCase(), "vanilla"));
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) throws Exception
    {
        Helper.loadOverrides(ImmutableList.<File>of());
        // Now register all the manual XML files
        File modFolder = new File(Loader.instance().getConfigDir(), Helper.MODID);
        if (!modFolder.exists()) modFolder.mkdirs();
        Helper.makeReadme(new File(modFolder, "README.txt"));
        File rootFolder = new File(modFolder, "overrides");
        if (!rootFolder.exists()) rootFolder.mkdirs();
        Path root = rootFolder.toPath();
        for (File f : Helper.findXMLFiles(rootFolder, new ArrayList<File>()))
        {
            String path = root.relativize(f.toPath()).toString().replaceFirst("\\\\|/", ":");
            XmlParser.addOverrideXml(new ResourceLocation(path), f);
        }
    }

    @Mod.EventHandler
    public void imc(FMLInterModComms.IMCEvent event)
    {
        for (FMLInterModComms.IMCMessage message : event.getMessages())
        {
            if (!message.isResourceLocationMessage())
            {
                log.error("Invalid IMC (Inter Mod Communications) message received from {}. Message must be a ResourceLocation.", message.getSender());
                continue;
            }

            try
            {
                ResourceLocation rl = message.getResourceLocationValue();
                log.info("Got an IMC from {} asking for {} to be loaded.", message.getSender(), rl);

                XmlParser.addRootXml(rl);
            }
            catch (Exception e)
            {
                log.error("Caught exception while trying to load an IMC XML", e);
            }
        }
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new ServerCommand());
    }
}
