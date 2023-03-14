package com.reactnativesamsunghealthandroid

import android.app.Activity
import android.util.Log
import com.facebook.react.bridge.*

import com.reactnativesamsunghealthandroid.DataStore
import com.samsung.android.sdk.healthdata.HealthConstants


class SamsungHealthAndroidModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    var showLogs: Boolean = false
    private var store: DataStore? = null

    override fun getName(): String {
        return "SamsungHealthAndroid"
    }

    override fun getConstants(): Map<String, Any>? {
        val reactConstants = HashMap<String, Any>()
        reactConstants.put("DailyTrend",HealthConstants.StepDailyTrend.COUNT)
        reactConstants.put("StepCount", HealthConstants.StepCount.HEALTH_DATA_TYPE)
        reactConstants.put("Sleep", HealthConstants.Sleep.HEALTH_DATA_TYPE)
        reactConstants.put("SleepStage", HealthConstants.SleepStage.HEALTH_DATA_TYPE)
        reactConstants.put("CaffeineIntake", HealthConstants.CaffeineIntake.HEALTH_DATA_TYPE)
        reactConstants.put("BodyTemperature", HealthConstants.BodyTemperature.HEALTH_DATA_TYPE)
        reactConstants.put("BloodPressure", HealthConstants.BloodPressure.HEALTH_DATA_TYPE)
        reactConstants.put("Electrocardiogram", HealthConstants.Electrocardiogram.HEALTH_DATA_TYPE)
        reactConstants.put("HeartRate", HealthConstants.HeartRate.HEALTH_DATA_TYPE)
        reactConstants.put("OxygenSaturation", HealthConstants.OxygenSaturation.HEALTH_DATA_TYPE)
        reactConstants.put("AmbientTemperature", HealthConstants.AmbientTemperature.HEALTH_DATA_TYPE)
        reactConstants.put("UvExposure", HealthConstants.UvExposure.HEALTH_DATA_TYPE)
        return reactConstants
    }

    @ReactMethod
    fun connect(debug: Boolean, promise: Promise) {
        showLogs = debug
        store = DataStore(debug)

        if (showLogs){
            Log.d("ReactNative", "Starting the shealth lib")
        }

        store!!.connect(reactApplicationContext, promise)
    }

    @ReactMethod
    fun disconnect(promise: Promise) {
        store!!.disconnect()

        promise.resolve(true)
    }

    @ReactMethod
    fun askPermissionAsync(permissions: ReadableArray, promise: Promise) {
        store!!.askPermissions(permissions, currentActivity, promise)
    }

    @ReactMethod
    fun getPermissionAsync(permissions: ReadableArray, promise: Promise) {
        store!!.checkPermissions(permissions, promise)
    }

    @ReactMethod
    fun readDataAsync(metric: ReadableMap, promise: Promise) {
        var reader: ReadHealthData = ReadHealthData(store!!.mStore, metric, promise)
        reader.readOnce()
    }
}
