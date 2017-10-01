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


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jkcarino.ankieditor.R;
import com.jkcarino.ankieditor.ui.editor.EditorActivity;
import com.jkcarino.ankieditor.util.AnkiDroidHelper;
import com.jkcarino.ankieditor.util.PlayStoreUtils;

public class SplashActivity extends AppCompatActivity implements SplashContract.View {

    private SplashPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        presenter = new SplashPresenter(this);

        if (isTaskRoot()) {
            // Start showing the splash screen
            presenter.start();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        presenter.stop();
        super.onDestroy();
    }

    private void showMainEditor() {
        Intent intent = new Intent(this, EditorActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void checkAnkiDroidAvailability() {
        if (!AnkiDroidHelper.isAnkiDroidInstalled(this)) {
            AnkiDroidHelper.showNoAnkiInstalledDialog(this);
        } else {
            if (AnkiDroidHelper.isApiAvailable(this, /* finish */ true)) {
                showMainEditor();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AnkiDroidHelper.RC_ANKIDROID_API:
            case PlayStoreUtils.RC_OPEN_PLAY_STORE:
                checkAnkiDroidAvailability();
                break;
        }
    }
}
