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


import android.support.annotation.NonNull;

import com.jkcarino.ankieditor.util.AnkiDroidHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
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
            Map<Long, String> noteTypes = sortByValue(ankiDroidHelper.getNoteTypes());
            List<Long> noteTypeIds = new ArrayList<>(noteTypes.keySet());
            List<String> noteTypesList = new ArrayList<>(noteTypes.values());

            editorView.showNoteTypes(noteTypeIds, noteTypesList);
        }
    }

    @Override
    public void populateNoteDecks() {
        if (editorView != null) {
            Map<Long, String> noteDecks = sortByValue(ankiDroidHelper.getNoteDecks());
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
}
