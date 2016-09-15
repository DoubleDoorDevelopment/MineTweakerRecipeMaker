package net.doubledoordev.mtrm.client.parts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import static net.doubledoordev.mtrm.client.ClientHelper.BTN_COLORS;
import static net.doubledoordev.mtrm.client.ClientHelper.TEXT_COLORS;
import static net.minecraft.client.renderer.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA;
import static net.minecraft.client.renderer.GlStateManager.DestFactor.ZERO;
import static net.minecraft.client.renderer.GlStateManager.SourceFactor.ONE;
import static net.minecraft.client.renderer.GlStateManager.SourceFactor.SRC_ALPHA;

/**
 * @author Dries007
 */
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
//

        hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
        // 0 = disabled, 1 = normal, 2 = hover
        int state = getHoverState(hovered);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO);
        GlStateManager.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA);

        int right = xPosition + width;
        int bottom = yPosition + height;

        drawRect(xPosition, yPosition, right, bottom, 0xFF000000);

        drawRect(xPosition + 1, yPosition + 1, right - 2, bottom - 3, 0xA0000000 | BTN_COLORS[state]);

        drawHorizontalLine(xPosition + 1, right - 2, yPosition + 1, 0x80555555 | BTN_COLORS[state]);
        drawVerticalLine(xPosition + 1, yPosition + 1, bottom - 1, 0x80555555 | BTN_COLORS[state]);
        drawRect(xPosition + 1, bottom - 3, right - 1, bottom - 1, 0x80000000 | BTN_COLORS[state]);
        drawVerticalLine(right - 2, yPosition + 1, bottom - 3, 0x80000000 | BTN_COLORS[state]);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO);
        GlStateManager.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA);
        mc.getTextureManager().bindTexture(Icon.TEXTURE);
        int iconId = icon.hasAlt() && !enabled ? icon.getAlt().ordinal() : icon.ordinal();
        drawTexturedModalRect(xPosition - 8 + width / 2, yPosition - 8 + height / 2, 16 * (iconId % 16), 16 * (iconId / 16), 16, 16);

        if (hovered)
        {
            FontRenderer fr = mc.fontRendererObj;
            int centerX = width / 2;
            int centerY = height / 2;
            int width = fr.getStringWidth(displayString) / 2 + 2;
            int height = fr.FONT_HEIGHT / 2 + 2;
            drawRect(xPosition + centerX - width, yPosition + centerY - height, xPosition + centerX + width, yPosition + centerY + height, 0xA0000000);
            drawCenteredString(fr, displayString, xPosition + this.width / 2, yPosition + (this.height - 8) / 2, TEXT_COLORS[state]);
        }
    }
}
