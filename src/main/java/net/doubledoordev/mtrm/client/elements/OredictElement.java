package net.doubledoordev.mtrm.client.elements;

import net.doubledoordev.mtrm.xml.elements.Slot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

/**
 * @author Dries007
 */
public class OredictElement extends SlotElement
{
    public OredictElement(GuiElementCallback callback, boolean optional, Slot.Type type, boolean wildcard, boolean metawildcard, boolean oredict, boolean stacksize)
    {
        super(callback, optional, type, wildcard, metawildcard, oredict, stacksize);
    }

    @Override
    public void initGui()
    {
        super.initGui();
    }

    @Override
    protected void focusStatusChanged()
    {
        super.focusStatusChanged();
    }

    @Override
    protected void setItemStack(ItemStack input)
    {
        super.setItemStack(input);
    }

    @Override
    public String save()
    {
        return super.save();
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        super.draw(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawItemStack(ItemStack stack, int x, int y, String altText)
    {
        super.drawItemStack(stack, x, y, altText);
    }

    @Override
    public void update()
    {
        super.update();
    }

    @Override
    protected ArrayList<String> getHoverLines()
    {
        return super.getHoverLines();
    }

    @Override
    public void drawHover(int mouseX, int mouseY, int maxWidth, int maxHeight)
    {
        super.drawHover(mouseX, mouseY, maxWidth, maxHeight);
    }

    @Override
    protected void onClickOn(int mouseX, int mouseY, int mouseButton)
    {
        super.onClickOn(mouseX, mouseY, mouseButton);
    }

    @Override
    public void setFocus(boolean focus)
    {
        super.setFocus(focus);
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode)
    {
        return super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean isValid()
    {
        return super.isValid();
    }

    @Override
    public void onClick(int mouseX, int mouseY, int mouseButton)
    {
        super.onClick(mouseX, mouseY, mouseButton);
    }
}
