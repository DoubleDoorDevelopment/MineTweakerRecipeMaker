package net.doubledoordev.mtrm.gui.client;

import com.google.common.collect.ImmutableList;
import net.doubledoordev.mtrm.MineTweakerRecipeMaker;
import net.doubledoordev.mtrm.gui.client.elements.GuiIconButton;
import net.doubledoordev.mtrm.gui.client.elements.Icon;
import net.doubledoordev.mtrm.network.MessageList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Thanks iChun for making Hats. I learned a lot from its GUI code.
 *
 * @author Dries007
 */
public class GuiList extends GuiBase implements GuiYesNoCallback
{
    private static final int BTN_UP = 3;
    private static final int BTN_DOWN = 4;
    private static final int BTN_EDIT = 5;
    private static final int BTN_ADD = 6;
    private static final int BTN_REMOVE = 7;
    private static final int BTN_UNDO = 8;
    private static final int BTN_REDO = 9;
    private static long fileHash = 0;
    private static List<String> lines = Collections.emptyList();
    private static Status status = Status.WAITING;
    private final Stack<List<String>> undo = new Stack<>();
    private final Stack<List<String>> redo = new Stack<>();
    private Status lastStatus = null;

    private int xSize = 256;
    private int ySize = 200;

    private int selected = -1;

    private GuiIconButton btnAdd;
    private GuiIconButton btnRemove;
    private GuiIconButton btnUp;
    private GuiIconButton btnDown;
    private GuiIconButton btnEdit;
    private GuiIconButton btnUndo;
    private GuiIconButton btnRedo;

    public static void handle(long hash, List<String> linesFromServer)
    {
        fileHash = hash;
        lines = linesFromServer;
        status = Status.OK;
        if (hash == 0) status = Status.ERROR;
    }

    public static void confirm(long hash)
    {
        fileHash = hash;
        if (hash == 0) status = Status.ERROR;
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    protected boolean needsScrolling()
    {
        return lines.size() > 10 && status == Status.OK;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (status != lastStatus)
        {
            initGui();
            lastStatus = status;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (status == Status.WAITING)
        {
            drawCenteredString(fontRendererObj, "Waiting for server data...", guiLeft + xSize / 2 - 10, guiTop + ySize / 2, 0xFFFFFF);
        }
        else if (status == Status.ERROR)
        {
            drawCenteredString(fontRendererObj, "An error occurred.", guiLeft + xSize / 2 - 10, guiTop + ySize / 2 - 10, 0xFFFFFF);
            drawCenteredString(fontRendererObj, "Try again or give up.", guiLeft + xSize / 2 - 10, guiTop + ySize / 2, 0xFFFFFF);
        }
        else if (status == Status.OK)
        {
            btnAdd.enabled = true;
            btnRemove.enabled = selected != -1;
            btnUp.enabled = selected != -1 && selected != 0;
            btnDown.enabled = selected != -1 && selected != lines.size() - 1;

            btnEdit.enabled = selected != -1;

            btnOk.enabled = changes;
            btnCancel.enabled = true;

            btnUndo.enabled = !undo.isEmpty();
            btnRedo.enabled = !redo.isEmpty();

            fontRendererObj.drawString("Lines loaded: ", guiLeft + 5, guiTop + 5, 0xC0C0C);
            fontRendererObj.drawString(String.valueOf(lines.size()), guiLeft + 5 + fontRendererObj.getStringWidth("Lines loaded: "), guiTop + 5, 0xC0C0C);

            int top = 0;
            done:
            {
                int i = (int) ((lines.size() - 1) * currentScroll);
                while (true)
                {
                    if (i >= lines.size()) break;
                    String line = lines.get(i);
                    for (String s : fontRendererObj.listFormattedStringToWidth(line, 222))
                    {
                        if (top + fontRendererObj.FONT_HEIGHT > ySize - 25) break done;
                        if (selected == i)
                            drawRect(guiLeft + 5, guiTop + 20 + top - 1, guiLeft + 5 + 225 + 1, guiTop + 20 + top + fontRendererObj.FONT_HEIGHT, 0xFF8B8B8B);
                        fontRendererObj.drawString(s, guiLeft + 5 + 2, guiTop + 20 + top, 0x0);
                        top += fontRendererObj.FONT_HEIGHT;
                    }
                    drawHorizontalLine(guiLeft + 5, guiLeft + 5 + 225, guiTop + 20 + top, 0xFF000000);
                    top += 2;
                    i++;
                }
            }
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();

        if (status == Status.OK)
        {
            buttonList.add(btnAdd = new GuiIconButton(BTN_ADD, guiLeft - 20, guiTop, "Add", Icon.PLUS));
            buttonList.add(btnRemove = new GuiIconButton(BTN_REMOVE, guiLeft - 20, guiTop + 20, "Remove", Icon.MINUS));

            buttonList.add(btnUp = new GuiIconButton(BTN_UP, guiLeft - 20, guiTop + 50, "Up", Icon.UP));
            buttonList.add(btnDown = new GuiIconButton(BTN_DOWN, guiLeft - 20, guiTop + 70, "Down", Icon.DOWN));

            buttonList.add(btnEdit = new GuiIconButton(BTN_EDIT, guiLeft - 20, guiTop + 100, "Edit", Icon.PENCIL));

            buttonList.add(btnUndo = new GuiIconButton(BTN_UNDO, guiLeft + xSize, guiTop + 50, "Undo", Icon.UNDO));
            buttonList.add(btnRedo = new GuiIconButton(BTN_REDO, guiLeft + xSize, guiTop + 70, "Redo", Icon.REDO));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        super.actionPerformed(button);
        if (!button.enabled) return;
        switch (button.id)
        {
            default:
                MineTweakerRecipeMaker.log.info("Unknown button {}", button.id);
                break;
            case BTN_ADD:
                this.mc.displayGuiScreen(new GuiAdd(this, selected));
                break;
            case BTN_REMOVE:
                if (selected != -1)
                {
                    undo.push(ImmutableList.copyOf(lines));
                    redo.clear();
                    lines.remove(selected);
                    selected = -1;
                    changes = true;
                }
                break;
            case BTN_UP:
                if (selected != -1)
                {
                    undo.push(ImmutableList.copyOf(lines));
                    redo.clear();
                    lines.add(selected - 1, lines.remove(selected--));
                    changes = true;
                }
                break;
            case BTN_DOWN:
                if (selected != -1)
                {
                    undo.push(ImmutableList.copyOf(lines));
                    redo.clear();
                    lines.add(selected + 1, lines.remove(selected++));
                    changes = true;
                }
                break;
            case BTN_EDIT:
                if (selected != -1) this.mc.displayGuiScreen(new GuiMultilineEdit(this, selected, lines.get(selected)));
                break;
            case BTN_UNDO:
                redo.push(ImmutableList.copyOf(lines));
                lines.clear();
                lines.addAll(undo.pop());
                changes = true;
                selected = -1;
                break;
            case BTN_REDO:
                undo.push(ImmutableList.copyOf(lines));
                lines.clear();
                lines.addAll(redo.pop());
                changes = true;
                selected = -1;
                break;
        }
    }

    @Override
    protected void ok()
    {
        MineTweakerRecipeMaker.getSnw().sendToServer(new MessageList(lines, fileHash));
    }

    protected void confirmExit()
    {
        this.mc.displayGuiScreen(new GuiYesNo(this, "Are you sure you want to leave?", "Changes won't be saved!", 0));
    }

    protected void exit()
    {
        status = Status.WAITING;
        selected = -1;
        changes = false;
        lines.clear();
        mc.displayGuiScreen(null);
        mc.setIngameFocus();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        boolean onList = mouseX >= guiLeft + 5 && mouseX < guiLeft + 222 && mouseY >= guiTop + 20 && mouseY < guiTop + ySize - 5;
        if (onList)
        {
            done:
            {
                int top = guiTop + 20;
                int i = (int) ((lines.size() - 1) * currentScroll);
                while (true)
                {
                    if (i >= lines.size()) break;
                    String line = lines.get(i);
                    for (String ignored : fontRendererObj.listFormattedStringToWidth(line, 222))
                    {
                        top += fontRendererObj.FONT_HEIGHT;
                        if (mouseY > top) continue;
                        selected = i;
                        break done;
                    }
                    top += 2;
                    i++;
                }
                selected = -1;
            }
        }
    }

    void callbackAdd(int id, String string)
    {
        undo.push(ImmutableList.copyOf(lines));
        redo.clear();
        if (id == -1) lines.add(string);
        else lines.add(id, string);
        changes = true;
        this.mc.displayGuiScreen(this);
    }

    void callbackEdit(int id, String string)
    {
        undo.push(ImmutableList.copyOf(lines));
        redo.clear();
        lines.set(id, string);
        changes = true;
        this.mc.displayGuiScreen(this);
    }

    private enum Status
    {
        WAITING, OK, ERROR
    }
}
