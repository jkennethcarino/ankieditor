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


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.jkcarino.ankieditor.ui.richeditor.RichEditorActivity;
import com.jkcarino.ankieditor.util.AnkiDroidHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditorPresenter implements EditorContract.Presenter {

    private EditorContract.View editorView;
    private AnkiDroidHelper ankiDroidHelper;

    public EditorPresenter(@NonNull EditorContract.View editorView,
                           @NonNull AnkiDroidHelper ankiDroidHelper) {
        this.editorView = editorView;
        this.ankiDroidHelper = ankiDroidHelper;

        editorView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        editorView = null;
        ankiDroidHelper = null;
    }

    @Override
    public void populateNoteTypes() {
        if (editorView != null) {
            Map<Long, String> noteTypes =
                    AnkiDroidHelper.sortByValue(ankiDroidHelper.getNoteTypes());
            List<Long> noteTypeIds = new ArrayList<>(noteTypes.keySet());
            List<String> noteTypesList = new ArrayList<>(noteTypes.values());

            editorView.showNoteTypes(noteTypeIds, noteTypesList);
        }
    }

    @Override
    public void populateNoteDecks() {
        if (editorView != null) {
            Map<Long, String> noteDecks =
                    AnkiDroidHelper.sortByValue(ankiDroidHelper.getNoteDecks());
            List<Long> noteDeckIds = new ArrayList<>(noteDecks.keySet());
            List<String> noteDecksList = new ArrayList<>(noteDecks.values());

            editorView.showNoteDecks(noteDeckIds, noteDecksList);
        }
    }

    @Override
    public void populateNoteTypeFields(long noteTypeId) {
        if (editorView != null) {
            String[] fields = ankiDroidHelper.getNoteTypeFields(noteTypeId);
            editorView.showNoteTypeFields(fields);
        }
    }

    @Override
    public void addNote(long typeId, long deckId, String[] fields) {
        if (editorView != null) {
            Long noteId = ankiDroidHelper.getApi().addNote(typeId, deckId, fields, null);
            if (noteId != null) {
                editorView.setAddNoteSuccess();
            } else {
                editorView.setAddNoteFailure();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (editorView == null) {
            return;
        }

        if (requestCode == EditorFragment.RC_FIELD_EDIT && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            int index = extras.getInt(RichEditorActivity.EXTRA_FIELD_INDEX);
            String text = extras.getString(RichEditorActivity.EXTRA_FIELD_TEXT);

            editorView.setFieldText(index, text);
        }
    }
}
