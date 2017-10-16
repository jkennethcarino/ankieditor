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
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.ichi2.anki.api.AddContentApi;
import com.jkcarino.ankieditor.R;
import com.jkcarino.ankieditor.ui.richeditor.RichEditorActivity;
import com.jkcarino.ankieditor.util.AnkiDroidHelper;
import com.jkcarino.ankieditor.util.PlayStoreUtils;

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

    public static final int RC_FIELD_EDIT = 0x02;

    @BindView(R.id.editor_layout) View editorLayout;
    @BindView(R.id.note_type_spinner) Spinner noteTypesSpinner;
    @BindView(R.id.deck_spinner) Spinner noteDecksSpinner;
    @BindView(R.id.note_fields_container) NoteTypeFieldsContainer noteTypeFieldsContainer;
    @BindView(R.id.request_permission_stub) ViewStub requestPermissionStub;

    private View rootView;
    private View requestPermissionView;

    private Unbinder unbinder;
    private EditorContract.Presenter presenter;

    /** ID of the selected note type */
    private long noteTypeId;

    /** ID of the selected deck */
    private long deckId;

    @NonNull
    public static EditorFragment newInstance() {
        return new EditorFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_editor, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        checkAnkiDroidAvailability();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        noteTypeFieldsContainer.setOnFieldOptionsClickListener(null);
        unbinder.unbind();
        unbinder = null;
    }

    @Override
    public void setPresenter(@NonNull EditorContract.Presenter presenter) {
        this.presenter = presenter;
    }

    private void checkAnkiDroidAvailability() {
        if (!AnkiDroidHelper.isAnkiDroidInstalled(getActivity())) {
            AnkiDroidHelper.showNoAnkiInstalledDialog(getActivity());
        } else {
            if (AnkiDroidHelper.isApiAvailable(getActivity())) {
                requestAnkiDroidPermissionIfNecessary();
            }
        }
    }

    private void loadAnkiEditor() {
        presenter.populateNoteTypes();
        presenter.populateNoteDecks();

        noteTypeFieldsContainer.setOnFieldOptionsClickListener(onFieldOptionsClickListener);

        if (requestPermissionView != null) {
            requestPermissionView.setVisibility(View.GONE);
        }
        editorLayout.setVisibility(View.VISIBLE);
    }

    private void setupRequestPermissionLayout() {
        // Permission not yet granted, hide the main editor
        editorLayout.setVisibility(View.GONE);

        // Show request permission layout
        requestPermissionView = requestPermissionStub.inflate();

        Button allowButton = requestPermissionView.findViewById(R.id.allow_button);
        allowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyPermissions.requestPermissions(EditorFragment.this,
                        getString(R.string.rationale_ad_api_permission_ask_again),
                        RC_AD_READ_WRITE_PERM,
                        AddContentApi.READ_WRITE_PERMISSION);
            }
        });
    }

    @AfterPermissionGranted(RC_AD_READ_WRITE_PERM)
    private void requestAnkiDroidPermissionIfNecessary() {
        if (EasyPermissions.hasPermissions(getActivity().getApplicationContext(),
                AddContentApi.READ_WRITE_PERMISSION)) {
            loadAnkiEditor();
        } else {
            setupRequestPermissionLayout();
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
            Snackbar.make(rootView, R.string.sb_permission_denied, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PlayStoreUtils.RC_OPEN_PLAY_STORE: {
                checkAnkiDroidAvailability();
                break;
            }
            case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE: {
                if (EasyPermissions.hasPermissions(getActivity().getApplicationContext(),
                        AddContentApi.READ_WRITE_PERMISSION)) {
                    loadAnkiEditor();
                }
                break;
            }
            case EditorFragment.RC_FIELD_EDIT: {
                if (resultCode != Activity.RESULT_OK) {
                    break;
                }

                Bundle extras = data.getExtras();
                int index = extras.getInt(RichEditorActivity.EXTRA_FIELD_INDEX);
                String text = extras.getString(RichEditorActivity.EXTRA_FIELD_TEXT, "");

                noteTypeFieldsContainer.setFieldText(index, text);
                break;
            }
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
    public void setInsertedClozeText(int index, @NonNull String text) {
        noteTypeFieldsContainer.setFieldText(index, text);
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
    void onAddNoteClick() {
        if (!AnkiDroidHelper.isAnkiDroidInstalled(getActivity())) {
            AnkiDroidHelper.showNoAnkiInstalledDialog(getActivity());
        } else {
            if (AnkiDroidHelper.isApiAvailable(getActivity())) {
                presenter.addNote(noteTypeId, deckId, noteTypeFieldsContainer.getFieldsText());
            }
        }
    }

    private final NoteTypeFieldsContainer.OnFieldItemClickListener onFieldOptionsClickListener =
            new NoteTypeFieldsContainer.OnFieldItemClickListener() {

                @Override
                public void onClozeDeletionClick(int index, @NonNull String text,
                                                 int selectionStart, int selectionEnd) {
                    presenter.insertClozeAround(index, text, selectionStart, selectionEnd);
                }

                @Override
                public void onAdvancedEditorClick(int index,
                                                  @NonNull String fieldName,
                                                  @NonNull String text) {
                    Intent intent = new Intent(getActivity(), RichEditorActivity.class);
                    intent.putExtra(RichEditorActivity.EXTRA_FIELD_NAME, fieldName);
                    intent.putExtra(RichEditorActivity.EXTRA_FIELD_INDEX, index);
                    intent.putExtra(RichEditorActivity.EXTRA_FIELD_TEXT, text);
                    startActivityForResult(intent, RC_FIELD_EDIT);
                }
            };
}
