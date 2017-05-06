/*
 * Copyright (c) 2015 - 2017, Dries007 & Double Door Development
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions via the Curse or CurseForge platform are not allowed without
 *   written prior approval.
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.doubledoordev.mtrm;

import com.google.common.collect.ImmutableList;
import net.doubledoordev.mtrm.xml.XmlParser;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
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
    private Configuration config;

    private String externalEditor;

    public static Logger log() { return instance.log; }

    public static String getExternalEditor() { return instance.externalEditor; }

//    private SimpleNetworkWrapper snw;
//
//    public static SimpleNetworkWrapper getSnw()
//    {
//        return instance.snw;
//    }

    /*
     * If the mod is only installed on the server, we are fine.
     * If the mod version is lower then 2.0, panic.
     * If the mod version is ${version}, we are in dev, assume we are good.
     */
    @NetworkCheckHandler
    public boolean networkChecker(Map<String, String> mods, Side side)
    {
        if (!mods.containsKey(Helper.MODID))
        {
            return side.isClient();
        }
        DefaultArtifactVersion remote = new DefaultArtifactVersion("Remote", mods.get(Helper.MODID));
        if (remote.getVersionString().equals("${version}"))
        {
            return true;
        }
        if (!new DefaultArtifactVersion("Minimum2.0", "[2.0,)").getRange().containsVersion(remote))
        {
            return false;
        }
        DefaultArtifactVersion local = new DefaultArtifactVersion("Local", metadata.version);
        return remote.compareTo(local) >= 0;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        log = event.getModLog();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

//        int id = 0;
//        snw = NetworkRegistry.INSTANCE.newSimpleChannel(Helper.MODID);
//        snw.registerMessage(MessageList.Handler.class, MessageList.class, id++, Side.CLIENT);
//        snw.registerMessage(MessageList.Handler.class, MessageList.class, id++, Side.SERVER);
//        snw.registerMessage(MessageConfirmEdit.Handler.class, MessageConfirmEdit.class, id++, Side.CLIENT);
//        snw.registerMessage(MessageReload.Handler.class, MessageReload.class, id++, Side.CLIENT);

        config = new Configuration(event.getSuggestedConfigurationFile());
        reloadConfig();
    }

    public static void reloadConfig()
    {
        final Configuration config = instance.config;

        instance.externalEditor = config.getString("external_editor", Configuration.CATEGORY_CLIENT, "", "Open the config with a specific editor instead of the system default. Use %f for the full path to the file. It will be stored in your environment temp folder when you edit.");

        if (config.hasChanged())
        {
            config.save();
        }
    }

    @Mod.EventHandler
    public void preInit(FMLInitializationEvent event) throws Exception
    {
        // Register the "vanilla" XML first, to allow others to reference it.
        XmlParser.addRootXml(new ResourceLocation(Helper.MODID.toLowerCase(), "vanilla"));
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) throws Exception
    {
        Helper.loadOverrides(ImmutableList.<File>of());
        // Now register all the manual XML files
        File rootFolder = new File(new File(Loader.instance().getConfigDir(), Helper.MODID), "overrides");
        if (!rootFolder.exists())
        {
            rootFolder.mkdirs();
        }
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
