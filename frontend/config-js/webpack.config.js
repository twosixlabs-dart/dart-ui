const path = require('path');
const webpack = require('webpack');
const CopyPlugin = require('copy-webpack-plugin');

const publicDirEnv = process.env.BACKEND_PUBLIC_DIR;
const publicDirDefault = path.resolve(__dirname, '../../public/');
const publicDir = publicDirEnv || publicDirDefault;

// Webpack definition to compile js side of frontend app
module.exports = {
  name: 'config',
  entry: path.resolve(__dirname, 'src/main.js'),
  output: {
    filename: 'js/config.js',
    path: publicDir,
  },
  mode: 'development',
  plugins: [
    new webpack.EnvironmentPlugin({
      DART_ENV: 'default',
      DART_ENABLE_LOGGING: null,
      DART_PUBLIC_REPORT_DURATION: null,
      DART_LOG_MAX_LENGTH: null,
      DART_AUTH_BYPASS: null,
      DART_BASE_PATH: null,
      TENANTS_URL: null,
      PROCUREMENT_URL: null,
      SEARCH_URL: null,
      RAW_DOC_URL: null,
      RAW_DOC_SOURCE: null,
    }),
    new CopyPlugin({
      patterns: [
        {
          from: path.resolve(__dirname, 'assets/'),
          to: publicDir,
          transform(content) {
            return content
              .toString()
              .replace('$AUTH_SERVER_URL', process.env.AUTH_SERVER_URL || 'http://localhost:8090/auth');
          },
        },
      ],
    }),
  ],
};
