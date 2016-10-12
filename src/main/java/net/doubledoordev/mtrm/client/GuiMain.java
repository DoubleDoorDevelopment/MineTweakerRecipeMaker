package net.doubledoordev.mtrm.client;

import net.doubledoordev.mtrm.Helper;
import net.doubledoordev.mtrm.client.elements.ButtonElement;
import net.doubledoordev.mtrm.client.elements.GuiElement;
import net.doubledoordev.mtrm.client.parts.GuiIconButton;
import net.doubledoordev.mtrm.client.parts.Icon;
import net.doubledoordev.mtrm.xml.Root;
import net.doubledoordev.mtrm.xml.XmlParser;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dries007
 */
public class GuiMain extends GuiListBase
{
    private static final int BTN_RELOAD = 10;
    private static final int BTN_EDIT = 11;

    private GuiIconButton btnEdit;
    private GuiIconButton btnReload;

    public GuiMain(Container inventorySlotsIn)
    {
        super(inventorySlotsIn);
    }

    private void load(boolean reload)
    {
        if (reload) XmlParser.reload();
        List<Root> roots = XmlParser.getLoadedRootXmls();
        guiElements.clear();
        for (final Root root : roots)
        {
            guiElements.add(new ButtonElement(GuiMain.this, false, root.name)
            {
                @Override
                protected void onClick()
                {
                    mc.displayGuiScreen(new GuiRoot(GuiMain.this, root));
                }

                @Override
                public ArrayList<String> getHoverLines()
                {
                    ArrayList<String> list = super.getHoverLines();
                    if (root.isOverride()) list.add(TextFormatting.RED + "Override");
                    return list;
                }
            });
        }
        if (reload)
        {
            for (GuiElement obj : guiElements) obj.initGui(listSizeX);
            doListCalculations();
        }
    }

    @Override
    public void initGui()
    {
        load(false);
        super.initGui();

        buttonList.add(btnEdit = new GuiIconButton(BTN_EDIT, guiLeft + xSize, guiTop + 50, "Edit script file", Icon.PENCIL));
        buttonList.add(btnReload = new GuiIconButton(BTN_RELOAD, guiLeft + xSize, guiTop + 70, "Reload XML files", Icon.REDO));

        btnOk.visible = false;
        btnCancel.enabled = true;
        btnCancel.displayString = "Exit";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        fontRendererObj.drawString(Helper.NAME, guiLeft + xSize/2 - fontRendererObj.getStringWidth(Helper.NAME) / 2, guiTop - 10, 0xFFFFFF);
    }

//    @Override
//    public void drawScreen(int mouseX, int mouseY, float partialTicks)
//    {
//        super.drawScreen(mouseX, mouseY, partialTicks);
//
//        if (openedRoot == null)
//        {
//            fontRendererObj.drawString("XML config files loaded: " + roots.size(), guiLeft + 5, guiTop + 5, 0xC0C0C);
//
//            for (int i = (int) ((roots.size() - 1) * currentScroll), top = 20;
//                 i < roots.size() && top + fontRendererObj.FONT_HEIGHT < ySize - fontRendererObj.FONT_HEIGHT;
//                 i ++, top += fontRendererObj.FONT_HEIGHT + 3)
//            {
//                Root root = roots.get(i);
//                drawHorizontalLine(guiLeft + 3, guiLeft + 3 + 4, guiTop + top + 3, 0xFF000000);
//                fontRendererObj.drawString(Helper.truncate(root.name, 30), guiLeft + 10, guiTop + top, 0x000000);
//                if (root.isOverride()) fontRendererObj.drawString("Override", guiLeft + 10 + 175, guiTop + top, 0xAA0000);
//            }
//        }
//        else
//        {
//            fontRendererObj.drawString(Helper.truncate(openedRoot.name, 30), guiLeft + 5, guiTop + 5, 0x000000);
//            if (openedRoot.isOverride()) fontRendererObj.drawString("Override", guiLeft + 10 + 175, guiTop + 5, 0xAA0000);
//
//            for (int i = (int) ((openedRoot.functionList.size() - 1) * currentScroll), top = 20;
//                 i < openedRoot.functionList.size() && top + fontRendererObj.FONT_HEIGHT < ySize - fontRendererObj.FONT_HEIGHT;
//                 i ++, top += fontRendererObj.FONT_HEIGHT + 3)
//            {
//                Function function = openedRoot.functionList.get(i);
//                drawHorizontalLine(guiLeft + 3, guiLeft + 3 + 4, guiTop + top + 3, 0xFF000000);
//                fontRendererObj.drawString(Helper.truncate(function.name, 40), guiLeft + 10, guiTop + top, 0x000000);
//            }
//        }
//    }
//
//    @Override
//    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
//    {
//        super.mouseClicked(mouseX, mouseY, mouseButton);
//        boolean onList = mouseX >= guiLeft + 5 && mouseX < guiLeft + 222 && mouseY >= guiTop + 20 && mouseY < guiTop + ySize - 5;
//        if (onList)
//        {
//            if (openedRoot == null)
//            {
//                for (int i = (int) ((roots.size() - 1) * currentScroll), top = 20;
//                     i < roots.size() && top + fontRendererObj.FONT_HEIGHT < ySize - fontRendererObj.FONT_HEIGHT;
//                     i++, top += fontRendererObj.FONT_HEIGHT + 3)
//                {
//                    if (mouseY - guiTop - fontRendererObj.FONT_HEIGHT - 3 < top)
//                    {
//                        openedRoot = roots.get(i);
//                        break;
//                    }
//                }
//            }
//            else
//            {
//                for (int i = (int) ((openedRoot.functionList.size() - 1) * currentScroll), top = 20;
//                     i < openedRoot.functionList.size() && top + fontRendererObj.FONT_HEIGHT < ySize - fontRendererObj.FONT_HEIGHT;
//                     i++, top += fontRendererObj.FONT_HEIGHT + 3)
//                {
//                    if (mouseY - guiTop - fontRendererObj.FONT_HEIGHT - 3 < top)
//                    {
//                        mc.displayGuiScreen(new GuiFunction(this, openedRoot.functionList.get(i)));
//                        break;
//                    }
//                }
//            }
//        }
//    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        super.actionPerformed(button);

        switch (button.id)
        {
            case BTN_RELOAD: load(true); break;
//            case BTN_EDIT: this.mc.displayGuiScreen(new GuiList()); break;
        }
    }

    @Override
    protected void exit()
    {
        this.mc.displayGuiScreen(null);
    }

    @Override
    protected void ok()
    {

    }
}
