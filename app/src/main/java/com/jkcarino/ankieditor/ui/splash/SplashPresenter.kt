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

import android.os.Handler

class SplashPresenter(
        private var splashView: SplashContract.View?
) : SplashContract.Presenter {

    private var handler: Handler? = null

    private val runnable = Runnable { splashView?.checkAnkiDroidAvailability() }

    override fun start() {
        if (handler == null) {
            handler = Handler()

            // Start the splash screen
            handler?.postDelayed(runnable, SPLASH_TIMEOUT_IN_MILLIS)
        }
    }

    override fun stop() {
        handler?.removeCallbacks(runnable)
        splashView = null
        handler = null
    }

    companion object {
        private const val SPLASH_TIMEOUT_IN_MILLIS = 500L
    }
}
