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

package com.jkcarino.ankieditor.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import com.ichi2.anki.api.AddContentApi
import com.jkcarino.ankieditor.R

class AnkiDroidHelper(context: Context) {

    val api: AddContentApi = AddContentApi(context.applicationContext)

    val noteTypes: Map<Long, String>
        get() = api.modelList.toList().sortedBy { (_, value) -> value }.toMap()

    val noteDecks: Map<Long, String>
        get() = api.deckList.toList().sortedBy { (_, value) -> value }.toMap()

    fun getNoteTypeFields(noteTypeId: Long): Array<String>? = api.getFieldList(noteTypeId)

    companion object {
        private const val ANKIDROID_PKG_NAME = "com.ichi2.anki"
        const val RC_ANKIDROID_API = 0x1000

        /**
         * Checks if AnkiDroid is installed.
         *
         * @param context The context of an application.
         * @return true if AnkiDroid is installed.
         */
        fun isAnkiDroidInstalled(context: Context): Boolean {
            return try {
                val pm = context.applicationContext.packageManager
                pm.getPackageInfo(ANKIDROID_PKG_NAME, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

        /**
         * Launches AnkiDroid app.
         *
         * @param activity The context of an activity.
         */
        private fun launchAnkiDroid(activity: Activity) {
            val intent = Intent().apply {
                setClassName(ANKIDROID_PKG_NAME, "$ANKIDROID_PKG_NAME.IntentHandler")
            }
            activity.startActivityForResult(intent, RC_ANKIDROID_API)
        }

        /**
         * Whether or not the API is available to use.
         * The API could be unavailable if AnkiDroid is not installed or the
         * user explicitly disabled the API.
         *
         * @param activity The context of an activity.
         * @return true if the API is available to use
         */
        fun isApiAvailable(activity: Activity, finish: Boolean = false): Boolean {
            val context = activity.applicationContext

            if (AddContentApi.getAnkiDroidPackageName(context) != null) {
                return true
            } else {
                AlertDialog.Builder(activity).apply {
                    setTitle(R.string.title_permissions_required)
                    setMessage(R.string.msg_ad_api_permission_required)
                    setPositiveButton(android.R.string.ok) { _, _ -> launchAnkiDroid(activity) }
                    setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
                        if (finish) {
                            activity.finish()
                        } else {
                            dialogInterface.cancel()
                        }
                    }
                    setCancelable(false)
                }.create().show()
            }
            return false
        }

        /**
         * Shows a dialog that informs the user to install AnkiDroid app on Google Play Store.
         *
         * @param activity The context of an activity
         */
        fun showNoAnkiInstalledDialog(activity: Activity) {
            AlertDialog.Builder(activity).apply {
                setTitle(R.string.title_no_ankidroid_installed)
                setMessage(R.string.msg_install_ankidroid)
                setPositiveButton(R.string.install) { _, _ ->
                    PlayStoreUtils.openApp(activity, ANKIDROID_PKG_NAME)
                }
                setNegativeButton(android.R.string.no) { _, _ -> activity.finish() }
                setCancelable(false)
            }.create().show()
        }
    }
}
