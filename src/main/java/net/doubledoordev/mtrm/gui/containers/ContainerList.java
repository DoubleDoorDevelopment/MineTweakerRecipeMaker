package net.doubledoordev.mtrm.gui.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * @author Dries007
 */
public class ContainerList extends Container
{
    public ContainerList()
    {

    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }
}
