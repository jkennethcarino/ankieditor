/*
 * Copyright (C) 2018 Jhon Kenneth Cariño
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

import com.jkcarino.ankieditor.ui.BasePresenter

interface EditorContract {

    interface View {

        fun setPresenter(presenter: Presenter)

        fun checkAnkiDroidAvailability()

        fun checkAnkiDroidReadWritePermission()

        fun showAnkiDroidError(message: String)

        fun showNoteTypes(ids: List<Long>, noteTypes: List<String>)

        fun showNoteDecks(ids: List<Long>, noteDecks: List<String>)

        fun showNoteTypeFields(fields: Array<String>)

        fun setInsertedClozeText(index: Int, text: String)

        fun setRichEditorFieldText(index: Int, text: String)

        fun setAddNoteSuccess()

        fun setAddNoteFailure()
    }

    interface Presenter : BasePresenter {

        var currentNoteTypeId: Long

        var currentDeckId: Long

        fun setupNoteTypesAndDecks()

        fun populateNoteTypeFields()

        fun insertClozeAround(
            index: Int,
            text: String,
            selectionStart: Int,
            selectionEnd: Int
        )

        fun addNote(fields: Array<String?>)
    }
}
