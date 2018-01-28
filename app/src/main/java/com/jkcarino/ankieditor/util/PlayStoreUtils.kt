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
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri

object PlayStoreUtils {

    const val RC_OPEN_PLAY_STORE = 0x1001

    /**
     * Opens an app on Google Play Store from the specified `packageName`.
     *
     * @param activity    The context of an activity
     * @param packageName The name of application's package
     */
    fun openApp(activity: Activity, packageName: String) {
        try {
            activity.startActivityForResult(Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName)), RC_OPEN_PLAY_STORE)
        } catch (e: ActivityNotFoundException) {
            activity.startActivityForResult(Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)),
                    RC_OPEN_PLAY_STORE)
        }
    }
}
