package net.doubledoordev.mtrm.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

/**
 * @author Dries007
 */
public class MTRMContainer extends Container
{
    public static final int RETURN_SLOT_ID = 1 + (3 * 3) + (3 * 9) + 9;
    /**
     * The crafting matrix inventory (3x3).
     */
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult = new InventoryCraftResult();
    public IInventory returnSlot = new InventoryCraftResult();

    public MTRMContainer(InventoryPlayer p_i1808_1_)
    {
        this.addSlotToContainer(new Slot(this.craftResult, 0, 138, 48));
        for (int y = 0; y < 3; ++y) for (int x = 0; x < 3; ++x) this.addSlotToContainer(new Slot(this.craftMatrix, x + y * 3, 22 + x * 26, 21 + y * 26));
        for (int y = 0; y < 3; ++y) for (int x = 0; x < 9; ++x) this.addSlotToContainer(new Slot(p_i1808_1_, x + y * 9 + 9, 8 + x * 18, 99 + y * 18));
        for (int x = 0; x < 9; ++x) this.addSlotToContainer(new Slot(p_i1808_1_, x, 8 + x * 18, 157));
        this.addSlotToContainer(new Slot(returnSlot, 0, -109, 143));

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory p_75130_1_)
    {
        super.onCraftMatrixChanged(p_75130_1_);
    }

    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return true;
    }

    @Override
    protected void retrySlotClick(int p_75133_1_, int p_75133_2_, boolean p_75133_3_, EntityPlayer p_75133_4_)
    {

    }

    @Override
    public ItemStack slotClick(int i, int mousebtn, ClickType clickTypeIn, EntityPlayer player)
    {
        ItemStack stack = ItemStack.EMPTY;
        if ((i >= 0 && i <= 9) || i == RETURN_SLOT_ID) // Fake slots
        {
            if (mousebtn == 2)
            {
                getSlot(i).putStack(ItemStack.EMPTY);
            }
            else if (mousebtn == 0)
            {
                InventoryPlayer playerInv = player.inventory;
//                getSlot(i).onSlotChanged();
                ItemStack stackSlot = getSlot(i).getStack();
                ItemStack stackHeld = playerInv.getItemStack();

                if (!stackSlot.isEmpty()) stack = stackSlot.copy();

                if (!stackHeld.isEmpty())
                {
                    ItemStack newStack = stackHeld.copy();
                    if (!(i == 0 || i == RETURN_SLOT_ID)) newStack.setCount(1);
                    getSlot(i).putStack(newStack);
                }
                else getSlot(i).putStack(ItemStack.EMPTY);
            }
            else if (mousebtn == 1)
            {
                InventoryPlayer playerInv = player.inventory;
                getSlot(i).onSlotChanged();
                ItemStack stackSlot = getSlot(i).getStack();
                ItemStack stackHeld = playerInv.getItemStack();

                stack = stackSlot.copy();

                if (!stackHeld.isEmpty())
                {
                    stackHeld = stackHeld.copy();
                    if (!stackSlot.isEmpty() && stackHeld.isItemEqual(stackSlot) && (i == 0 || i == RETURN_SLOT_ID))
                    {
                        if (stackSlot.getCount() < stackSlot.getMaxStackSize()) stackSlot.grow(1);
                    }
                    else
                    {
                        stackSlot.setCount(1);
                    }
                    getSlot(i).putStack(stackSlot);
                }
                else
                {
                    if (!stackSlot.isEmpty())
                    {
                        stackSlot.shrink(1);
                        if (stackSlot.isEmpty()) getSlot(i).putStack(ItemStack.EMPTY);
                    }
                }
            }
        }
        else
        {
            stack = super.slotClick(i, mousebtn, clickTypeIn, player);
        }
        return stack;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slots)
    {
        if (slots < 10 || slots == RETURN_SLOT_ID) inventorySlots.get(slots).putStack(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

//    public boolean func_94530_a(ItemStack p_94530_1_, Slot p_94530_2_)
//    {
//        return p_94530_2_.inventory != craftResult && p_94530_2_.inventory != returnSlot && super.func_94530_a(p_94530_1_, p_94530_2_);
//    }
}
