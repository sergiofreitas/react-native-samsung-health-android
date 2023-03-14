package com.reactnativesamsunghealthandroid;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionKey;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionType;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionResult;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class DataStore {
    public HealthDataStore mStore;
    private Boolean showLog;
    private Promise connectionPromise;
    private Promise permissionPromise;

    public DataStore(boolean debug) {
        showLog = debug;
    }

    public HealthDataStore connect(Context activity, Promise promise) {
        connectionPromise = promise;
        mStore = new HealthDataStore(activity, connectionListener);

        try {
            mStore.connectService();
        } catch (Exception e) {
            Log.d("ReactNative", "Connection fails");
            Log.d("ReactNative", e.toString());
        }

        return mStore;
    }

    public void disconnect() {
        mStore.disconnectService();
    }

    public void askPermissions(ReadableArray permissionList, Activity activity, Promise promise) {
        permissionPromise = promise;
        HealthPermissionManager permissions = new HealthPermissionManager(mStore);
        Set<PermissionKey> permKeys = new HashSet<PermissionKey>();

        for(int i = 0; i < permissionList.size(); i++) {
            permKeys.add(new PermissionKey(permissionList.getString(i), PermissionType.READ));
        }

        try {
            permissions.requestPermissions(permKeys, activity).setResultListener(permissionListener);
        } catch (Exception e) {
            permissionPromise.reject("-1", e.toString());

            if (showLog) {
                Log.d("ReactNative", "fail to ask for permissions");
                Log.d("ReactNative", e.toString());
            }
        }
    }

    public void checkPermissions(ReadableArray permissionList, Promise promise) {
        HealthPermissionManager permissions = new HealthPermissionManager(mStore);
        Set<PermissionKey> permKeys = new HashSet<PermissionKey>();
        WritableMap promiseMap = new WritableNativeMap();

        for(int i = 0; i < permissionList.size(); i++) {
            permKeys.add(new PermissionKey(permissionList.getString(i), PermissionType.READ));
        }

        try {
            Map<PermissionKey, Boolean> resultMap = permissions.isPermissionAcquired(permKeys);

            for(Map.Entry<PermissionKey, Boolean> entry : resultMap.entrySet()) {
                promiseMap.putBoolean(entry.getKey().getDataType(), entry.getValue());
            }

            promise.resolve(promiseMap);
        } catch (Exception e) {
            if (showLog) {
                Log.d("ReactNative", "fail to ask for permissions");
                Log.d("ReactNative", e.toString());
            }

            promise.reject("-1", "not connected");
        }
    }

    private final HealthResultHolder.ResultListener<PermissionResult> permissionListener = new HealthResultHolder.ResultListener<PermissionResult>() {
        @Override
        public void onResult(PermissionResult result) {
            Map<PermissionKey, Boolean> resultMap = result.getResultMap();
            WritableMap promiseMap = new WritableNativeMap();

            if (resultMap.values().contains(Boolean.FALSE)) {
                Log.d("ReactNative", "Some Permissions are not granted");
                Log.d("ReactNative", resultMap.toString());
            } else {
                Log.d("ReactNative", "All ready to get the data!");
            }

            for(Map.Entry<PermissionKey, Boolean> entry : resultMap.entrySet()) {
                promiseMap.putBoolean(entry.getKey().getDataType(), entry.getValue());
            }

            permissionPromise.resolve(promiseMap);
        }
    };

    private final HealthDataStore.ConnectionListener connectionListener = new HealthDataStore.ConnectionListener() {
        @Override
        public void onConnected() {
            if (showLog) {
                Log.d("ReactNative", "Health is connected");
            }
            connectionPromise.resolve(true);
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {
            if (error.hasResolution()) {
                String message = "";
                Log.d("ReactNative", "Occurs a error reversible error");

                switch(error.getErrorCode()) {
                    case HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED:
                        message = "Please install Samsung Health";
                        break;
                    case HealthConnectionErrorResult.OLD_VERSION_PLATFORM:
                        message = "Please upgrade Samsung Health";
                        break;
                    case HealthConnectionErrorResult.PLATFORM_DISABLED:
                        message = "Please enable Samsung Health";
                        break;
                    case HealthConnectionErrorResult.USER_AGREEMENT_NEEDED:
                        message = "Please agree with Samsung Health policy";
                        break;
                    default:
                        message = "Please make Samsung Health available";
                        break;
                }

                Log.d("ReactNative", message);
                connectionPromise.reject(Integer.toString(error.getErrorCode()), message);
            } else {
                if (showLog) {
                    Log.d("ReactNative", "Heath is not available");
                }
                connectionPromise.reject("-1", error.toString());
            }
        }

        @Override
        public void onDisconnected() {
            if (showLog) {
                Log.d("ReactNative", "Health is disconnected");
            }
        }
    };
}
