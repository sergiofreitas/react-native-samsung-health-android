package com.reactnativesamsunghealthandroid;

import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

public class ReadHealthData {
    private HealthDataStore store;
    private Promise dataPromise;
    private ReadableMap metric;

    public ReadHealthData(HealthDataStore ds, ReadableMap metricMap, Promise promise) {
        this.store = ds;
        this.dataPromise = promise;
        this.metric = metricMap;

        Log.d("ReactNative", "received metric");
        Log.d("ReactNative", metricMap.toString());
    }

    private long getStartTimeOfToday() {
        Calendar today = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return today.getTimeInMillis();
    }

    public void readOnce() {
        ReadableArray propertiesArray = metric.getArray("properties");
        HealthDataResolver resolver = new HealthDataResolver(this.store, null);
        String[] properties = new String[propertiesArray.size()];

        for (int i = 0; i < propertiesArray.size(); i++) {
            properties[i] = propertiesArray.getString(i);
        }

        long startTime = Double.valueOf(metric.getDouble("start") * 1000).longValue();
        long endTime = Double.valueOf(metric.getDouble("end") * 1000).longValue();

        HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType(metric.getString("type"))
                .setProperties(properties)
                .setLocalTimeRange(
                        HealthConstants.SessionMeasurement.START_TIME,
                        HealthConstants.SessionMeasurement.TIME_OFFSET,
                        startTime,
                        endTime
                )
                .build();

        try {
            resolver.read(request).setResultListener(resultListener);
        } catch (Exception e) {
            Log.d("ReactNative", e.toString());
        }
    }

    private final HealthResultHolder.ResultListener<HealthDataResolver.ReadResult> resultListener;

    {
        resultListener = new HealthResultHolder.ResultListener<HealthDataResolver.ReadResult>() {

            @Override
            public void onResult(HealthDataResolver.ReadResult healthData) {
                try {
                    Iterator<HealthData> iterator = healthData.iterator();
                    BuildPayload payload = new BuildPayload(metric.getString("type"), metric.getArray("properties"));

                    Log.d("ReactNative", "will start to iterate over data");

                    while(iterator.hasNext()) {
                        HealthData data = iterator.next();
                        payload.push(data);
                    }

                    dataPromise.resolve(payload.toArray());

                } finally {
                    healthData.close();
                }
            }
        };
    }
}
