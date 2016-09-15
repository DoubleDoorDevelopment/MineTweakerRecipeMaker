package net.doubledoordev.mtrm.client.parts;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;

import java.util.List;

/**
 * @author Dries007
 */
public class GuiMultilineTextField extends GuiTextField
{
    private final FontRenderer fr;
    private List<String> lines = Lists.newArrayList();
    private float scroll = 0;

    public GuiMultilineTextField(int componentId, FontRenderer fr, int x, int y, int par5Width, int par6Height)
    {
        super(componentId, fr, x, y, par5Width, par6Height);
        this.fr = fr;
        setEnabled(true);
        setMaxStringLength(Integer.MAX_VALUE / 2);
    }

    public void drawTextBox()
    {
        if (!visible) return;

        if (enableBackgroundDrawing)
        {
            drawRect(xPosition - 1, yPosition - 1, xPosition + width + 1, yPosition + height + 1, -6250336);
            drawRect(xPosition, yPosition, xPosition + width, yPosition + height, -16777216);
        }

        final int txtColor = enableBackgroundDrawing ? enabledColor : disabledColor;
        int x = enableBackgroundDrawing ? xPosition + 4 : xPosition;
        int y = enableBackgroundDrawing ? yPosition + 4 : yPosition;

        y -= (int) (scroll * (lines.size() - 1)) * fr.FONT_HEIGHT;

        int cursorPosition = this.cursorPosition;
        int selectionEnd = this.selectionEnd;
        if (selectionEnd < cursorPosition)
        {
            selectionEnd = this.cursorPosition;
            cursorPosition = this.selectionEnd;
        }

        int chars = 0;
        boolean selected = false;
        for (int i = 0; i < lines.size(); i++)
        {
            final String line = lines.get(i);
            final int length = line.length();

            boolean render = y > yPosition && y < yPosition + height - fr.FONT_HEIGHT;

            int subSelectionStart = 0;

            boolean startSelection = cursorPosition >= chars && cursorPosition <= chars + length;

            if (startSelection)
            {
                subSelectionStart = cursorPosition - chars;
                selected = true;
            }

            if (selected)
            {
                int subSelectionEnd = Math.min(length, selectionEnd - chars);
                if (subSelectionEnd < length) selected = false;

                int offsetL = fontRendererInstance.getStringWidth(line.substring(0, subSelectionStart));
                int offsetR = fontRendererInstance.getStringWidth(line.substring(0, subSelectionEnd));

                if (render)
                {
                    if (subSelectionEnd != subSelectionStart)
                    {
                        Gui.drawRect(x + offsetL, y, x + offsetR, y + 1 + this.fontRendererInstance.FONT_HEIGHT, -3092272);
                        fontRendererInstance.drawStringWithShadow(line.substring(0, subSelectionStart), x, y, txtColor);
                        fontRendererInstance.drawStringWithShadow(line.substring(subSelectionStart, subSelectionEnd), x + offsetL, y, txtColor);
                        fontRendererInstance.drawStringWithShadow(line.substring(subSelectionEnd, length), x + offsetR, y, txtColor);
                    }
                    else
                    {
                        fontRendererInstance.drawStringWithShadow(line, x, y, txtColor);
                    }
                }
            }
            else if (render)
            {
                fontRendererInstance.drawStringWithShadow(line, x, y, txtColor);
            }

            int offsetL = fontRendererInstance.getStringWidth(line.substring(0, subSelectionStart));
            if (render && startSelection && (subSelectionStart != 0 || i == 0) && isFocused && cursorCounter / 6 % 2 == 0)
            {
                this.drawVerticalLine(x + offsetL, y - 1, y + 1 + this.fontRendererInstance.FONT_HEIGHT, 0xFFFF0000);
            }

            chars += length;
            y += fr.FONT_HEIGHT;
        }
    }

    private void recomputeLines()
    {
        lines = fr.listFormattedStringToWidth(text, width - 8);
        int chars = 0;
        for (int i = 0; i < lines.size(); i++)
        {
            String line = lines.get(i);
            chars += line.length();
            if (text.length() > chars && text.charAt(chars) == ' ')
            {
                lines.set(i, line + ' ');
                chars++;
            }
        }
        if (!needsScrolling()) setScroll(0);
    }

    public boolean needsScrolling()
    {
        return lines.size() > (height - (enableBackgroundDrawing ? 8 : 0)) / fr.FONT_HEIGHT;
    }

    public void setScroll(float scroll)
    {
        this.scroll = scroll;
    }

    @Override
    public void setText(String textIn)
    {
        super.setText(textIn);
        recomputeLines();
    }

    @Override
    public void writeText(String textToWrite)
    {
        super.writeText(textToWrite);
        recomputeLines();
    }

    @Override
    public void deleteFromCursor(int num)
    {
        super.deleteFromCursor(num);
        recomputeLines();
    }

    @Override
    public void setMaxStringLength(int length)
    {
        super.setMaxStringLength(length);
        recomputeLines();
    }
}
