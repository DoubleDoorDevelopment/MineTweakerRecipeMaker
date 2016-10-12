package net.doubledoordev.mtrm.client;

import net.doubledoordev.mtrm.client.elements.GuiElement;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dries007
 */
public abstract class GuiListBase extends GuiBase implements GuiElement.GuiElementCallback
{
    /** Must be initialized BEFORE the init method is called! */
    protected final List<GuiElement> guiElements = new ArrayList<>();
    protected int listLeft;
    protected int listRight;
    protected int listSizeX;
    protected int listTop;
    protected int listBottom;
    protected int listSizeY;
    protected int listInternalHeight;

    public GuiListBase(Container inventorySlotsIn)
    {
        super(inventorySlotsIn);
    }

    /**
     * This function moves all the elements around to 'scroll' and causes them to be (in)visible when appropriate.
     * Call after updating any heights or when element list is modified.
     * Already called at init.
     */
    protected void doListCalculations()
    {
        // Calculate internal height, used for scrolling
        listInternalHeight = 0;
        for (GuiElement obj : guiElements) listInternalHeight += obj.getHeight() + 1;

        // posY = top of the element, starts out above the top of the list if scrolled
        int posY = listTop - (int)((listInternalHeight - listSizeY) * currentScroll);
        GuiElement tooBig = null;
        for (GuiElement obj : guiElements)
        {
            obj.setPosition(listLeft, posY);
            int elementHeight = obj.getHeight();
            if (obj.isFocused() && elementHeight > listSizeY) // if huge
            {
                tooBig = obj;
                // Make huge stuff show up. Ugly but it works
                currentScroll = 0;
                obj.setPosition(listLeft, listTop + listSizeY / 2 - elementHeight / 2);
                obj.setVisible(true);
                break; // stop here already, no need to waist time
            }
            else if (posY < listTop) // start above top
            {
                obj.setVisible(false);
            }
            else if (posY + elementHeight > listBottom) // ends below bottom
            {
                if (obj.isFocused()) // but is focused
                {
                    // reposition to the top of the screen, and make all other elements invisible.
                    tooBig = obj;
                    obj.setVisible(true);
                    currentScroll = 0;
                    obj.setPosition(listLeft, listTop);
                    break; // stop here already, no need to waist time
                }
                else // not focused
                {
                    obj.setVisible(false);
                }
            }
            else // in between top and bottom
            {
                obj.setVisible(true);
            }
            posY += elementHeight + 1; // 1 px of separation space
        }

        // Nuke all other elements if there is a tooBig
        if (tooBig != null) for (GuiElement obj : guiElements) if (obj != tooBig) obj.setVisible(false);
    }

    @Override
    protected void scrolled()
    {
        doListCalculations();
    }

    @Override
    public void resizeCallback(GuiElement element)
    {
        doListCalculations();
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

        doListCalculations();
        updateButtons(null);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        for (GuiElement obj : guiElements) obj.update();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        for (GuiElement obj : guiElements)
            if (obj.isVisible())
            {
                int x = obj.getPosX();
                int y = obj.getPosY();
                drawRect(x, y, x + obj.getWidth(), y + obj.getHeight(), 0x50FF0000);
                obj.draw(mouseX, mouseY, partialTicks);
            }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-guiLeft, -guiTop, 0.0F);
        for (GuiElement obj : guiElements) if (obj.isVisible() && obj.isOver(mouseX, mouseY)) obj.drawHover(mouseX, mouseY, width, height);
        GlStateManager.popMatrix();
    }

    @Override
    public void updateButtons(GuiElement element)
    {
        btnOk.enabled = true;
        for (GuiElement obj : guiElements) btnOk.enabled &= obj.isValid();
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
    protected boolean needsScrolling()
    {
        return listInternalHeight > listSizeY;
    }
}
