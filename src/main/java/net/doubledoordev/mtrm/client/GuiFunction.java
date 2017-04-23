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

package net.doubledoordev.mtrm.client;

import net.doubledoordev.mtrm.MineTweakerRecipeMaker;
import net.doubledoordev.mtrm.client.elements.GuiElement;
import net.doubledoordev.mtrm.client.elements.StringElement;
import net.doubledoordev.mtrm.xml.Function;
import net.doubledoordev.mtrm.xml.XmlParser;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.inventory.GuiContainer;

/**
 * @author Dries007
 */
public class GuiFunction extends GuiListBase implements GuiElement.GuiElementCallback
{
    protected static final int ID_SAVE = 10;
    private final GuiContainer parent;
    private final Function function;
    private final String genericText;
    private String currentText;

    public GuiFunction(GuiContainer parent, Function function)
    {
        super(parent.inventorySlots);
        this.parent = parent;
        this.function = function;
        StringBuilder textBuilder = new StringBuilder();
        for (Object obj : function.parts)
        {
            if (obj instanceof String)
            {
                textBuilder.append(obj);
                guiElements.add(new StringElement(this, (String) obj));
            }
            else
            {
                XmlParser.IStringObject sObj = (XmlParser.IStringObject) obj;
                textBuilder.append(sObj.toHumanText());
                GuiElement e = sObj.toGuiElement(this);
                if (e != null)
                {
                    guiElements.add(e);
                }
                else
                {
                    guiElements.add(new StringElement(this, sObj.toHumanText(), 0xFF0000));
                }
            }
            textBuilder.append(' ');
        }
        genericText = textBuilder.toString();
        currentText = "";
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        StringBuilder sb = new StringBuilder();
        for (GuiElement obj : guiElements)
        {
            sb.append(obj.save());
        }
        currentText = sb.toString();
    }

    @Override
    public void initGui()
    {
        super.initGui();
        changes = true;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        // todo: remove
        // debug start
        fontRendererObj.drawString("Generic", -200, -100, 0xFF5050);
        fontRendererObj.drawSplitString(genericText, -200, -90, 200, 0xFFFFFF);
        fontRendererObj.drawString("Current", xSize + 25, -100, 0xFF5050);
        fontRendererObj.drawSplitString(currentText, xSize + 25, -90, 200, 0xFFFFFF);
        // debug end
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        fontRendererObj.drawString(function.name, guiLeft + xSize / 2 - fontRendererObj.getStringWidth(function.name) / 2, guiTop - 10, 0xFFFFFF);
    }

    @Override
    protected void exit()
    {
        this.mc.displayGuiScreen(parent);
    }

    @Override
    protected void ok()
    {
        this.mc.displayGuiScreen(new GuiYesNo(this, "Does this look good?", currentText, ID_SAVE));
    }

    @Override
    public void confirmClicked(boolean result, int id)
    {
        switch (id)
        {
        case ID_SAVE:
            if (result)
            {
                confirm_ok();
            }
            else
            {
                this.mc.displayGuiScreen(this);
            }
            break;
        default:
            super.confirmClicked(result, id);
        }
    }

    private void confirm_ok()
    {
        //todo: save currentText
        // Serverside:
        // do packet stuff
        // figure out place in file
        // add author + timestamp
        MineTweakerRecipeMaker.log().info("Saved: {}", currentText);
        this.mc.displayGuiScreen(parent);
    }
}
