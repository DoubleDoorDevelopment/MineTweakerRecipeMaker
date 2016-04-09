package net.doubledoordev.mtrm.gui;

import net.doubledoordev.mtrm.gui.client.GuiList;
import net.doubledoordev.mtrm.gui.containers.ContainerList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * @author Dries007
 */
public class GuiHandler implements IGuiHandler
{
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        try
        {
            switch (Id.values()[ID])
            {
                case LIST:
                    return new ContainerList();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        try
        {
            switch (Id.values()[ID])
            {
                case LIST:
                    return new GuiList();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public enum Id
    {
        LIST
    }
}
