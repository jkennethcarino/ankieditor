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


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ichi2.anki.api.AddContentApi;
import com.jkcarino.ankieditor.R;
import com.jkcarino.ankieditor.util.AnkiDroidHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class EditorFragment extends Fragment implements
        EditorContract.View, EasyPermissions.PermissionCallbacks {

    private static final int RC_AD_READ_WRITE_PERM = 0x01;

    @BindView(R.id.note_type_spinner) Spinner noteTypesSpinner;
    @BindView(R.id.deck_spinner) Spinner noteDecksSpinner;
    @BindView(R.id.note_fields_container) NoteTypeFieldsContainer noteTypeFieldsContainer;

    private View rootView;
    private Unbinder unbinder;
    private EditorContract.Presenter presenter;

    /** ID of the selected note type */
    private long noteTypeId;

    /** ID of the selected deck */
    private long deckId;

    public static EditorFragment newInstance() {
        return new EditorFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_editor, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        if (AnkiDroidHelper.isApiAvailable(getActivity())) {
            requestAnkiDroidPermissionIfNecessary();
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        unbinder = null;
    }

    @Override
    public void setPresenter(@NonNull EditorContract.Presenter presenter) {
        this.presenter = presenter;
    }

    private void loadAnkiEditor() {
        presenter.populateNoteTypes();
        presenter.populateNoteDecks();
    }

    @AfterPermissionGranted(RC_AD_READ_WRITE_PERM)
    private void requestAnkiDroidPermissionIfNecessary() {
        if (EasyPermissions.hasPermissions(getActivity().getApplicationContext(),
                AddContentApi.READ_WRITE_PERMISSION)) {
            loadAnkiEditor();
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.rationale_ad_api_permission_ask_again),
                    RC_AD_READ_WRITE_PERM,
                    AddContentApi.READ_WRITE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            // TODO: Show a permission denied layout
            Snackbar.make(rootView, R.string.sb_permission_denied, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void showNoteTypes(final List<Long> ids, final List<String> noteTypes) {
        // Update the note types spinner
        ArrayAdapter<String> noteTypesAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, noteTypes);
        noteTypesSpinner.setAdapter(noteTypesAdapter);
        noteTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                noteTypeId = ids.get(pos);
                presenter.populateNoteTypeFields(noteTypeId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void showNoteDecks(final List<Long> ids, final List<String> noteDecks) {
        // Update the note decks spinner
        ArrayAdapter<String> noteDecksAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, noteDecks);
        noteDecksSpinner.setAdapter(noteDecksAdapter);
        noteDecksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                deckId = ids.get(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void showNoteTypeFields(String[] fields) {
        noteTypeFieldsContainer.addFields(fields);
    }

    @Override
    public void setAddNoteSuccess() {
        noteTypeFieldsContainer.clearFields();
        Snackbar.make(rootView, R.string.sb_add_note_success, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setAddNoteFailure() {
        Snackbar.make(rootView, R.string.sb_add_note_failure, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.add_note_button)
    void onSaveClick() {
        if (AnkiDroidHelper.isApiAvailable(getActivity())) {
            presenter.addNote(noteTypeId, deckId, noteTypeFieldsContainer.getFieldsText());
        }
    }
}
