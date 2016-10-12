/*
 * Copyright (c) 2015 - 2016, Dries007 & Double Door Development
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
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

import com.mojang.realmsclient.gui.ChatFormatting;
import net.doubledoordev.mtrm.Helper;
import net.doubledoordev.mtrm.xml.elements.Slot;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.ArrayList;

import static net.doubledoordev.mtrm.client.GuiBase.BASE;

/**
 * @author Dries007
 */
public class SlotElement extends GuiElement
{
    protected final Slot.Type type;
    protected final boolean wildcard;
    protected final boolean metawildcard;
    protected final boolean oredict;
    protected final boolean stacksize;

    protected ItemStack stack;

    public SlotElement(GuiElementCallback callback, boolean optional, Slot.Type type, boolean wildcard, boolean metawildcard, boolean oredict, boolean stacksize)
    {
        super(callback, optional);
        this.type = type;
        this.wildcard = wildcard;
        this.metawildcard = metawildcard;
        this.oredict = oredict;
        this.stacksize = stacksize;
    }

    @Override
    public void initGui()
    {
        height = 18;
        width = 18;
    }

    protected void focusStatusChanged()
    {
        // todo: option buttons
        // todo: add modifiers
        if (isFocused())
        {
            height = 32;
            width = 32;
        }
        else
        {
            height = 18;
            width = 18;
        }
        resizeCallback();
    }

    protected void setItemStack(ItemStack input)
    {
        if (input == null)
        {
            stack = null;
            updateButtonsCallback();
            return;
        }
        stack = input.copy();
        stack.setTagCompound(null);
        if (!stacksize) stack.stackSize = 1;
        updateButtonsCallback();
    }

    @Override
    public String save()
    {
        return Helper.itemstackToString(stack);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        mc.getTextureManager().bindTexture(BASE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(posX, posY, 256 - 18, 256 - 18, 18, 18);
        RenderHelper.enableGUIStandardItemLighting();
        drawItemStack(stack, posX+1, posY+1, null);
        RenderHelper.disableStandardItemLighting();
    }

    protected void drawItemStack(ItemStack stack, int x, int y, String altText)
    {
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        zLevel = 200.0F;
        RenderItem itemRender = mc.getRenderItem();
        itemRender.zLevel = 200.0F;
        FontRenderer font = null;
        if (stack != null) font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = mc.fontRendererObj;
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        itemRender.renderItemOverlayIntoGUI(font, stack, x, y, altText);
        zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
    }

    @Override
    public void update()
    {
        super.update();
    }

    protected ArrayList<String> getHoverLines()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add(ChatFormatting.AQUA + "Options:");
        list.add("- Type: " + type);
        list.add("- Optional: " + optional);
        list.add("- Wildcard: " + (wildcard ? "Allowed" : "Not Allowed"));
        list.add("- Meta Wildcard: " + (metawildcard ? "Allowed" : "Not Allowed"));
        list.add("- Ore Dictionary: " + (oredict ? "Allowed" : "Not Allowed"));
        list.add("- Stack size: " + (stacksize ? "Allowed" : "Not Allowed"));
        list.add(ChatFormatting.AQUA + "Current value:");
        if (stack != null) list.addAll(stack.getTooltip(mc.thePlayer, true));
        else list.add("null");
        return list;
    }

    @Override
    public void drawHover(int mouseX, int mouseY, int maxWidth, int maxHeight)
    {
        super.drawHover(mouseX, mouseY, maxWidth, maxHeight);
        GuiUtils.drawHoveringText(getHoverLines(), mouseX, mouseY, maxWidth, maxHeight, -1, mc.fontRendererObj);
    }

    @Override
    protected void onClickOn(int mouseX, int mouseY, int mouseButton)
    {
        super.onClickOn(mouseX, mouseY, mouseButton);
        ItemStack heldStack = mc.thePlayer.inventory.getItemStack();
        if (heldStack != null) setItemStack(heldStack);
        else if (stack != null)
        {
            if (mouseButton == 1)
            {
                setItemStack(null);
            }
        }
    }

    @Override
    public void setFocus(boolean focus)
    {
        boolean oldFocus = this.isFocused();
        super.setFocus(focus);
        if (oldFocus != focus) focusStatusChanged();
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode)
    {
        return super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean isValid()
    {
        return stack != null || optional;
    }

    @Override
    public void onClick(int mouseX, int mouseY, int mouseButton)
    {
        super.onClick(mouseX, mouseY, mouseButton);
    }
}
