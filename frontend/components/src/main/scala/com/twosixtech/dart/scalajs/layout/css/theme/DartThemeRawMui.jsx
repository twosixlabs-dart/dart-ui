import React, { Component } from 'react';
// import ReactDOM from 'react-dom';
import {
  createTheme,
  CssBaseline,
  MuiThemeProvider,
} from '@material-ui/core';
import PropTypes from 'prop-types';

const theme = createTheme({
  typography: {
    button: {
      textTransform: 'none',
    },
  },
});

class DartGridRaw extends Component {
  render() {
    const {
      children,
    } = this.props;

    return (
      <MuiThemeProvider theme={theme}>
        <CssBaseline>
          {children}
        </CssBaseline>
      </MuiThemeProvider>
    );
  }
}

DartGridRaw.propTypes = {
  children: PropTypes.node.isRequired,
};

export default DartGridRaw;
