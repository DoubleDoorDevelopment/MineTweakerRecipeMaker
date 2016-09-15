package net.doubledoordev.mtrm.client;

import net.doubledoordev.mtrm.Helper;
import net.doubledoordev.mtrm.client.parts.GuiIconButton;
import net.doubledoordev.mtrm.client.parts.Icon;
import net.doubledoordev.mtrm.xml.Function;
import net.doubledoordev.mtrm.xml.Root;
import net.doubledoordev.mtrm.xml.XmlParser;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;
import java.util.List;

/**
 * @author Dries007
 */
public class GuiMain extends GuiBase
{
    private static final int BTN_RELOAD = 10;
    private static final int BTN_EDIT = 11;

    private List<Root> roots;
    private Root openedRoot;

    private GuiIconButton btnEdit;
    private GuiIconButton btnReload;

    private void reload(boolean reloadXML)
    {
        if (reloadXML) XmlParser.reload();
        roots = XmlParser.getLoadedRootXmls();
        openedRoot = null;
        currentScroll = 0;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        reload(false);

        buttonList.add(btnEdit = new GuiIconButton(BTN_EDIT, guiLeft + xSize, guiTop + 50, "Edit script file", Icon.PENCIL));
        buttonList.add(btnReload = new GuiIconButton(BTN_RELOAD, guiLeft + xSize, guiTop + 70, "Reload XML files", Icon.REDO));

        btnOk.enabled = false;
        btnCancel.enabled = false;
    }

    @Override
    protected void scrolled()
    {
        //todo: use
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

        fontRendererObj.drawString(Helper.NAME, guiLeft + xSize/2 - fontRendererObj.getStringWidth(Helper.NAME) / 2, guiTop - 10, 0xFFFFFF);

        if (openedRoot == null)
        {
            fontRendererObj.drawString("XML config files loaded: " + roots.size(), guiLeft + 5, guiTop + 5, 0xC0C0C);

            for (int i = (int) ((roots.size() - 1) * currentScroll), top = 20;
                 i < roots.size() && top + fontRendererObj.FONT_HEIGHT < ySize - fontRendererObj.FONT_HEIGHT;
                 i ++, top += fontRendererObj.FONT_HEIGHT + 3)
            {
                Root root = roots.get(i);
                drawHorizontalLine(guiLeft + 3, guiLeft + 3 + 4, guiTop + top + 3, 0xFF000000);
                fontRendererObj.drawString(Helper.truncate(root.name, 30), guiLeft + 10, guiTop + top, 0x000000);
                if (root.isOverride()) fontRendererObj.drawString("Override", guiLeft + 10 + 175, guiTop + top, 0xAA0000);
            }
        }
        else
        {
            fontRendererObj.drawString(Helper.truncate(openedRoot.name, 30), guiLeft + 5, guiTop + 5, 0x000000);
            if (openedRoot.isOverride()) fontRendererObj.drawString("Override", guiLeft + 10 + 175, guiTop + 5, 0xAA0000);

            for (int i = (int) ((openedRoot.functionList.size() - 1) * currentScroll), top = 20;
                 i < openedRoot.functionList.size() && top + fontRendererObj.FONT_HEIGHT < ySize - fontRendererObj.FONT_HEIGHT;
                 i ++, top += fontRendererObj.FONT_HEIGHT + 3)
            {
                Function function = openedRoot.functionList.get(i);
                drawHorizontalLine(guiLeft + 3, guiLeft + 3 + 4, guiTop + top + 3, 0xFF000000);
                fontRendererObj.drawString(Helper.truncate(function.name, 40), guiLeft + 10, guiTop + top, 0x000000);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean onList = mouseX >= guiLeft + 5 && mouseX < guiLeft + 222 && mouseY >= guiTop + 20 && mouseY < guiTop + ySize - 5;
        if (onList)
        {
            if (openedRoot == null)
            {
                for (int i = (int) ((roots.size() - 1) * currentScroll), top = 20;
                     i < roots.size() && top + fontRendererObj.FONT_HEIGHT < ySize - fontRendererObj.FONT_HEIGHT;
                     i++, top += fontRendererObj.FONT_HEIGHT + 3)
                {
                    if (mouseY - guiTop - fontRendererObj.FONT_HEIGHT - 3 < top)
                    {
                        openedRoot = roots.get(i);
                        break;
                    }
                }
            }
            else
            {
                for (int i = (int) ((openedRoot.functionList.size() - 1) * currentScroll), top = 20;
                     i < openedRoot.functionList.size() && top + fontRendererObj.FONT_HEIGHT < ySize - fontRendererObj.FONT_HEIGHT;
                     i++, top += fontRendererObj.FONT_HEIGHT + 3)
                {
                    if (mouseY - guiTop - fontRendererObj.FONT_HEIGHT - 3 < top)
                    {
                        mc.displayGuiScreen(new GuiFunction(this, openedRoot.functionList.get(i)));
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        super.actionPerformed(button);

        switch (button.id)
        {
            case BTN_RELOAD: reload(true); break;
//            case BTN_EDIT: this.mc.displayGuiScreen(new GuiList()); break;
        }
    }

    @Override
    public void drawBackground(int tint)
    {
        super.drawBackground(tint);
    }

    @Override
    protected void exit()
    {
        if (openedRoot == null) this.mc.displayGuiScreen(null);
        else openedRoot = null;
    }

    @Override
    protected void ok()
    {
        this.mc.displayGuiScreen(null);
    }

    @Override
    protected boolean needsScrolling()
    {
        int size = openedRoot == null ? roots.size() : openedRoot.functionList.size();
        return size * (fontRendererObj.FONT_HEIGHT + 3) > ySize - 20 - fontRendererObj.FONT_HEIGHT;
    }
}
