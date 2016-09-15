package net.doubledoordev.mtrm.client.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;

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
    protected String displayText;

    protected ButtonElement(GuiElementCallback callback, boolean optional, String displayText)
    {
        super(callback, optional);
        this.displayText = displayText;
        mc = Minecraft.getMinecraft();
        fontRendererObj = mc.fontRendererObj;
        defaultHeight = fontRendererObj.FONT_HEIGHT + 6;
    }

    @Override
    public void initGui()
    {
        this.height = defaultHeight;
    }

    /**
     * @see GuiButton#drawButton
     */
    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        // 0 = disabled, 1 = normal, 2 = hover
        int state = isEnabled() ? isOver(mouseX, mouseY) ? 2 : 1 : 0;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO);
        GlStateManager.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA);

        int right = posX + width;
        int bottom = posY + height;

        drawRect(posX, posY, right, bottom, 0xFF000000);

        drawRect(posX + 1, posY + 1, right - 2, bottom - 3, 0xA0000000 | BTN_COLORS[state]);

        drawHorizontalLine(posX + 1, right - 2, posY + 1, 0x80555555 | BTN_COLORS[state]);
        drawVerticalLine(posX + 1, posY + 1, bottom - 1, 0x80555555 | BTN_COLORS[state]);
        drawRect(posX + 1, bottom - 3, right - 1, bottom - 1, 0x80000000 | BTN_COLORS[state]);
        drawVerticalLine(right - 2, posY + 1, bottom - 3, 0x80000000 | BTN_COLORS[state]);

        drawCenteredString(fontRendererObj, displayText, posX + width / 2, posY + (height - 8) / 2, TEXT_COLORS[state]);
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

    protected void onClick()
    {

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
}
