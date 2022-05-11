const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const path = require('path');


module.exports = (env, { mode }) => ({
  entry: {
    app: path.resolve(__dirname, 'src/main/main.js'),
  },
  output: {
    path: path.join(__dirname, '../../public/'),
    publicPath: '/',
    filename: mode === 'production' ? 'js/dart-ui.[name].[chunkhash].js' : 'js/dart-ui.[name].[hash].js',
  },
  devtool: mode === 'production' ? undefined : 'source-map',
  optimization: {
    splitChunks: { chunks: 'all' },
  },
  plugins: [
    new HtmlWebpackPlugin({
      filename: 'index.html',
      template: path.resolve(__dirname, 'src/assets/template.html'),
    }),
    new webpack.EnvironmentPlugin({
      LAYOUT_TYPE: 'mui',
    }),
  ],
  resolve: {
    extensions: ['.js', '.jsx', '.ts', '.tsx'],
    alias: {
      '@material-ui/styles': path.resolve(__dirname, 'node_modules/@material-ui/styles'),
      '@material-ui/core': path.resolve(__dirname, 'node_modules/@material-ui/core'),
    },
  },
  module: {
    rules: [
      {
        test: new RegExp('\\.js$'),
        enforce: 'pre',
        use: ['scalajs-friendly-source-map-loader'],
      },
      {
        test: /\.worker\.js$/,
        exclude: {
          or: [
            /node_modules/,
            /main/,
          ],
        },
        use: { loader: 'worker-loader' },
      },
      {
        test: /\.(js|jsx)$/,
        exclude: {
          or: [
            /node_modules/,
            /main/,
          ],
        },
        loader: 'babel-loader',
        options: {
          presets: ['@babel/preset-env', '@babel/preset-react'],
          plugins: ['@babel/plugin-proposal-class-properties', 'syntax-dynamic-import'],
        },
      },
      {
        test: /\.(js|jsx)$/,
        exclude: {
          or: [
            /node_modules/,
            /opt\./,
            /main/,
          ],
        },
        loader: 'eslint-loader',
      },
      {
        test: /\.mjs$/,
        include: /node_modules/,
        type: 'javascript/auto'
      },
    ],
  },
  devServer: {
    static: {
      directory: path.join(__dirname, '../../public/')
    },
    port: 8080,
    hot: true,
    historyApiFallback: true,
  }
});
