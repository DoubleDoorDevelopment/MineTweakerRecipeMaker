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

import net.doubledoordev.mtrm.client.elements.ButtonElement;
import net.doubledoordev.mtrm.xml.Function;
import net.doubledoordev.mtrm.xml.Root;
import net.minecraft.client.gui.inventory.GuiContainer;

/**
 * @author Dries007
 */
public class GuiRoot extends GuiListBase
{
    private final GuiContainer parent;
    private final Root root;

    public GuiRoot(GuiContainer parent, Root root)
    {
        super(parent.inventorySlots);
        this.parent = parent;
        this.root = root;

        for (final Function function : root.functionList)
        {
            guiElements.add(new ButtonElement(GuiRoot.this, false, function.name)
            {
                @Override
                protected void onClick()
                {
                    mc.displayGuiScreen(new GuiFunction(GuiRoot.this, function));
                }
            });
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();

        btnOk.visible = false;
        btnCancel.enabled = true;
        btnCancel.displayString = "Back";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        fontRendererObj.drawString(root.name, guiLeft + xSize / 2 - fontRendererObj.getStringWidth(root.name) / 2, guiTop - 10, 0xFFFFFF);
    }

    @Override
    protected void exit()
    {
        this.mc.displayGuiScreen(parent);
    }

    @Override
    protected void ok()
    {

    }
}
