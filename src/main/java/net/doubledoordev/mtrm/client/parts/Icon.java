/*
 * Copyright (c) 2015 - 2016, Dries007 & Double Door Development
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.doubledoordev.mtrm.client.parts;

import net.minecraft.util.ResourceLocation;

/**
 * @author Dries007
 */
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
