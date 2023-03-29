package com.reactnativesamsunghealthandroid

import android.util.Log
import com.facebook.react.bridge.*
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthDataResolver
import com.samsung.android.sdk.healthdata.HealthDataResolver.ReadRequest
import com.samsung.android.sdk.healthdata.HealthDataStore


class SamsungHealthAndroidModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    var showLogs: Boolean = false
    private var store: DataStore? = null

  override fun getName(): String {
        return "SamsungHealthAndroid"
    }

  fun getStore(): HealthDataStore? {
    return store!!.mStore
  }

  @ReactMethod
  fun readStepCountDailies(
    startDate: Double,
    endDate: Double,
    error: Callback,
    success: Callback?
  ) {
    val resolver = HealthDataResolver(store!!.mStore, null)
    val filter = HealthDataResolver.Filter.and(
      HealthDataResolver.Filter.greaterThanEquals("day_time", startDate.toLong()),
      HealthDataResolver.Filter.lessThanEquals("day_time", endDate.toLong())
    )
    val request = ReadRequest.Builder()
      .setDataType("com.samsung.shealth.step_daily_trend")
      .setProperties(
        arrayOf(
          HealthConstants.StepCount.COUNT, HealthConstants.StepCount.DISTANCE,
          "day_time", HealthConstants.StepCount.CALORIE,
          HealthConstants.StepCount.SPEED, HealthConstants.StepCount.DEVICE_UUID
        )
      )
      .setFilter(filter).build()
    try {
      resolver.read(request).setResultListener(HealthDataResultListener(this, error, success))
    } catch (e: Exception) {
      Log.e("RNSamsungHealth", e.javaClass.name + " - " + e.message)
      Log.e("RNSamsungHealth", "Getting step count fails.")
      error.invoke("Getting step count fails.")
    }
  }

  override fun getConstants(): Map<String, Any>? {
        val reactConstants = HashMap<String, Any>()
      reactConstants["STEP_DAILY_TREND"] =  "com.samsung.shealth.step_daily_trend"
      reactConstants["StepCount"] = HealthConstants.StepCount.HEALTH_DATA_TYPE
      reactConstants["Sleep"] = HealthConstants.Sleep.HEALTH_DATA_TYPE
      reactConstants["SleepStage"] = HealthConstants.SleepStage.HEALTH_DATA_TYPE
      reactConstants["CaffeineIntake"] = HealthConstants.CaffeineIntake.HEALTH_DATA_TYPE
      reactConstants["BodyTemperature"] = HealthConstants.BodyTemperature.HEALTH_DATA_TYPE
      reactConstants["BloodPressure"] = HealthConstants.BloodPressure.HEALTH_DATA_TYPE
      reactConstants["Electrocardiogram"] = HealthConstants.Electrocardiogram.HEALTH_DATA_TYPE
      reactConstants["HeartRate"] = HealthConstants.HeartRate.HEALTH_DATA_TYPE
      reactConstants["OxygenSaturation"] = HealthConstants.OxygenSaturation.HEALTH_DATA_TYPE
      reactConstants["AmbientTemperature"] = HealthConstants.AmbientTemperature.HEALTH_DATA_TYPE
      reactConstants["UvExposure"] = HealthConstants.UvExposure.HEALTH_DATA_TYPE
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
//  @ReactMethod
//  fun readStepCountDailies(metric: ReadableMap, promise: Promise) {
//    var reader: ReadHealthData = ReadHealthData(store!!.mStore, metric, promise)
//  }

}
