import * as React from 'react';
import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import SamsungHealthAndroid from 'react-native-samsung-health-android';

const permissions = [SamsungHealthAndroid.Types.StepCount];

const connect = (setLoading: (state: boolean) => void) => {
  SamsungHealthAndroid.connect(true).then(() => setLoading(false));

  return () => SamsungHealthAndroid.disconnect();
};

const authorize = async () => {
  const allowed = await SamsungHealthAndroid.getPermissionAsync(permissions);

  if (allowed[SamsungHealthAndroid.Types.StepCount]) {
    return allowed;
  }

  return SamsungHealthAndroid.askPermissionAsync(permissions);
};

const readStepCount = async () => {
  try {
    const allowed = await authorize();
    const startTime = new Date();
    const endTime = new Date();

    startTime.setHours(0, 0, 0, 0);
    startTime.setDate(startTime.getDate() - 1);
    endTime.setHours(23, 59, 59, 999);

    if (allowed[SamsungHealthAndroid.Types.StepCount]) {
      const stepInfo = await SamsungHealthAndroid.readDataAsync(
        SamsungHealthAndroid.createMetric({
          type: SamsungHealthAndroid.Types.StepCount,
          start: startTime.getTime() / 1000,
          end: endTime.getTime() / 1000,
        })
      );

      const stepCount = stepInfo.reduce(
        (sum: number, item: Record<string, number>) => sum + item.count,
        0
      );

      console.log('response from steps', stepCount);
    } else {
      console.log('we dont have permissions :(', allowed);
    }
  } catch (error) {
    console.log('this is a big error', error);
  }
};

export default function App() {
  const [loading, setLoading] = React.useState<boolean>(true);

  React.useEffect(() => {
    connect(setLoading);
  }, []);

  return (
    <View style={styles.container}>
      {loading ? (
        <Text>Conectando com Samsung Health</Text>
      ) : (
        <TouchableOpacity
          onPress={() => {
            readStepCount();
          }}
        >
          <Text>Iniciar leitura</Text>
        </TouchableOpacity>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
