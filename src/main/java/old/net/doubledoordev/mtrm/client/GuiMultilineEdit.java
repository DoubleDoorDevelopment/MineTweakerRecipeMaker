//package net.doubledoordev.mtrm.client;
//
//import com.google.common.base.Predicate;
//import net.doubledoordev.mtrm.client.parts.GuiMultilineTextField;
//import net.minecraft.client.gui.GuiYesNoCallback;
//
//import javax.annotation.Nullable;
//
///**
// * @author Dries007
// */
//public class GuiMultilineEdit extends GuiBase implements GuiYesNoCallback
//{
//    private final GuiList parent;
//    private final int callbackId;
//    private final String originalText;
//
//    private GuiMultilineTextField displayText;
//
//    public GuiMultilineEdit(GuiList parent, int callbackId, String displayText)
//    {
//        this.parent = parent;
//        this.callbackId = callbackId;
//        this.originalText = displayText;
//    }
//
//    @Override
//    public void drawScreen(int mouseX, int mouseY, float partialTicks)
//    {
//        super.drawScreen(mouseX, mouseY, partialTicks);
//        displayText.setScroll(currentScroll);
//        displayText.drawTextBox();
//    }
//
//    @Override
//    public void initGui()
//    {
//        super.initGui();
//        displayText = new GuiMultilineTextField(0, fontRendererObj, guiLeft + 5, guiTop + 5, xSize - 35, ySize - 10);
//        displayText.setText(originalText);
//        displayText.setFocused(true);
//        displayText.setCanLoseFocus(false);
//        displayText.setCursorPositionEnd();
//        displayText.setValidator(new Predicate<String>()
//        {
//            @Override
//            public boolean apply(@Nullable String input)
//            {
//                GuiMultilineEdit.this.btnOk.enabled = !originalText.equals(input);
//                return true;
//            }
//        });
//    }
//
//    @Override
//    protected void scrolled()
//    {
//
//    }
//
//    @Override
//    public void updateScreen()
//    {
//        displayText.updateCursorCounter();
//    }
//
//    @Override
//    protected void ok()
//    {
//        parent.callbackEdit(callbackId, displayText.getText());
//    }
//
//    @Override
//    protected void exit()
//    {
//        this.mc.displayGuiScreen(parent);
//    }
//
//    @Override
//    protected boolean needsScrolling()
//    {
//        return displayText.needsScrolling();
//    }
//
//    @Override
//    protected void keyTyped(char typedChar, int keyCode)
//    {
//        super.keyTyped(typedChar, keyCode);
//        displayText.textboxKeyTyped(typedChar, keyCode);
//    }
//}
