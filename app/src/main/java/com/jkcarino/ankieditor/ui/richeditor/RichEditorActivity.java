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

package com.jkcarino.ankieditor.ui.richeditor;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jkcarino.ankieditor.R;
import com.jkcarino.rtexteditorview.RTextEditorToolbar;
import com.jkcarino.rtexteditorview.RTextEditorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RichEditorActivity extends AppCompatActivity implements ColorPickerDialogListener {

    @BindView(R.id.editor_view) RTextEditorView editorView;
    @BindView(R.id.editor_toolbar) RTextEditorToolbar editorToolbar;

    public static final String EXTRA_FIELD_NAME = "extra_field_name";
    public static final String EXTRA_FIELD_INDEX = "extra_field_index";
    public static final String EXTRA_FIELD_TEXT = "extra_field_text";

    private static final int DIALOG_TEXT_FORE_COLOR_ID = 0x01;
    private static final int DIALOG_TEXT_BACK_COLOR_ID = 0x02;

    private Unbinder unbinder;

    private int fieldIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_editor);
        unbinder = ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        fieldIndex = extras.getInt(EXTRA_FIELD_INDEX);
        String fieldName = extras.getString(EXTRA_FIELD_NAME, getString(R.string.app_name));
        String fieldText = extras.getString(EXTRA_FIELD_TEXT, "");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(fieldName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        editorToolbar.setEditorView(editorView);
        editorView.setHtml(fieldText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rich_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_undo) {
            editorView.undo();
            return true;
        } else if (id == R.id.action_redo) {
            editorView.redo();
            return true;
        } else if (id == R.id.action_done) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_FIELD_INDEX, fieldIndex);
            intent.putExtra(EXTRA_FIELD_TEXT, editorView.getHtml());

            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editorView.removeAllViews();
        editorView.destroy();
        editorView = null;

        unbinder.unbind();
        unbinder = null;
    }

    @OnClick(R.id.text_fore_color)
    void onTextForeColorClick() {
        ColorPickerDialog.newBuilder()
                .setDialogId(DIALOG_TEXT_FORE_COLOR_ID)
                .setDialogTitle(R.string.dialog_title_text_color)
                .setShowAlphaSlider(false)
                .setAllowCustom(true)
                .show(this);
    }

    @OnClick(R.id.text_back_color)
    void onTextBackColorClick() {
        ColorPickerDialog.newBuilder()
                .setDialogId(DIALOG_TEXT_BACK_COLOR_ID)
                .setDialogTitle(R.string.dialog_title_text_back_color)
                .setShowAlphaSlider(false)
                .setAllowCustom(true)
                .show(this);
    }

    @OnClick(R.id.insert_table)
    void onInsertTableClick() {
        InsertTableDialogFragment dialog = InsertTableDialogFragment.newInstance();
        dialog.setOnInsertClickListener(onInsertTableClickListener);
        dialog.show(getSupportFragmentManager(), "insert-table-dialog");
    }

    @OnClick(R.id.insert_link)
    void onInsertLinkClick() {
        InsertLinkDialogFragment dialog = InsertLinkDialogFragment.newInstance();
        dialog.setOnInsertClickListener(onInsertLinkClickListener);
        dialog.show(getSupportFragmentManager(), "insert-link-dialog");
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        if (dialogId == DIALOG_TEXT_FORE_COLOR_ID) {
            editorView.setTextColor(color);
        } else if (dialogId == DIALOG_TEXT_BACK_COLOR_ID) {
            editorView.setTextBackgroundColor(color);
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    private final InsertTableDialogFragment.OnInsertClickListener onInsertTableClickListener =
            new InsertTableDialogFragment.OnInsertClickListener() {
                @Override
                public void onInsertClick(int colCount, int rowCount) {
                    editorView.insertTable(colCount, rowCount);
                }
            };

    private final InsertLinkDialogFragment.OnInsertClickListener onInsertLinkClickListener =
            new InsertLinkDialogFragment.OnInsertClickListener() {
                @Override
                public void onInsertClick(@NonNull String title, @NonNull String url) {
                    editorView.insertLink(title, url);
                }
            };
}
