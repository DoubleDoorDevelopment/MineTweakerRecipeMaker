package net.doubledoordev.mtrm;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.Arrays;
import java.util.List;

/**
 * @author Dries007
 */
public class MTRMCommand extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "minetweakerrecipemaker";
    }

    @Override
    public List getCommandAliases()
    {
        return Arrays.asList("mtrm");
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "Step 4: PROFIT?";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_)
    {
        return MinecraftServer.getServer().isSinglePlayer() || super.canCommandSenderUseCommand(p_71519_1_);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (sender instanceof EntityPlayer) ((EntityPlayer) sender).openGui(MineTweakerRecipeMaker.instance, 0, sender.getEntityWorld(), 0, 0, 0);
    }
}
