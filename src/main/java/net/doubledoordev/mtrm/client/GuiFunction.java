package net.doubledoordev.mtrm.client;

import com.google.common.collect.ImmutableList;
import net.doubledoordev.mtrm.MineTweakerRecipeMaker;
import net.doubledoordev.mtrm.client.elements.GuiElement;
import net.doubledoordev.mtrm.client.elements.StringElement;
import net.doubledoordev.mtrm.xml.Function;
import net.doubledoordev.mtrm.xml.XmlParser;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;

import java.io.IOException;
import java.util.List;

/**
 * @author Dries007
 */
public class GuiFunction extends GuiBase implements GuiElement.GuiElementCallback
{
    protected static final int ID_SAVE = 10;
    private final GuiMain parent;
    private final Function function;
    private final List<GuiElement> guiElements;
    private final String genericText;
    private String currentText;
    private int listLeft;
    private int listRight;
    private int listSizeX;
    private int listTop;
    private int listBottom;
    private int listSizeY;
    private int listInternalHeight;

    public GuiFunction(GuiMain parent, Function function)
    {
        this.parent = parent;
        this.function = function;
        StringBuilder textBuilder = new StringBuilder();
        ImmutableList.Builder<GuiElement> elementBuilder = ImmutableList.builder();
        for (Object obj : function.parts)
        {
            if (obj instanceof String)
            {
                textBuilder.append(obj);
                elementBuilder.add(new StringElement(this, (String) obj));
            }
            else
            {
                XmlParser.IStringObject sObj = (XmlParser.IStringObject) obj;
                textBuilder.append(sObj.toHumanText());
                GuiElement e = sObj.toGuiElement(this);
                if (e != null) elementBuilder.add(e);
                else elementBuilder.add(new StringElement(this, sObj.toHumanText(), 0xFF0000));
            }
        }
        guiElements = elementBuilder.build();
        genericText = textBuilder.toString();
        currentText = "";
    }

    @Override
    protected void scrolled()
    {
        positionElements(null);
    }

    private void positionElements(GuiElement e)
    {
        listInternalHeight = 0;
        for (GuiElement obj : guiElements)
        {
            listInternalHeight += obj.getHeight() + 1;
        }

        GuiElement tooBig = null;

        int posY = listTop - (int)(listInternalHeight * currentScroll);
        for (GuiElement obj : guiElements)
        {
            obj.setPosition(listLeft, posY);
            int elementHeight = obj.getHeight();
            if (obj.isFocused() && elementHeight > listSizeY)
            {
                tooBig = obj;
                currentScroll = 0;
                obj.setPosition(listLeft, listTop + listSizeY / 2 - elementHeight / 2);
                obj.setVisible(true);
                break;
            }
            else if (posY < listTop)
            {
                obj.setVisible(false);
            }
            else if (posY + elementHeight > listBottom)
            {
                if (obj.isFocused())
                {
                    tooBig = obj;
                    obj.setVisible(true);
                    currentScroll = 0;
                    obj.setPosition(listLeft, listTop);
                }
                else
                {
                    obj.setVisible(false);
                }
            }
            else
            {
                obj.setVisible(true);
            }
            posY += elementHeight + 1;
        }

        if (tooBig != null)
        {
            for (GuiElement obj : guiElements)
            {
                if (obj != tooBig) obj.setVisible(false);
            }
        }
    }

    @Override
    public void resizeCallback(GuiElement element)
    {
        positionElements(element);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        listLeft = guiLeft + 5;
        listRight = guiLeft + xSize - 30;
        listSizeX = listRight - listLeft;

        listTop = guiTop + 5;
        listBottom = guiTop + ySize - 8;
        listSizeY = listBottom - listTop;

        for (GuiElement obj : guiElements) obj.initGui(listSizeX);

        positionElements(null);
        changes = true;
        btnOk.enabled = true;
    }

    @Override
    public void updateButtons(GuiElement element)
    {
        btnOk.enabled = true;
        for (GuiElement obj : guiElements) btnOk.enabled &= obj.isValid();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        StringBuilder sb = new StringBuilder();
        for (GuiElement obj : guiElements)
        {
            obj.update();
            sb.append(obj.save());
        }
        currentText = sb.toString();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

        fontRendererObj.drawString(function.name, guiLeft + xSize / 2 - fontRendererObj.getStringWidth(function.name) / 2, guiTop - 10, 0xFFFFFF);

        // todo: remove
        // debug start
        fontRendererObj.drawString("Generic", guiLeft + xSize + 25, guiTop - 100, 0xFF5050);
        fontRendererObj.drawSplitString(genericText, guiLeft + xSize + 25, guiTop - 90, 200, 0xFFFFFF);
        fontRendererObj.drawString("Current", guiLeft + xSize + 25, guiTop + 100, 0xFF5050);
        fontRendererObj.drawSplitString(currentText, guiLeft + xSize + 25, guiTop + 110, 200, 0xFFFFFF);
//        drawHorizontalLine(mouseX - 10, mouseX + 10, mouseY, 0xFFAAAAAA);
//        drawVerticalLine(mouseX, mouseY - 10, mouseY + 10, 0xFFAAAAAA);
//        drawRect(listLeft, listTop, listRight, listBottom, 0x50FFFFFF);
        // debug end

        for (GuiElement obj : guiElements) if (obj.isVisible()) obj.draw(mouseX, mouseY, partialTicks);
        for (GuiElement obj : guiElements) if (obj.isVisible() && obj.isOver(mouseX, mouseY)) obj.drawHover(mouseX, mouseY, width, height);

        // todo: remove
        // debug start
//        fontRendererObj.drawString("Debug info", guiLeft - 200, guiTop - 110, 0xFF5050);
//        for (int i = 0; i < guiElements.size(); i++)
//        {
//            GuiElement obj = guiElements.get(i);
//            StringBuilder sb = new StringBuilder("Element ").append(i).append(' ');
//            sb.append(obj.isEnabled()).append(' ');
//            sb.append(obj.isVisible()).append(' ');
//            sb.append(obj.isFocused()).append(' ');
//
//            fontRendererObj.drawString(sb.toString(), guiLeft - 200, guiTop - 100 + i * fontRendererObj.FONT_HEIGHT, 0xFFFFFF);
//        }
        // debug end
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (GuiElement element : guiElements) if (element.isVisible()) element.onClick(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode)
    {
        boolean handled = false;
        for (GuiElement element : guiElements) if (element.isFocused()) handled |= element.keyTyped(typedChar, keyCode);
        if (!handled) super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        super.actionPerformed(button);
    }

    @Override
    public void drawBackground(int tint)
    {
        super.drawBackground(tint);
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
                if (result) confirm_ok();
                else this.mc.displayGuiScreen(this);
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

    @Override
    protected boolean needsScrolling()
    {
        return listInternalHeight > listSizeY;
    }
}
