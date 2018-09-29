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

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.jkcarino.ankieditor.R
import com.jkcarino.ankieditor.extensions.addFragmentToActivity
import com.jkcarino.ankieditor.util.AnkiDroidHelper
import kotlinx.android.synthetic.main.activity_editor.*

class EditorActivity : AppCompatActivity() {

    private lateinit var presenter: EditorPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        setSupportActionBar(toolbar)

        // Create the fragment
        val fragment: EditorFragment = supportFragmentManager.findFragmentById(R.id.content_frame)
                as? EditorFragment ?: EditorFragment.newInstance()

        if (savedInstanceState == null) {
            addFragmentToActivity(supportFragmentManager, fragment, R.id.content_frame)
        }

        // Create the presenter
        presenter = EditorPresenter(fragment, AnkiDroidHelper(this)).also { it.start() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return when (id) {
            R.id.action_open_source_licenses -> {
                LicensesDialogFragment()
                        .show(supportFragmentManager, LicensesDialogFragment.DIALOG_TAG)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.stop()
    }
}
