package com.reactnativesamsunghealthandroid

import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.samsung.android.sdk.healthdata.HealthConstants
import com.samsung.android.sdk.healthdata.HealthData

val accessWithFloat = arrayOf(
        HealthConstants.StepCount.CALORIE,
        HealthConstants.StepCount.SPEED,
        HealthConstants.StepCount.DISTANCE
)

val accessWithInt = arrayOf(
        HealthConstants.StepCount.COUNT
)
// var accessWithLong = arrayOf()

class BuildPayload {
    var properties: ArrayList<String> = ArrayList<String>(0)
    var payload: WritableArray = Arguments.createArray()
    private var type: String

    constructor(type: String, propertiesArray: ReadableArray) {
        this.type = type
        for(i in 1..propertiesArray.size()) {
            val prop = propertiesArray.getString(i-1)

            Log.d("ReactNative", "add prop to property list: ${prop}")
            if (prop != null){
                this.properties.add(prop)
            }
        }
    }

    fun transformData(data: HealthData): WritableMap {
        var item = Arguments.createMap()
        item.putString("type", type);

        Log.d("ReactNative", "transforming data")

        for(prop in this.properties) {
            if (prop in accessWithFloat) {
                item.putDouble(prop, data.getFloat(prop).toDouble())
            }
            if (prop in accessWithInt) {
                item.putInt(prop, data.getInt(prop))
            }
        }

        Log.d("ReactNative", "Finish this block ${item.toString()}")

        return item
    }

    fun push(data: HealthData) {
        this.payload.pushMap(this.transformData(data))
    }

    fun toArray(): WritableArray {
        return this.payload
    }
}
