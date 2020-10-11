import { NativeModules } from 'react-native';

type SamsungHealthAndroidType = {
  multiply(a: number, b: number): Promise<number>;
};

const { SamsungHealthAndroid } = NativeModules;

export default SamsungHealthAndroid as SamsungHealthAndroidType;
