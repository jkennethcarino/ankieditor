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

package com.jkcarino.ankieditor.ui.splash;


import android.os.Handler;
import android.support.annotation.NonNull;

public class SplashPresenter implements SplashContract.Presenter {

    private static final int SPLASH_TIMEOUT_IN_MILLIS = 500;

    private SplashContract.View splashView;
    private Handler handler;

    public SplashPresenter(@NonNull SplashContract.View splashView) {
        this.splashView = splashView;
    }

    @Override
    public void start() {
        if (handler == null) {
            handler = new Handler();

            // Start the splash screen
            handler.postDelayed(mRunnable, SPLASH_TIMEOUT_IN_MILLIS);
        }
    }

    @Override
    public void stop() {
        if (handler != null) {
            handler.removeCallbacks(mRunnable);
            handler = null;
            splashView = null;
        }
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (splashView != null) {
                splashView.checkAnkiDroidAvailability();
            }
        }
    };
}
