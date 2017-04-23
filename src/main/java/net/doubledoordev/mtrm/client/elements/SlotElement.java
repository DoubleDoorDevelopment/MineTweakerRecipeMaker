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
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

import static net.doubledoordev.mtrm.client.GuiBase.BASE;

/**
 * @author Dries007
 */
public class SlotElement extends GuiElement
{
    protected final Slot.Type type;
    protected final boolean wildcardAllowed;
    protected final boolean metaWildcardAllowed;
    protected final boolean oredictAllowed;
    protected final boolean stacksizeAllowed;
    protected final boolean oredictRequired;

    protected ItemStack stack;

    protected int tickCounter;
    protected String oredict;
    protected int oredictCounter;
    protected List<ItemStack> oredictList;
    // For looping trough the available list when the same stack is clicked twice.
    protected ItemStack oredictPrevStack;
    protected int[] oredictIds;
    protected int oredictIdCounter;

    public SlotElement(GuiElementCallback callback, boolean optional, Slot.Type type, boolean wildcardAllowed, boolean metaWildcardAllowed, boolean oredictAllowed, boolean stacksizeAllowed, boolean oredictRequired)
    {
        super(callback, optional);
        this.type = type;
        this.wildcardAllowed = wildcardAllowed;
        this.metaWildcardAllowed = metaWildcardAllowed;
        this.oredictAllowed = oredictAllowed;
        this.stacksizeAllowed = stacksizeAllowed;
        this.oredictRequired = oredictRequired;
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

    protected void setItemStackOrOredict(ItemStack input)
    {
        if (input == null) reset();
        else if (!oredictAllowed) setItemStack(input);
        else if (input == oredictPrevStack)
        {
            oredictIdCounter++;
            oredictIdCounter %= oredictIds.length;
            setOredict(OreDictionary.getOreName(oredictIds[oredictIdCounter]));
        }
        else
        {
            oredictPrevStack = input;
            oredictIds = OreDictionary.getOreIDs(input);
        }
    }

    /**
     * Reset
     */
    protected void reset()
    {
        tickCounter = 0;
        oredict = null;
        oredictCounter = 0;
        oredictList = null;
        stack = null;
        updateButtonsCallback();
    }

    protected void setItemStack(ItemStack input)
    {
        if (oredictRequired) return; // Shouldn't happen, but you never know
        reset();
        stack = input.copy();
        stack.setTagCompound(null);
        if (!stacksizeAllowed) stack.stackSize = 1;
        updateButtonsCallback();
    }

    protected void setOredict(String value)
    {
        if (!oredictAllowed) return; // Shouldn't happen, but you never know
        reset();
        oredictList = OreDictionary.getOres(value, false);
        updateButtonsCallback();
    }

    @Override
    public String save()
    {
        return (oredict != null) ? String.valueOf(oredict) : Helper.itemstackToString(stack);
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
        if (stack == null) return;
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        zLevel = 200.0F;
        RenderItem itemRender = mc.getRenderItem();
        itemRender.zLevel = 200.0F;
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        //noinspection ConstantConditions
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
        if (oredict != null)
        {
            if (tickCounter / 6 % 2 == 0)
            {
                int stacksize = 0;
                if (stack != null) stacksize = stack.stackSize;
                stack = oredictList.get(oredictCounter++ % oredictList.size()).copy();
                stack.stackSize = stacksize;
            }
            tickCounter ++;
        }
    }

    protected ArrayList<String> getHoverLines()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add(ChatFormatting.AQUA + "Options:");
        list.add("- Type: " + type);
        list.add("- Optional: " + optional);
        list.add("- Wildcard: " + (wildcardAllowed ? "Allowed" : "Not Allowed"));
        list.add("- Meta Wildcard: " + (metaWildcardAllowed ? "Allowed" : "Not Allowed"));
        list.add("- Ore Dictionary: " + (oredictAllowed ? "Allowed" : "Not Allowed"));
        list.add("- Stack size: " + (stacksizeAllowed ? "Allowed" : "Not Allowed"));
        list.add(ChatFormatting.AQUA + "Current value:");
        if (oredict != null) list.add(oredict);
        else if (stack != null) list.addAll(stack.getTooltip(mc.thePlayer, true));
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
        if (heldStack != null) setItemStackOrOredict(heldStack);
        else if (stack != null && mouseButton == 1) setItemStackOrOredict(null);
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
        return oredict != null || stack != null || optional;
    }

    @Override
    public void onClick(int mouseX, int mouseY, int mouseButton)
    {
        super.onClick(mouseX, mouseY, mouseButton);
    }
}
