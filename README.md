# react-native-samsung-health-android

React native bridge to connect with samsung health sdk for android

## Installation

```sh
npm install react-native-samsung-health-android
```

You must add the permissions in Add `android/app/src/main/AndroidManifest.xml`:

```xml
<application>

<meta-data
  android:name="com.samsung.android.health.permission.read"
  android:value="com.samsung.health.step_count;com.samsung.shealth.step_daily_trend;com.samsung.health.weight" />
</application>
```

## Usage

```js
import SamsungHealthAndroid from "react-native-samsung-health-android";

// ...

const instance = await SamsungHealthAndroid.initialize();
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
