/*
 * Copyright (c) 2015 - 2017, Dries007 & Double Door Development
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions via the Curse or CurseForge platform are not allowed without
 *   written prior approval.
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

import net.doubledoordev.mtrm.client.parts.Icon;
import net.doubledoordev.mtrm.xml.XmlParser;
import net.doubledoordev.mtrm.xml.elements.Oredict;
import net.doubledoordev.mtrm.xml.elements.Slot;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dries007
 */
public class ArrayElement extends GuiElement implements GuiElement.GuiElementCallback
{
    public static final int SIDE_BUTTON_SIZE = 9;
    private final XmlParser.IStringObject component;
    private final int min;
    private final int max;
    private int count;
    private final int dim;
    private final List<GuiElement> subs = new ArrayList<>();
    private final SmallButtonElement add;
    private final SmallButtonElement remove;
    private final boolean row;

    public ArrayElement(GuiElementCallback callback, boolean optional, XmlParser.IStringObject component, int min, int max, int dim)
    {
        super(callback, optional);
        this.component = component;
        this.min = min;
        this.max = max;
        this.dim = dim;
        this.count = min;

        row = (component instanceof Slot || component instanceof Oredict) && dim == 1;

        add = new SmallButtonElement(this, false, "Add", Icon.SMALL_PLUS)
        {
            @Override
            protected void onClick()
            {
                if (count < ArrayElement.this.max)
                {
                    count++;
                    updateAddRemove();
                }
            }
        };
        remove = new SmallButtonElement(this, false, "Remove", Icon.SMALL_MINUS)
        {
            @Override
            protected void onClick()
            {
                if (count > ArrayElement.this.min)
                {
                    count--;
                    updateAddRemove();
                }
            }
        };

        for (int i = 0; i < max; i++)
        {
            subs.add(component.toGuiElement(this));
        }
    }

    @Override
    public void drawHover(int mouseX, int mouseY, int maxWidth, int maxHeight)
    {
        super.drawHover(mouseX, mouseY, maxWidth, maxHeight);
        if (add.isVisible() && add.isOver(mouseX, mouseY))
        {
            add.drawHover(mouseX, mouseY, maxWidth, maxHeight);
        }
        if (remove.isVisible() && remove.isOver(mouseX, mouseY))
        {
            remove.drawHover(mouseX, mouseY, maxWidth, maxHeight);
        }
        for (GuiElement obj : subs)
        {
            if (obj.isVisible() && obj.isOver(mouseX, mouseY))
            {
                obj.drawHover(mouseX, mouseY, maxWidth, maxHeight);
            }
        }
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode)
    {
        boolean handled = false;
        for (GuiElement element : subs)
        {
            if (element.isFocused())
            {
                handled |= element.keyTyped(typedChar, keyCode);
            }
        }
        if (!handled)
        {
            handled = super.keyTyped(typedChar, keyCode);
        }
        return handled;
    }

    protected void updateAddRemove()
    {
        add.setEnabled(count < max);
        remove.setEnabled(count > min);
        updateButtonsCallback();
    }

    protected void doArrayCalculations()
    {
        int height = 0;
        int i = 0;

        if (row)
        {
            int width = 0;
            int totalHeight = 0;
            for (GuiElement obj : subs)
            {
                obj.setVisible(i++ < count);
                if (!obj.isVisible())
                {
                    continue;
                }
                int objw = obj.getWidth();
                if (width + objw >= maxWidth - SIDE_BUTTON_SIZE)
                {
                    totalHeight += height;
                    height = 0;
                    width = 0;
                }
                obj.setPosition(posX + width, posY + totalHeight);
                width += objw;
                height = Math.max(height, obj.getHeight());
            }
            height += totalHeight;
        }
        else
        {
            for (GuiElement obj : subs)
            {
                obj.setVisible(i++ < count);
                if (!obj.isVisible())
                {
                    continue;
                }
                obj.setPosition(posX, posY + height);
                height += obj.getHeight();
            }
        }

        add.setPosition(posX + width - SIDE_BUTTON_SIZE, posY);
        remove.setPosition(posX + width - SIDE_BUTTON_SIZE, posY + SIDE_BUTTON_SIZE);
        setSize(width, Math.max(height, 2 * SIDE_BUTTON_SIZE));
    }

    @Override
    public void setPosition(int x, int y)
    {
        super.setPosition(x, y);
        doArrayCalculations();
    }

    @Override
    public void initGui()
    {
        add.setVisible(true);
        add.initGui(SIDE_BUTTON_SIZE);
        add.setSize(SIDE_BUTTON_SIZE, SIDE_BUTTON_SIZE);
        remove.setVisible(true);
        remove.initGui(SIDE_BUTTON_SIZE);
        remove.setSize(SIDE_BUTTON_SIZE, SIDE_BUTTON_SIZE);
        for (GuiElement obj : subs)
        {
            obj.initGui(maxWidth - SIDE_BUTTON_SIZE);
        }
        doArrayCalculations();
        updateAddRemove();
    }

    @Override
    public void update()
    {
        super.update();
    }

    @Override
    public String save()
    {
        // Arbitrary guess of avg element size
        StringBuilder sb = new StringBuilder(50 * dim * count);
        for (int i = 0; ; i++)
        {
            sb.append(subs.get(i).save());
            if (i + 1 == count)
            {
                return sb.append(']').toString();
            }
            sb.append(", ");
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        if (add.isValid())
        {
            add.draw(mouseX, mouseY, partialTicks);
        }
        if (remove.isValid())
        {
            remove.draw(mouseX, mouseY, partialTicks);
        }
        for (GuiElement sub : subs)
        {
            if (!sub.isVisible())
            {
                continue;
            }
            int x = sub.getPosX();
            int y = sub.getPosY();
            drawRect(x, y, x + sub.getWidth(), y + sub.getHeight(), dim == 1 ? 0x5000FF00 : 0x500000FF);
            sub.draw(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void onClickOn(int mouseX, int mouseY, int mouseButton)
    {
        add.onClick(mouseX, mouseY, mouseButton);
        remove.onClick(mouseX, mouseY, mouseButton);
        for (GuiElement sub : subs)
        {
            if (!sub.isVisible())
            {
                continue;
            }
            sub.onClick(mouseX, mouseY, mouseButton);
        }
        if (add.isOver(mouseX, mouseY) || remove.isOver(mouseX, mouseY))
        {
            doArrayCalculations();
            resizeCallback();
        }
    }

    @Override
    public boolean isValid()
    {
        if (!super.isValid())
        {
            return false;
        }
        if (subs.size() < min && subs.size() > max)
        {
            return false;
        }
        for (GuiElement sub : subs)
        {
            if (sub.isVisible() && !sub.isValid())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void resizeCallback(@Nullable GuiElement element)
    {
        doArrayCalculations();
        resizeCallback();
    }

    @Override
    public void updateButtons(@Nullable GuiElement element)
    {
        updateButtonsCallback();
    }

    @Override
    public void setFocus(boolean focus)
    {
        super.setFocus(focus);
        if (!focus)
        {
            add.setFocus(false);
            remove.setFocus(false);
            for (GuiElement sub : subs)
            {
                sub.setFocus(false);
            }
        }
    }
}
