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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.jkcarino.ankieditor.R;

public class NoteTypeFieldsContainer extends LinearLayout {

    private OnFieldItemClickListener listener;

    public NoteTypeFieldsContainer(Context context) {
        super(context);
    }

    public NoteTypeFieldsContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoteTypeFieldsContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnFieldOptionsClickListener(@Nullable OnFieldItemClickListener listener) {
        this.listener = listener;
    }

    public void addFields(String[] fields) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        removeAllViews();

        for (int i = 0; i < fields.length; i++) {
            View view = inflater.inflate(R.layout.item_note_type_field, null);

            // Set floating label
            final TextInputLayout fieldHint = view.findViewById(R.id.note_field_til);
            fieldHint.setHint(fields[i]);

            final NoteTypeFieldEditText fieldEditText = view.findViewById(R.id.note_field_edittext);
            fieldEditText.setTag(i);

            final AppCompatImageButton options = view.findViewById(R.id.note_field_options);
            options.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener == null) {
                        return;
                    }

                    final int index = (int) fieldEditText.getTag();
                    final String text = fieldEditText.getFieldText();

                    PopupMenu popup = new PopupMenu(getContext(), view);
                    popup.getMenuInflater().inflate(R.menu.menu_field_options, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            switch (id) {
                                case R.id.menu_cloze_deletion: {
                                    String fieldText = fieldEditText.getText().toString();
                                    int selectionStart = fieldEditText.getSelectionStart();
                                    int selectionEnd = fieldEditText.getSelectionEnd();
                                    listener.onClozeDeletionClick(
                                            index, fieldText, selectionStart, selectionEnd);
                                    break;
                                }
                                case R.id.menu_advanced_editor: {
                                    listener.onAdvancedEditorClick(
                                            index, fieldHint.getHint().toString(), text);
                                    break;
                                }
                            }

                            return true;
                        }
                    });
                    popup.show();
                }
            });

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

    public void setFieldText(int index, @NonNull String text) {
        if (getChildCount() > 0) {
            NoteTypeFieldEditText fieldEditText = findViewWithTag(index);
            fieldEditText.setFieldText(text);
            fieldEditText.requestFocus();
        }
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

    public interface OnFieldItemClickListener {

        void onClozeDeletionClick(int index, @NonNull String text,
                                  int selectionStart, int selectionEnd);

        void onAdvancedEditorClick(int index, @NonNull String fieldName, @NonNull String text);
    }
}
