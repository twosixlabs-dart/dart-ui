import dfault from './env/default';

const env = process.env.DART_ENV;

const configuration = {
  DartConfig: {},
};

switch (env.toLowerCase()) {
  case 'default': {
    configuration.DartConfig = dfault;
    break;
  }

  default:
    configuration.DartConfig = dfault;
}

export default configuration.DartConfig;
