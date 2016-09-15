package net.doubledoordev.mtrm.client.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

/**
 * @author Dries007
 */
public class StringElement extends GuiElement
{
    private final String string;
    private final int color;
    private final FontRenderer fontRendererObj;

    public StringElement(GuiElementCallback callback, String string)
    {
        this(callback, string, 0x000000);
    }

    public StringElement(GuiElementCallback callback, String string, int color)
    {
        super(callback, false);
        this.string = string;
        this.color = color;
        fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
    }

    @Override
    public void initGui()
    {
        height = 1 + fontRendererObj.listFormattedStringToWidth(string, width).size() * fontRendererObj.FONT_HEIGHT;
    }

    @Override
    public void setSize(int width, int height)
    {
        super.setSize(width, height);
        initGui();
    }

    @Override
    public String save()
    {
        return string;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        fontRendererObj.drawSplitString(string, posX, posY + 1, maxWidth, color);
    }
}
