/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dvtonder.dashclock;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import androidx.legacy.content.WakefulBroadcastReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.util.Set;

/**
 * Broadcast receiver used to watch for changes to installed packages on the device. This triggers
 * a cleanup of extensions (in case one was uninstalled), or a data update request to an extension
 * if it was updated (its package was replaced).
 */
public class ExtensionPackageChangeReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "PackageChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Package changed broadcast received");
        ExtensionManager extensionManager = ExtensionManager.getInstance(context);
        if (extensionManager.cleanupExtensions()) {
            Log.d(TAG, "Extension cleanup performed and action taken.");
        }

        // If this is a replacement or change in the package, update all active extensions from this package.
        String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
                || Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
            if (intent.getData() == null) {
                return;
            }
            String packageName = intent.getData().getSchemeSpecificPart();
            if (TextUtils.isEmpty(packageName)) {
                return;
            }

            Set<ComponentName> activeExtensions = extensionManager.getActiveExtensionNames();
            for (ComponentName cn : activeExtensions) {
                if (packageName.equals(cn.getPackageName())) {
                    Log.d(TAG, "Package for extension " + cn + " changed; asking it for an update.");
                    Intent extensionUpdateIntent = new Intent(context, DashClockService.class);
                    extensionUpdateIntent.setAction(DashClockService.ACTION_UPDATE_EXTENSIONS);
                    // TODO: UPDATE_REASON_PACKAGE_CHANGED
                    extensionUpdateIntent.putExtra(DashClockService.EXTRA_COMPONENT_NAME, cn.flattenToShortString());
                    startWakefulService(context, extensionUpdateIntent);
                }
            }
        }
    }
}
