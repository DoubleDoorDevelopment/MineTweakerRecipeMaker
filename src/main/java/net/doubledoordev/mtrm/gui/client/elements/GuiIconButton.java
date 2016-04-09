package net.doubledoordev.mtrm.gui.client.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiIconButton extends GuiButton
{
    private final Icon icon;

    public GuiIconButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, Icon icon)
    {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.icon = icon;
    }

    public GuiIconButton(int buttonId, int x, int y, String buttonText, Icon icon)
    {
        this(buttonId, x, y, 20, 20, buttonText, icon);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (!visible) return;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        FontRenderer fr = mc.fontRendererObj;

        hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
        int i = getHoverState(hovered);

        mc.getTextureManager().bindTexture(buttonTextures);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        drawTexturedModalRect(xPosition, yPosition, 0, 46 + i * 20, width / 2, height);
        drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 46 + i * 20, width / 2, height);

        mc.getTextureManager().bindTexture(Icon.TEXTURE);
        int iconId = icon.hasAlt() && !enabled ? icon.getAlt().ordinal() : icon.ordinal();
        drawTexturedModalRect(xPosition - 8 + width / 2, yPosition - 8 + height / 2, 16 * (iconId % 16), 16 * (iconId / 16), 16, 16);

        mouseDragged(mc, mouseX, mouseY);
        int j = 14737632;

        if (packedFGColour != 0)
        {
            j = packedFGColour;
        }
        else if (!enabled)
        {
            j = 10526880;
        }
        else if (hovered)
        {
            j = 16777120;
        }

        if (hovered)
        {
            int centerX = width / 2;
            int centerY = height / 2;
            int width = fr.getStringWidth(displayString) / 2 + 2;
            int height = fr.FONT_HEIGHT / 2 + 2;
            drawRect(xPosition + centerX - width, yPosition + centerY - height, xPosition + centerX + width, yPosition + centerY + height, 0xA0000000);
            drawCenteredString(fr, displayString, xPosition + this.width / 2, yPosition + (this.height - 8) / 2, j);
        }
    }
}
