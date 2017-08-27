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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.ichi2.anki.api.AddContentApi;
import com.jkcarino.ankieditor.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AnkiDroidHelper {

    private static final String ANKIDROID_PKG_NAME = "com.ichi2.anki";

    private Context context;
    private AddContentApi ankiDroidApi;

    public AnkiDroidHelper(@NonNull Context context) {
        this.context = context;
        this.ankiDroidApi = new AddContentApi(context.getApplicationContext());
    }

    /**
     * Checks if AnkiDroid is installed.
     *
     * @param context The context of an application.
     * @return true if AnkiDroid is installed.
     */
    private static boolean isAnkiDroidInstalled(@NonNull Context context) {
        try {
            PackageManager pm = context.getApplicationContext().getPackageManager();
            pm.getPackageInfo(ANKIDROID_PKG_NAME, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * Launches AnkiDroid app.
     *
     * @param context The context of an application.
     */
    private static void launchAnkiDroid(@NonNull Context context) {
        PackageManager manager = context.getPackageManager();
        Intent intent = manager.getLaunchIntentForPackage(ANKIDROID_PKG_NAME);
        context.startActivity(intent);
    }

    /**
     * Whether or not the API is available to use.
     * The API could be unavailable if AnkiDroid is not installed or the
     * user explicitly disabled the API.
     *
     * @param activity The context of an activity.
     * @return true if the API is available to use
     */
    public static boolean isApiAvailable(@NonNull final Activity activity) {
        final Context context = activity.getApplicationContext();

        if (AddContentApi.getAnkiDroidPackageName(context) != null) {
            return true;
        } else {
            int title = R.string.title_no_ankidroid_installed;
            int message = R.string.msg_install_ankidroid;
            int positiveButton = R.string.install;

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            if (isAnkiDroidInstalled(context)) {
                // AnkiDroid app is installed, but the AnkiDroid API is not available
                title = R.string.title_permissions_required;
                message = R.string.msg_ad_api_permission_required;
                positiveButton = android.R.string.ok;
            }

            builder.setTitle(title);
            builder.setMessage(message)
                    .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (isAnkiDroidInstalled(context)) {
                                launchAnkiDroid(context);
                            } else {
                                PlayStoreUtils.openApp(activity, ANKIDROID_PKG_NAME);
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    });
            builder.setCancelable(false);
            builder.create();
            builder.show();
        }
        return false;
    }

    public AddContentApi getApi() {
        return ankiDroidApi;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public Map<Long, String> getNoteTypes() {
        return ankiDroidApi.getModelList();
    }

    public Map<Long, String> getNoteDecks() {
        return ankiDroidApi.getDeckList();
    }

    public String[] getNoteTypeFields(long noteTypeId) {
        return ankiDroidApi.getFieldList(noteTypeId);
    }
}
