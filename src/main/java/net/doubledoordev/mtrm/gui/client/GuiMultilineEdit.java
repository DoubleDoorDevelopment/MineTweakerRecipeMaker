package net.doubledoordev.mtrm.gui.client;

import com.google.common.base.Predicate;
import net.doubledoordev.mtrm.gui.client.elements.GuiMultilineTextField;
import net.minecraft.client.gui.GuiYesNoCallback;

import javax.annotation.Nullable;

public class GuiMultilineEdit extends GuiBase implements GuiYesNoCallback
{
    private final GuiList parent;
    private final int callbackId;
    private final String originalText;

    private GuiMultilineTextField text;

    public GuiMultilineEdit(GuiList parent, int callbackId, String text)
    {
        this.parent = parent;
        this.callbackId = callbackId;
        this.originalText = text;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        text.setScroll(currentScroll);
        text.drawTextBox();
    }

    @Override
    public void initGui()
    {
        super.initGui();
        text = new GuiMultilineTextField(0, fontRendererObj, guiLeft + 5, guiTop + 5, xSize - 35, ySize - 10);
        text.setText(originalText);
        text.setFocused(true);
        text.setCanLoseFocus(false);
        text.setCursorPositionEnd();
        text.setValidator(new Predicate<String>()
        {
            @Override
            public boolean apply(@Nullable String input)
            {
                GuiMultilineEdit.this.btnOk.enabled = !originalText.equals(input);
                return true;
            }
        });
    }

    @Override
    public void updateScreen()
    {
        text.updateCursorCounter();
    }

    @Override
    protected void ok()
    {
        parent.callbackEdit(callbackId, text.getText());
    }

    @Override
    protected void exit()
    {
        this.mc.displayGuiScreen(parent);
    }

    @Override
    protected boolean needsScrolling()
    {
        return text.needsScrolling();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode)
    {
        super.keyTyped(typedChar, keyCode);
        text.textboxKeyTyped(typedChar, keyCode);
    }
}
