/*
 * Copyright (C) 2018 Jhon Kenneth Carino
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

package com.jkcarino.ankieditor.ui.editor

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.PopupMenu
import com.jkcarino.ankieditor.R
import kotlinx.android.synthetic.main.item_note_type_field.view.*

class NoteTypeFieldsContainer @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    var onFieldOptionsClickListener: OnFieldOptionsClickListener? = null

    val fieldsText: Array<String?>
        get() {
            val totalFields = childCount
            val fields = arrayOfNulls<String>(totalFields)

            for (i in 0 until totalFields) {
                fields[i] = getChildAt(i).note_field_edittext.fieldText
            }
            return fields
        }

    fun addFields(fields: Array<String>) {
        val inflater = LayoutInflater.from(context)

        removeAllViews()

        for (field in fields.indices) {
            val view = inflater.inflate(R.layout.item_note_type_field, null)

            // Set floating label
            val fieldHint = view.note_field_til.apply { hint = fields[field] }

            val fieldEditText = view.note_field_edittext.apply { tag = field }

            view.note_field_options.setOnClickListener {
                val index = fieldEditText.tag as Int
                val text = fieldEditText.fieldText

                PopupMenu(context, it).apply {
                    menuInflater.inflate(R.menu.menu_field_options, menu)
                    setOnMenuItemClickListener { item ->
                        val id = item.itemId
                        when (id) {
                            R.id.menu_cloze_deletion -> {
                                val fieldText = fieldEditText.text.toString()
                                val selectionStart = fieldEditText.selectionStart
                                val selectionEnd = fieldEditText.selectionEnd
                                onFieldOptionsClickListener?.onClozeDeletionClick(
                                        index, fieldText, selectionStart, selectionEnd)
                            }
                            R.id.menu_advanced_editor -> {
                                onFieldOptionsClickListener?.onAdvancedEditorClick(
                                        index, fieldHint.hint.toString(), text
                                )
                            }
                        }

                        true
                    }
                }.show()
            }

            addView(view)
        }
    }

    fun setFieldText(index: Int, text: String) {
        if (childCount > 0) {
            findViewWithTag<NoteTypeFieldEditText>(index).apply {
                fieldText = text
                requestFocus()
            }
        }
    }

    fun clearFields() {
        (0 until childCount)
                .map { getChildAt(it).note_field_edittext }
                .forEach { it.text = null }

        // Set the focus to the first field
        getChildAt(0).note_field_edittext.requestFocus()
    }

    interface OnFieldOptionsClickListener {

        fun onClozeDeletionClick(index: Int, text: String, selectionStart: Int, selectionEnd: Int)

        fun onAdvancedEditorClick(index: Int, fieldName: String, text: String)
    }
}
