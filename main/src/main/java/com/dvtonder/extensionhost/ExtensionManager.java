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

package com.dvtonder.extensionhost;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.google.android.apps.dashclock.api.host.ExtensionListing;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A singleton class in charge of extension registration, activation (change in user-specified
 * 'active' extensions), and data caching.
 */
class ExtensionManager {
    private static final String TAG = "ExtensionManager";

    private final Context mApplicationContext;

    private final Set<ExtensionWithData> mActiveExtensions = new HashSet<>();

    private Map<ComponentName, ExtensionWithData> mExtensionInfoMap = new HashMap<>();
    private List<OnChangeListener> mOnChangeListeners = new ArrayList<>();

    private SharedPreferences mValuesPreferences;
    private Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    private static ExtensionManager sInstance;

    private ExtensionManager(Context context) {
        mApplicationContext = context.getApplicationContext();
        mValuesPreferences = mApplicationContext.getSharedPreferences("extension_data", 0);
        Log.d(TAG, "ExtensionManager instantiated.");
    }

    static ExtensionManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ExtensionManager(context);
        }

        return sInstance;
    }

    /**
     * De-activates active extensions that are unsupported or are no longer installed.
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean cleanupExtensions() {
        Set<ComponentName> availableExtensions = new HashSet<>();
        for (ExtensionListing info : getAvailableExtensions()) {
            // Ensure the extension protocol version is supported. If it isn't, don't allow its use.
            if (!info.compatible()) {
                Log.w(TAG, "Extension '" + info.title() + "' using unsupported protocol version "
                        + info.protocolVersion() + ".");
                continue;
            }
            availableExtensions.add(info.componentName());
        }

        boolean cleanupRequired = false;
        Set<ComponentName> newActiveExtensions = new HashSet<>();

        synchronized (mActiveExtensions) {
            for (ExtensionWithData ewd : mActiveExtensions) {
                if (availableExtensions.contains(ewd.listing.componentName())) {
                    newActiveExtensions.add(ewd.listing.componentName());
                } else {
                    cleanupRequired = true;
                }
            }
        }

        if (cleanupRequired) {
            setActiveExtensions(newActiveExtensions);
            return true;
        }

        return false;
    }

    /**
     * Replaces the set of active extensions with the given list.
     */
    void setActiveExtensions(Set<ComponentName> extensions) {
        // Join external and internal extensions
        Set<ComponentName> allExtensions = new HashSet<>(extensions);
        Map<ComponentName, ExtensionListing> infos = new HashMap<>();
        for (ExtensionListing info : getAvailableExtensions()) {
            infos.put(info.componentName(), info);
        }

        Set<ComponentName> activeExtensionNames = getActiveExtensionNames();
        if (activeExtensionNames.equals(allExtensions)) {
            Log.d(TAG, "No change to list of active extensions.");
            return;
        }

        // Clear cached data for any no-longer-active extensions.
        for (ComponentName cn : activeExtensionNames) {
            if (!allExtensions.contains(cn)) {
                destroyExtensionData(cn);
            }
        }

        // Set the new list of active extensions, loading cached data if necessary.
        List<ExtensionWithData> newActiveExtensions = new ArrayList<>();

        for (ComponentName cn : allExtensions) {
            if (mExtensionInfoMap.containsKey(cn)) {
                newActiveExtensions.add(mExtensionInfoMap.get(cn));
            } else {
                ExtensionWithData ewd = new ExtensionWithData();
                ewd.listing = infos.get(cn);
                if (ewd.listing == null) {
                    ewd.listing = new ExtensionListing();
                    ewd.listing.componentName(cn);
                }
                ewd.latestData = deserializeExtensionData(ewd.listing.componentName());
                newActiveExtensions.add(ewd);
            }
        }

        mExtensionInfoMap.clear();
        for (ExtensionWithData ewd : newActiveExtensions) {
            mExtensionInfoMap.put(ewd.listing.componentName(), ewd);
        }

        synchronized (mActiveExtensions) {
            mActiveExtensions.clear();
            mActiveExtensions.addAll(newActiveExtensions);
        }

        Log.d(TAG, "List of active extensions has changed.");
        notifyOnChangeListeners(null);
    }

    /**
     * Updates and caches the user-visible data for a given extension.
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean updateExtensionData(ComponentName cn, ExtensionData data) {
        data.clean();

        ExtensionWithData ewd = mExtensionInfoMap.get(cn);
        if (ewd != null && !ExtensionData.equals(ewd.latestData, data)) {
            ewd.latestData = data;
            serializeExtensionData(ewd.listing.componentName(), data);
            notifyOnChangeListeners(ewd.listing.componentName());
            return true;
        }
        return false;
    }

    private ExtensionData deserializeExtensionData(ComponentName componentName) {
        ExtensionData extensionData = new ExtensionData();
        String val = mValuesPreferences.getString(componentName.flattenToString(), "");
        if (!TextUtils.isEmpty(val)) {
            try {
                extensionData.deserialize((JSONObject) new JSONTokener(val).nextValue());
            } catch (JSONException e) {
                Log.e(TAG, "Error loading extension data cache for " + componentName + ".",
                        e);
            }
        }
        return extensionData;
    }

    private void serializeExtensionData(ComponentName componentName, ExtensionData extensionData) {
        try {
            mValuesPreferences.edit()
                    .putString(componentName.flattenToString(),
                            extensionData.serialize().toString())
                    .apply();
        } catch (JSONException e) {
            Log.e(TAG, "Error storing extension data cache for " + componentName + ".", e);
        }
    }

    private void destroyExtensionData(ComponentName componentName) {
        mValuesPreferences.edit()
                .remove(componentName.flattenToString())
                .apply();
    }

    ExtensionWithData getExtensionWithData(ComponentName extension) {
        return mExtensionInfoMap.get(extension);
    }

    List<ExtensionWithData> getActiveExtensionsWithData() {
        ArrayList<ExtensionWithData> activeExtensions;
        synchronized (mActiveExtensions) {
            activeExtensions = new ArrayList<>(mActiveExtensions);
        }
        return activeExtensions;
    }

    Set<ComponentName> getActiveExtensionNames() {
        Set<ComponentName> list = new HashSet<>();
        for (ExtensionWithData ci : mActiveExtensions) {
            list.add(ci.listing.componentName());
            Log.d(TAG, "Active extension: " + ci.listing.componentName().getClassName());
        }
        return list;
    }

    /**
     * Returns a listing of all available (installed) extensions, including those that aren't
     * world-readable.
     */
    List<ExtensionListing> getAvailableExtensions() {
        List<ExtensionListing> availableExtensions = new ArrayList<>();
        PackageManager pm = mApplicationContext.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentServices(new Intent(DashClockExtension.ACTION_EXTENSION), PackageManager.GET_META_DATA);

        Log.d(TAG, "Searching for available extensions...");

        for (ResolveInfo resolveInfo : resolveInfos) {
            Log.d(TAG, "Checking resolveInfo: " + resolveInfo.loadLabel(pm).toString());

            ExtensionListing info = new ExtensionListing();
            info.componentName(new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name));
            info.title(resolveInfo.loadLabel(pm).toString());
            Bundle metaData = resolveInfo.serviceInfo.metaData;
            if (metaData != null) {
                info.compatible(ExtensionHost.supportsProtocolVersion(metaData.getInt("protocolVersion")));
                info.worldReadable(metaData.getBoolean("worldReadable", false));
                info.description(metaData.getString("description"));
                String settingsActivity = metaData.getString("settingsActivity");
                if (!TextUtils.isEmpty(settingsActivity)) {
                    info.settingsActivity(ComponentName.unflattenFromString(resolveInfo.serviceInfo.packageName + "/" + settingsActivity));
                }
            }

            info.icon(resolveInfo.getIconResource());
            availableExtensions.add(info);
        }

        Log.d(TAG, "Found " + availableExtensions.size() + " Extensions");
        return availableExtensions;
    }

    /**
     * Registers a listener to be triggered when either the list of active extensions changes or an
     * extension's data changes.
     */
    void addOnChangeListener(OnChangeListener onChangeListener) {
        mOnChangeListeners.add(onChangeListener);
    }

    /**
     * Removes a listener previously registered with {@link #addOnChangeListener}.
     */
    void removeOnChangeListener(OnChangeListener onChangeListener) {
        mOnChangeListeners.remove(onChangeListener);
    }

    private void notifyOnChangeListeners(final ComponentName sourceExtension) {
        mMainThreadHandler.post(() -> {
            for (OnChangeListener listener : mOnChangeListeners) {
                listener.onExtensionsChanged(sourceExtension);
            }
        });
    }

    public interface OnChangeListener {
        /**
         * @param sourceExtension null if not related to any specific extension (e.g. list of
         *                        extensions has changed).
         */
        void onExtensionsChanged(ComponentName sourceExtension);
    }

    static class ExtensionWithData {
        ExtensionListing listing;
        ExtensionData latestData;
    }
}
