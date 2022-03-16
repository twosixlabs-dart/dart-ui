const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const DynamicCdnWebpackPlugin = require('dynamic-cdn-webpack-plugin');
const path = require('path');

const scalaJsConfig = require('./scalajs.webpack.config');

scalaJsConfig.module.rules = scalaJsConfig.module.rules.map((v) => {
  if (v.use.includes('source-map-loader')) {
    const newV = v;
    newV.use = ['scalajs-friendly-source-map-loader'];
    return newV;
  } return v;
});
scalaJsConfig.module.rules.push(
  {
    test: /\.(js|jsx)$/,
    exclude: {
      or: [
        /node_modules/,
        /target/,
      ],
    },
    loader: 'babel-loader',
    options: {
      presets: ['@babel/preset-env', '@babel/preset-react'],
      plugins: ['@babel/plugin-proposal-class-properties'],
    },
  },
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
        /target/,
      ],
    },
    loader: 'eslint-loader',
  },
);

// Entry must be defined relative to the scalajs-bundler directory in the app
// subproject's target directory
scalaJsConfig.output.path = path.join(scalaJsConfig.output.path, '../../../../../../public');
scalaJsConfig.output.publicPath = '/';
scalaJsConfig.output.filename = 'js/taxex.[name].[chunkhash].js';
if (scalaJsConfig.optimization) scalaJsConfig.optimization.splitChunks = { chunks: 'all' };
else {
  scalaJsConfig.optimization = {
    splitChunks: { chunks: 'all' },
  };
}
if (scalaJsConfig.plugins) {
  scalaJsConfig.plugins.push(
    new HtmlWebpackPlugin({
      filename: 'index.html',
      template: '../../../../src/assets/template.html',
    }),
    new DynamicCdnWebpackPlugin(),
    new webpack.EnvironmentPlugin({
      LAYOUT_TYPE: 'mui',
    }),
  );
} else {
  scalaJsConfig.plugins = [
    new HtmlWebpackPlugin({
      filename: 'index.html',
      template: '../../../../src/assets/template.html',
    }),
    new DynamicCdnWebpackPlugin(),
    new webpack.EnvironmentPlugin({
      LAYOUT_TYPE: 'mui',
    }),
  ];
}

module.exports = scalaJsConfig;

module.exports.resolve = {
  extensions: ['.js', '.jsx'],
  alias: {
    '@material-ui/styles': path.resolve('./node_modules/@material-ui/styles'),
    '@material-ui/core': path.resolve('./node_modules/@material-ui/core'),
  },
  modules: [ path.resolve('./node_modules') ],
};
