package net.doubledoordev.mtrm;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.List;

/**
 * @author Dries007
 */
public class ServerCommand extends CommandBase
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
        player.openGui(MineTweakerRecipeMaker.instance, 0, sender.getEntityWorld(), 0, 0, 0);
    }
}
