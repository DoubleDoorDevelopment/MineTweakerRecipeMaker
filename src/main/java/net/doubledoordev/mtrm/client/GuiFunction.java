package net.doubledoordev.mtrm.client;

import net.doubledoordev.mtrm.MineTweakerRecipeMaker;
import net.doubledoordev.mtrm.client.elements.GuiElement;
import net.doubledoordev.mtrm.client.elements.StringElement;
import net.doubledoordev.mtrm.xml.Function;
import net.doubledoordev.mtrm.xml.XmlParser;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.inventory.GuiContainer;

/**
 * @author Dries007
 */
public class GuiFunction extends GuiListBase implements GuiElement.GuiElementCallback
{
    protected static final int ID_SAVE = 10;
    private final GuiContainer parent;
    private final Function function;
    private final String genericText;
    private String currentText;

    public GuiFunction(GuiContainer parent, Function function)
    {
        super(parent.inventorySlots);
        this.parent = parent;
        this.function = function;
        StringBuilder textBuilder = new StringBuilder();
        for (Object obj : function.parts)
        {
            if (obj instanceof String)
            {
                textBuilder.append(obj);
                guiElements.add(new StringElement(this, (String) obj));
            }
            else
            {
                XmlParser.IStringObject sObj = (XmlParser.IStringObject) obj;
                textBuilder.append(sObj.toHumanText());
                GuiElement e = sObj.toGuiElement(this);
                if (e != null) guiElements.add(e);
                else guiElements.add(new StringElement(this, sObj.toHumanText(), 0xFF0000));
            }
            textBuilder.append(' ');
        }
        genericText = textBuilder.toString();
        currentText = "";
    }

    @Override
    public void initGui()
    {
        super.initGui();
        changes = true;
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        StringBuilder sb = new StringBuilder();
        for (GuiElement obj : guiElements) sb.append(obj.save()).append(' ');
        currentText = sb.toString();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        fontRendererObj.drawString(function.name, guiLeft + xSize / 2 - fontRendererObj.getStringWidth(function.name) / 2, guiTop - 10, 0xFFFFFF);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        // todo: remove
        // debug start
        fontRendererObj.drawString("Generic", -200, -100, 0xFF5050);
        fontRendererObj.drawSplitString(genericText, -200, -90, 200, 0xFFFFFF);
        fontRendererObj.drawString("Current", xSize + 25, -100, 0xFF5050);
        fontRendererObj.drawSplitString(currentText, xSize + 25, -90, 200, 0xFFFFFF);
        // debug end
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
}
