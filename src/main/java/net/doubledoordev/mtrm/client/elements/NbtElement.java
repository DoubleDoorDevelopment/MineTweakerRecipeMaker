package net.doubledoordev.mtrm.client.elements;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;

/**
 * @author Dries007
 */
public class NbtElement extends StringInputElement
{
    protected String error = null;

    public NbtElement(GuiElementCallback callback, boolean optional)
    {
        super(callback, optional, "NBT");
    }

    protected void validate()
    {
        try
        {
            JsonToNBT.getTagFromJson(text);
            error = null;
        }
        catch (NBTException e)
        {
            error = e.getLocalizedMessage();
        }
        updateButtonsCallback();
    }

    @Override
    public void initGui()
    {
        super.initGui();
        validate();
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode)
    {
        boolean flag = super.keyTyped(typedChar, keyCode);
        validate();
        return flag;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        validate();
    }

    @Override
    protected void onClick()
    {
        super.onClick();
        setData(mc.thePlayer.inventory.getItemStack());
    }

    private void setData(ItemStack stack)
    {
        if (stack == null) return;
        NBTTagCompound root = stack.getTagCompound();
        if (root == null) text = "";
        else text = root.toString();
        validate();
    }

    @Override
    public boolean isValid()
    {
        if (!enabled) return optional;
        return error == null;
    }

    @Override
    public ArrayList<String> getHoverLines()
    {
        ArrayList<String> list = super.getHoverLines();
        if (!isValid()) list.add(TextFormatting.RED + error);
        return list;
    }

    @Override
    public String save()
    {
        return text;
    }
}
