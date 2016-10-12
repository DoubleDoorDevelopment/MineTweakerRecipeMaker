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
