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

package com.jkcarino.ankieditor.util;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

public class PlayStoreUtils {

    /**
     * Opens an app on Google Play Store from the specified {@code packageName}.
     *
     * @param activity    The context of an activity
     * @param packageName The name of application's package
     */
    public static void openApp(@NonNull Activity activity, @NonNull String packageName) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }
}
