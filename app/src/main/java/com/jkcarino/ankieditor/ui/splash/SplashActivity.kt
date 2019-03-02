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

package com.jkcarino.ankieditor.ui.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.jkcarino.ankieditor.R
import com.jkcarino.ankieditor.ui.editor.EditorActivity
import com.jkcarino.ankieditor.util.AnkiDroidHelper
import com.jkcarino.ankieditor.util.PlayStoreUtils
import com.jkcarino.ankieditor.util.WeakRunnable

class SplashActivity : AppCompatActivity() {

    private var handler: Handler? = null

    private val weakRunnable = WeakRefRunnable(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (isTaskRoot) {
            // Start showing the splash screen
            if (handler == null) {
                handler = Handler()

                // Start the splash screen
                handler?.postDelayed(weakRunnable, SPLASH_TIMEOUT_IN_MILLIS)
            }
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        handler?.removeCallbacks(weakRunnable)
        handler = null
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            AnkiDroidHelper.RC_ANKIDROID_API,
            PlayStoreUtils.RC_OPEN_PLAY_STORE -> {
                handler?.post(weakRunnable)
            }
        }
    }

    private class WeakRefRunnable(act: Activity) : WeakRunnable<Activity>(act) {

        override fun run(referent: Activity) {
            if (!AnkiDroidHelper.isAnkiDroidInstalled(referent)) {
                AnkiDroidHelper.showNoAnkiInstalledDialog(referent)
            } else {
                if (AnkiDroidHelper.isApiAvailable(referent, /* finish */ true)) {
                    val intent = Intent(referent, EditorActivity::class.java)
                    referent.startActivity(intent).also {
                        referent.finish()
                        referent.overridePendingTransition(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val SPLASH_TIMEOUT_IN_MILLIS = 500L
    }
}
