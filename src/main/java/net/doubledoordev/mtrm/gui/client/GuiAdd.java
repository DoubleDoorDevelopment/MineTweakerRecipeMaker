package net.doubledoordev.mtrm.gui.client;

import net.minecraft.client.gui.GuiYesNoCallback;

public class GuiAdd extends GuiBase implements GuiYesNoCallback
{
    private final GuiList parent;
    private final int callback;

    public GuiAdd(GuiList parent, int callback)
    {
        this.parent = parent;
        this.callback = callback;
    }

    @Override
    protected void ok()
    {
        parent.callbackAdd(callback, "-- todo: implement --");
    }

    @Override
    protected void exit()
    {
        this.mc.displayGuiScreen(parent);
    }

    @Override
    protected boolean needsScrolling()
    {
        return false;
    }
}
