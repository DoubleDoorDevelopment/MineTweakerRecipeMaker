/*
 * Copyright (c) 2015 - 2017, Dries007 & Double Door Development
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions via the Curse or CurseForge platform are not allowed without
 *   written prior approval.
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
    REDO, UNDO, REDO_ALT(REDO), UNDO_ALT(UNDO), SMALL_PLUS, SMALL_MINUS;

    public static final ResourceLocation TEXTURE = new ResourceLocation("mtrm:gui/icons.png");

    static
    {
        Icon[] vals = Icon.values();
        for (int i = 0; i < Icon.values().length - 1; i++)
        {
            for (int j = i; j < Icon.values().length; j++)
            {
                if (vals[j].alt == vals[i].alt)
                {
                    vals[i].alt = vals[j].alt;
                }
            }
        }
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
