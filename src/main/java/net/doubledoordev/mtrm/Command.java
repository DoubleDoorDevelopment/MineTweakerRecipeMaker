package net.doubledoordev.mtrm;

import com.google.common.base.Throwables;
import com.google.common.io.Files;
import net.doubledoordev.mtrm.gui.GuiHandler;
import net.doubledoordev.mtrm.network.MessageList;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

/**
 * @author Dries007
 */
public class Command extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "minetweakerrecipemaker";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Collections.singletonList("mtrm");
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/mtrm";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        player.openGui(MineTweakerRecipeMaker.instance, GuiHandler.Id.LIST.ordinal(), sender.getEntityWorld(), 0, 0, 0);
        try
        {
            File file = Helper.getScriptFile();
            MessageList parser = new MessageList(file);
            parser = Files.readLines(file, Charset.defaultCharset(), parser);
            MineTweakerRecipeMaker.getSnw().sendTo(parser, player);
        }
        catch (IOException e)
        {
            Throwables.propagate(e);
        }
    }
}
