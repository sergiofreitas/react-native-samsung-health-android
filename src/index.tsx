import { NativeModules } from 'react-native';

type SamsungHealthAndroidType = {
  Types: Record<string, string>;
  createMetric(
    props: SamsungHealthAndroidMetricProps
  ): SamsungHealthAndroidMetric;
  connect(debug: boolean): Promise<boolean>;
  disconnect(): Promise<boolean>;
  askPermissionAsync(permissions: string[]): Promise<Record<string, boolean>>;
  getPermissionAsync(permissions: string[]): Promise<Record<string, boolean>>;
  readDataAsync(
    metric: SamsungHealthAndroidMetric
  ): Promise<Record<string, number>[]>;

  getStepCountDailie(options: options): Promise<Record<string, number>[]>;
};

type SamsungHealthAndroidMetric = {
  type: string;
  properties: string[];
  start: number;
  end: number;
};

type SamsungHealthAndroidMetricProps = {
  type: string;
  start: number;
  end: number;
};

type options = {
  startDate: string;
  endDate: string;
};

const { SamsungHealthAndroid } = NativeModules;

console.log("SamsungHealthAndroid", SamsungHealthAndroid);


// const samsungHealth = NativeModules.RNSamsungHealth;

SamsungHealthAndroid.Types = SamsungHealthAndroid.getConstants();
SamsungHealthAndroid.createMetric = (
  props: SamsungHealthAndroidMetricProps
): SamsungHealthAndroidMetric => {
  const { type } = props;
  let properties: string[] = [];

  switch (type) {
    case SamsungHealthAndroid.Types.DailyTrend:
      properties = ['calorie', 'count', 'distance', 'speed'];
      break;
    case SamsungHealthAndroid.Types.StepCount:
      properties = ['update_time', 'calorie', 'count', 'distance', 'speed'];
      break;
    case SamsungHealthAndroid.Types.Sleep:
      properties = ['start_time', 'end_time', 'custom', 'comment'];
      break;
    case SamsungHealthAndroid.Types.SleepStage:
      properties = ['start_time', 'end_time', 'stage', 'sleep_id'];
      break;
    case SamsungHealthAndroid.Types.CaffeineIntake:
      properties = ['update_time', 'amount', 'unit_amount'];
      break;
    case SamsungHealthAndroid.Types.BodyTemperature:
      properties = ['update_time', 'temperature'];
      break;
    case SamsungHealthAndroid.Types.BloodPressure:
      properties = ['update_time', 'diastolic', 'mean', 'pulse', 'systolic'];
      break;
    case SamsungHealthAndroid.Types.Electrocardiogram:
      properties = [
        'data',
        'data_format',
        'max_heart_rate',
        'mean_heart_rate',
        'min_heart_rate',
        'sample_count',
        'sample_frequency',
        'update_time',
      ];
      break;
    case SamsungHealthAndroid.Types.HeartRate:
      properties = [
        'min',
        'max',
        'heart_rate',
        'heart_bate_count',
        'binning_data',
        'update_time',
      ];
      break;
    case SamsungHealthAndroid.Types.OxygenSaturation:
      properties = ['update_time', 'heart_rate', 'spo2'];
      break;
    case SamsungHealthAndroid.Types.AmbientTemperature:
      properties = [
        'accuracy',
        'altitude',
        'humidity',
        'latitude',
        'longitude',
        'temperature',
        'update_time',
      ];
      break;
    case SamsungHealthAndroid.Types.UvExposure:
      properties = [
        'accuracy',
        'altitude',
        'latitude',
        'longitude',
        'uv_index',
        'update_time',
      ];
      break;
    default:
      throw new Error(`Type ${type} is not supported`);
  }

  return {
    ...props,
    properties,
  };
};

SamsungHealthAndroid.getStepCountDailie = (options: options): any => {

  console.log("options", options);
  
  let startDate =
    options.startDate != undefined
      ? options.startDate
      : new Date().setHours(0, 0, 0, 0);
  let endDate =
    options.endDate != undefined ? options.endDate : new Date().valueOf();

  return new Promise((resolve, reject) => {
    SamsungHealthAndroid.readStepCountDailies(
      startDate,
      endDate,
      (msg: any) => reject(msg, false),
      (res: any) => resolve(res)
    );
  });
};

export default SamsungHealthAndroid as SamsungHealthAndroidType;
