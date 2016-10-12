package net.doubledoordev.mtrm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

/**
 * @author Dries007
 */
public class InventoryContainer extends Container
{
    public final int offsetX = 217;
    public final int offsetY = 48;

    private final EntityPlayer player;

    public InventoryContainer(EntityPlayer player)
    {
        this.player = player;

        for (int x = 0; x < 3; ++x) for (int y = 0; y < 9; ++y) this.addSlotToContainer(new Slot(player.inventory, y + x * 9 + 9, offsetY + y * 18, x * 18 + offsetX));
        for (int x = 0; x < 9; ++x) this.addSlotToContainer(new Slot(player.inventory, x, offsetY + x * 18, 58 + offsetX));
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return player == playerIn;
    }
}
