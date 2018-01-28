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

import com.jkcarino.ankieditor.util.AnkiDroidHelper

class EditorPresenter(
        private var editorView: EditorContract.View?,
        private var ankiDroidHelper: AnkiDroidHelper?
) : EditorContract.Presenter {

    init {
        editorView?.setPresenter(this)
    }

    override fun start() {

    }

    override fun stop() {
        editorView = null
        ankiDroidHelper = null
    }

    override fun populateNoteTypes() {
        editorView?.let { view ->
            ankiDroidHelper?.noteTypes?.let { noteTypes ->
                val noteTypeIds = ArrayList<Long>(noteTypes.keys)
                val noteTypesList = ArrayList<String>(noteTypes.values)
                view.showNoteTypes(noteTypeIds, noteTypesList)
            }
        }
    }

    override fun populateNoteDecks() {
        editorView?.let { view ->
            ankiDroidHelper?.noteDecks?.let { noteDecks ->
                val noteDeckIds = ArrayList<Long>(noteDecks.keys)
                val noteDecksList = ArrayList<String>(noteDecks.values)
                view.showNoteDecks(noteDeckIds, noteDecksList)
            }
        }
    }

    override fun populateNoteTypeFields(noteTypeId: Long) {
        editorView?.let { view ->
            ankiDroidHelper?.getNoteTypeFields(noteTypeId)?.let { fields ->
                view.showNoteTypeFields(fields)
            }
        }
    }

    override fun insertClozeAround(index: Int, text: String,
                                   selectionStart: Int, selectionEnd: Int) {
        editorView?.let { view ->
            val selectionMin = Math.min(selectionStart, selectionEnd)
            val selectionMax = Math.max(selectionStart, selectionEnd)
            val insertedCloze = (text.substring(0, selectionMin)
                    + "{{c1::" + text.substring(selectionMin, selectionMax)
                    + "}}" + text.substring(selectionMax))
            view.setInsertedClozeText(index, insertedCloze)
        }
    }

    override fun addNote(typeId: Long, deckId: Long, fields: Array<String?>) {
        editorView?.let { view ->
            val noteId = ankiDroidHelper?.api?.addNote(typeId, deckId, fields, null)
            if (noteId != null) {
                view.setAddNoteSuccess()
            } else {
                view.setAddNoteFailure()
            }
        }
    }
}
