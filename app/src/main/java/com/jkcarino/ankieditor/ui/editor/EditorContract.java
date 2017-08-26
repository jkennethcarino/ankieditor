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

import com.jkcarino.ankieditor.ui.BasePresenter;

import java.util.List;

public interface EditorContract {

    interface View {

        void setPresenter(@NonNull Presenter presenter);

        void showNoteTypes(List<Long> ids, List<String> noteTypes);

        void showNoteDecks(List<Long> ids, List<String> noteDecks);

        void showNoteTypeFields(String[] fields);
    }

    interface Presenter extends BasePresenter {

        void populateNoteTypes();

        void populateNoteDecks();

        void populateNoteTypeFields(long noteTypeId);
    }
}
