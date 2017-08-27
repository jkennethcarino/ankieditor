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
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.jkcarino.ankieditor.R;

public class NoteTypeFieldsContainer extends LinearLayout {

    public NoteTypeFieldsContainer(Context context) {
        super(context);
    }

    public NoteTypeFieldsContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoteTypeFieldsContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addFields(String[] fields) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        removeAllViews();
        for (String field : fields) {
            View view = inflater.inflate(R.layout.item_note_type_field, null);

            // Set floating label
            TextInputLayout textInputLayout = view.findViewById(R.id.note_field_til);
            textInputLayout.setHint(field);

            addView(view);
        }
    }

    public String[] getFieldsText() {
        int totalFields = getChildCount();
        String[] fields = new String[totalFields];

        for (int field = 0; field < totalFields; field++) {
            NoteTypeFieldEditText fieldEditText =
                    getChildAt(field).findViewById(R.id.note_field_edittext);
            fields[field] = fieldEditText.getFieldText();
        }
        return fields;
    }

    public void clearFields() {
        for (int field = 0; field < getChildCount(); field++) {
            NoteTypeFieldEditText fieldEditText =
                    getChildAt(field).findViewById(R.id.note_field_edittext);
            fieldEditText.setText("");
        }

        // Set the focus to the first field
        NoteTypeFieldEditText firstFieldEditText =
                getChildAt(0).findViewById(R.id.note_field_edittext);
        firstFieldEditText.requestFocus();
    }
}
