package net.doubledoordev.mtrm;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.Arrays;
import java.util.List;

/**
 * @author Dries007
 */
public class MTRMCommand extends CommandBase
{
    @Override
    public String getName()
    {
        return "minetweakerrecipemaker";
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("mtrm");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        getCommandSenderAsPlayer(sender).openGui(MineTweakerRecipeMaker.instance, 0, sender.getEntityWorld(), 0, 0, 0);
    }

    @Override
    public String getUsage(ICommandSender p_71518_1_)
    {
        return "Step 4: PROFIT?";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return server.isSinglePlayer() || super.checkPermission(server, sender);
    }
}
