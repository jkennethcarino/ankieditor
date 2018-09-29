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

import android.app.Activity
import android.content.Intent
import com.jkcarino.ankieditor.ui.richeditor.RichEditorActivity
import com.jkcarino.ankieditor.util.AnkiDroidHelper
import com.jkcarino.ankieditor.util.PlayStoreUtils
import pub.devrel.easypermissions.AppSettingsDialog

class EditorPresenter(
        private var editorView: EditorContract.View?,
        private var ankiDroidHelper: AnkiDroidHelper?
) : EditorContract.Presenter {

    /** ID of the selected note type  */
    override var currentNoteTypeId: Long = 0L

    /** ID of the selected deck  */
    override var currentDeckId: Long = 0L

    init {
        editorView?.setPresenter(this)
    }

    override fun start() {

    }

    override fun stop() {
        editorView = null
        ankiDroidHelper = null
    }

    override fun setupNoteTypesAndDecks() {
        editorView?.let { view ->
            try {
                // Display all note types
                ankiDroidHelper?.noteTypes?.let { noteTypes ->
                    val noteTypeIds = ArrayList<Long>(noteTypes.keys)
                    val noteTypesList = ArrayList<String>(noteTypes.values)
                    view.showNoteTypes(noteTypeIds, noteTypesList)
                }

                // Display all note decks
                ankiDroidHelper?.noteDecks?.let { noteDecks ->
                    val noteDeckIds = ArrayList<Long>(noteDecks.keys)
                    val noteDecksList = ArrayList<String>(noteDecks.values)
                    view.showNoteDecks(noteDeckIds, noteDecksList)
                }
            } catch (e: IllegalStateException) {
                view.showAnkiDroidError(e.localizedMessage)
            }
        }
    }

    override fun populateNoteTypeFields() {
        editorView?.let { view ->
            ankiDroidHelper?.getNoteTypeFields(currentNoteTypeId)?.let { fields ->
                view.showNoteTypeFields(fields)
            }
        }
    }

    override fun result(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PlayStoreUtils.RC_OPEN_PLAY_STORE -> {
                editorView?.checkAnkiDroidAvailability()
            }
            AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE -> {
                editorView?.checkAnkiDroidReadWritePermission()
            }
            EditorFragment.RC_FIELD_EDIT -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.extras?.let {
                        val index = it.getInt(RichEditorActivity.EXTRA_FIELD_INDEX)
                        val text = it.getString(RichEditorActivity.EXTRA_FIELD_TEXT, "")
                        editorView?.setRichEditorFieldText(index, text)
                    }
                }
            }
        }
    }

    override fun insertClozeAround(
            index: Int,
            text: String,
            selectionStart: Int,
            selectionEnd: Int
    ) {
        editorView?.let { view ->
            val selectionMin = Math.min(selectionStart, selectionEnd)
            val selectionMax = Math.max(selectionStart, selectionEnd)
            val insertedCloze = (text.substring(0, selectionMin)
                    + "{{c1::" + text.substring(selectionMin, selectionMax)
                    + "}}" + text.substring(selectionMax))
            view.setInsertedClozeText(index, insertedCloze)
        }
    }

    override fun addNote(fields: Array<String?>) {
        editorView?.let { view ->
            val noteId = ankiDroidHelper?.api?.addNote(
                    currentNoteTypeId,
                    currentDeckId,
                    fields,
                    null
            )
            if (noteId != null) {
                view.setAddNoteSuccess()
            } else {
                view.setAddNoteFailure()
            }
        }
    }
}
