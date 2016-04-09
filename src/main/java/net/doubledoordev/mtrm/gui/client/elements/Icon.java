package net.doubledoordev.mtrm.gui.client.elements;

import net.minecraft.util.ResourceLocation;

public enum Icon
{
    CHECK, CROSS, PLUS, MINUS, LEFT, RIGHT, UP, DOWN, LEFT_ALT(LEFT), RIGHT_ALT(RIGHT), UP_ALT(UP), DOWN_ALT(DOWN), COPY, PASTE, NEW, PENCIL,
    REDO, UNDO, REDO_ALT(REDO), UNDO_ALT(UNDO);

    public static final ResourceLocation TEXTURE = new ResourceLocation("mtrm:gui/icons.png");

    static
    {
        LEFT.alt = LEFT_ALT;
        RIGHT.alt = RIGHT_ALT;
        UP.alt = UP_ALT;
        DOWN.alt = DOWN_ALT;
        REDO.alt = REDO_ALT;
        UNDO.alt = UNDO_ALT;
    }

    private Icon alt;

    Icon()
    {

    }

    Icon(Icon alt)
    {
        this.alt = alt;
    }

    public boolean hasAlt()
    {
        return alt != null;
    }

    public Icon getAlt()
    {
        return alt;
    }
}
