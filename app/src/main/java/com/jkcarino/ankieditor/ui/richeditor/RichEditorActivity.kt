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

package com.jkcarino.ankieditor.ui.richeditor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.jkcarino.ankieditor.R
import kotlinx.android.synthetic.main.activity_rich_editor.*

class RichEditorActivity : AppCompatActivity(), ColorPickerDialogListener {

    private var fieldIndex: Int = 0

    private val onInsertTableClickListener =
            object : InsertTableDialogFragment.OnInsertClickListener {
                override fun onInsertClick(colCount: Int, rowCount: Int) {
                    editor_view.insertTable(colCount, rowCount)
                }
            }

    private val onInsertLinkClickListener =
            object : InsertLinkDialogFragment.OnInsertClickListener {
                override fun onInsertClick(title: String, url: String) {
                    editor_view.insertLink(title, url)
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rich_editor)

        val extras = intent.extras
        fieldIndex = extras.getInt(EXTRA_FIELD_INDEX)
        val fieldName = extras.getString(EXTRA_FIELD_NAME, getString(R.string.app_name))
        val fieldText = extras.getString(EXTRA_FIELD_TEXT, "")

        supportActionBar?.let { toolbar ->
            toolbar.title = fieldName
            toolbar.setDisplayHomeAsUpEnabled(true)
            toolbar.setDisplayShowHomeEnabled(true)
        }

        setupViews()

        editor_toolbar.setEditorView(editor_view)
        editor_view.html = fieldText
    }

    override fun onDestroy() {
        super.onDestroy()
        editor_view.removeAllViews()
        editor_view.destroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_rich_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return when (id) {
            R.id.action_undo -> {
                editor_view.undo()
                true
            }
            R.id.action_redo -> {
                editor_view.redo()
                true
            }
            R.id.action_done -> {
                val intent = Intent().apply {
                    putExtra(EXTRA_FIELD_INDEX, fieldIndex)
                    putExtra(EXTRA_FIELD_TEXT, editor_view.html)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupViews() {
        text_fore_color.setOnClickListener {
            ColorPickerDialog.newBuilder().apply {
                setDialogId(DIALOG_TEXT_FORE_COLOR_ID)
                setDialogTitle(R.string.dialog_title_text_color)
                setShowAlphaSlider(false)
                setAllowCustom(true)
            }.show(this)
        }

        text_back_color.setOnClickListener {
            ColorPickerDialog.newBuilder().apply {
                setDialogId(DIALOG_TEXT_BACK_COLOR_ID)
                setDialogTitle(R.string.dialog_title_text_back_color)
                setShowAlphaSlider(false)
                setAllowCustom(true)
            }.show(this)
        }

        insert_table.setOnClickListener {
            InsertTableDialogFragment.newInstance().apply {
                onInsertClickListener = onInsertTableClickListener
            }.show(supportFragmentManager, DIALOG_INSERT_TABLE_TAG)
        }

        insert_link.setOnClickListener {
            InsertLinkDialogFragment.newInstance().apply {
                onInsertClickListener = onInsertLinkClickListener
            }.show(supportFragmentManager, DIALOG_INSERT_LINK_TAG)
        }
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        when (dialogId) {
            DIALOG_TEXT_FORE_COLOR_ID -> editor_view.setTextColor(color)
            DIALOG_TEXT_BACK_COLOR_ID -> editor_view.setTextBackgroundColor(color)
        }
    }

    override fun onDialogDismissed(dialogId: Int) = Unit

    companion object {
        private const val EXTRA_FIELD_NAME = "extra_field_name"
        const val EXTRA_FIELD_INDEX = "extra_field_index"
        const val EXTRA_FIELD_TEXT = "extra_field_text"

        private const val DIALOG_TEXT_FORE_COLOR_ID = 0x01
        private const val DIALOG_TEXT_BACK_COLOR_ID = 0x02
        private const val DIALOG_INSERT_TABLE_TAG = "insert-table-dialog"
        private const val DIALOG_INSERT_LINK_TAG = "insert-link-dialog"

        fun newIntent(
                context: Context,
                index: Int,
                fieldName: String,
                text: String
        ): Intent {
            return Intent(context, RichEditorActivity::class.java).apply {
                putExtra(EXTRA_FIELD_INDEX, index)
                putExtra(EXTRA_FIELD_NAME, fieldName)
                putExtra(EXTRA_FIELD_TEXT, text)
            }
        }
    }
}
