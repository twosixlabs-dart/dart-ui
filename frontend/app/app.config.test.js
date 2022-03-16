const path = require('path');

// const scalaJsConfig = require('./scalajs.webpack.config');
module.exports = {
  resolve: {
    extensions: ['.js', '.jsx', '.ts', '.tsx'],
    alias: {
      '@material-ui/styles': path.resolve('./node_modules/@material-ui/styles'),
      '@material-ui/core': path.resolve('./node_modules/@material-ui/core'),
      react: path.resolve('./node_modules/react'),
      'react-dom': path.resolve('./node_modules/react-dom'),
    },
  },
  module: {
    rules: [
      {
        test: /\.worker\.js$/,
        exclude: /node_modules/,
        use: { loader: 'worker-loader' },
      },
      {
        test: /\.(js|jsx)$/,
        exclude: {
          or: [
            /node_modules/,
            /opt\./,
            /main\./,
          ],
        },
        loader: 'babel-loader',
        options: {
          presets: ['@babel/preset-env', '@babel/preset-react'],
          plugins: ['@babel/plugin-proposal-class-properties'],
        },
      },
      {
        test: /\.(js|jsx)$/,
        exclude: {
          or: [
            /node_modules/,
            /opt/,
            /main\./,
            /src/,
          ],
        },
        loader: 'eslint-loader',
      },
    ],
  },
};
