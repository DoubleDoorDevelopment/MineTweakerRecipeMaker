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

import net.doubledoordev.mtrm.client.ClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.ArrayList;

import static net.doubledoordev.mtrm.client.ClientHelper.BTN_COLORS;
import static net.doubledoordev.mtrm.client.ClientHelper.TEXT_COLORS;
import static net.minecraft.client.renderer.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA;
import static net.minecraft.client.renderer.GlStateManager.DestFactor.ZERO;
import static net.minecraft.client.renderer.GlStateManager.SourceFactor.ONE;
import static net.minecraft.client.renderer.GlStateManager.SourceFactor.SRC_ALPHA;

/**
 * @author Dries007
 */
public abstract class ButtonElement extends GuiElement
{
    protected final FontRenderer fontRendererObj;
    protected final Minecraft mc;

    protected int defaultHeight;
    private String displayTextOriginal;
    private String displayText = "";

    protected ButtonElement(GuiElementCallback callback, boolean optional, String displayText)
    {
        super(callback, optional);
        mc = Minecraft.getMinecraft();
        fontRendererObj = mc.fontRendererObj;
        defaultHeight = fontRendererObj.FONT_HEIGHT + 6;
        displayTextOriginal = displayText;
    }

    protected abstract void onClick();

    @Override
    public String save()
    {
        return null;
    }

    @Override
    public void initGui()
    {
        this.height = defaultHeight;
        setDisplayText(displayTextOriginal);
    }

    /**
     * @see GuiButton#drawButton
     */
    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        // 0 = disabled, 1 = normal, 2 = hover, 3 = error
        int state = isValid() ? isEnabled() ? isOver(mouseX, mouseY) ? 2 : 1 : 0 : 3;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO);
        GlStateManager.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA);

        int right = posX + width;
        int bottom = posY + height;

        drawRect(posX, posY, right, bottom, 0xFF000000);

        int color = BTN_COLORS[state];
        drawRect(posX + 1, posY + 1, right - 2, bottom - 3, 0xA0000000 | color);
        drawHorizontalLine(posX + 1, right - 2, posY + 1, 0x80555555 | color);
        drawVerticalLine(posX + 1, posY + 1, bottom - 1, 0x80555555 | color);
        drawRect(posX + 1, bottom - 3, right - 1, bottom - 1, 0x80000000 | color);
        drawVerticalLine(right - 2, posY + 1, bottom - 3, 0x80000000 | color);

        drawCenteredString(fontRendererObj, displayText, posX + width / 2, posY + (height - 8) / 2, TEXT_COLORS[state]);
    }

    public ArrayList<String> getHoverLines()
    {
        ArrayList<String> list = new ArrayList<>();
        if (displayText.length() != displayTextOriginal.length()) list.add(displayTextOriginal);
        return list;
    }

    @Override
    public void drawHover(int mouseX, int mouseY, int maxWidth, int maxHeight)
    {
        super.drawHover(mouseX, mouseY, maxWidth, maxHeight);
        GuiUtils.drawHoveringText(getHoverLines(), mouseX, mouseY, maxWidth, maxHeight, -1, fontRendererObj);
    }

    @Override
    protected void onClickOn(int mouseX, int mouseY, int mouseButton)
    {
        if (isEnabled() && mouseButton == 0)
        {
            playSound();
            onClick();
        }
        else if (optional && mouseButton == 1)
        {
            setEnabled(!enabled);
        }
    }

    protected void playSound()
    {
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode)
    {
        return false;
    }

    @Override
    public void setSize(int width, int height)
    {
        super.setSize(width, height);
        setDisplayText(displayTextOriginal);
    }

    public void setDisplayText(String target)
    {
        displayTextOriginal = target;
        if (fontRendererObj.getStringWidth(target) > width - 10)
            displayText = ClientHelper.split(fontRendererObj, target, width - 10, 1).get(0);
        else displayText = displayTextOriginal;
    }
}
