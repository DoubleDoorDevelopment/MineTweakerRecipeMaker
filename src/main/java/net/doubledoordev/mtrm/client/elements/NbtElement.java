/*
 * Copyright (c) 2015 - 2017, Dries007 & Double Door Development
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * + Redistributions via the Curse or CurseForge platform are not allowed without
 *   written prior approval.
 *
 * + Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * + Redistributions in binary form must reproduce the above copyright notice,
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
