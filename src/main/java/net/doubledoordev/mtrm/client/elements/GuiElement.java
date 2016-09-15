package net.doubledoordev.mtrm.client.elements;

import net.minecraft.client.gui.Gui;

/**
 * @author Dries007
 */
public abstract class GuiElement extends Gui
{
    protected int width;
    protected int height;
    protected int posX;
    protected int posY;
    protected int maxWidth;

    protected boolean enabled = true;
    protected boolean visible;

    public final boolean optional;

    private final GuiElementCallback callback;
    private boolean focus;

    protected GuiElement(GuiElementCallback callback, boolean optional)
    {
        this.callback = callback;
        this.optional = optional;
    }

    public abstract void initGui();

    public void update() {}

    /**
     * Return the current value to string
     */
    public abstract String save();

    /**
     * Only called if visible. Mouse coordinates are absolute
     */
    public abstract void draw(int mouseX, int mouseY, float partialTicks);

    /**
     * Only called if visible and if isOver returns true. Called after all elements have been drawn
     */
    public void drawHover(int mouseX, int mouseY, int maxWidth, int maxHeight) {}

    /**
     * Only called if click was a hit
     */
    protected void onClickOn(int mouseX, int mouseY, int mouseButton) {}

    /**
     * Only called if in focus
     * @return true if you handled the event
     */
    public boolean keyTyped(char typedChar, int keyCode) { return false; }

    /**
     * To force an update, use the updateButtonsCallback
     * @return false to disable the ok button
     */
    public boolean isValid() { return true; }

    /**
     * Call when your content becomes invalid / valid again.
     */
    protected final void updateButtonsCallback()
    {
        callback.updateButtons(this);
    }

    /**
     * Call when the element itself decided to resize.
     * NOT when setSize or initGui is called.
     * This is meant to facilitate lists in the parent GUI
     */
    protected final void resizeCallback()
    {
        callback.resizeCallback(this);
    }

    /**
     * Helper method
     */
    public void initGui(int maxWidth)
    {
        this.maxWidth = maxWidth;
        this.width = maxWidth;
        initGui();
    }

    /**
     * Called (if visible) regardless if click was a hit. Used to (un)set focus
     */
    public void onClick(int mouseX, int mouseY, int mouseButton)
    {
        if (isOver(mouseX, mouseY))
        {
            setFocus(true);
            onClickOn(mouseX, mouseY, mouseButton);
            if (!isEnabled()) setFocus(false);
        }
        else setFocus(false);
    }

    public boolean isOver(int x, int y)
    {
        int relX = x - posX;
        int relY = y - posY;
        return relX > 0 && relY > 0 && relX < width && relY < height;
    }

    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public void setPosition(int x, int y)
    {
        this.posX = x;
        this.posY = y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getPosX()
    {
        return posX;
    }

    public int getPosY()
    {
        return posY;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        if (!visible) setFocus(false);
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
        if (!visible) setFocus(false);
    }

    public void setFocus(boolean focus)
    {
        this.focus = focus;
    }

    public boolean isFocused()
    {
        return focus;
    }

    public interface GuiElementCallback
    {
        void resizeCallback(GuiElement element);

        void updateButtons(GuiElement element);
    }
}