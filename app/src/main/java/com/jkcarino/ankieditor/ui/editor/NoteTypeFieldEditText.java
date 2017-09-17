/*
 * Copyright (C) 2017 Jhon Kenneth Cariño
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jkcarino.ankieditor.ui.editor;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.AttributeSet;

public class NoteTypeFieldEditText extends TextInputEditText {

    private static final String NEW_LINE = System.getProperty("line.separator");

    public NoteTypeFieldEditText(Context context) {
        super(context);
    }

    public NoteTypeFieldEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoteTypeFieldEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setFieldText(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            text = "";
        }
        setText(text.replaceAll("<br(\\s*\\/*)>", NEW_LINE));
    }

    public String getFieldText() {
        return getText().toString().replace(NEW_LINE, "<br>");
    }
}
