package net.doubledoordev.mtrm.client;

import net.doubledoordev.mtrm.client.parts.GuiIconButton;
import net.doubledoordev.mtrm.client.parts.Icon;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

/**
 * @author Dries007
 */
public abstract class GuiBase extends GuiContainer
{
    public static final ResourceLocation BASE = new ResourceLocation("mtrm:gui/base.png");
    public static final ResourceLocation INVENTORY = new ResourceLocation("mtrm:gui/inventory.png");
    protected static final int ID_CANCEL = 0;
    protected static final int BTN_OK = 1;
    protected static final int BTN_CANCEL = 2;

    {
        xSize = 256;
        ySize = 200;
    }

    //0 = top, 1 = bottom
    protected float currentScroll;
    protected boolean isScrolling;
    protected boolean wasClicking;
    protected boolean changes;

    protected GuiIconButton btnOk;
    protected GuiIconButton btnCancel;

    protected int guiLeft;
    protected int guiTop;

    public GuiBase(Container inventorySlotsIn)
    {
        super(inventorySlotsIn);

    }

    protected abstract void exit();

    protected abstract void ok();

    protected abstract boolean needsScrolling();

    protected abstract void scrolled();

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        mc.getTextureManager().bindTexture(INVENTORY);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        // 88 = 176/2 = width of inventory gui
        drawTexturedModalRect(guiLeft + xSize/2 - 88, guiTop + ySize + 9, 0, 0, 176, 90);

        mc.getTextureManager().bindTexture(BASE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        int left = guiLeft + xSize - 23;
        int right = guiLeft + xSize - 11;
        int top = guiTop + 7;
        int bottom = guiTop + ySize - 8;

        boolean leftClick = Mouse.isButtonDown(0);

        if (!wasClicking && leftClick && mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom)
        {
            isScrolling = this.needsScrolling();
        }
        if (!leftClick)
        {
            isScrolling = false;
        }
        wasClicking = leftClick;
        if (isScrolling)
        {
            currentScroll = MathHelper.clamp_float((float) (mouseY - 6 - top) / (bottom - top - 16), 0.0F, 1.0F);
            this.scrolled();
        }

        mc.getTextureManager().bindTexture(BASE);
        drawTexturedModalRect(left, top + (int) ((bottom - top - 15) * currentScroll), this.needsScrolling() ? 0 : 12, 241, 12, 15);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        Keyboard.enableRepeatEvents(true);

        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;

        buttonList.clear();

        buttonList.add(btnOk = new GuiIconButton(BTN_OK, guiLeft + xSize, guiTop, "Ok", Icon.CHECK));
        buttonList.add(btnCancel = new GuiIconButton(BTN_CANCEL, guiLeft + xSize, guiTop + 20, "Cancel", Icon.CROSS));
        btnOk.enabled = false;
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0 && this.needsScrolling())
        {
            if (i > 0) i = 1;
            if (i < 0) i = -1;

            currentScroll = (currentScroll - i / 75.0f);
            currentScroll = MathHelper.clamp_float(currentScroll, 0.0F, 1.0F);
            this.scrolled();
        }
    }

    protected void confirmExit()
    {
        mc.displayGuiScreen(new GuiYesNo(this, "Are you sure you want to leave?", "Changes won't be saved!", ID_CANCEL));
    }

    @Override
    public void confirmClicked(boolean result, int id)
    {
        switch (id)
        {
            case ID_CANCEL:
                if (result) exit();
                else mc.displayGuiScreen(this);
                break;
            default:
                super.confirmClicked(result, id);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_ESCAPE)
        {
            if (changes) confirmExit();
            else exit();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (!button.enabled) return;
        switch (button.id)
        {
            case BTN_OK:
                changes = false;
                ok();
                break;
            case BTN_CANCEL:
                if (changes) confirmExit();
                else exit();
                break;
        }
    }
}
