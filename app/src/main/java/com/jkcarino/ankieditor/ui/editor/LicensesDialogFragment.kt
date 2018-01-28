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

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.webkit.WebView
import com.jkcarino.ankieditor.R

class LicensesDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val webView = WebView(activity)
        webView.loadUrl(LICENSES_HTML)

        return AlertDialog.Builder(activity!!).apply {
            setTitle(R.string.open_source_licenses)
            setView(webView)
            setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
        }.create()
    }

    override fun show(manager: FragmentManager, tag: String) {
        if (manager.findFragmentByTag(tag) == null) {
            super.show(manager, tag)
        }
    }

    companion object {
        const val DIALOG_TAG = "licenses-dialog"
        private const val LICENSES_HTML = "file:///android_asset/licenses.html"
    }
}
